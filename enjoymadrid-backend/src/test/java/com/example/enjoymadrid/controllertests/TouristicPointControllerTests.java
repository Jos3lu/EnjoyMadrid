package com.example.enjoymadrid.controllertests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.interfaces.TouristicPointInterfaces;
import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.example.enjoymadrid.services.TouristicPointService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
// @TestPropertySource(locations = "file:src/main/resources/test.properties")
@AutoConfigureMockMvc
public class TouristicPointControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private TouristicPointService touristicPointService;

	@Test
	public void getTouristicPointsByCategory_valid() throws Exception {
		String category = "Parques y jardines";
		
		int size = new Random().nextInt(11);
		List<TouristicPoint> expectedTouristicPoints = new ArrayList<>();
		for (int i = 1; i <= size; i++) {
			TouristicPoint touristicPoint = 
					new TouristicPoint("Tourist Point" + i, new Random().nextDouble(100.0), new Random().nextDouble(100.0));
			touristicPoint.setId((long) i);
			expectedTouristicPoints.add(touristicPoint);
		}
		
		when(touristicPointService.getTouristicPointsByCategory(anyString())).thenReturn(expectedTouristicPoints);
		
		MvcResult mvcResult = mockMvc
				.perform(get("/api/tourist-points/search-category").contentType(MediaType.APPLICATION_JSON)
						.content(category)).andExpect(status().isOk()).andReturn();
		
		verify(touristicPointService).getTouristicPointsByCategory(anyString());
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(TouristicPointInterfaces.BasicData.class)
				.writeValueAsString(expectedTouristicPoints);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	public void getTouristicPointsByQuery_valid() throws Exception {
		String query = "Patatas fritas";
		
		TouristicPoint touristicPoint1 = new TouristicPoint("Tourist Point 1", -3.704349, 40.416137);
		touristicPoint1.setId(1L);
		TouristicPoint touristicPoint2 = new TouristicPoint("Tourist Point 2", -3.693230, 40.422353);
		touristicPoint2.setId(2L);
		TouristicPoint touristicPoint3 = new TouristicPoint("Tourist Point 3", -3.692698, 40.414706);
		touristicPoint3.setId(3L);
		List<TouristicPoint> expectedTouristicPoints = new ArrayList<>(Arrays
				.asList(touristicPoint1, touristicPoint2, touristicPoint3));
		
		when(touristicPointService.getTouristicPointsByQuery(anyString())).thenReturn(expectedTouristicPoints);
		
		MvcResult mvcResult = mockMvc
				.perform(get("/api/tourist-points/search-query").contentType(MediaType.APPLICATION_JSON)
						.content(query)).andExpect(status().isOk()).andReturn();
		
		verify(touristicPointService).getTouristicPointsByQuery(anyString());
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(TouristicPointInterfaces.BasicData.class)
				.writeValueAsString(expectedTouristicPoints);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void getUserTouristicPoints_valid() throws Exception {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		Long userId = 1L;
		user.setId(userId);
		
		TouristicPoint touristicPoint1 = new TouristicPoint("Tourist Point 1", -3.704349, 40.416137);
		touristicPoint1.setId(2L);
		touristicPoint1.setUsers(new ArrayList<>(Arrays.asList(user)));
		TouristicPoint touristicPoint2 = new TouristicPoint("Tourist Point 2", -3.693230, 40.422353);
		touristicPoint2.setId(3L);
		touristicPoint2.setUsers(new ArrayList<>(Arrays.asList(user)));
		TouristicPoint touristicPoint3 = new TouristicPoint("Tourist Point 3", -3.692698, 40.414706);
		touristicPoint3.setId(4L);
		touristicPoint3.setUsers(new ArrayList<>(Arrays.asList(user)));
		List<TouristicPoint> expectedTouristicPoints = new ArrayList<>(Arrays
				.asList(touristicPoint1, touristicPoint2, touristicPoint3));
		user.setTouristicPoints(expectedTouristicPoints);
		
		when(touristicPointService.getUserTouristicPoints(anyLong())).thenReturn(expectedTouristicPoints);
		
		MvcResult mvcResult = mockMvc
				.perform(get("/api/users/{userId}/tourist-points", userId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		verify(touristicPointService).getUserTouristicPoints(anyLong());
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(UserInterfaces.TouristicPointData.class)
				.writeValueAsString(expectedTouristicPoints);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void addTouristicPointToUser_valid() throws Exception {
		Long userId = 1L;
		Long touristicPointId = 2L;
		
		mockMvc.perform(post("/api/users/{userId}/tourist-points/{touristPointId}", userId, touristicPointId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
		
		verify(touristicPointService).addTouristicPointToUser(anyLong(), anyLong());
	}
		
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void deleteUserTouristicPoint_valid() throws Exception {
		Long userId = 1L;
		Long touristicPointId = 2L;
		
		mockMvc.perform(delete("/api/users/{userId}/tourist-points/{touristPointId}", userId, touristicPointId)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
		
		verify(touristicPointService).deleteUserTouristicPoint(anyLong(), anyLong());
	}
	
}
