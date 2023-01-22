package com.example.enjoymadrid.serviceslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.Dictionary;
import com.example.enjoymadrid.models.DictionaryScoreSpec;
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryLoadService;
import com.example.enjoymadrid.services.DictionaryService;
import com.example.enjoymadrid.services.ModelService;

@Service
public class DictionaryLoadServiceLogic implements DictionaryLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoadService.class);
		
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

	// Term frequencies
	private ConcurrentHashMap<String, ConcurrentHashMap<TouristicPoint, Integer>> termFreq = new ConcurrentHashMap<>();
	// Square root of the sum of the squared term frequencies in a document D 
	private ConcurrentHashMap<TouristicPoint, Double> tfSumDoc = new ConcurrentHashMap<>();
	// Term frequencies in collection
	private ConcurrentHashMap<String, LongAdder> termFreqCollection = new ConcurrentHashMap<>();
	// Total number of documents/touristic points
	private LongAdder totalDocs = new LongAdder();
	// Number of documents where the term T appears
	private ConcurrentHashMap<String, LongAdder> docFreq = new ConcurrentHashMap<>();
	// Length of the document D in words
	private ConcurrentHashMap<TouristicPoint, Integer> docsLength = new ConcurrentHashMap<>();
	// Length of the collection C in words
	private LongAdder collectionLength = new LongAdder();
	
	// Dependency injection
	private final DictionaryRepository dictionaryRepository;
	private final ModelService modelService;
	private final DictionaryService dictionaryService;
	private final MixedMinAndMaxModelServiceLogic mixedMinAndMaxModelServiceLogic;
	private final VectorSpaceModelServiceLogic vectorSpaceModelServiceLogic;
	private final BM25ModelServiceLogic bm25ModelServiceLogic;
	private final DirichletSmoothingModelServiceLogic dirichletSmoothingModelServiceLogic;
	
	public DictionaryLoadServiceLogic(DictionaryRepository dictionaryRepository,
			DictionaryService dictionaryService, 
			MixedMinAndMaxModelServiceLogic mixedMinAndMaxModelServiceLogic, 
			VectorSpaceModelServiceLogic vectorSpaceModelServiceLogic, 
			BM25ModelServiceLogic bm25ModelServiceLogic, 
			DirichletSmoothingModelServiceLogic dirichletSmoothingModelServiceLogic
	) {
		this.dictionaryRepository = dictionaryRepository;
		this.dictionaryService = dictionaryService;
		this.modelService = mixedMinAndMaxModelServiceLogic;
		this.mixedMinAndMaxModelServiceLogic = mixedMinAndMaxModelServiceLogic;
		this.vectorSpaceModelServiceLogic = vectorSpaceModelServiceLogic;
		this.bm25ModelServiceLogic = bm25ModelServiceLogic;
		this.dirichletSmoothingModelServiceLogic = dirichletSmoothingModelServiceLogic;
	}

	@Override
	public void loadTerms(TouristicPoint point) {
		// Get title & description from point
		StringJoiner text = new StringJoiner(" ");
		text.add(getStringIfNotNull(point.getName()));
		text.add(getStringIfNotNull(point.getAddress()));
		text.add(getStringIfNotNull(point.getZipcode()));
		text.add(getStringIfNotNull(parseHtml(point.getDescription())));
		
		// Tokenize string, lowercase tokens, filter symbols/stop words
		List<String> resultTerms = this.dictionaryService.analyze(text.toString(), new StandardAnalyzer(StopFilter.makeStopSet(STOP_WORDS))); 
		// Total terms in text/document
		LongAdder docLength = new LongAdder();
		// Remove numbers, stemming, then group by frequency in Map
		Map<String, Long> termFreqDocs = resultTerms.stream()
				//.filter(term -> !term.matches("[0-9]+"))
				.map(term -> this.dictionaryService.stem(term))
				.peek(term -> docLength.increment())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				
		// Doc -> doc length
		docsLength.put(point, docLength.intValue());
		// Add terms count to collection
		collectionLength.add(docLength.longValue());
		// Add doc to total
		totalDocs.increment();
		
		// Sum of the squared frequency terms (logarithmically scaled) in a document D 
		double tfSum = 0.0;
		
		for (Map.Entry<String, Long> entry : termFreqDocs.entrySet()) {
			// Term -> (Document -> Frequency)
			termFreq.computeIfAbsent(entry.getKey(), v -> new ConcurrentHashMap<TouristicPoint, Integer>())
				.put(point, entry.getValue().intValue());
			// Term -> Frequency collection
			termFreqCollection.computeIfAbsent(entry.getKey(), v -> new LongAdder()).add(entry.getValue().longValue());
			// Increase occurrences of the term in documents
			docFreq.computeIfAbsent(entry.getKey(), v -> new LongAdder()).increment();
			// Add squared tf (logarithmically scaled) for each term T in the document D
			tfSum += Math.pow(1 + Math.log10(entry.getValue().intValue()), 2);
		}
		// Set square root of tfSum into document/point
		tfSumDoc.put(point, Math.sqrt(tfSum));
	}
	
	@Override
	public void calculateScoreTerms() {
				
		// Fill in terms -> document with 0 frequency (BM25 & Dirichlet Smoohting)
		termFreq.entrySet().parallelStream().forEach(entry -> {
			String term = entry.getKey();
			Set<TouristicPoint> termPoints = entry.getValue().keySet();
			for (TouristicPoint point : docsLength.keySet()) {
				if (!termPoints.contains(point)) termFreq.get(term).put(point, 0);
			}
		});
		// Iterate over: terms -> (Tourist points -> frequency) to calculate score
		termFreq.entrySet().parallelStream().forEach(entryTerm -> {
			String term = entryTerm.getKey();
			Map<TouristicPoint, Double> scores = new HashMap<>();
			for (Entry<TouristicPoint, Integer> entryPoint: entryTerm.getValue().entrySet()) {
				// Get data to calculate score
				TouristicPoint touristicPoint = entryPoint.getKey();
				int tf = entryPoint.getValue().intValue();
				
				// Model to use for documents score
				double score = this.mixedMinAndMaxModelServiceLogic.calculateScore(
						new DictionaryScoreSpec(tf, totalDocs.intValue(), docFreq.get(term).intValue()));
				double score1 = this.vectorSpaceModelServiceLogic
						.calculateScore(new DictionaryScoreSpec(tf, totalDocs.intValue(), docFreq.get(term).intValue(),
								tfSumDoc.get(touristicPoint)));
				double score2 = this.bm25ModelServiceLogic
						.calculateScore(new DictionaryScoreSpec(tf, totalDocs.intValue(), docFreq.get(term).intValue(),
								docsLength.get(touristicPoint).intValue(), collectionLength.longValue() / totalDocs.intValue()));
				double score3 = this.dirichletSmoothingModelServiceLogic
						.calculateScore(new DictionaryScoreSpec(tf, termFreqCollection.get(term).intValue(),
								docsLength.get(touristicPoint).intValue(), collectionLength.longValue()));
				
				// Don't store a score = 0
				if (score == 0) continue;
				// Save the score associated to the tourist point (document)
				scores.put(touristicPoint, score);
			}
			// Save the term & scores in DB
			this.dictionaryRepository.save(new Dictionary(term, scores));
		});
		
		// Reset variables
		termFreq.clear();
		tfSumDoc.clear();
		termFreqCollection.clear();
		totalDocs.reset();
		docFreq.clear();
		docsLength.clear();
		collectionLength.reset();
		
		logger.info("Terms from descriptions of tourist points updated");
	}
				
	/**
	 * Parse html into normalized, combined text
	 * 
	 * @param html Html to normalized text
	 * @return Normalized text
	 */
	private String parseHtml(String html) {
		return Jsoup.parse(html).text();
	}
	
	/**
	 * Check if object not null & then get string
	 * 
	 * @param object Object to check
	 * @return Get string if not null, otherwise return empty string
	 */
	private String getStringIfNotNull(Object object) {
		return object == null ? "" : object.toString();
	}
	
}
