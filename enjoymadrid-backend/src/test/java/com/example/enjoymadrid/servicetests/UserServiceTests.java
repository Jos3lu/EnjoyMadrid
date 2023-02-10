package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.repositories.UserRepository;
import com.example.enjoymadrid.servicesimpl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class UserServiceTests {
		
	@Mock
	private UserRepository userRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	private User user;
			
    @BeforeEach
    public void setUp(){
    	user = new User("Sam", "SamSmith", "12345ABCdef");
    	user.setId(1L);
    }
    
    @Test
    public void getUser() {
    	when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    	
    	User userResult = userService.getUser(1L);
    	assertThat(user).isEqualTo(userResult);
    }
    
    @Test
    public void getUser_exception() {
    	// User not found
    	Long userId = 2L;
    	when(userRepository.findById(userId)).thenReturn(Optional.empty());
    	
    	assertThrows(ResponseStatusException.class, () -> userService.getUser(2L));
    }
    
    @Test
    public void getUserByUsername() {
    	when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    	
    	User userResult = userService.getUserByUsername("SamSmith");
    	assertThat(user).isEqualTo(userResult);
    }
    
    @Test
    public void getUserByUsername_exception() {
    	// User not found
    	String username = "SamSmith";
    	when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    	
    	assertThrows(ResponseStatusException.class, () -> userService.getUserByUsername(username));
    	verify(userRepository).findByUsername(username);
    }
    
    @Test
    public void createUser() {
    	when(userRepository.save(any())).thenReturn(user);
    	when(userRepository.existsByUsername(anyString())).thenReturn(false);
    	when(passwordEncoder.encode(anyString())).thenReturn("EncryptedPassword");
    	
    	assertDoesNotThrow(() -> userService.createUser(user));
    }
    
    @Test
    public void createUser_exception() {
    	// User already exists
    	when(userRepository.existsByUsername(anyString())).thenReturn(true);
    	
    	assertThrows(ResponseStatusException.class, () -> userService.createUser(user));
    }
    
    @Test
    public void updateUser() {
    	User updateUser = new User("Sam Smith", "SamSmith", "12345ABCdefg");
    	updateUser.setId(1L);
    	
    	when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    	when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    	when(userRepository.save(any(User.class))).thenReturn(updateUser);
    	
    	User userResult = userService.updateUser(1L, updateUser, "12345ABCdef");
    	assertThat(updateUser).isEqualTo(userResult);
    }
    
    @Test
    public void updateUser_exception() {
    	// Password not valid
    	User updateUser = new User("Sam Smith", "SamSmith", "12345ABCdefg");
    	updateUser.setId(1L);
    	
    	when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    	
    	assertThrows(ResponseStatusException.class, 
    			() -> userService.updateUser(1L, updateUser, "12345"));
    }
    
    @Test
    public void updateUserImage() throws Exception {
    	User updateUser = new User("Sam Smith", "SamSmith", "12345ABCdefg");
    	updateUser.setId(1L);
		MockMultipartFile file = new MockMultipartFile("imageUser", "image.jpeg", 
				MediaType.IMAGE_JPEG_VALUE, "mockImage".getBytes());
    	updateUser.setPhoto(file.getBytes());
    	
    	when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    	when(userRepository.save(any(User.class))).thenReturn(updateUser);
    	
    	User userResult = userService.updateUserImage(1L, file);
    	assertThat(updateUser).isEqualTo(userResult);
    }
    
    @Test
    public void deleteUser() {
    	when(userRepository.findById(anyLong()))
    		.thenReturn(Optional.of(user)).thenReturn(Optional.empty());
    	
    	userService.deleteUser(anyLong());
    	
    	assertThrows(ResponseStatusException.class, () -> userService.getUser(1L));
    }
    
    @Test
    public void deleteUser_exception() {
    	// User not found
    	when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
    	
    	assertThrows(ResponseStatusException.class, () -> userService.deleteUser(1L));
    }

    @Test
    public void deleteTouristicPointOfUser() {
    	User userInput = new User("Sam", "SamSmith", "12345ABCdef");
    	userInput.setId(1L);
    	
    	TouristicPoint touristPoint = new TouristicPoint("Point 1", 40.321, -3.129);
    	touristPoint.setId(2L);
    	touristPoint.setUsers(new ArrayList<User>(Arrays.asList(userInput)));
    	userInput.setTouristicPoints(new ArrayList<TouristicPoint>(Arrays.asList(touristPoint)));
    	
    	when(userRepository.save(any())).thenReturn(user).thenReturn(Optional.empty());
    	
    	assertDoesNotThrow(
    			() -> userService.deleteTouristicPointOfUser(userInput, touristPoint));
    }
    
}
