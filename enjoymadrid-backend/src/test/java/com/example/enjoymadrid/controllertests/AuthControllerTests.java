package com.example.enjoymadrid.controllertests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.enjoymadrid.models.dtos.RefreshTokenRequestDto;
import com.example.enjoymadrid.models.dtos.RefreshTokenResponseDto;
import com.example.enjoymadrid.models.dtos.SignInRequestDto;
import com.example.enjoymadrid.models.dtos.SignInResponseDto;
import com.example.enjoymadrid.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
// @TestPropertySource(locations = "file:src/main/resources/test.properties")
@AutoConfigureMockMvc
public class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private AuthService authService;
	
	@Test
	public void signIn_valid() throws Exception {
		SignInRequestDto signInRequestDto = new SignInRequestDto();
		signInRequestDto.setUsername("SamSmith");
		signInRequestDto.setPassword("12345ABCdef");
				
		SignInResponseDto expectedSignInResponseDto = new SignInResponseDto("mockToken", 
				"mockRefreshToken", 1L, "Sam", "SamSmith", null, null, null);
		
		when(authService.signIn(any(SignInRequestDto.class)))
			.thenReturn(expectedSignInResponseDto);
		
		mockMvc.perform(post("/api/signin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequestDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.refreshToken", equalTo("mockRefreshToken")));
		
		ArgumentCaptor<SignInRequestDto> signInRequestDtoCaptor = 
				ArgumentCaptor.forClass(SignInRequestDto.class);
		verify(authService).signIn(signInRequestDtoCaptor.capture());
		assertThat(signInRequestDtoCaptor.getValue().getUsername())
			.isEqualTo(signInRequestDto.getUsername());
	}
	
	@Test
	public void signIn_invalid() throws Exception {
		// Bad password
		SignInRequestDto signInRequestDto = new SignInRequestDto();
		signInRequestDto.setUsername("SamSmith");
		signInRequestDto.setPassword("abcdef");
				
		mockMvc.perform(post("/api/signin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequestDto))).andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void signOut_valid() throws Exception {
		when(authService.signOut(any(HttpServletRequest.class), any(HttpServletResponse.class)))
			.thenReturn(HttpStatus.OK);
		
		mockMvc.perform(post("/api/signout")).andExpect(status().isOk());
		
		verify(authService).signOut(any(HttpServletRequest.class), any(HttpServletResponse.class));
	}
	
	@Test
	@WithMockUser(username = "SamSmith", password = "12345ABCdef")
	public void refreshToken_valid() throws Exception {
		RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
		refreshTokenRequestDto.setRefreshToken("mockRefreshToken");
		
		RefreshTokenResponseDto expecteRefreshTokenResponseDto = new RefreshTokenResponseDto("mockToken", "mockRefreshToken");
		
		when(authService.refreshToken(any(RefreshTokenRequestDto.class)))
			.thenReturn(expecteRefreshTokenResponseDto);
		
		mockMvc.perform(post("/api/refreshtoken").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken", equalTo("mockToken")));
	}
}
