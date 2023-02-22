package com.example.enjoymadrid.servicesimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.enjoymadrid.models.AirQualityPoint;
import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.services.AirQualityLoadService;
import com.example.enjoymadrid.services.SharedService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import reactor.core.publisher.Mono;

@Service
public class AirQualityLoadServiceImpl implements AirQualityLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(AirQualityLoadService.class);
	
	private final AirQualityPointRepository airQualityPointRepository;
	private final SharedService sharedService;
	
	public AirQualityLoadServiceImpl(AirQualityPointRepository airQualityPointRepository, SharedService sharedService) {
		this.airQualityPointRepository = airQualityPointRepository;
		this.sharedService = sharedService;
	}

	@Override
	public void loadAirQualityPoints() {
		// Query to get number of air quality points from DB
		long airQualityPointsDB = this.airQualityPointRepository.count();
		
		if (airQualityPointsDB > 0) {
			// Update the air quality data
			updateAqiData();
			
			return;
		}

		Document document = null;
		try {
			// For jar file, instead of getting getFile use getInputStream and transform into file
			InputStream stationsCoordStream = new ClassPathResource("static/qualityair/estaciones_calidad_aire.geo").getInputStream();
			File stationsCoordFile = File.createTempFile("estaciones_calidad_aire", "geo");
			stationsCoordFile.deleteOnExit();
			FileOutputStream outputStream = new FileOutputStream(stationsCoordFile);
			IOUtils.copy(stationsCoordStream, outputStream);
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stationsCoordFile);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			logger.error(e.getMessage());
			return;
		}

		// Normalize the xml response
		document.getDocumentElement().normalize();

		NodeList listNodes = document.getElementsByTagName("entry");

		for (int i = 0; i < listNodes.getLength(); i++) {
			Node node = listNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String name = element.getElementsByTagName("title").item(0).getTextContent();
				Double longitude = this.sharedService
						.tryParseDouble(element.getElementsByTagName("geo:long").item(0).getTextContent());
				Double latitude = this.sharedService
						.tryParseDouble(element.getElementsByTagName("geo:lat").item(0).getTextContent());

				// Save air quality point in DB
				this.airQualityPointRepository.save(new AirQualityPoint(name, longitude, latitude));
			}
		}

		// Update the air quality data
		updateAqiData();
	}

	@Override
	public void updateAqiData() {
		/*
		 * Pages ->
		 * 1.
		 * https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_4~~TEMP_UNIT_c~~WIND_UNIT_kmh~
		 * 2.
		 * https://website-api.airvisual.com/v1/stations/by/cityID/igp7hSLYmouA2JFhu?AQI=US&language=es
		 */

		// Add stations to a list before add it to the DB
		Map<String, Integer> airQualityPoints = new HashMap<>();

		// Web page https://www.iqair.com/es/
		WebClient client = WebClient.create(
				"https://website-api.airvisual.com");

		ArrayNode response = client.get()
				.uri("/v1/stations/by/cityID/igp7hSLYmouA2JFhu?AQI=US&language=es")
				.retrieve()
				.bodyToMono(ArrayNode.class)
				.onErrorResume(WebClientResponseException.class, error -> Mono.empty())
				.block();
		
		if (response == null) response = new ArrayNode(null);
		
		for (JsonNode point : response) {
			String name = point.get("name").asText();
			Integer aqi = point.get("aqi").asInt();
			airQualityPoints.put(name, aqi);
		}

		try {
			// Web page https://www.eltiempo.es
			org.jsoup.nodes.Document page = Jsoup
					.connect("https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_4~~TEMP_UNIT_c~~WIND_UNIT_kmh~")
					.get();

			org.jsoup.nodes.Element tableMadrid = page.select("table").stream()
					.filter(table -> table.getElementsByTag("th").get(0).text().equals("Madrid")).findFirst().get();

			Elements rows = tableMadrid.select("tr");

			Map<String, String> namePoints = Map.of(
					"Barajas - Pueblo", "Barajas Pueblo",
					"Cuatro Caminos-Pablo Iglesias", "Cuatro Caminos",
					"Fernandez Ladreda-Oporto", "Plaza Eliptica",
					"Plaza Castilla-Canal", "Plaza Castilla",
					"Mendez Alvaro", "Méndez Álvaro",
					"Puente De Vallecas", "Vallecas",
					"Retiro", "Parque del Retiro",
					"Urbanizacion Embajada", "Urbanización Embajada");
			for (int i = 1; i < rows.size(); i++) {
				Elements points = rows.get(i).select("td");
				String name = points.get(0).ownText();
				Integer aqi = this.sharedService.tryParseInteger(points.get(1).child(0).text());
				
				// Change the station name to match with the other information source
				if (namePoints.containsKey(name)) {
					name = namePoints.get(name);
				}

				if (!airQualityPoints.containsKey(name)) {
					airQualityPoints.put(name, aqi);
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		}

		// Query to get air quality points from DB and transform into map
		Map<String, AirQualityPoint> airQualityPointsDB = this.airQualityPointRepository.findAll().stream()
				.collect(Collectors.toMap(point -> point.getName().toLowerCase(), point -> point));
		
		// Iterate over the air quality stations
		for (Map.Entry<String, Integer> airQualityPoint : airQualityPoints.entrySet()) {
			// Search station in DB
			AirQualityPoint airQualityPointDB = airQualityPointsDB.get(airQualityPoint.getKey().toLowerCase());

			// If not in DB skip it
			if (airQualityPointDB == null) {
				continue;
			}

			airQualityPointDB.setAqi(airQualityPoint.getValue());
			this.airQualityPointRepository.save(airQualityPointDB);
		}

		logger.info("Air quality stations updated");
	}

}
