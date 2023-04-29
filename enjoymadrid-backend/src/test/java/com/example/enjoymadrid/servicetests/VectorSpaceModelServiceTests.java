package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.servicesimpl.VectorSpaceModelServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class VectorSpaceModelServiceTests {

	@InjectMocks
	private VectorSpaceModelServiceImpl modelService;
	
	@Test
	public void rank() {
		double score = 2.7;
		double weight = 0.5;
		int freq = 3;
		double expectedResult = score + (weight * (1 + Math.log10(freq)));
		
		double result = modelService.rank(score, weight, freq);
		assertThat(result).isEqualTo(expectedResult);
	}
	
	@Test
	public void calculateScore() {
		int termFreq = 2;
		int totalDocs = 7;
		int docFreq = 1;
		double tfSumDoc = 0.32;
		TermWeightSpec termWeightSpec = new TermWeightSpec(termFreq, totalDocs, docFreq, tfSumDoc);
		
		double expectedResult = ((1 + Math.log10(termFreq)) / tfSumDoc) * (Math.log10(totalDocs / docFreq));
		
		double result = modelService.calculateWeight(termWeightSpec);
		assertThat(result).isEqualTo(expectedResult);
	}
	
}
