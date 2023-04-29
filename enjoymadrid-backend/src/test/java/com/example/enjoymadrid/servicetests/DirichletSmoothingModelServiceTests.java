package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.servicesimpl.DirichletSmoothingModelServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class DirichletSmoothingModelServiceTests {

	@InjectMocks
	private DirichletSmoothingModelServiceImpl modelService;
	
	@Test
	public void rank() {
		double score = 2.7;
		double weight = 0.5;
		int freq = 3;
		double expectedResult = score * Math.pow(weight, freq);;
		
		double result = modelService.rank(score, weight, freq);
		assertThat(result).isEqualTo(expectedResult);
	}
	
	@Test
	public void calculateScore() {
		int termFreq = 2;
		int docLength = 50;
		double probTermCol = 0.07;
		TermWeightSpec termWeightSpec = new TermWeightSpec(termFreq, docLength, probTermCol);
		
		double mu = 2000;
		double expectedResult = (termFreq + mu * probTermCol) / (docLength + mu);
		
		double result = modelService.calculateWeight(termWeightSpec);
		assertThat(result).isEqualTo(expectedResult);
	}
}
