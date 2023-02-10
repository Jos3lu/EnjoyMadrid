package com.example.enjoymadrid.controllertests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

import com.example.enjoymadrid.models.User;
import com.example.enjoymadrid.models.dtos.UserCreateDto;
import com.example.enjoymadrid.models.dtos.UserUpdateDto;
import com.example.enjoymadrid.models.interfaces.UserInterfaces;
import com.example.enjoymadrid.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
// @TestPropertySource(locations = "file:src/main/resources/test.properties")
@AutoConfigureMockMvc
public class UserControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
		
	@MockBean
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void createUser_valid() throws Exception {		
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName("John");
		userCreateDto.setUsername("JohnSmith");
		userCreateDto.setPassword("12345ABCdef");
		
		mockMvc.perform(post("/api/signup").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCreateDto))).andExpect(status().isCreated());
		
		verify(userService).createUser(any(User.class));
	}
	
	@Test
	public void createUser_invalid() throws Exception {
		// Bad password
		UserCreateDto userCreateDto = new UserCreateDto();
		userCreateDto.setName("John");
		userCreateDto.setUsername("JohnSmith");
		userCreateDto.setPassword("1234");
		
		mockMvc.perform(post("/api/signup").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCreateDto))).andExpect(status().isBadRequest());
		
		verify(userService, times(0)).createUser(any(User.class));
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void updateUser_valid() throws Exception {
		User expectedUser = new User("Sam", "SamSmith", passwordEncoder.encode("12345ABCdef"));
		Long userId = 1L;
		expectedUser.setId(userId);
		
		UserUpdateDto userUpdateDto = new UserUpdateDto();
		userUpdateDto.setName("Sam Smith");
		userUpdateDto.setUsername("SamSmith");
		userUpdateDto.setPassword("12345ABCdef");
		userUpdateDto.setOldPassword("12345ABCdefg");
		
		when(userService.updateUser(anyLong(), any(User.class), anyString())).thenReturn(expectedUser);
		
		MvcResult mvcResult = mockMvc.perform(put("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(userUpdateDto)))
				.andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();
		
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userService).updateUser(anyLong(), userCaptor.capture(), anyString());
		assertThat(userCaptor.getValue().getUsername()).isEqualTo("SamSmith");
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(UserInterfaces.UserData.class)
				.writeValueAsString(expectedUser);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}

	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void updateUserImage_valid() throws Exception {
		User expectedUser = new User("Sam", "SamSmith", passwordEncoder.encode("12345ABCdef"));
		Long userId = 1L;
		expectedUser.setId(userId);
		MockMultipartFile file = new MockMultipartFile("imageUser", "image.jpeg", 
				MediaType.IMAGE_JPEG_VALUE, "mockImage".getBytes());
		expectedUser.setPhoto(file.getBytes());
		
		when(userService.updateUserImage(anyLong(), any(MultipartFile.class))).thenReturn(expectedUser);
		
		MvcResult mvcResult = mockMvc.perform(multipart("/api/users/{userId}/picture", userId)
				.file(file)).andExpect(status().isOk()).andReturn();
		
		verify(userService).updateUserImage(anyLong(), any(MultipartFile.class));
		String actualRespBody = mvcResult.getResponse().getContentAsString();
		String expectedRespBody = objectMapper.writerWithView(UserInterfaces.PictureData.class)
				.writeValueAsString(expectedUser);
		assertThat(expectedRespBody).isEqualToIgnoringWhitespace(actualRespBody);
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void deleteUser_valid() throws Exception {
		Long userId = 1L;
		
		mockMvc.perform(delete("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
		verify(userService).deleteUser(anyLong());
	}
	
}
