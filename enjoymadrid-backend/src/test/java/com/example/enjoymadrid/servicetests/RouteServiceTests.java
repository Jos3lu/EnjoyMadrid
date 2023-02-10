package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockedStatic.Verification;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.AirQualityPoint;
import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RouteResultDto;
import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.RouteRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.UserService;
import com.example.enjoymadrid.servicesimpl.RouteServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class RouteServiceTests {
		
	@Mock
	private RouteRepository routeRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private TransportPointRepository transportPointRepository;
	
	@Mock
	private AirQualityPointRepository airQualityPointRepository;
	
	@Mock
	private TouristicPointRepository touristicPointRepository;
	
	@Mock
	private PublicTransportLineRepository publicTransportLineRepository;
			
	@Mock
	private UserService userService;
	
	@Mock
	private WebClient webClient;
	
	@Mock
	private WebClient.RequestBodyUriSpec requestBodyUriSpec;
	
	@Mock
	private WebClient.RequestBodySpec requestBodySpec;
	
	@Mock
	private Mono<ObjectNode> mono;
	
	@Mock
	private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
	
	@Mock
	private WebClient.ResponseSpec responseSpec;
	
	@InjectMocks
	private RouteServiceImpl routeService;
	
	private static ObjectMapper objectMapper;
	private static MockedStatic<WebClient> webClientStatic;
	
	@BeforeAll
	public static void setUp() {
		objectMapper = new ObjectMapper();
		webClientStatic = Mockito.mockStatic(WebClient.class);
	}
	
	@Test
	public void getUserRoutes() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(1L);
		
		Route route1 = new Route();
		route1.setId(2L);
		route1.setName("Route 1");
		Route route2 = new Route();
		route2.setId(3L);
		route2.setName("Route 2");
		Route route3 = new Route();
		route3.setId(4L);
		route3.setName("Route 3");
		List<Route> expectedRoutes = new ArrayList<>(Arrays
				.asList(route1, route2, route3));
		user.setRoutes(expectedRoutes);
		
		when(userService.getUser(anyLong())).thenReturn(user);
		
		List<Route> actualRoutes = routeService.getUserRoutes(user.getId());
		assertThat(actualRoutes).isEqualTo(expectedRoutes);
		verify(userService).getUser(user.getId());
	}
	
	@Test
	public void deleteRoute() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(1L);
		
		Route route1 = new Route();
		route1.setId(2L);
		route1.setName("Route 1");
		List<Route> expectedRoutes = new ArrayList<>(Arrays
				.asList(route1));
		user.setRoutes(expectedRoutes);
		
		when(routeRepository.findById(anyLong())).thenReturn(Optional.of(route1));
		when(userService.getUser(anyLong())).thenReturn(user);
		
		assertDoesNotThrow(() -> routeService.deleteRoute(route1.getId(), user.getId()));
		assertThat(user.getRoutes()).isEmpty();
		verify(routeRepository).findById(anyLong());
		verify(userService).getUser(user.getId());
	}
	
	@Test
	public void deleteRoute_exception() {
		// Route not found
		when(routeRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		assertThrows(ResponseStatusException.class, 
				() -> routeService.deleteRoute(1L, 2L));
		verify(routeRepository).findById(anyLong());
	}
	
	@Test
	public void createRoute() throws IOException {
		Route route = new Route();
		route.setName("Route");
		route.setOrigin(new TransportPoint("Origin", -3.706009, 40.420331, ""));
		route.setDestination(new TransportPoint("Destination", -3.703678, 40.416798, ""));
		route.setMaxDistance(1.2);
		route.setPreferences(new HashMap<>());
		
		AirQualityPoint airQualityPoint = new AirQualityPoint("Air quality", -3.852, 40.232);
		airQualityPoint.setId(1L);
		airQualityPoint.setAqi(7);
				
		when(publicTransportLineRepository.findAll()).thenReturn(new ArrayList<>());
		when(airQualityPointRepository.findByAqiIsNotNull())
			.thenReturn(Arrays.asList(airQualityPoint));
		when(touristicPointRepository.findAll()).thenReturn(new ArrayList<>());
		when(transportPointRepository.findByTypeIn(anyCollection())).thenReturn(new ArrayList<>());
		
		// Mock WebClient::create
		webClientStatic.when((Verification) WebClient.create(anyString())).thenReturn(webClient);
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
		when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
		when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
		when(requestBodySpec.body(any())).thenAnswer(x -> requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(ArgumentMatchers.<Class<ObjectNode>>notNull()))
			.thenReturn(mono);
		when(mono.block()).thenReturn(objectMapper.readValue(
				"{\"type\":\"FeatureCollection\",\"features\":[{\"bbox\":[-3.706178,40.416636,-3.703539,40.420343],\"type\":\"Feature\",\"properties\":{\"segments\":[{\"distance\":513.4,\"duration\":369.7,\"steps\":[{\"distance\":3.4,\"duration\":2.4,\"type\":11,\"instruction\":\"Camina hacia el sur en Gran Vía\",\"name\":\"Gran Vía\",\"way_points\":[0,2]},{\"distance\":12.5,\"duration\":9.0,\"type\":5,\"instruction\":\"Gire ligeramente a la derecha a Calle de Jacometrezo\",\"name\":\"Calle de Jacometrezo\",\"way_points\":[2,3]},{\"distance\":85.5,\"duration\":61.5,\"type\":0,\"instruction\":\"Gire a la izquierda\",\"name\":\"-\",\"way_points\":[3,9]},{\"distance\":21.5,\"duration\":15.5,\"type\":1,\"instruction\":\"Gire a la derecha\",\"name\":\"-\",\"way_points\":[9,10]},{\"distance\":322.2,\"duration\":232.0,\"type\":0,\"instruction\":\"Gire a la izquierda\",\"name\":\"-\",\"way_points\":[10,20]},{\"distance\":19.1,\"duration\":13.7,\"type\":0,\"instruction\":\"Gire a la izquierda\",\"name\":\"-\",\"way_points\":[20,22]},{\"distance\":35.6,\"duration\":25.6,\"type\":1,\"instruction\":\"Gire a la derecha\",\"name\":\"-\",\"way_points\":[22,29]},{\"distance\":13.7,\"duration\":9.9,\"type\":0,\"instruction\":\"Gire a la izquierda\",\"name\":\"-\",\"way_points\":[29,30]},{\"distance\":0.0,\"duration\":0.0,\"type\":10,\"instruction\":\"Llegar a su destino, es recto\",\"name\":\"-\",\"way_points\":[30,30]}]}],\"summary\":{\"distance\":513.4,\"duration\":369.7},\"way_points\":[0,30]},\"geometry\":{\"coordinates\":[[-3.70606,40.420343],[-3.706058,40.420335],[-3.706067,40.420314],[-3.706178,40.420239],[-3.706123,40.420206],[-3.706054,40.420157],[-3.705997,40.420114],[-3.705814,40.419982],[-3.705605,40.419803],[-3.705502,40.419674],[-3.705718,40.419572],[-3.705663,40.419509],[-3.705175,40.418776],[-3.705156,40.41875],[-3.705091,40.418658],[-3.704874,40.418349],[-3.704563,40.41788],[-3.704517,40.417812],[-3.704223,40.417356],[-3.70407,40.41712],[-3.703978,40.416995],[-3.70385,40.416981],[-3.703762,40.416951],[-3.703738,40.416898],[-3.70372,40.416806],[-3.703714,40.416725],[-3.703713,40.416717],[-3.703709,40.416688],[-3.703705,40.416666],[-3.7037,40.416636],[-3.703539,40.416649]],\"type\":\"LineString\"}}],\"bbox\":[-3.706178,40.416636,-3.703539,40.420343],\"metadata\":{\"attribution\":\"openrouteservice.org | OpenStreetMap contributors\",\"service\":\"routing\",\"timestamp\":1675883741391,\"query\":{\"coordinates\":[[-3.70606,40.420343],[-3.703539,40.41665]],\"profile\":\"foot-walking\",\"format\":\"geojson\",\"language\":\"es-es\"},\"engine\":{\"version\":\"6.8.0\",\"build_date\":\"2022-10-21T14:34:31Z\",\"graph_date\":\"2023-01-31T14:47:31Z\"}}}", 
				ObjectNode.class)
		);	
		
		RouteResultDto resultDto = routeService.createRoute(route, null);
		assertThat(resultDto.getName()).isEqualTo("Route");
		assertThat(resultDto.getDuration()).isPositive();
		assertThat(resultDto.getPoints()).isNotEmpty();
		assertThat(resultDto.getSegments()).isNotEmpty();
		verify(publicTransportLineRepository).findAll();
		verify(airQualityPointRepository).findByAqiIsNotNull();
		verify(touristicPointRepository).findAll();
		verify(transportPointRepository).findByTypeIn(anyCollection());
	}

}
