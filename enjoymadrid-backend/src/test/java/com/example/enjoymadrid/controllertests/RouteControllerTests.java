package com.example.enjoymadrid.controllertests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.enjoymadrid.models.Route;
import com.example.enjoymadrid.models.TransportPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.RouteResultDto;
import com.example.enjoymadrid.models.interfaces.RouteInterfaces;
import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.example.enjoymadrid.services.RouteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
// @TestPropertySource(locations = "file:src/main/resources/test.properties")
@AutoConfigureMockMvc
public class RouteControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private RouteService routeService;
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void getUserRoutes_valid() throws Exception {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		Long userId = 1L;
		user.setId(userId);
		
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
		
		when(routeService.getUserRoutes(anyLong())).thenReturn(expectedRoutes);
		
		MvcResult mvcResult = mockMvc
				.perform(get("/api/users/{userId}/routes", userId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		verify(routeService).getUserRoutes(anyLong());
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(UserInterfaces.RouteData.class)
				.writeValueAsString(expectedRoutes);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	public void createRoute_valid() throws Exception {
		Route route = new Route();
		route.setName("Route");
		route.setOrigin(new TransportPoint());
		route.setDestination(new TransportPoint());
		route.setMaxDistance(730.0);
		
		RouteResultDto expecteRouteResultDto = new RouteResultDto("Route", 27.3, new ArrayList<>(), new ArrayList<>());
		
		when(routeService.createRoute(any(Route.class), any())).thenReturn(expecteRouteResultDto);
		
		MvcResult mvcResult = mockMvc
				.perform(post("/api/routes").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(route)))
				.andExpect(status().isCreated()).andReturn();
		
		ArgumentCaptor<Route> routeCaptor = ArgumentCaptor.forClass(Route.class);
		verify(routeService).createRoute(routeCaptor.capture(), any());
		assertThat(routeCaptor.getValue().getName()).isEqualTo("Route");
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(RouteInterfaces.RouteResponseData.class)
				.writeValueAsString(expecteRouteResultDto);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void createRouteUser_valid() throws Exception {
		Long userId = 1L;
		Route route = new Route();
		route.setId(2L);
		route.setName("Route");
		route.setOrigin(new TransportPoint());
		route.setDestination(new TransportPoint());
		route.setMaxDistance(730.0);
		
		RouteResultDto expecteRouteResultDto = new RouteResultDto("Route", 27.3, new ArrayList<>(), new ArrayList<>());
		
		when(routeService.createRoute(any(Route.class), any())).thenReturn(expecteRouteResultDto);
		
		MvcResult mvcResult = mockMvc
				.perform(post("/api/users/{userId}/routes", userId).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(route)))
				.andExpect(status().isCreated()).andReturn();
		
		ArgumentCaptor<Route> routeCaptor = ArgumentCaptor.forClass(Route.class);
		verify(routeService).createRoute(routeCaptor.capture(), any());
		assertThat(routeCaptor.getValue().getName()).isEqualTo("Route");
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(RouteInterfaces.RouteResponseData.class)
				.writeValueAsString(expecteRouteResultDto);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void createRouteUser_invalid() throws Exception {
		// Route not valid
		Long userId = 1L;
		Route route = new Route();
		
		mockMvc.perform(post("/api/users/{userId}/routes", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(route))).andExpect(status().isBadRequest());
		
		verify(routeService, times(0)).createRoute(any(Route.class), anyLong());
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void deleteRoute() throws Exception {
		Long userId = 1L;
		Long routeId = 2L;
		
		mockMvc.perform(delete("/api/users/{userId}/routes/{routeId}", userId, routeId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
		verify(routeService).deleteRoute(anyLong(), anyLong());
	}
	
}
