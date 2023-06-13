package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.UserService;
import com.example.enjoymadrid.servicesimpl.TouristicPointServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class TouristicPointServiceTests {
	
	@Mock
	private TouristicPointRepository touristicPointRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserService userService;
	
	@Mock
	private DictionaryService dictionaryService;

	@InjectMocks
	private TouristicPointServiceImpl touristicPointService;
	
	private List<TouristicPoint> touristicPoints;
	
	@BeforeEach
	public void setUp() {
		TouristicPoint touristicPoint = new TouristicPoint("El perro Paco", -3.6953184, 40.413246); 
		touristicPoint.setId(1L);
		TouristicPoint touristicPoint2 = new TouristicPoint("ABC Arcade", -3.6874425, 40.4319053);
		touristicPoint.setId(2L);
		TouristicPoint touristicPoint3 = new TouristicPoint("Casa de Velázquez", -3.730481, 40.441382);
		touristicPoint.setId(3L);
		
		touristicPoints = new ArrayList<TouristicPoint>(Arrays.asList(touristicPoint, touristicPoint2, touristicPoint3));
	}
	
	@Test
	public void getTouristicPointsByCategory() {
		when(touristicPointRepository.findByCategory(anyString())).thenReturn(touristicPoints);
		
		List<TouristicPoint> pointsResult = touristicPointService.getTouristicPointsByCategory("Instalaciones culturales");
		assertThat(touristicPoints).isEqualTo(pointsResult);
		verify(touristicPointRepository).findByCategory("Instalaciones culturales");
	}
	
	@Test
	public void getTouristicPointsByCategory_notFound() {
		// No tourist points belong to the category
		when(touristicPointRepository.findByCategory(anyString())).thenReturn(new ArrayList<>());
		
		List<TouristicPoint> pointsResult = touristicPointService.getTouristicPointsByCategory("Instalaciones culturales");
		assertThat(pointsResult).isEmpty();
		verify(touristicPointRepository).findByCategory("Instalaciones culturales");
	}
	
	@Test
	public void getTouristicPointsByQuery() {
		when(dictionaryService.getTouristicPoints(anyString())).thenReturn(touristicPoints);
		
		List<TouristicPoint> pointsResult = touristicPointService.getTouristicPointsByQuery("Velázquez");
		assertThat(touristicPoints).isEqualTo(pointsResult);
		verify(dictionaryService).getTouristicPoints("Velázquez");
	}
	
	@Test
	public void getTouristicPointsByQuery_notFound() {
		// No tourist points found
		when(dictionaryService.getTouristicPoints(anyString())).thenReturn(new ArrayList<>());
		
		List<TouristicPoint> pointsResult = touristicPointService.getTouristicPointsByQuery("Velázquez");
		assertThat(pointsResult).isEmpty();
		verify(dictionaryService).getTouristicPoints("Velázquez");
	}
	
	@Test
	public void getUserTouristicPoints() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(4L);
		user.setTouristicPoints(touristicPoints);
		
		when(userService.getUser(anyLong())).thenReturn(user);
		
		List<TouristicPoint> pointsResult = touristicPointService.getUserTouristicPoints(user.getId());
		assertThat(touristicPoints).isEqualTo(pointsResult);
		verify(userService).getUser(user.getId());
	}
		
	@Test
	public void addTouristicPointToUser() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(4L);
		user.setTouristicPoints(touristicPoints);
		
		TouristicPoint touristicPoint = new TouristicPoint("Museo Thyssen", -3.4638293, 40.37463545);
		touristicPoint.setId(5L);
		
		when(userService.getUser(anyLong())).thenReturn(user);
		when(touristicPointRepository.findById(anyLong())).thenReturn(Optional.of(touristicPoint));
		
		assertDoesNotThrow(
				() -> touristicPointService.addTouristicPointToUser(user.getId(), touristicPoint.getId()));
		assertThat(user.getTouristicPoints()).contains(touristicPoint);
		verify(userService).getUser(user.getId());
		verify(touristicPointRepository).findById(touristicPoint.getId());	
	}
		
	@Test
	public void deleteUserTouristicPoint() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(4L);
		user.setTouristicPoints(touristicPoints);
		
		TouristicPoint touristicPoint = touristicPoints.get(0);
		
		when(userService.getUser(anyLong())).thenReturn(user);
		when(touristicPointRepository.findById(anyLong())).thenReturn(Optional.of(touristicPoint));
		
		assertDoesNotThrow(
				() -> touristicPointService.deleteUserTouristicPoint(4L, 1L));
		assertThat(user.getTouristicPoints()).doesNotContain(touristicPoint);
		verify(userService).getUser(user.getId());
		verify(touristicPointRepository, times(0)).findById(touristicPoint.getId());
	}
	
	@Test
	public void deleteUserTouristicPoint_exception() {
		// Tourist point not found
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(4L);
		
		when(userService.getUser(anyLong())).thenReturn(user);
		when(touristicPointRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		assertThrows(ResponseStatusException.class, 
				() -> touristicPointService.deleteUserTouristicPoint(user.getId(), 7L));
		verify(userService).getUser(user.getId());
		verify(touristicPointRepository).findById(anyLong());
	}
	
	@Test
	public void deleteTouristicPointFromUsers() {
		User user = new User("Sam", "SamSmith", "12345ABCdef");
		user.setId(4L);
		User user2 = new User("John", "JohnWilliam", "263434GDGdshdAAF");
		user.setId(5L);
		user.setTouristicPoints(touristicPoints);
		user2.setTouristicPoints(touristicPoints);
		
		Set<User> users = new HashSet<>();
		users.add(user);
		users.add(user2);
		
		TouristicPoint touristicPoint = touristicPoints.get(0);
		
		when(userRepository.findByTouristicPoints(any(TouristicPoint.class))).thenReturn(users);
		
		assertDoesNotThrow(
				() -> touristicPointService.deleteTouristicPointFromUsers(touristicPoint));
		assertThat(user.getTouristicPoints()).doesNotContain(touristicPoint);
		assertThat(user.getTouristicPoints()).doesNotContain(touristicPoint);
		verify(userRepository).findByTouristicPoints(touristicPoint);
	}
	
}
