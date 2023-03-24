package com.example.enjoymadrid.servicesimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.enjoymadrid.models.BicycleTransportPoint;
import com.example.enjoymadrid.models.Frequency;
import com.example.enjoymadrid.models.MaxNearbyTouristicPointsType;
import com.example.enjoymadrid.models.Polyline;
import com.example.enjoymadrid.models.PublicTransportLine;
import com.example.enjoymadrid.models.PublicTransportPoint;
import com.example.enjoymadrid.models.Schedule;
import com.example.enjoymadrid.models.Time;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.MaxNearbyTouristicPointsTypeRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.services.SharedService;
import com.example.enjoymadrid.services.TransportLoadService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Mono;

@Service
public class TransportLoadServiceImpl implements TransportLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransportLoadService.class);
	
	// AccessToken for EMT API (get information about BiciMad stations), auto-extend each the API is invoked
	private static String accessToken;
	// Number of nearby tourist points to a station that match a preference
	private static ConcurrentHashMap<String, BiFunction<List<TouristicPoint>, String, Long>> preferenceFunctions;
	// Preferences of tourist points
	private static final List<String> PREFERENCE_TYPES = Arrays.asList("C_Instalaciones culturales", "C_Parques y jardines", 
			"C_Escuelas de cocina y catas de vinos y aceites", "C_Empresas de guías turísticos", 
			"C_Edificios y monumentos", "C_Parques y centros de ocio", "D_Deportes", "T_Tiendas", "R_Restauración");
	
	private final TouristicPointRepository touristicPointRepository;
	private final TransportPointRepository transportPointRepository;
	private final PublicTransportLineRepository publicTransportLineRepository;
	private final MaxNearbyTouristicPointsTypeRepository maxNearbyTouristicPointsTypeRepository;
	private final SharedService sharedService;
	
	public TransportLoadServiceImpl(TouristicPointRepository touristicPointRepository, TransportPointRepository transportPointRepository, 
			PublicTransportLineRepository publicTransportLineRepository, SharedService sharedService, 
			MaxNearbyTouristicPointsTypeRepository maxNearbyTouristicPointsTypeRepository) {
		this.touristicPointRepository = touristicPointRepository;
		this.transportPointRepository = transportPointRepository;
		this.publicTransportLineRepository = publicTransportLineRepository;
		this.maxNearbyTouristicPointsTypeRepository = maxNearbyTouristicPointsTypeRepository;
		this.sharedService = sharedService;
	}

	@Override
	public void loadTransportPoints() {
		// Get accessToken for EMT Api
		loginEMTApi();
		
		// Get all the touristic points
		List<TouristicPoint> touristicPoints = this.touristicPointRepository.findAll();
		
		// There are transport points already loaded in DB
		boolean existsTransportPoints = this.transportPointRepository.count() > 0;
		
		// HashMap for calculating the number of places near the station that match the preference
		preferenceFunctions = new ConcurrentHashMap<>();
		preferenceFunctions.put("C_", (places, placeType) -> places.stream()
		    .filter(place -> place.getCategories().contains(placeType)).count());
		preferenceFunctions.put("R_", (places, placeType) -> places.stream()
		    .filter(place -> place.getType().equals("Restaurantes") || place.getType().equals("Clubs")).count());
		preferenceFunctions.put("D_", (places, placeType) -> places.stream()
		    .filter(place -> place.getType().equals(placeType) || place.getCategories().contains("Instalaciones deportivas")).count());
		preferenceFunctions.put("T_", (places, placeType) -> places.stream()
		    .filter(place -> place.getType().equals(placeType)).count());
		
		// Max nearby tourist points of each preference type
		ConcurrentHashMap<String, Long> maxNearbyTouristicPoints = new ConcurrentHashMap<>();
		
		// Data sources
		String[][] transportTypes = {
				{"Metro", "static/subway/stops_subway.geojson", "static/subway/lines_subway.json"}, 
				{"Bus", "static/bus/stops_bus.geojson", "static/bus/lines_bus.json"}, 
				{"Cercanías", "static/commuter/stops_commuter.geojson", "static/commuter/lines_commuter.json"},
				{"BiciMAD", "static/bicycle/stops_bicycle.geojson", ""}
		};
		
		// Index last transportTypes array
		int transportLast = transportTypes.length - 1;
		
		// Thread for each type of transport
		ExecutorService ex = Executors.newFixedThreadPool(transportLast);
		
		// Sync threads pool
		CyclicBarrier waitToEnd = new CyclicBarrier(transportTypes.length);
		
		for (int i = 0; i < transportLast; i++) {
			final int transportIndex = i;
			ex.execute(() -> loadTransportPoints(transportTypes[transportIndex][0], transportTypes[transportIndex][1],
					transportTypes[transportIndex][2], touristicPoints, maxNearbyTouristicPoints, waitToEnd));
		}	
		ex.shutdown();
		
		// Main working at the same time as threads
		loadTransportPoints(transportTypes[transportLast][0], transportTypes[transportLast][1],
				transportTypes[transportLast][2], touristicPoints, maxNearbyTouristicPoints, waitToEnd);
		
		if (existsTransportPoints) {
			// Update in DB nearby tourist points & max nearby tourist points
			updateNearbyTouristicPoints(touristicPoints, maxNearbyTouristicPoints);
		} else {
			// Update in DB max nearby tourist points
			updateMaxNearbyTouristicPoints(maxNearbyTouristicPoints);
		}
		
		logger.info("Transport points updated");
	}
		
	/**
	 * Load for each data source the transport points
	 * 
	 * @param type Transport mode
	 * @param stopsPath Path of point's data source
	 * @param linesPath Path of lines' data source
	 * @param touristicPoints Tourist points
	 * @param maxNearbyTouristicPoints Max nearby tourist points of each type
	 * @param waitToEnd Synchronization aid
	 */
	private void loadTransportPoints(String type, String stopsPath, String linesPath, List<TouristicPoint> touristicPoints, 
			Map<String, Long> maxNearbyTouristicPoints, CyclicBarrier waitToEnd) {
		
		// Query to get number of entities in DB
		boolean transportPointsDB = this.transportPointRepository.existsByType(type);
		
		if (transportPointsDB) {			
			// Add availability of each Bike station
			if (type.equals("BiciMAD")) 
				updateBiciMADPoints();
			
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
			Set<PublicTransportPoint> publicTransportPoints = null;
			// Map with each line associated to the stop
			Map<String, PublicTransportPoint> linePublicTransportPoints = null;
			// Map with each line associated to the stops and its arrival times
			Map<String, Map<String, Schedule>> timesPublicTransportPoints = null;
			// Map with each line associated to the stops and its polylines
			Map<String, Map<Integer, Polyline>> polylinesPublicTransportPoints = null;
			
			// Initialize variables just in case that lines file is passed as parameter
			if (!linesPath.isBlank()) {
				publicTransportPoints = new HashSet<>();
				linePublicTransportPoints = new HashMap<>();
				timesPublicTransportPoints = new HashMap<>();
				polylinesPublicTransportPoints = new HashMap<>();
			}
			
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
				Set<String[]> stopLines = null;
				if (!linesPath.isBlank())
					stopLines = new HashSet<>();
				
				// Get the lines of the stop in the file
				JsonNode lines = stop.get("lines");
				if (!lines.isNull()) {
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
									daysArrivalTimes.put(dayArrivalTimes, lineTimes.toArray(new LocalTime[lineTimes.size()]));
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
				}
				
				// Get tourist points near the station (by type)
				Map<String, Long> nearbyTouristicPoint = getNearbyTouristicPoints(touristicPoints, latitude, longitude,
						maxNearbyTouristicPoints);
				
				// Save stop in DB
				if (type.equals("BiciMAD")) {
					String stationNumber = stop.get("number").asText();
					this.transportPointRepository.save((new BicycleTransportPoint(stationNumber, name, longitude,
							latitude, type, nearbyTouristicPoint, 0, 0, 0, false, true, 0)));
				} else {
					PublicTransportPoint publicTransportPoint = this.transportPointRepository
							.save(new PublicTransportPoint(name, longitude, latitude, type, nearbyTouristicPoint,
									stopLines));
					
					// Store transport points
					publicTransportPoints.add(publicTransportPoint);
									
					// Set in the map each line associated to its point
					for (String[] line: stopLines) {
						linePublicTransportPoints.put(line[0] + "_" + line[1] + "_" + line[2], publicTransportPoint);
					}
				}			
			}
			
			// In case of BiciMAD end the function
			if (type.equals("BiciMAD")) {
				// Add availability of each Bike station
				updateBiciMADPoints();
				
				try {
					waitToEnd.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					logger.error(e.getMessage());
				}
				
				return;
			}
			
			// Set next stops of our transport point
			for (PublicTransportPoint transportPoint : publicTransportPoints) {
				Map<String, PublicTransportPoint> nextStops = new HashMap<>();
				// Get the neighboring points of the current
				for (String[] line : transportPoint.getStopLines()) {
					String lineNextStop = line[0] + "_" + line[1] + "_" + (Integer.parseInt(line[2]) + 1);
					PublicTransportPoint nextStop = linePublicTransportPoints.get(lineNextStop);
					if (nextStop != null)
						nextStops.put(line[0] + " [" + line[1] + "]", nextStop);
				}
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
	
	@Override
	public void updateNearbyTouristicPoints(List<TouristicPoint> touristicPoints, Map<String, Long> maxNearbyTouristicPoints) {		
		this.transportPointRepository.findAll().parallelStream().forEach(transport -> {
			transport.setNearbyTouristicPoints(
					getNearbyTouristicPoints(touristicPoints, transport.getLatitude(), transport.getLongitude(), 
							maxNearbyTouristicPoints));
			this.transportPointRepository.save(transport);
		});
		
		updateMaxNearbyTouristicPoints(maxNearbyTouristicPoints);
	}
	
	/**
	 * Get nearby tourist points of a transport station
	 * 
	 * @param touristicPoints Tourist points
	 * @param latitude Latitude of transport station
	 * @param longitude Longitude of transport station
	 * @param maxNearbyTouristicPoints Max nearby tourist points
	 * @return Nearby tourist points
	 */
	private Map<String, Long> getNearbyTouristicPoints(List<TouristicPoint> touristicPoints, double latitude, double longitude, 
			Map<String, Long> maxNearbyTouristicPoints) {
		// Get tourist points within a radius of 500 meters
		List<TouristicPoint> nearbyTouristicPoints = touristicPoints.stream()
				.filter(touristicPoint -> this.sharedService.haversine(touristicPoint.getLatitude(),
						touristicPoint.getLongitude(), latitude, longitude) <= 0.5)
				.collect(Collectors.toList());
		
		// Associate the type of preference with the number of tourist points encountered
		return PREFERENCE_TYPES.stream()
				.collect(Collectors.toMap(Function.identity(), preferenceType -> {
					// Get preference type
					String preferenceName = preferenceType.substring(preferenceType.indexOf('_') + 1);
					
					// A reference to a separate instance of the lambda function is returned
					BiFunction<List<TouristicPoint>, String, Long> preferenceFunction = preferenceFunctions
							.get(preferenceType.substring(0, 2));

					long nTouristicPoints = preferenceFunction != null
							? preferenceFunction.apply(nearbyTouristicPoints, preferenceName)
							: 0L;
					
					// Max nearby tourist points
					maxNearbyTouristicPoints.compute(preferenceType, 
							(k, v) -> v == null || nTouristicPoints > v ? nTouristicPoints : v);
					
					return nTouristicPoints;
				}));
	}
	
	/**
	 * Update in DB max nearby tourist points
	 * 
	 * @param maxNearbyTouristicPoints Max nearby tourist points of each type
	 */
	private void updateMaxNearbyTouristicPoints(Map<String, Long> maxNearbyTouristicPoints) {
		this.maxNearbyTouristicPointsTypeRepository.deleteAll();
		this.maxNearbyTouristicPointsTypeRepository.save(
				new MaxNearbyTouristicPointsType(maxNearbyTouristicPoints));
	}
	
	@Override
	public void updateBiciMADPoints() {
		// Query to get bicycle stations from DB with the number of the station as the key
		Map<String, BicycleTransportPoint> bicycleTransportPointsDB = this.transportPointRepository.findByType("BiciMAD").stream()
				.map(transportPoint -> (BicycleTransportPoint) transportPoint)
				.collect(Collectors.toMap(BicycleTransportPoint::getStationNumber, point -> point));
		
		// Web page EMT api
		WebClient client = WebClient.create("https://openapi.emtmadrid.es");
				
		// Web page EMT api for stations
		ObjectNode response = client.get()
				.uri("/v1/transport/bicimad/stations/")
				.header("accessToken", accessToken)
				.retrieve()
				.bodyToMono(ObjectNode.class)
				.onErrorResume(WebClientResponseException.class, error -> Mono.empty())
				.block();
		
		if (response == null) {
			logger.error("Could not update the information of the bicycle stations");
			return;
		}
		
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
	
	/**
	 * Login in the EMT API & get access token 
	 */
	private void loginEMTApi() {
		// Web page EMT api
		WebClient client = WebClient.create("https://openapi.emtmadrid.es");
		
		// Web page EMT login to get accessToken
		ObjectNode response = client.get()
				.uri("/v1/mobilitylabs/user/login/")
				.headers(httpHeaders -> {
					httpHeaders.set("email", "enjoymadridapp1@gmail.com");
					httpHeaders.set("password", "EnjoyMadrid123");
					//httpHeaders.set("X-ClientId", "c49496a8-53bf-4b86-bb2a-66b3ecbdc494");
					//httpHeaders.set("passKey",
					//		"FD89F93FAE0DB64EE23E7B0D2B8B719EF1EF06FC5F88E5AB9CF2000EF8133799CD44F59380BC194475E526BA2B6EA5E5676E340B87C4BBF3AF4DC1D681E");
				})
				.retrieve()
				.bodyToMono(ObjectNode.class)
				.onErrorResume(WebClientResponseException.class, error -> Mono.empty())
				.block();
		
		if (response == null) {
			logger.error("Error when trying to create a session with the EMT Api");
			return;	
		}
		// Get accessToken for the api requests
		accessToken = response.get("data").get(0).get("accessToken").asText();
	}
	
	/**
	 * Get the days for the arrival times / frequencies in a time slot
	 * 
	 * @param scheduleDay Days on which transport methods operate
	 * @return Days for the arrival times / frequencies in a time slot
	 */
	private List<String> getDaysWeek(JsonNode scheduleDay) {
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

}
