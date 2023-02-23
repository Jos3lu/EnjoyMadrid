package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.servicesimpl.SharedServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class SharedServiceTests {
	
	@InjectMocks
	private SharedServiceImpl sharedService;
	
	@Test
	public void tryParseDouble() {
		double expectedResult = 27.0;
		Double actualResult = this.sharedService.tryParseDouble("27");
		assertThat(actualResult).isEqualTo(expectedResult);
	}
	
	@Test
	public void tryParserDouble_null() {
		Double actualResult = this.sharedService.tryParseDouble("Hola mundo");
		assertThat(actualResult).isEqualTo(null);
	}
	
	@Test
	public void tryParseInteger() {
		int expectedResult = 10;
		Integer actualResult = this.sharedService.tryParseInteger("10");
		assertThat(actualResult).isEqualTo(expectedResult);
	}
	
	@Test
	public void tryParserInteger_null() {
		Integer actualResult = this.sharedService.tryParseInteger("Hola mundo");
		assertThat(actualResult).isEqualTo(null);
	}
	
	@Test
	public void haversine() {
		double expectedResult = 1.3211719482335482;
		double actualResult = this.sharedService.haversine(40.430223, -3.703253, 40.438584, -3.692162);
		assertThat(actualResult).isEqualTo(expectedResult);
	}

}
