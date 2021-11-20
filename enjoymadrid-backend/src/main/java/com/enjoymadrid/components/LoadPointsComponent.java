package com.enjoymadrid.components;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.enjoymadrid.model.TouristicPoint;
import com.enjoymadrid.model.User;
import com.enjoymadrid.model.repositories.TouristicPointRepository;
import com.enjoymadrid.model.repositories.UserRepository;

@Component
@EnableScheduling
public class LoadPointsComponent implements CommandLineRunner{

	private CyclicBarrier waitToEnd;
	private List<TouristicPoint> touristicPoints;
	private static final Logger logger = LoggerFactory.getLogger(LoadPointsComponent.class);
	
	private final TouristicPointRepository touristicPointRepository;
	private final UserRepository userRepository;
	
	
	public LoadPointsComponent(TouristicPointRepository pointRepository, UserRepository userRepository) {
		this.touristicPointRepository = pointRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		
		User user1 = new User("Ramon","ramoneitor", new BCryptPasswordEncoder().encode("1fsdfsdAff3"));
		userRepository.save(user1);
		
		User user2 = new User("Pepe", "pepeitor", new BCryptPasswordEncoder().encode("dfdsjhf3213DS"));
		userRepository.save(user2);
		
		User user3 = new User("Juan", "juaneitor", new BCryptPasswordEncoder().encode("dsd321AJDJdfd"));
		userRepository.save(user3);
		
		loadDataTouristicPoints();
	}

	/**
	 * This method is executed all the days at 8 a.m (and the first time the server is activated), 
	 * checking for new information and deleting old information
	 */
	@Scheduled(cron = "0 0 8 * * *", zone = "Europe/Madrid")
	private void loadDataTouristicPoints() {
		String[] dataOrigins = {"turismo_v1_es.xml", "deporte_v1_es.xml", "tiendas_v1_es.xml", "noche_v1_es.xml", "restaurantes_v1_es.xml"};

		ExecutorService ex = Executors.newFixedThreadPool(dataOrigins.length);
		waitToEnd = new CyclicBarrier(dataOrigins.length + 1);
		touristicPoints = Collections.synchronizedList(new LinkedList<>());

		for (String origin : dataOrigins) {
			ex.execute(() -> loadDataTouristicPoints(origin));
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
		touristicPointRepository.findAll()
			.stream()
			.filter(point -> !touristicPoints.contains(point))
			.forEach(point -> touristicPointRepository.delete(point));
			
		logger.info("Touristic points updated in database");
	}

	private void loadDataTouristicPoints(String typeTourism) {

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
				touristicPoints.add(new TouristicPoint(longitude, latitude, name));

				// If point is already in database and has been updated or is not in the database we update/add the point in the DB
				Optional<TouristicPoint> pointDB = touristicPointRepository.findTopByNameIgnoreCaseAndLongitudeAndLatitude(name, longitude, latitude);
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
				
				TouristicPoint point = new TouristicPoint(longitude, latitude, name, address, zipcode, phone, 
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
