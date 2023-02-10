package com.example.enjoymadrid.servicesimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.models.repositories.TouristicPointRepository;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryServiceImpl implements DictionaryService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
	
	// Stop words
	private static final String[] STOP_WORDS = new String[] {
			"a", "actualmente", "acá", "adelante", "ademas", "además", "afirmó", "agregó", "ahi", "ahora", "ahí", "al", "algo", "alguna", "algunas", "alguno", "algunos", "algún", "alli", "allí", "alrededor", "ambos", "ante", "anterior", "antes", 
			"apenas", "aproximadamente", "aquel", "aquella", "aquellas", "aquello", "aquellos", "aqui", "aquél", "aquélla", "aquéllas", "aquéllos", "aquí", "arriba", "aseguró", "asi", "así", "atrás", "aun", "aunque", "ayer", "añadió", "aún", 
			"b", "bastante", "bien", "c", "cada", "casi", "cierta", "ciertas", "cierto", "ciertos", "cinco", "comentó", "como", "con", "conmigo", "conocer", "conseguimos", "conseguir", "considera", "consideró", "consigo", "consigue", "consiguen", 
			"consigues", "contigo", "contra", "creo", "cual", "cuales", "cualquier", "cuando", "cuanta", "cuantas", "cuanto", "cuantos", "cuatro", "cuenta", "cuál", "cuáles", "cuándo", "cuánta", "cuántas", "cuánto", "cuántos", "cómo", 
			"d", "da", "dado", "dan", "dar", "de", "debajo", "debe", "deben", "debido", "decir", "dejó", "del", "delante", "demasiado", "demás", "dentro", "deprisa", "desde", "despacio", "despues", "después", "detras", "detrás", "dia", "dias", 
			"dice", "dicen", "dicho", "dieron", "dijeron", "dijo", "dio", "disfrutar", "donde", "dos", "durante", "día", "días", "dónde", "e", "ejemplo", "el", "ella", "ellas", "ello", "ellos", "embargo", "empleais", "emplean", "emplear", 
			"empleas", "empleo", "en", "encima", "encontrar", "encuentra", "encuentran", "enfrente", "enseguida", "entonces", "entre", "era", "erais", "eramos", "eran", "eras", "eres", "es", "esa", "esas", "ese", "eso", "esos", "esta", "estaba", 
			"estabais", "estaban", "estabas", "estad", "estada", "estadas", "estado", "estados", "estais", "estamos", "estan", "estando", "estar", "estaremos", "estará", "estarán", "estarás", "estaré", "estaréis", "estaría", "estaríais", "estaríamos", 
			"estarían", "estarías", "estas", "este", "estemos", "esto", "estos", "estoy", "estuve", "estuviera", "estuvierais", "estuvieran", "estuvieras", "estuvieron", "estuviese", "estuvieseis", "estuviesen", "estuvieses", "estuvimos", "estuviste", 
			"estuvisteis", "estuviéramos", "estuviésemos", "estuvo", "está", "estábamos", "estáis", "están", "estás", "esté", "estéis", "estén", "estés", "etc", "ex", "excepto", "existe", "existen", "explicó", "expresó", "f", "fin", "fue", "fuera", 
			"fuerais", "fueran", "fueras", "fueron", "fuese", "fueseis", "fuesen", "fueses", "fui", "fuimos", "fuiste", "fuisteis", "fuéramos", "fuésemos", "g", "gueno", "h", "ha", "haber", "habia", "habida", "habidas", "habido", "habidos", "habiendo", 
			"habla", "hablan", "habremos", "habrá", "habrán", "habrás", "habré", "habréis", "habría", "habríais", "habríamos", "habrían", "habrías", "habéis", "había", "habíais", "habíamos", "habían", "habías", "hace", "haceis", "hacemos", "hacen", 
			"hacer", "hacerlo", "haces", "hacia", "haciendo", "hago", "han", "has", "hasta", "hay", "haya", "hayamos", "hayan", "hayas", "hayáis", "he", "hecho", "hemos", "hicieron", "hizo", "horas", "hoy", "hube", "hubiera", "hubierais", "hubieran", 
			"hubieras", "hubieron", "hubiese", "hubieseis", "hubiesen", "hubieses", "hubimos", "hubiste", "hubisteis", "hubiéramos", "hubiésemos", "hubo", "i", "incluso", "indicó", "informo", "informó", "intenta", "intentais", "intentamos", "intentan", 
			"intentar", "intentas", "intento", "ir", "j", "junto", "k", "l", "la", "lado", "las", "le", "les", "llegó", "lleva", "llevar", "lo", "los", "luego", "lugar", "m", "mal", "manera", "manifestó", "mas", "me", "mediante", "mejor", "mencionó", 
			"menos", "menudo", "mi", "mia", "mias", "mientras", "mio", "mios", "mis", "misma", "mismas", "mismo", "mismos", "modo", "momento", "mucha", "muchas", "mucho", "muchos", "muy", "más", "mí", "mía", "mías", "mío", "míos", "n", "nada", "nadie", 
			"ni", "ninguna", "ningunas", "ninguno", "ningunos", "ningún", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nunca", "o", "ocho", "ofrece", "ofrecen", "os", "otra", "otras", "otro", "otros", "p", "pais", 
			"para", "parece", "parte", "partir", "peor", "pero", "pesar", "poca", "pocas", "poco", "pocos", "podeis", "podemos", "poder", "podria", "podriais", "podriamos", "podrian", "podrias", "podrá", "podrán", "podría", "podrían", "poner", "por", 
			"porque", "posible", "primer", "primera", "primero", "primeros", "principalmente", "pronto", "propia", "propias", "propio", "propios", "proximo", "próximo", "próximos", "pudo", "pueda", "puede", "pueden", "puedo", "pues", "q", "que", "quedó", 
			"queremos", "quien", "quienes", "quiere", "quiza", "quizas", "quizá", "quizás", "quién", "quiénes", "qué", "r", "raras", "realizado", "realizar", "realizó", "repente", "respecto", "s", "sabe", "sabeis", "sabemos", "saben", "saber", "sabes", 
			"salvo", "se", "sea", "seamos", "sean", "seas", "segun", "segunda", "segundo", "según", "seis", "ser", "sera", "seremos", "será", "serán", "serás", "seré", "seréis", "sería", "seríais", "seríamos", "serían", "serías", "seáis", "señaló", "si", 
			"sido", "siempre", "siendo", "siete", "sigue", "siguiente", "sin", "sino", "situada", "sobre", "sois", "sola", "solamente", "solas", "solo", "solos", "somos", "son", "soy", "su", "supuesto", "sus", "suya", "suyas", "suyo", "suyos", "sé", "sí", 
			"sólo", "t", "tal", "tambien", "también", "tampoco", "tan", "tanto", "tarde", "te", "temprano", "tendremos", "tendrá", "tendrán", "tendrás", "tendré", "tendréis", "tendría", "tendríais", "tendríamos", "tendrían", "tendrías", "tened", "tenemos", 
			"tener", "tenga", "tengamos", "tengan", "tengas", "tengo", "tengáis", "tenida", "tenidas", "tenido", "tenidos", "teniendo", "tenéis", "tenía", "teníais", "teníamos", "tenían", "tenías", "tercera", "ti", "tiene", "tienen", "tienes", "toda", "todas", 
			"todavia", "todavía", "todo", "todos", "trabaja", "trabajais", "trabajamos", "trabajan", "trabajar", "trabajas", "trabajo", "tras", "trata", "través", "tres", "tu", "tus", "tuve", "tuviera", "tuvierais", "tuvieran", "tuvieras", "tuvieron", "tuviese", 
			"tuvieseis", "tuviesen", "tuvieses", "tuvimos", "tuviste", "tuvisteis", "tuviéramos", "tuviésemos", "tuvo", "tuya", "tuyas", "tuyo", "tuyos", "tú", "u", "ultimo", "un", "una", "unas", "uno", "unos", "usa", "usais", "usamos", "usan", "usar", "usas", 
			"uso", "usted", "ustedes", "v", "va", "vais", "vamos", "van", "varias", "varios", "vaya", "veces", "ver", "verdad", "verdadera", "verdadero", "vez", "vosotras", "vosotros", "voy", "vuestra", "vuestras", "vuestro", "vuestros", "w", "x", "y", "ya", "yo", 
			"z", "él", "éramos", "ésa", "ésas", "ése", "ésos", "ésta", "éstas", "éste", "éstos", "última", "últimas", "último", "últimos"
	};
	
	// Word stemming
	private final SpanishStemmer spanishStemmer = new SpanishStemmer();
	
	private final ModelService modelService;
	private final DictionaryRepository dictionaryRepository;
	private final TouristicPointRepository touristicPointRepository;
	
	public DictionaryServiceImpl(DictionaryRepository dictionaryRepository,
			TouristicPointRepository touristicPointRepository,
			@Qualifier("dirichletSmoothingModel") ModelService modelService) {
		this.dictionaryRepository = dictionaryRepository;
		this.touristicPointRepository = touristicPointRepository;
		this.modelService = modelService;
	}
	
	@Override
	public List<TouristicPoint> getTouristicPoints(String query) {
		// Tokenize string, lowercase tokens, stemming & group tokens by frequencies
		Map<String, Long> terms = stemAndGetFreq(analyze(query)); 
		
		// Score of each tourist point
		Map<TouristicPoint, Double> scores = new HashMap<>();
		//ConcurrentHashMap<TouristicPoint, DoubleAccumulator> scores = new ConcurrentHashMap<>();
		
		// (DS Model) At least a query term appears in a tourist point
		Set<TouristicPoint> pointsFreqNotZero = new HashSet<>();
		
		// Tourist points (if Dirichlet Smoothing Model used get points from DB)
		List<TouristicPoint> points = new ArrayList<>();
		if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
			points = this.touristicPointRepository.findAll();
		}
		
		// Iterate over terms of query
		//terms.forEach((term, freq) -> {
		for (Entry<String, Long> entry : terms.entrySet()) {
			Optional<Dictionary> optDict = this.dictionaryRepository.findByTerm(entry.getKey());
			if (optDict.isEmpty()) continue;
			
			// Get weights of term associated to the tourist points
			Map<TouristicPoint, Double> weights = optDict.get().getWeights();
			
			// For DS Model (to take account of absent terms)
			if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
				for (TouristicPoint point : points) {
					Double weight = weights.get(point);
					if (weight == null) {
						// Calculate score for absent term
						weight = this.modelService.calculateScore(
								new DictionaryScoreSpec(0, point.getDocLength(), optDict.get().getProbTermCol()));
					} else {
						pointsFreqNotZero.add(point);
					}
					// Get accumulative score of query (DS Model)
					calculateQueryScore(scores, point, 1.0, weight, entry.getValue().intValue());
				}
			} 
			// For VS & BM25 Model
			else {
				for (Entry<TouristicPoint, Double> entryPoint : weights.entrySet()) {
					// Get accumulative score of query (VS & BM25 Model)
					calculateQueryScore(scores, entryPoint.getKey(), 0.0, entryPoint.getValue(), entry.getValue().intValue());
				}
			}			
		}
						
		// Order scores
		List<Entry<TouristicPoint, Double>> termEntries = new ArrayList<>(scores.entrySet());
		Collections.sort(termEntries, Collections.reverseOrder(Entry.comparingByValue()));
		// Get only Tourist points
		points = termEntries.stream()
				.map(entry -> entry.getKey())
				.collect(Collectors.toList());
				
		if (this.modelService.getClass() == DirichletSmoothingModelServiceImpl.class) {
			// Delimit result
			points.retainAll(pointsFreqNotZero);
		}
		
		return points;
	}
	
	/**
	 * Set accumulative score to rank tourist points
	 * 
	 * @param scores Map with tourist points associated to scores
	 * @param point Tourist point
	 * @param initValue Initial value for Tourist point (0 or 1)
	 * @param weight Weight of tourist point associated to a term
	 * @param freq Query term frequency
	 */
	private void calculateQueryScore(Map<TouristicPoint, Double> scores, TouristicPoint point, double initValue, double weight, int freq) {
		double score = scores.getOrDefault(point, initValue);
		score = this.modelService.rank(score, weight, freq);
		scores.put(point, score);
	}
		
	@Override
	public void deleteTouristicPointOfTerm(TouristicPoint point) {
		// Get Terms -> (points, scores) & remove obsolete points 
		Set<Dictionary> keywords = this.dictionaryRepository.findByWeightsTouristicPoint(point);
		for (Dictionary dictionary : keywords) {
			Map<TouristicPoint, Double> weights = dictionary.getWeights();
			weights.remove(point);
			this.dictionaryRepository.save(dictionary);
		}
	}
	
	@Override
	public List<String> analyze(String text) {
		return analyze(text, new StandardAnalyzer(StopFilter.makeStopSet(STOP_WORDS)));
	}
	
	/**
	 * Use analyzer to tokenize & filter stop words/symbols
	 * 
	 * @param text Text to analyze
	 * @param analyzer Analyzer to use
	 * @return List with analyzed tokens
	 */
	private List<String> analyze(String text, Analyzer analyzer) {
		List<String> result = new ArrayList<>();
		TokenStream tokenStream = analyzer.tokenStream("content", text);
		CharTermAttribute attribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(attribute.toString());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	@Override
	public Map<String, Long> stemAndGetFreq(List<String> terms) {
		return terms.stream()
				//.filter(term -> !term.matches("[0-9]+"))
				.map(term -> stem(term))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
	
	/**
	 * Word stemming using Snowball algorithm
	 * 
	 * @param term Term to stem
	 * @return Stemmed term
	 */
	private synchronized String stem(String term) {
		spanishStemmer.setCurrent(term);
		spanishStemmer.stem();
		return spanishStemmer.getCurrent();
	}
	
}
