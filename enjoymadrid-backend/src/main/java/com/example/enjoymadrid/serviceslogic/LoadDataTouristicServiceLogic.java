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

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

@Service
public class LoadDataTouristicServiceLogic implements LoadDataTouristicService {
	
	private static final Logger logger = LoggerFactory.getLogger(LoadDataTouristicService.class);
	// Filter symbols
	private static final String[] SYMBOLS = { 
			"¿", "?", "~", "`", "!", "¡", "@", "#", "$", "%", "^", "*", "(", ")", "_", "-", "+", "=", "{", "}", "[", "]", "\\", ":", 
			";", "<", ">", "/", ".", ",", "&", "\n", "\r", "\t", "|", "“", "\"", "–", "”", "©", "-", "—", "…", ";", "‘", "’" 
	};
	// Stop words
	private static final String[] STOP_WORDS = { 
			"a", "actualmente", "adelante", "además", "afirmó", "agregó", "ahora", "ahí", "al", "algo", "alguna", "algunas", "alguno", "algunos", "algún", "alrededor", "ambos", "ampleamos", "ante", "anterior", "antes", "apenas", 
			"aproximadamente", "aquel", "aquellas", "aquellos", "aqui", "aquí", "arriba", "aseguró", "así", "atras", "aunque", "ayer", "añadió", "aún", "bajo", "bastante", "bien", "buen", "buena", "buenas", "bueno", "buenos", 
			"cada", "casi", "cerca", "cierta", "ciertas", "cierto", "ciertos", "cinco", "comentó", "como", "con", "conocer", "conseguimos", "conseguir", "considera", "consideró", "consigo", "consigue", "consiguen", "consigues", 
			"contra", "cosas", "creo", "cual", "cuales", "cualquier", "cuando", "cuanto", "cuatro", "cuenta", "cómo", "da", "dado", "dan", "dar", "de", "debe", "deben", "debido", "decir", "dejó", "del", "demás", "dentro", "desde", 
			"después", "dice", "dicen", "dicho", "dieron", "diferente", "diferentes", "dijeron", "dijo", "dio", "donde", "dos", "durante", "e", "ejemplo", "el", "ella", "ellas", "ello", "ellos", "embargo", "empleais", "emplean", 
			"emplear", "empleas", "empleo", "en", "encima", "encuentra", "entonces", "entre", "era", "erais", "eramos", "eran", "eras", "eres", "es", "esa", "esas", "ese", "eso", "esos", "esta", "estaba", "estabais", "estaban", 
			"estabas", "estad", "estada", "estadas", "estado", "estados", "estais", "estamos", "estan", "estando", "estar", "estaremos", "estará", "estarán", "estarás", "estaré", "estaréis", "estaría", "estaríais", "estaríamos", 
			"estarían", "estarías", "estas", "este", "estemos", "esto", "estos", "estoy", "estuve", "estuviera", "estuvierais", "estuvieran", "estuvieras", "estuvieron", "estuviese", "estuvieseis", "estuviesen", "estuvieses", 
			"estuvimos", "estuviste", "estuvisteis", "estuviéramos", "estuviésemos", "estuvo", "está", "estábamos", "estáis", "están", "estás", "esté", "estéis", "estén", "estés", "ex", "existe", "existen", "explicó", "expresó", 
			"fin", "fue", "fuera", "fuerais", "fueran", "fueras", "fueron", "fuese", "fueseis", "fuesen", "fueses", "fui", "fuimos", "fuiste", "fuisteis", "fuéramos", "fuésemos", "gran", "grandes", "gueno", "ha", "haber", "habida", 
			"habidas", "habido", "habidos", "habiendo", "habremos", "habrá", "habrán", "habrás", "habré", "habréis", "habría", "habríais", "habríamos", "habrían", "habrías", "habéis", "había", "habíais", "habíamos", "habían", "habías", 
			"hace", "haceis", "hacemos", "hacen", "hacer", "hacerlo", "haces", "hacia", "haciendo", "hago", "han", "has", "hasta", "hay", "haya", "hayamos", "hayan", "hayas", "hayáis", "he", "hecho", "hemos", "hicieron", "hizo", "hoy", 
			"hube", "hubiera", "hubierais", "hubieran", "hubieras", "hubieron", "hubiese", "hubieseis", "hubiesen", "hubieses", "hubimos", "hubiste", "hubisteis", "hubiéramos", "hubiésemos", "hubo", "igual", "incluso", "indicó", 
			"informó", "intenta", "intentais", "intentamos", "intentan", "intentar", "intentas", "intento", "ir", "junto", "la", "lado", "largo", "las", "le", "les", "llegó", "lleva", "llevar", "lo", "los", "luego", "lugar", "manera", 
			"manifestó", "mayor", "me", "mediante", "mejor", "mencionó", "menos", "mi", "mientras", "mio", "mis", "misma", "mismas", "mismo", "mismos", "modo", "momento", "mucha", "muchas", "mucho", "muchos", "muy", "más", "mí", "mía", 
			"mías", "mío", "míos", "nada", "nadie", "ni", "ninguna", "ningunas", "ninguno", "ningunos", "ningún", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nueva", "nuevas", "nuevo", "nuevos", 
			"nunca", "o", "ocho", "os", "otra", "otras", "otro", "otros", "para", "parece", "parte", "partir", "pasada", "pasado", "pero", "pesar", "poca", "pocas", "poco", "pocos", "podeis", "podemos", "poder", "podria", "podriais", 
			"podriamos", "podrian", "podrias", "podrá", "podrán", "podría", "podrían", "poner", "por", "por qué", "porque", "posible", "primer", "primera", "primero", "primeros", "principalmente", "propia", "propias", "propio", "propios", 
			"próximo", "próximos", "pudo", "pueda", "puede", "pueden", "puedo", "pues", "que", "quedó", "queremos", "quien", "quienes", "quiere", "quién", "qué", "realizado", "realizar", "realizó", "respecto", "sabe", "sabeis", "sabemos", 
			"saben", "saber", "sabes", "se", "sea", "seamos", "sean", "seas", "segunda", "segundo", "según", "seis", "ser", "seremos", "será", "serán", "serás", "seré", "seréis", "sería", "seríais", "seríamos", "serían", "serías", "seáis", 
			"señaló", "si", "sido", "siempre", "siendo", "siete", "sigue", "siguiente", "sin", "sino", "sobre", "sois", "sola", "solamente", "solas", "solo", "solos", "somos", "son", "soy", "su", "sus", "suya", "suyas", "suyo", "suyos", 
			"sí", "sólo", "tal", "también", "tampoco", "tan", "tanto", "te", "tendremos", "tendrá", "tendrán", "tendrás", "tendré", "tendréis", "tendría", "tendríais", "tendríamos", "tendrían", "tendrías", "tened", "teneis", "tenemos", 
			"tener", "tenga", "tengamos", "tengan", "tengas", "tengo", "tengáis", "tenida", "tenidas", "tenido", "tenidos", "teniendo", "tenéis", "tenía", "teníais", "teníamos", "tenían", "tenías", "tercera", "ti", "tiempo", "tiene", 
			"tienen", "tienes", "toda", "todas", "todavía", "todo", "todos", "total", "trabaja", "trabajais", "trabajamos", "trabajan", "trabajar", "trabajas", "trabajo", "tras", "trata", "través", "tres", "tu", "tus", "tuve", "tuviera", 
			"tuvierais", "tuvieran", "tuvieras", "tuvieron", "tuviese", "tuvieseis", "tuviesen", "tuvieses", "tuvimos", "tuviste", "tuvisteis", "tuviéramos", "tuviésemos", "tuvo", "tuya", "tuyas", "tuyo", "tuyos", "tú", "ultimo", "un", 
			"una", "unas", "uno", "unos", "usa", "usais", "usamos", "usan", "usar", "usas", "uso", "usted", "va", "vais", "valor", "vamos", "van", "varias", "varios", "vaya", "veces", "ver", "verdad", "verdadera", "verdadero", "vez", 
			"vosotras", "vosotros", "voy", "vuestra", "vuestras", "vuestro", "vuestros", "y", "ya", "yo", "él", "éramos", "ésta", "éstas", "éste", "éstos", "última", "últimas", "último", "últimos" 
	};
	
	
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
					point.setUsers(pointDB.getUsers());
				}

				// Save the point in the database
				point = this.touristicPointRepository.save(point);
				
				// Get description of each place and store terms (& weight) associated in DB
				this.loadTermsInformationRetrieval(point);
			}
		}

		try {
			waitToEnd.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	private void loadTermsInformationRetrieval(TouristicPoint point) {
		// Create stemmer for the terms to be stored
		SnowballStemmer snowballStemmer = new SnowballStemmer(ALGORITHM.SPANISH);
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
