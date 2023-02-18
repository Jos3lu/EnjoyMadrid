package com.example.enjoymadrid.servicetests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.tartarus.snowball.ext.SpanishStemmer;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.servicesimpl.DictionaryServiceImpl;
import com.example.enjoymadrid.servicesimpl.DirichletSmoothingModelServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class DictionaryServiceTests {
	
	@Mock
	private DictionaryRepository dictionaryRepository;
	
	@Mock
	private SpanishStemmer spanishStemmer;
	
	@Mock
	private DirichletSmoothingModelServiceImpl modelService;
	//private ModelService modelService;
	
	@Mock
	private TouristicPointRepository touristicPointRepository;
	
	@InjectMocks
	private DictionaryServiceImpl dictionaryService;
		
	@Test
	public void getTouristicPoints() {
		TouristicPoint touristicPoint = new TouristicPoint("El perro Paco", -3.6953184, 40.413246); 
		touristicPoint.setId(1L);
		touristicPoint.setAddress("de Huertas, 71");
		touristicPoint.setZipcode(28014);
		touristicPoint.setDescription("Descripción de El perro de Paco.");
		TouristicPoint touristicPoint2 = new TouristicPoint("ABC Arcade", -3.6874425, 40.4319053);
		touristicPoint.setId(2L);
		touristicPoint2.setAddress("de Serrano, 61");
		touristicPoint2.setZipcode(28006);
		touristicPoint2.setDescription("Descripción de ABC Arcade.");
		TouristicPoint touristicPoint3 = new TouristicPoint("Casa de Velázquez", -3.730481, 40.441382);
		touristicPoint.setId(3L);
		touristicPoint3.setAddress("de Paul Guinard, 3");
		touristicPoint3.setZipcode(28040);
		touristicPoint3.setDescription("Descripción de Casa de Velázquez.");
				
		Map<TouristicPoint, Double> weights = Map.ofEntries(
				entry(touristicPoint, 0.82),
				entry(touristicPoint2, 0.16),
				entry(touristicPoint3, 0.11)
		);
		Dictionary dictionary = new Dictionary("pac", weights);
		
		List<TouristicPoint> touristicPoints = new ArrayList<TouristicPoint>(Arrays.asList(touristicPoint, touristicPoint2, touristicPoint3));
		
		when(touristicPointRepository.findAll()).thenReturn(touristicPoints);
		when(dictionaryRepository.findByTerm(anyString())).thenReturn(Optional.of(dictionary));
		when(modelService.rank(anyDouble(), anyDouble(), anyInt())).thenReturn(3.12);
		
		List<TouristicPoint> pointsResult = dictionaryService.getTouristicPoints("Paco");
		assertThat(pointsResult).containsAll(touristicPoints);
		verify(dictionaryRepository).findByTerm("pac");
		verify(modelService, times(3)).rank(anyDouble(), anyDouble(), anyInt());
	}
		
	@Test
	public void analyze() {
		List<String> expectedTokens = Arrays.asList("perro", "paco");
		
		List<String> tokens = dictionaryService.analyze("El perro Paco");
		assertThat(tokens).isEqualTo(expectedTokens);
	}
	
	@Test
	public void stemAndGetFreq( ) {
		Map<String, Long> expectedTokensFreq = new HashMap<>();
		expectedTokensFreq.put("perr", 2L);
		expectedTokensFreq.put("pac", 1L);
		expectedTokensFreq.put("perrit", 1L);
		
		Map<String, Long> tokensFreq = dictionaryService
				.stemAndGetFreq(Arrays.asList("perro", "paco", "perros", "perrito"));
		assertThat(tokensFreq).isEqualTo(expectedTokensFreq);
	}
	
}
