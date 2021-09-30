package com.enjoymadrid.components;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.enjoymadrid.model.Point;
import com.enjoymadrid.model.repositories.PointRepository;

@Component
@EnableScheduling
public class LoadPointsComponent implements CommandLineRunner {

	@Autowired
	private PointRepository pointRepository;

	@Scheduled(cron = "0 0 8 * * *", zone = "Europe/Madrid")
	public void loadData() throws SAXException, IOException, ParserConfigurationException {
		RestTemplate template = new RestTemplate();
		byte[] response = template.getForObject("https://www.esmadrid.com/opendata/turismo_v1_es.xml", String.class)
				.getBytes();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(response));
		document.getDocumentElement().normalize();

		NodeList listNodes = document.getElementsByTagName("service");
		
		String currentDate = ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toString().split("T")[0]; // Current date in
		// Spain/Madrid
		for (int i = 0; i < listNodes.getLength(); i++) {
			Node node = listNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String name = element.getElementsByTagName("name").item(0).getTextContent();
				if (pointRepository.findByNameIgnoreCase(name) != null
						&& !currentDate.equals(element.getAttribute("fechaActualizacion"))) {
					continue;
				}

				Double longitude = tryParseDouble(element.getElementsByTagName("longitude").item(0).getTextContent());
				Double latitude = tryParseDouble(element.getElementsByTagName("latitude").item(0).getTextContent());
				String address = element.getElementsByTagName("address").item(0).getTextContent();
				Integer zipcode = tryParseInteger(element.getElementsByTagName("zipcode").item(0).getTextContent());
				String phone = element.getElementsByTagName("phone").item(0).getTextContent();
				String web = element.getElementsByTagName("web").item(0).getTextContent();
				String description = element.getElementsByTagName("body").item(0).getTextContent();
				String email = element.getElementsByTagName("email").item(0).getTextContent();
				String type = "";
				String paymentServices = "";
				String horary = "";
				List<String> categories = new LinkedList<>();
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
				
				Point point = new Point(longitude, latitude, name, address, zipcode, phone, web, 
						description, email, paymentServices, horary, type, categories, images);
				pointRepository.save(point);
			}
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

	@Override
	public void run(String... args) throws Exception {
		loadData();
	}

}
