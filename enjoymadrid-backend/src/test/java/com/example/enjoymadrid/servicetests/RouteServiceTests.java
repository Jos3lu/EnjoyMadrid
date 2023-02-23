package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.AirQualityPoint;
import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RouteResultDto;
import com.example.enjoymadrid.models.repositories.AirQualityPointRepository;
import com.example.enjoymadrid.models.repositories.PublicTransportLineRepository;
import com.example.enjoymadrid.models.repositories.RouteRepository;
import com.example.enjoymadrid.models.repositories.TransportPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.SharedService;
import com.example.enjoymadrid.services.UserService;
import com.example.enjoymadrid.servicesimpl.RouteServiceImpl;
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
	private PublicTransportLineRepository publicTransportLineRepository;
			
	@Mock
	private UserService userService;
	
	@Mock
	private SharedService sharedService;
	
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
	
	@Mock
	private WebClientResponseException webClientResponseException;
	
	@InjectMocks
	private RouteServiceImpl routeService;
		
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
		route.setOrigin(new TransportPoint("Origin", -3.706009, 40.420331, "", null));
		route.setDestination(new TransportPoint("Destination", -3.703678, 40.416798, "", null));
		route.setMaxDistance(1.2);
		route.setPreferences(new HashMap<>());
		
		AirQualityPoint airQualityPoint = new AirQualityPoint("Air quality", -3.852, 40.232);
		airQualityPoint.setId(1L);
		airQualityPoint.setAqi(7);
				
		when(publicTransportLineRepository.findAll()).thenReturn(new ArrayList<>());
		when(airQualityPointRepository.findByAqiIsNotNull())
			.thenReturn(Arrays.asList(airQualityPoint));
		when(transportPointRepository.findByTypeIn(anyCollection())).thenReturn(new ArrayList<>());
		when(sharedService.haversine(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(0.7);
		
		RouteResultDto resultDto = routeService.createRoute(route, null);
		assertThat(resultDto.getName()).isEqualTo("Route");
		assertThat(resultDto.getDuration()).isPositive();
		assertThat(resultDto.getPoints()).isNotEmpty();
		assertThat(resultDto.getSegments()).isNotEmpty();
		verify(publicTransportLineRepository).findAll();
		verify(airQualityPointRepository).findByAqiIsNotNull();
		verify(transportPointRepository).findByTypeIn(anyCollection());
	}

}
