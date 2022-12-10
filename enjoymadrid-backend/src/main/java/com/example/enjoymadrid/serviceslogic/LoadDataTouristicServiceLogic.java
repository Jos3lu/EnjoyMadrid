package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.services.LoadDataTouristicService;

@Service
public class LoadDataTouristicServiceLogic implements LoadDataTouristicService {
	
	private static final Logger logger = LoggerFactory.getLogger(LoadDataTouristicService.class);
	
	private final TouristicPointRepository touristicPointRepository;
	
	public LoadDataTouristicServiceLogic(TouristicPointRepository touristicPointRepository) {
		this.touristicPointRepository = touristicPointRepository;
	}

	@Override
	public void loadDataTouristicPoints() {
		// Query to get touristic points from DB
		List<TouristicPoint> touristicPointsDB = this.touristicPointRepository.findAll();
		Map<String, TouristicPoint> touristicPointsDBMap = touristicPointsDB.stream()
				.collect(Collectors.toMap(
						point -> point.getName() + "-" + point.getLongitude() + "-" + point.getLatitude() + "-" + point.getType(), point -> point));
		// Points extracted from the Madrid city hall page
		List<TouristicPoint> touristicPoints = Collections.synchronizedList(new ArrayList<>());
		
		// Data sources
		String[] dataOrigins = { "turismo_v1_es.xml", "deporte_v1_es.xml", "tiendas_v1_es.xml", "noche_v1_es.xml",
				"restaurantes_v1_es.xml" };

		// Sync threads pool (dataOriginsLength - 1 + Main)
		CyclicBarrier waitToEnd = new CyclicBarrier(dataOrigins.length);

		// Index last dataOrigins array
		int originLast = dataOrigins.length - 1;
		// Each thread for type of tourism
		ExecutorService ex = Executors.newFixedThreadPool(originLast);

		for (int i = 0; i < originLast; i++) {
			final int originIndex = i;
			ex.execute(() -> loadDataTouristicPoints(dataOrigins[originIndex], waitToEnd, touristicPointsDBMap, touristicPoints));
		}
		ex.shutdown();
		
		// Keep the Main busy
		loadDataTouristicPoints(dataOrigins[originLast], waitToEnd, touristicPointsDBMap, touristicPoints);

		// Delete points not found anymore on the Madrid city hall page
		touristicPointsDB.removeAll(touristicPoints);
		touristicPointsDB.forEach(point -> this.touristicPointRepository.delete(point));

		logger.info("Touristic points updated");
	}
	
	private void loadDataTouristicPoints(String typeTourism, CyclicBarrier waitToEnd, Map<String, TouristicPoint> touristicPointsDB,
			List<TouristicPoint> touristicPoints) {

		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new URL("https://www.esmadrid.com/opendata/" + typeTourism).openStream());
		} catch (SAXException | IOException | ParserConfigurationException e) {
			logger.error(e.getMessage());
			try {
				waitToEnd.await();
			} catch (InterruptedException | BrokenBarrierException e1) {
				logger.error(e.getMessage());
			}
			return;
		}

		// Normalize the xml response
		document.getDocumentElement().normalize();

		NodeList listNodes = document.getElementsByTagName("service");

		// Spain/Madrid current day
		LocalDate currentDate = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toLocalDate();
		for (int i = 0; i < listNodes.getLength(); i++) {			
			Node node = listNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String name = element.getElementsByTagName("name").item(0).getTextContent();
				// Unescapes a string containing entity escapes to a string containing the actual Unicode characters
				name = StringEscapeUtils.unescapeHtml4(name);
				Double longitude = tryParseDouble(element.getElementsByTagName("longitude").item(0).getTextContent());
				Double latitude = tryParseDouble(element.getElementsByTagName("latitude").item(0).getTextContent());
				String type = element.getElementsByTagName("extradata").item(0).getChildNodes().item(1).getTextContent();

				// To delete the points that are removed from the page of Madrid
				touristicPoints.add(new TouristicPoint(name, longitude, latitude));

				// If point is already in database and has been updated or it's not in the
				// database we update/add the point in the DB
				TouristicPoint pointDB = touristicPointsDB.get(name + "-" + longitude + "-" + latitude + "-" + type);
				LocalDate updateDate = LocalDate.parse(element.getAttribute("fechaActualizacion"));
				if (pointDB != null && Period.between(updateDate, currentDate).getDays() > 30) {
					continue;
				}

				String address = element.getElementsByTagName("address").item(0).getTextContent();
				Integer zipcode = tryParseInteger(element.getElementsByTagName("zipcode").item(0).getTextContent());
				String phone = element.getElementsByTagName("phone").item(0).getTextContent();
				String description = element.getElementsByTagName("body").item(0).getTextContent();
				String email = element.getElementsByTagName("email").item(0).getTextContent();
				String paymentServices = "";
				String horary = "";
				List<String> categories = new ArrayList<>();
				List<String> subcategories = new ArrayList<>();
				List<String> images = new ArrayList<>();

				NodeList listNodesItems = element.getElementsByTagName("item");
				for (int j = 0; j < listNodesItems.getLength(); j++) {
					Node nodeItem = listNodesItems.item(j);

					if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
						Element elementItem = (Element) nodeItem;

						switch (elementItem.getAttribute("name")) {
						case "Horario":
							horary = elementItem.getTextContent();
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

				if (pointDB != null) {
					point.setId(pointDB.getId());
				}

				// Save the point in the database
				this.touristicPointRepository.save(point);
			}
		}

		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	
	
	private Double tryParseDouble(String parseString) {
		// Try to parse to Double if not possible then return null
		try {
			return Double.parseDouble(parseString);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private Integer tryParseInteger(String parseString) {
		// Try to parse to Integer if not possible then return null
		try {
			return Integer.parseInt(parseString);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
