package com.example.enjoymadrid.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.RefreshTokenRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.models.AirQualityPoint;
import com.example.enjoymadrid.models.BicycleTransportPoint;
import com.example.enjoymadrid.models.Frequency;
import com.example.enjoymadrid.models.Polyline;
import com.example.enjoymadrid.models.PublicTransportLine;
import com.example.enjoymadrid.models.PublicTransportPoint;
import com.example.enjoymadrid.models.Schedule;
import com.example.enjoymadrid.models.Time;
import com.example.enjoymadrid.models.TouristicPoint;

@Component
@EnableScheduling
public class LoadPointsComponent implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(LoadPointsComponent.class);

	private final TransportPointRepository transportPointRepository;
	private final PublicTransportLineRepository publicTransportLineRepository;
	private final AirQualityPointRepository airQualityPointRepository;
	private final TouristicPointRepository touristicPointRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public LoadPointsComponent(TransportPointRepository transportPointRepository, PublicTransportLineRepository publicTransportLineRepository,
			AirQualityPointRepository airQualityPointRepository, TouristicPointRepository touristicPointRepository, 
			RefreshTokenRepository refreshTokenRepository) {
		this.transportPointRepository = transportPointRepository;
		this.publicTransportLineRepository = publicTransportLineRepository;
		this.airQualityPointRepository = airQualityPointRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		
		new Thread(() -> loadDataAirQualityPoints()).start();
		loadDataTouristicPoints();
		loadDataTransportPoints();
				
	}
	
	/**
	 * This method is executed Mondays at 5am
	 * Purge expired refresh tokens. 
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 5 * * 0", zone = "Europe/Madrid")
	private void purgeExpiredTokens() {
		Instant now = Instant.now();
		this.refreshTokenRepository.deleteByExpiryDateLessThan(now);
	}

	/**
	 * Add air quality stations if not already in DB an then update the
	 * air quality data
	 */
	private void loadDataAirQualityPoints() {
		
		// Query to get number of air quality points from DB
		long airQualityPointsDB = this.airQualityPointRepository.count();
		
		if (airQualityPointsDB > 0) {
			// Update the air quality data
			updateAqiPoints();
			
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
				Double longitude = tryParseDouble(element.getElementsByTagName("geo:long").item(0).getTextContent());
				Double latitude = tryParseDouble(element.getElementsByTagName("geo:lat").item(0).getTextContent());

				// Save air quality point in DB
				this.airQualityPointRepository.save(new AirQualityPoint(name, longitude, latitude));
			}
		}

		// Update the air quality data
		updateAqiPoints();

	}

	/**
	 * This method is executed six times a day (12 a.m / 4 a.m / 8 a.m / 12 p.m. / 4 p.m. / 8 p.m),
	 * updating the air quality data at each station. 
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0/4 * * *", zone = "Europe/Madrid")
	private void scheduleAqiPoints() {
		/*
		 * Execute the method updateIcaStations at a random minute. Scheduled annotation
		 * (above) should end at minute 0. If pool tries to execute at minute 0, there
		 * might be a race condition with the actual thread running this block. In
		 * variable minuteExecuteUpdate we exclude the first minute for this reason.
		 */
		Integer minuteExecuteUpdate = 1 + new Random().nextInt(59);
		ScheduledThreadPoolExecutor ex = new ScheduledThreadPoolExecutor(1);

		ex.schedule(() -> updateAqiPoints(), minuteExecuteUpdate, TimeUnit.MINUTES);
	}

	/**
	 * Update Aqi levels of each air quality station
	 */
	private void updateAqiPoints() {

		/*
		 * Pages ->
		 * 1.
		 * https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_6~~TEMP_UNIT_c~~WIND_UNIT_kmh~
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
				.block();

		for (JsonNode point : response) {
			String name = point.get("name").asText();
			Integer aqi = point.get("aqi").asInt();
			airQualityPoints.put(name, aqi);
		}

		try {
			// Web page https://www.eltiempo.es
			org.jsoup.nodes.Document page = Jsoup
					.connect("https://www.eltiempo.es/calidad-aire/madrid~ROW_NUMBER_6~~TEMP_UNIT_c~~WIND_UNIT_kmh~")
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
				Integer aqi = tryParseInteger(points.get(1).child(0).text());
				
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

	/**
	 * This method is executed the first day of every month at 12:00 a.m. (and the first time the
	 * server is activated), checking for new information and deleting old information in tourist points.
	 * cron: Seconds, Minutes, Hour, Day of the month, Month, Day of the week
	 */
	@Scheduled(cron = "0 0 0 1 * *", zone = "Europe/Madrid")
	private void loadDataTouristicPoints() {
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

	/**
	 * Load the information of all the transport points to DB if not already
	 */
	private void loadDataTransportPoints() {		
		// Data sources
		String[][] publicTransportTypes = {
				{"Metro", "static/subway/stops_subway.geojson", "static/subway/lines_subway.json"}, 
				{"Bus", "static/bus/stops_bus.geojson", "static/bus/lines_bus.json"}, 
				{"Cercanías", "static/commuter/stops_commuter.geojson", "static/commuter/lines_commuter.json"},
		};
		
		// Thread for each type of transport
		ExecutorService ex = Executors.newFixedThreadPool(publicTransportTypes.length);
		
		// Sync threads pool
		CyclicBarrier waitToEnd = new CyclicBarrier(publicTransportTypes.length + 1);
		
		for (String[] publicTransport : publicTransportTypes) {
			ex.execute(() -> loadDataPublicTransportPoints(publicTransport[0], publicTransport[1], publicTransport[2], waitToEnd));
		}	
		ex.shutdown();
		
		// Main working at the same time as threads
		loadDataBiciMADPoints("BiciMAD", "static/bicycle/stops_bicycle.geojson", waitToEnd);
		
		logger.info("Transport points updated");
	}
	
	private void loadDataPublicTransportPoints(String type, String stopsPath, String linesPath, CyclicBarrier waitToEnd) {
		
		// Query to get number of entities in DB
		long publicTransportPointsDB = this.transportPointRepository.findByType(type).size();
		
		if (publicTransportPointsDB > 0) {
			try {
				waitToEnd.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				logger.error(e.getMessage());
			}
			
			return;
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {

			// Set with the stops
			Set<PublicTransportPoint> publicTransportPoints = new HashSet<>();
			// Map with each line associated to the stop
			Map<String, PublicTransportPoint> linePublicTransportPoints = new HashMap<>();
			// Map with each line associated to the stops and its arrival times
			Map<String, Map<String, Schedule>> timesPublicTransportPoints = new HashMap<>();
			// Map with each line associated to the stops and its polylines
			Map<String, Map<Integer, Polyline>> polylinesPublicTransportPoints = new HashMap<>();
			
			// For jar file, instead of getting getFile use getInputStream and transform into file
			InputStream stopsStream = new ClassPathResource(stopsPath).getInputStream();			
			File stopsFile = File.createTempFile("stops", "geojson");
			stopsFile.deleteOnExit();
			FileOutputStream outputStream = new FileOutputStream(stopsFile);
			IOUtils.copy(stopsStream, outputStream);
			
			// Transform json file to tree model
			JsonNode stops = objectMapper.readTree(stopsFile).get("data");
			for (JsonNode stop: stops) {
				String name = stop.get("name").asText();
				Double longitude = stop.get("geometry").get("coordinates").get(0).asDouble();
				Double latitude = stop.get("geometry").get("coordinates").get(1).asDouble();
				
				// Store lines of the stop
				Set<String[]> stopLines = new HashSet<>();
				// Get the lines of the stop in the file
				JsonNode lines = stop.get("lines");
				for (JsonNode line: lines) {
					String lineName = line.get("line").asText();
					String direction = line.get("direction").asText();
					Integer order = line.get("order").asInt();
					// Distance in meters
					Double distance = line.get("distance_previous_segment").asDouble();
					// Speed in kilometers/hour
					Double speed = line.get("speed_previous_segment").asDouble();
					
					stopLines.add(new String[] {lineName, direction, Integer.toString(order)});
					
					JsonNode stopTimes = line.get("stop_times");
					if (!stopTimes.isNull()) {
						// Map with arrival times of the stop (in a line)
						Map<String, LocalTime[]> daysArrivalTimes = new HashMap<>();
						
						for (JsonNode arrivalTimesWeek: stopTimes) {
							// Get days of the week
							List<String> daysList = getDaysWeek(arrivalTimesWeek);
							
							List<LocalTime> lineTimes = new ArrayList<>();
							JsonNode arrivalTimes = arrivalTimesWeek.get("arrival_times");
							for (JsonNode arrivalTime: arrivalTimes) {
								LocalTime time = LocalTime.parse(arrivalTime.asText());
								lineTimes.add(time);
							}
							
							// Store frequencies in a map
							for (String dayArrivalTimes : daysList) {
								daysArrivalTimes.put(dayArrivalTimes, lineTimes.toArray(new LocalTime[0]));
							}
							
						}
						
						// Put the arrival times
						Map<String, Schedule> stopTimesMap = timesPublicTransportPoints.get(lineName + " [" + direction + "]");
						if (stopTimesMap == null) {
							stopTimesMap = new HashMap<>();
						}
						stopTimesMap.put(Integer.toString(order), new Time(daysArrivalTimes));
						timesPublicTransportPoints.put(lineName + " [" + direction + "]", stopTimesMap);
						
					}
					
					JsonNode polyline = line.get("geometry");
					if (!polyline.isNull()) {
						JsonNode polylineCoords = polyline.get("coordinates");
						List<Double[]> coordinates = new ArrayList<>();
						for (JsonNode point: polylineCoords) {
							Double longitudePoint = point.get(0).asDouble();
							Double latitudePoint = point.get(1).asDouble();
							coordinates.add(new Double[] {latitudePoint, longitudePoint});
						}
						
						Map<Integer, Polyline> stopPolylines = polylinesPublicTransportPoints.get(lineName + " [" + direction + "]");
						if (stopPolylines == null) {
							stopPolylines = new HashMap<>();
						}
						
						// Duration in seconds
						Double duration = 0.0;
						if (speed != 0.0) {
							duration = distance / (speed * (1000.0 / 3600.0));
						}
						stopPolylines.put(order, new Polyline(duration, coordinates));
						polylinesPublicTransportPoints.put(lineName + " [" + direction + "]", stopPolylines);
					}
				}
				// Save stop in DB
				PublicTransportPoint publicTransportPoint = 
						this.transportPointRepository.save(new PublicTransportPoint(name, longitude, latitude, type, stopLines));
				
				// Store transport points
				publicTransportPoints.add(publicTransportPoint);
								
				// Set in the map each line associated to its point
				for (String[] line: stopLines) {
					linePublicTransportPoints.put(line[0] + "_" + line[1] + "_" + line[2], publicTransportPoint);
				}
								
			}
			
			// Set next stops of our transport point
			for (PublicTransportPoint transportPoint : publicTransportPoints) {
				Map<String, PublicTransportPoint> nextStops = new HashMap<>();
				// Get the neighboring points of the current
				transportPoint.getStopLines().forEach(line -> {
					String lineNextStop = line[0] + "_" + line[1] + "_" + (Integer.parseInt(line[2]) + 1);
					PublicTransportPoint nextStop = linePublicTransportPoints.get(lineNextStop);
					if (nextStop != null)
						nextStops.put(line[0] + " [" + line[1] + "]", nextStop);
				});
				transportPoint.setNextStops(nextStops);
				this.transportPointRepository.save(transportPoint);
			}
			
			// For jar file, instead of getting getFile use getInputStream and transform into file
			InputStream linesStream = new ClassPathResource(linesPath).getInputStream();
			File linesFile = File.createTempFile("lines", "json");
			stopsFile.deleteOnExit();
			outputStream = new FileOutputStream(linesFile);
			IOUtils.copy(linesStream, outputStream);
			
			// Transform json file to tree model
			JsonNode lines = objectMapper.readTree(linesFile).get("data");
			for (JsonNode line: lines) {
				String lineName = line.get("line").asText();
				String direction = line.get("direction").asText();
				String lineHeadsign = line.get("line_headsign").asText();
				String lineColor = line.get("line_color").asText();
								
				// Map with day associated to the start/end of line service
				Map<String, LocalTime> startSchedules = new HashMap<>();
				Map<String, LocalTime> endSchedules = new HashMap<>();
				
				// Map with day of week associated to the different frequencies throughout the day
				Map<String, Schedule> lineFrequencies = new HashMap<>();
				
				JsonNode weekSchedule = line.get("week_schedule");
				if (!weekSchedule.isNull()) {
					int i = 1;
					for (JsonNode schedules: weekSchedule) {
						if (schedules.asText().isBlank()) {
							i++;
							continue;
						}
						// Get day & schedule service
						String[] scheduleDay = schedules.asText().split(" - ");
						LocalTime startSchedule = LocalTime.parse(scheduleDay[0]);
						LocalTime endSchedule = LocalTime.parse(scheduleDay[1]);
						DayOfWeek day = DayOfWeek.of(i);
						// Store in maps
						startSchedules.put(day.toString(), startSchedule);
						endSchedules.put(day.toString(), endSchedule);
						// Update index of week day
						i++;
					}
				}
				
				JsonNode weekFrequencies = line.get("week_frequencies");
				if (!weekFrequencies.isNull()) {
					for (JsonNode frequencyWeek: weekFrequencies) {
						// Get days of the week
						List<String> daysList = getDaysWeek(frequencyWeek);
						
						// Map with time slot associated to the frequency of arrival
						Map<String, Integer> frequenciesMap = new HashMap<>();
						
						// Iterate over the frequencies of the line
						JsonNode frequencies = frequencyWeek.get("frequencies");
						for (JsonNode frequency: frequencies) {
							String startTime = frequency.get("start_time").asText();
							String endTime = frequency.get("end_time").asText();
							Integer frequencyTime = frequency.get("frequency").asInt();
							frequenciesMap.put(startTime + "-" + endTime, frequencyTime);
						}
						
						// Store frequencies in a map
						for (String dayFrequencies : daysList) {
							lineFrequencies.put(dayFrequencies, 
									new Frequency(frequenciesMap, startSchedules.get(dayFrequencies), endSchedules.get(dayFrequencies)));
						}
						
					}	
				}
								
				Map<Integer, Polyline> stopPolylines = polylinesPublicTransportPoints.get(lineName + " [" + direction + "]");
				Map<String, Schedule> stopSchedules = timesPublicTransportPoints.isEmpty() ? 
						lineFrequencies : timesPublicTransportPoints.get(lineName + " [" + direction + "]");
				Character scheduleType = timesPublicTransportPoints.isEmpty() ? 'F' : 'T';
				
				this.publicTransportLineRepository.save(
						new PublicTransportLine(type, lineName, direction, lineHeadsign, lineColor, scheduleType, stopPolylines, stopSchedules));
				
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
	
	private List<String> getDaysWeek(JsonNode scheduleDay) {
		// Get the days for the arrival times / frequencies in a time slot
		String timeDay = scheduleDay.get("week_day").asText();
		String[] days = timeDay.split("-");
		
		// Store all the days between the first & last day
		List<String> daysList = new ArrayList<>();
		if (days.length > 1) {
			int firstDay = DayOfWeek.valueOf(days[0]).getValue();
			int lastDay = DayOfWeek.valueOf(days[1]).getValue();
			DayOfWeek day;
			if (firstDay < lastDay) {
				for (int i = firstDay; i <= lastDay; i++) {
					day = DayOfWeek.of(i);
					daysList.add(day.toString());
				}
			} else {
				for (int i = 0; i <= 7; i++) {
					if (i <= lastDay && i >= firstDay) {
						day = DayOfWeek.of(i);
						daysList.add(day.toString());
					}
				}
			}
		} else {
			daysList.add(days[0]);
		}
		
		return daysList;
	}

	private void loadDataBiciMADPoints(String type, String stopsPath, CyclicBarrier waitToEnd) {
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Transform json file to tree model
		try {
			
			// Query to get bicycle stations from DB with the number of the station as the key
			Set<String> bicycleTransportPointsDB = this.transportPointRepository.findByType(type).stream()
					.map(transportPoint -> (BicycleTransportPoint) transportPoint)
					.map(BicycleTransportPoint::getStationNumber)
					.collect(Collectors.toSet());

			// For jar file, instead of getting getFile use getInputStream and transform into file
			InputStream stopsStream = new ClassPathResource(stopsPath).getInputStream();
			File stopsFile = File.createTempFile("stops", "gejson");
			stopsFile.deleteOnExit();
			FileOutputStream outputStream = new FileOutputStream(stopsFile);
			IOUtils.copy(stopsStream, outputStream);
			
			// Transform json file to tree model
			JsonNode stops = objectMapper.readTree(stopsFile).get("data");
			for (JsonNode stop: stops) {
				String name = stop.get("name").asText();
				String stationNumber = stop.get("number").asText();
				Double longitude = stop.get("geometry").get("coordinates").get(0).asDouble();
				Double latitude = stop.get("geometry").get("coordinates").get(1).asDouble();
				
				if (!bicycleTransportPointsDB.contains(stationNumber)) {
					// Save it in the DB
					this.transportPointRepository.save(
							new BicycleTransportPoint(stationNumber, name, longitude, latitude, type, 
									0, 0, 0, false, true, 0));	
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
		
		// Query to get bicycle stations from DB with the number of the station as the key
		Map<String, BicycleTransportPoint> bicycleTransportPointsDB = this.transportPointRepository.findByType("BiciMAD").stream()
				.map(transportPoint -> (BicycleTransportPoint) transportPoint)
				.collect(Collectors.toMap(BicycleTransportPoint::getStationNumber, point -> point));
		
		// Web page EMT api
		WebClient client = WebClient.create(
				"https://openapi.emtmadrid.es/v1/transport/bicimad/stations/");

		ObjectNode response = client.get()
				//.uri("/v1/transport/bicimad/stations/")
				.retrieve()
				.bodyToMono(ObjectNode.class)
				.block();
		
		JsonNode stations = response.get("data");
		
		for (JsonNode station : stations) {
			String stationNumber = station.get("number").asText();
			
			BicycleTransportPoint bicycleTransportPointDB = bicycleTransportPointsDB.get(stationNumber);
			
			if (bicycleTransportPointDB == null) {
				continue;
			}
			
			Integer activate = station.get("activate").asInt();
			Integer no_available = station.get("no_available").asInt();
			Integer total_bases = station.get("total_bases").asInt();
			Integer dock_bikes = station.get("dock_bikes").asInt();
			Integer free_bases = station.get("free_bases").asInt();
			Integer reservations_count = station.get("reservations_count").asInt();
			
			bicycleTransportPointDB.setActivate(activate == 1 ? true : false);
			bicycleTransportPointDB.setNo_available(no_available == 1 ? true : false);
			bicycleTransportPointDB.setTotalBases(total_bases);
			bicycleTransportPointDB.setDockBases(dock_bikes);
			bicycleTransportPointDB.setFreeBases(free_bases);
			bicycleTransportPointDB.setReservations(reservations_count);
			
			this.transportPointRepository.save(bicycleTransportPointDB);
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