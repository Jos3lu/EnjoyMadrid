package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import com.example.enjoymadrid.models.TermWeightSpec;
import com.example.enjoymadrid.servicesimpl.BM25ModelServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class BM25ModelServiceTests {

	@InjectMocks
	private BM25ModelServiceImpl modelService;
	
	@Test
	public void rank() {
		double score = 2.7;
		double weight = 0.5;
		int freq = 3;
		double expectedResult = score + (weight * freq);
		
		double result = modelService.rank(score, weight, freq);
		assertThat(result).isEqualTo(expectedResult);
	}
	
	@Test
	public void calculateWeight() {
		int termFreq = 2;
		int totalDocs = 7;
		int docFreq = 1;
		int docLength = 50;
		double avgDoc = 65.5;
		TermWeightSpec termWeightSpec = new TermWeightSpec(termFreq, totalDocs, docFreq, docLength, avgDoc);
		
		double k1 = 1.2;
		double b = 0.75;
		double expectedResult = termFreq * (k1 + 1) / (termFreq + k1 * (1 - b + b * docLength / avgDoc))
				* Math.log10((totalDocs - docFreq + 0.5) / (docFreq + 0.5) + 1);
		
		double result = modelService.calculateWeight(termWeightSpec);
		assertThat(result).isEqualTo(expectedResult);
	}
	
}
