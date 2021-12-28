package com.enjoymadrid.components;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.enjoymadrid.models.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.enjoymadrid.models.repositories.TouristicPointRepository;
import com.enjoymadrid.models.repositories.TransportPointRepository;
import com.enjoymadrid.models.AirQualityPoint;
import com.enjoymadrid.models.BycicleTransportPoint;
import com.enjoymadrid.models.PublicTransportPoint;
import com.enjoymadrid.models.TouristicPoint;
import com.enjoymadrid.models.TransportPoint;
import com.enjoymadrid.models.User;

@Component
@EnableScheduling
public class LoadPointsComponent implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(LoadPointsComponent.class);

	private final TransportPointRepository transportPointRepository;
	private final AirQualityPointRepository airQualityStationRepository;
	private final TouristicPointRepository touristicPointRepository;
	private final UserRepository userRepository;

	public LoadPointsComponent(TransportPointRepository transportPointRepository,
			AirQualityPointRepository airQualityStationRepository, TouristicPointRepository touristicPointRepository,
			UserRepository userRepository) {
		this.transportPointRepository = transportPointRepository;
		this.airQualityStationRepository = airQualityStationRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) throws Exception {

		User user1 = new User("Ramon", "ramoneitor", new BCryptPasswordEncoder().encode("1fsdfsdAff3"));
		userRepository.save(user1);

		User user2 = new User("Pepe", "pepeitor", new BCryptPasswordEncoder().encode("dfdsjhf3213DS"));
		userRepository.save(user2);

		User user3 = new User("Juan", "juaneitor", new BCryptPasswordEncoder().encode("dsd321AJDJdfd"));
		userRepository.save(user3);

		loadDataAirQualityPoints();
		loadDataTouristicPoints();
		loadDataTransportPoints();
	}

	/**
	 * Add air quality stations if not already in DB an then update the
	 * air quality data
	 */
	private void loadDataAirQualityPoints() {

		Document document = null;
		try {
			File stationsCoord = new ClassPathResource("static/qualityair/estaciones_calidad_aire.geo").getFile();
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stationsCoord);
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
				Double longitude = tryParseDouble(element.getElementsByTagName("geo:long").item(0).getTextContent());
				Double latitude = tryParseDouble(element.getElementsByTagName("geo:lat").item(0).getTextContent());

				// Search if station already exists in DB
				Boolean airQualityPointDB = airQualityStationRepository
						.existsByLongitudeAndLatitude(longitude, latitude);

				if (!airQualityPointDB) {
					airQualityStationRepository.save(new AirQualityPoint(name, longitude, latitude));
				}

			}
		}

		// Update the air quality data
		new Thread(() -> updateAqiPoints()).start();

	}

	/**
	 * This method is executed three times a day (12 a.m. / 8 a.m. / 4 p.m.),
	 * updating the air quality data at each station. 
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0/8 * * *", zone = "Europe/Madrid")
	private void scheduleAqiStations() {
		/*
		 * Execute the method updateIcaStations at a random minute. Scheduled annotation
		 * (above) should end at minute 0. If pool tries to execute at minute 0, there
		 * might be a race condition with the actual thread running this block. In
		 * variable minuteExecuteUpdate we exclude the first 5 minutes for this reason.
		 */
		Integer minuteExecuteUpdate = 5 + new Random().nextInt(55);
		ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

		ex.schedule(() -> updateAqiPoints(), minuteExecuteUpdate, TimeUnit.MINUTES);
	}

	/**
	 * Update Aqi levels of each air quality station
	 */
	private void updateAqiPoints() {

		/*
		 * Pages ->
		 * https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_5~~TEMP_UNIT_c~~WIND_UNIT_kmh~
		 * https://website-api.airvisual.com/v1/stations/by/cityID/igp7hSLYmouA2JFhu?AQI=US&language=es
		 */

		// Add stations to a list before add it to the DB
		Map<String, Integer> airQualityPoints = new HashMap<>();

		// Web page https://www.iqair.com/es/
		WebClient client = WebClient.create(
				"https://website-api.airvisual.com/v1/stations/by/cityID/igp7hSLYmouA2JFhu?AQI=US&language=es");

		ArrayNode response = client.get().retrieve().bodyToMono(ArrayNode.class).block();

		for (JsonNode station : response) {
			String name = station.get("name").asText();
			Integer aqi = station.get("aqi").asInt();
			airQualityPoints.put(name, aqi);
		}

		try {
			// Web page https://www.eltiempo.es
			org.jsoup.nodes.Document page = Jsoup
					.connect("https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_5~~TEMP_UNIT_c~~WIND_UNIT_kmh~")
					.get();

			org.jsoup.nodes.Element tableMadrid = page.select("table").stream()
					.filter(table -> table.getElementsByTag("th").get(0).text().equals("Madrid")).findFirst().get();

			Elements rows = tableMadrid.select("tr");

			for (int i = 1; i < rows.size(); i++) {
				Elements stations = rows.get(i).select("td");
				String name = stations.get(0).ownText();
				Integer aqi = tryParseInteger(stations.get(1).child(0).text());

				switch (name) {
				case "Barajas - Pueblo":
					name = "Barajas Pueblo";
					break;

				case "Cuatro Caminos-Pablo Iglesias":
					name = "Cuatro Caminos";
					break;

				case "Fernandez Ladreda-Oporto":
					name = "Plaza Eliptica";
					break;

				case "Plaza Castilla-Canal":
					name = "Plaza Castilla";
					break;

				case "Mendez Alvaro":
					name = "Méndez Álvaro";
					break;

				case "Puente De Vallecas":
					name = "Vallecas";
					break;

				case "Retiro":
					name = "Parque del Retiro";
					break;

				case "Urbanizacion Embajada":
					name = "Urbanización Embajada";
					break;

				}

				if (!airQualityPoints.containsKey(name)) {
					airQualityPoints.put(name, aqi);
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		}

		for (Map.Entry<String, Integer> airQualityStation : airQualityPoints.entrySet()) {
			// Search station in DB
			Optional<AirQualityPoint> airQualityPointDB = airQualityStationRepository
					.findByNameIgnoreCase(airQualityStation.getKey());

			// If not in DB skip it
			if (airQualityPointDB.isEmpty()) {
				continue;
			}

			airQualityPointDB.get().setAqi(airQualityStation.getValue());
			airQualityStationRepository.save(airQualityPointDB.get());
		}

		logger.info("Air quality stations updated");
	}

	/**
	 * This method is executed all the Mondays at 12:00 a.m. (and the first time the
	 * server is activated), checking for new information and deleting old information in tourist points.
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0 ? * 1", zone = "Europe/Madrid")
	private void loadDataTouristicPoints() {
		// Data sources
		String[] dataOrigins = { "turismo_v1_es.xml", "deporte_v1_es.xml", "tiendas_v1_es.xml", "noche_v1_es.xml",
				"restaurantes_v1_es.xml" };

		// Sync threads pool
		CyclicBarrier waitToEnd = new CyclicBarrier(dataOrigins.length + 1);
		// Points extracted from the Madrid city hall page
		List<TouristicPoint> touristicPoints = Collections.synchronizedList(new LinkedList<>());

		// Each thread for type of tourism
		ExecutorService ex = Executors.newFixedThreadPool(dataOrigins.length);

		for (String origin : dataOrigins) {
			ex.execute(() -> loadDataTouristicPoints(origin, waitToEnd, touristicPoints));
		}
		ex.shutdown();

		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
			ex.shutdownNow();
			return;
		}

		// Delete points not found anymore on the Madrid city hall page
		List<TouristicPoint> touristicPointsDB = touristicPointRepository.findAll();
		touristicPointsDB.removeAll(touristicPoints);
		touristicPointsDB.forEach(point -> touristicPointRepository.delete(point));

		/*
		 * touristicPointRepository.findAll().stream().filter(point ->
		 * !touristicPoints.contains(point)) .forEach(point ->
		 * touristicPointRepository.delete(point));
		 */

		logger.info("Touristic points updated");
	}

	private void loadDataTouristicPoints(String typeTourism, CyclicBarrier waitToEnd,
			List<TouristicPoint> touristicPoints) {

		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new URL("https://www.esmadrid.com/opendata/" + typeTourism).openStream());
		} catch (SAXException | IOException | ParserConfigurationException e) {
			logger.error(e.getMessage());
			return;
		}

		// Normalize the xml response
		document.getDocumentElement().normalize();

		NodeList listNodes = document.getElementsByTagName("service");

		// Spain/Madrid current day
		String currentDate = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate().toString();
		for (int i = 0; i < listNodes.getLength(); i++) {
			Node node = listNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String name = element.getElementsByTagName("name").item(0).getTextContent();
				Double longitude = tryParseDouble(element.getElementsByTagName("longitude").item(0).getTextContent());
				Double latitude = tryParseDouble(element.getElementsByTagName("latitude").item(0).getTextContent());

				// To delete the points that are removed from the page of Madrid
				touristicPoints.add(new TouristicPoint(name, longitude, latitude));

				// If point is already in database and has been updated or is not in the
				// database we update/add the point in the DB
				Optional<TouristicPoint> pointDB = touristicPointRepository
						.findTopByNameIgnoreCaseAndLongitudeAndLatitude(name, longitude, latitude);
				if (pointDB.isPresent() && !currentDate.equals(element.getAttribute("fechaActualizacion"))) {
					continue;
				}

				String address = element.getElementsByTagName("address").item(0).getTextContent();
				Integer zipcode = tryParseInteger(element.getElementsByTagName("zipcode").item(0).getTextContent());
				String phone = element.getElementsByTagName("phone").item(0).getTextContent();
				String description = element.getElementsByTagName("body").item(0).getTextContent();
				String email = element.getElementsByTagName("email").item(0).getTextContent();
				String paymentServices = "";
				String horary = "";
				String type = "";
				List<String> categories = new LinkedList<>();
				List<String> subcategories = new LinkedList<>();
				List<String> images = new LinkedList<>();

				NodeList listNodesItems = element.getElementsByTagName("item");
				for (int j = 0; j < listNodesItems.getLength(); j++) {
					Node nodeItem = listNodesItems.item(j);

					if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
						Element elementItem = (Element) nodeItem;

						switch (elementItem.getAttribute("name")) {
						case "Horario":
							horary = elementItem.getTextContent();
							break;

						case "Tipo":
							type = elementItem.getTextContent();
							break;

						case "Servicios de pago":
							paymentServices = elementItem.getTextContent();
							break;

						case "Categoria":
							categories.add(elementItem.getTextContent());
							break;

						case "SubCategoria":
							subcategories.add(elementItem.getTextContent());
							break;
						}
					}
				}

				NodeList listNodesMedia = element.getElementsByTagName("media");
				for (int j = 0; j < listNodesMedia.getLength(); j++) {
					Node nodeMedia = listNodesMedia.item(j);
					if (nodeMedia.getNodeType() == Node.ELEMENT_NODE) {
						Element elementMedia = (Element) nodeMedia;
						images.add(elementMedia.getTextContent());
					}
				}

				TouristicPoint point = new TouristicPoint(name, longitude, latitude, address, zipcode, phone,
						description, email, paymentServices, horary, type, categories, subcategories, images);

				if (pointDB.isPresent()) {
					point.setId(pointDB.get().getId());
				}

				// Save the point in the database
				touristicPointRepository.save(point);
			}
		}

		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * Load the information of all the transport points to DB if not already
	 */
	private void loadDataTransportPoints() {		
		// Data sources
		String[][] publicTransportTypes = {
				{"subway", "static/subway/estaciones_red_de_metro.geojson", "static/subway/paradas_por_itinerario_red_de_metro.geojson"}, 
				{"bus", "static/bus/estaciones_red_de_autobuses_urbanos_de_madrid__EMT.geojson", "static/bus/paradas_por_itinerario_red_de_autobuses_urbanos_de_madrid__EMT.geojson"}, 
				{"commuter", "static/commuter/estaciones_red_de_cercanias.geojson", "static/commuter/paradas_por_itinerario_red_de_cercanias.geojson"}
		};
		
		// Thread for each type of transport
		ExecutorService ex = Executors.newFixedThreadPool(publicTransportTypes.length + 1);
		
		// Sync threads pool
		CyclicBarrier waitToEnd = new CyclicBarrier(publicTransportTypes.length + 2);
		
		for (String[] publicTransport: publicTransportTypes) {
			ex.execute(() -> loadDataPublicTransportPoints(publicTransport[0], publicTransport[1], publicTransport[2], waitToEnd));
		}	
		ex.execute(() -> loadDataBiciMADPoints("bicycle", "static/bicycle/estaciones_bici_transporte_publico.geojson", waitToEnd));
		ex.shutdown();
				
		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
			ex.shutdownNow();
			return;
		}
		
		// Add the neighbors

		
		logger.info("Transport points updated");
	}
	
	private void loadDataPublicTransportPoints(String type, String stopsFile, String itineraryStopsFile, CyclicBarrier waitToEnd) {
		ObjectMapper objectMapper = new ObjectMapper();

		// Store stops before save it in DataBase
		Map<String, PublicTransportPoint> publicTransportStops = new HashMap<>();

		// Transform json file to tree model
		try {
			File stopsCoord = new ClassPathResource(stopsFile).getFile();

			JsonNode stops = objectMapper.readTree(stopsCoord).get("features");
			// For each node get the name and coordinates, and save in HashMap
			for (JsonNode stop : stops) {
				String name = stop.get("properties").get("DENOMINACION").asText();
				Double longitude = stop.get("geometry").get("coordinates").get(0).asDouble();
				Double latitude = stop.get("geometry").get("coordinates").get(1).asDouble();
				publicTransportStops.putIfAbsent(name,
						new PublicTransportPoint(StringUtils.capitalize(name.toLowerCase()), longitude, latitude, type));
			}

			File stopsItinerary = new ClassPathResource(itineraryStopsFile)
					.getFile();

			stops = objectMapper.readTree(stopsItinerary).get("features");
			// In the file get line information for each node
			for (JsonNode stop : stops) {
				String name = stop.get("properties").get("DENOMINACION").asText();
				String line = stop.get("properties").get("NUMEROLINEAUSUARIO").asText();
				Integer direction = stop.get("properties").get("SENTIDO").asInt();
				Integer order = stop.get("properties").get("NUMEROORDEN").asInt();
					
				PublicTransportPoint publicTransportPoint = publicTransportStops.get(name);
				publicTransportPoint.getLines().add(line + " [" + direction + "] " + ": " + order);
			}
			
			for (TransportPoint transportPoint : publicTransportStops.values()) {
				// Search if stop already exists in DB
				Boolean transportPointDB = transportPointRepository
						.existsByNameIgnoreCaseAndLongitudeAndLatitude(transportPoint.getName(),
								transportPoint.getLongitude(), transportPoint.getLatitude());
				if (!transportPointDB) {
					transportPointRepository.save(transportPoint);
				}

			}
			
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	private void loadDataBiciMADPoints(String type, String stopsFile, CyclicBarrier waitToEnd) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Transform json file to tree model
		try {
			File stopsCoord = new ClassPathResource(stopsFile).getFile();
			
			JsonNode stops = objectMapper.readTree(stopsCoord).get("data");
			// In file get name and coordinates for each stop
			for (JsonNode stop: stops) {
				String name = stop.get("name").asText();
				String stationNumber = stop.get("number").asText();
				Double longitude = stop.get("geometry").get("coordinates").get(0).asDouble();
				Double latitude = stop.get("geometry").get("coordinates").get(1).asDouble();
				
				// Search if stop already exists in DB
				Boolean transportPointDB = transportPointRepository
						.existsByStationNumber(stationNumber);
				if (!transportPointDB) {
					transportPointRepository.save(new BycicleTransportPoint(stationNumber, name, longitude, latitude, type));
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} 
		
		// Add availability of each Bike station
		updateBiciMADStations();
		
		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	/**
	 * This method is executed all the days every 30 minutes (and the first time the
	 * server is activated), checking for new information and deleting old information.
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0/30 * * * ?", zone = "Europe/Madrid")
	private void updateBiciMADStations() {
		// Web page EMT api
		WebClient client = WebClient.create(
				"https://openapi.emtmadrid.es/v1/transport/bicimad/stations/");

		ObjectNode response = client.get().retrieve().bodyToMono(ObjectNode.class).block();
		
		JsonNode stations = response.get("data");
		
		for (JsonNode station : stations) {
			String stationNumber = station.get("number").asText();
			
			Optional<BycicleTransportPoint> bycicleTransportPointDBOpt = transportPointRepository.findByStationNumber(stationNumber);
			
			if (bycicleTransportPointDBOpt.isEmpty()) {
				continue;
			}
			
			Integer activate = station.get("activate").asInt();
			Integer no_available = station.get("no_available").asInt();
			Integer total_bases = station.get("total_bases").asInt();
			Integer dock_bikes = station.get("dock_bikes").asInt();
			Integer free_bases = station.get("free_bases").asInt();
			Integer reservations_count = station.get("reservations_count").asInt();
			
			BycicleTransportPoint bycicleTransportPointDB = bycicleTransportPointDBOpt.get();
			bycicleTransportPointDB.setActivate(activate == 1 ? true : false);
			bycicleTransportPointDB.setNo_available(no_available == 1 ? true : false);
			bycicleTransportPointDB.setTotalBases(total_bases);
			bycicleTransportPointDB.setDockBases(dock_bikes);
			bycicleTransportPointDB.setFreeBases(free_bases);
			bycicleTransportPointDB.setReservations(reservations_count);
			
			transportPointRepository.save(bycicleTransportPointDB);
		}
		
	}
	

	private Double tryParseDouble(String parseString) {
		try {
			return Double.parseDouble(parseString);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer tryParseInteger(String parseString) {
		try {
			return Integer.parseInt(parseString);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
