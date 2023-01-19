package com.example.enjoymadrid.serviceslogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryLoadService;
import com.example.enjoymadrid.services.DictionaryService;

@Service
public class DictionaryLoadServiceLogic implements DictionaryLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoadService.class);
		
	// Stop words
	private static final String[] STOP_WORDS = new String[] {
			"a", "acá","actualmente","adelante","ademas","además","afirmó","agregó","ahi","ahora","ahí","al","algo","alguna","algunas","alguno","algunos","algún","alli","allí","alrededor","ambos", "ante","anterior", "antes","apenas","aproximadamente",
			"aquel","aquella","aquellas","aquello","aquellos","aqui","aquél","aquélla","aquéllas","aquéllos","aquí","arriba","aseguró","asi","así","atrás","aun","aunque","ayer","añadió","aún","b","bastante","bien","c","cada","casi","cierta",
			"ciertas","cierto","ciertos","cinco","comentó","como","con","conmigo","conocer","conseguimos","conseguir","considera","consideró","consigo","consigue","consiguen","consigues","contigo","contra","creo","cual","cuales","cualquier",
			"cuando","cuanta","cuantas","cuanto","cuantos","cuatro","cuenta","cuál","cuáles","cuándo","cuánta","cuántas","cuánto","cuántos","cómo","d","da","dado","dan","dar","de","debajo","debe","deben","debido","decir","dejó","del","delante",
			"demasiado","demás","dentro","deprisa","desde","despacio","despues","después","detras","detrás","dia","dias","dice","dicen","dicho","dieron","dijeron","dijo","dio","disfrutar","donde","dos","durante","día","días","dónde","e","ejemplo",
			"el","ella","ellas","ello","ellos","embargo","empleais","emplean","emplear","empleas","empleo","en","encima","encuentra","encuentran","encontrar","enfrente","enseguida","entonces","entre","era","eramos","eran","eras","eres","es","esa",
			"esas","ese","eso","esos","esta","estaba","estaban","estado","estados","estais","estamos","estan","estar","estará","estas","este","esto","estos","estoy","estuvo","está","están","etc","ex","excepto","existe","existen","explicó","expresó",
			"f","fin","fue","fuera","fueron","fui","fuimos","g","gueno","h","ha","haber","habia","habla","hablan","habrá","había","habían","hace","haceis","hacemos","hacen","hacer","hacerlo","haces","hacia","haciendo","hago","han","hasta","hay","haya",
			"he","hecho","hemos","hicieron","hizo","horas","hoy","hubo","i","incluso","indicó","informo","informó","intenta","intentais","intentamos","intentan","intentar","intentas","intento","ir","j","junto","k","l","la","lado","las","le","les","llegó",
			"lleva","llevar","lo","los","luego","lugar","m","mal","manera","manifestó","mas","me","mediante","mejor","mencionó","menos","menudo","mi","mia","mias","mientras","mio","mios","mis","misma","mismas","mismo","mismos","modo","momento","mucha",
			"muchas","mucho","muchos","muy","más","mí","mía","mías","mío","míos","n","nada","nadie","ni","ninguna","ningunas","ninguno","ningunos","ningún","no","nos","nosotras","nosotros","nuestra","nuestras","nuestro","nuestros","nunca","o","ocho",
			"ofrece","ofrecen","os","otra","otras","otro","otros","p","pais","para","parece","parte","partir","peor","pero","pesar","poca","pocas","poco","pocos","podeis","podemos","poder","podria","podriais","podriamos","podrian","podrias","podrá",
			"podrán","podría","podrían","poner","por","porque","posible","primer","primera","primero","primeros","principalmente","pronto","propia","propias","propio","propios","proximo","próximo","próximos","pudo","pueda","puede","pueden","puedo","pues",
			"q","que","quedó","queremos","quien","quienes","quiere","quiza","quizas","quizá","quizás","quién","quiénes","qué","r","raras","realizado","realizar","realizó","repente","respecto","s","sabe","sabeis","sabemos","saben","saber","sabes","salvo",
			"se","sea","sean","segun","segunda","segundo","según","seis","ser","sera","será","serán","sería","señaló","si","sido","siempre","siendo","siete","sigue","siguiente","sin","sino","situada","sobre","sois","sola","solamente","solas","solo","solos",
			"somos","son","soy","su","supuesto","sus","suya","suyas","suyo","sé","sí","sólo","t","tal","tambien","también","tampoco","tan","tanto","tarde","te","temprano","tendrá","tendrán","teneis","tenemos","tener","tenga","tengo","tenido","tenía","tercera",
			"ti","tiene","tienen","toda","todas","todavia","todavía","todo","todos","trabaja","trabajais","trabajamos","trabajan","trabajar","trabajas","trabajo","tras","trata","través","tres","tu","tus","tuvo","tuya","tuyas","tuyo","tuyos","tú","u","ultimo",
			"un","una","unas","uno","unos","usa","usais","usamos","usan","usar","usas","uso","usted","ustedes","v","va","vais","vamos","van","varias","varios","vaya","veces","ver","verdad","verdadera","verdadero","vez","vosotras","vosotros","voy","vuestra",
			"vuestras","vuestro","vuestros","w","x","y","ya","yo","z","él","ésa","ésas","ése","ésos","ésta","éstas","éste","éstos","última","últimas","último","últimos"
	};

	// Term frequencies
	private static ConcurrentHashMap<String, ConcurrentHashMap<TouristicPoint, Integer>> termFreq = new ConcurrentHashMap<>();
	// Term frequencies in collection
	private static ConcurrentHashMap<String, LongAdder> termFreqCollection = new ConcurrentHashMap<>();
	// Max term frequencies
	private static ConcurrentHashMap<TouristicPoint, Integer> maxTermFreq = new ConcurrentHashMap<>();
	// Total number of documents/touristic points
	private static LongAdder totalDocs = new LongAdder();
	// Number of documents where the term t appears
	private static ConcurrentHashMap<String, LongAdder> nTermDocs = new ConcurrentHashMap<>();
	// Length of the document D in words
	private static ConcurrentHashMap<TouristicPoint, Integer> docsLength = new ConcurrentHashMap<>();
	// Length of the collection C in words
	private static LongAdder collectionLength = new LongAdder();
		
	// Dependency injection
	private final DictionaryRepository dictionaryRepository;
	private final DictionaryService dictionaryService;
	private final MixedMinAndMaxModelServiceLogic mixedMinAndMaxModelServiceLogic;
	private final VectorialModelServiceLogic vectorialModelServiceLogic;
	private final BM25ModelServiceLogic bm25ModelServiceLogic;
	private final DirichletSmoothingModelServiceLogic dirichletSmoothingModelServiceLogic;
	
	public DictionaryLoadServiceLogic(DictionaryRepository dictionaryRepository,
			DictionaryService dictionaryService, 
			MixedMinAndMaxModelServiceLogic mixedMinAndMaxModelServiceLogic, 
			VectorialModelServiceLogic vectorialModelServiceLogic, 
			BM25ModelServiceLogic bm25ModelServiceLogic, 
			DirichletSmoothingModelServiceLogic dirichletSmoothingModelServiceLogic
	) {
		this.dictionaryRepository = dictionaryRepository;
		this.dictionaryService = dictionaryService;
		this.mixedMinAndMaxModelServiceLogic = mixedMinAndMaxModelServiceLogic;
		this.vectorialModelServiceLogic = vectorialModelServiceLogic;
		this.bm25ModelServiceLogic = bm25ModelServiceLogic;
		this.dirichletSmoothingModelServiceLogic = dirichletSmoothingModelServiceLogic;
	}

	@Override
	public void loadTerms(TouristicPoint point) {
		// Get title & description from point
		StringBuilder text = new StringBuilder(point.getName());
		text.append(' ');
		text.append(parseHtml(point.getDescription()));
		
		// Tokenize string, lowercase tokens, filter symbols/stop words & stemming
		List<String> resultTerms = this.dictionaryService.analyze(text.toString(), new StandardAnalyzer(StopFilter.makeStopSet(STOP_WORDS))); 
		// Total terms in text/document		
		LongAdder docLength = new LongAdder();
		// Remove numbers & words of 1 character length, then group by frequency in Map
		Map<String, Long> termFreqDocs = resultTerms.stream()
				.filter(term -> term.matches("[a-z]+") && term.length() > 1)
				.map(term -> this.dictionaryService.stem(term))
				.peek(term -> docLength.increment())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				
		// Doc -> doc length
		docsLength.put(point, docLength.intValue());
		// Add terms count to collection
		collectionLength.add(docLength.longValue());
		// Add doc to total
		totalDocs.increment();
				
		// Add term frequencies & max term frequency in document
		int maxFreq = 0;
		for (Map.Entry<String, Long> entry : termFreqDocs.entrySet()) {
			// Term -> (Document -> Frequency)
			termFreq.computeIfAbsent(entry.getKey(), v -> new ConcurrentHashMap<TouristicPoint, Integer>())
				.put(point, entry.getValue().intValue());
			// Term -> Frequency collection
			termFreqCollection.computeIfAbsent(entry.getKey(), v -> new LongAdder()).add(entry.getValue().longValue());
			// Get max term from document
			if (entry.getValue() > maxFreq) maxFreq = entry.getValue().intValue();
			// Increase occurrences of the term in documents
			nTermDocs.computeIfAbsent(entry.getKey(), v -> new LongAdder()).increment();;
		}
		
		// Set max term frequency in document
		maxTermFreq.put(point, maxFreq);
	}
	
	@Override
	public void calculateScoreTerms() {
		// Iterate over: terms -> (Tourist points -> frequency)
		termFreq.entrySet().parallelStream().forEach(entryTerm -> {
			String term = entryTerm.getKey();
			Map<TouristicPoint, Double> scores = new HashMap<>();
			for (Entry<TouristicPoint, Integer> entryPoint: entryTerm.getValue().entrySet()) {
				// Get data to calculate score
				TouristicPoint touristicPoint = entryPoint.getKey();
				int tf = entryPoint.getValue().intValue();
				
				// Model to use for documents score
				double score = this.mixedMinAndMaxModelServiceLogic.calculateScore(tf, maxTermFreq.get(touristicPoint).intValue(), 
						totalDocs.intValue(), nTermDocs.get(term).intValue());
//				double score1 = this.vectorialModelServiceLogic.calculateScore(tf, totalDocs.intValue(), nTermDocs.get(term).intValue());
//				double score2 = this.bm25ModelServiceLogic.calculateScore(tf, totalDocs.intValue(), nTermDocs.get(term).intValue(), 
//						docsLength.get(touristicPoint).intValue());
//				double score3 = this.dirichletSmoothingModelServiceLogic.calculateScore(tf, termFreqCollection.get(term).intValue(), 
//						docsLength.get(touristicPoint).intValue(), collectionLength.intValue());
				
				// Save the score associated to the tourist point (document)
				scores.put(touristicPoint, score);
			}
			// Save the term & scores in DB
			this.dictionaryRepository.save(new Dictionary(term, scores));
		});
		
		// Reset variables
		termFreq.clear();
		maxTermFreq.clear();
		totalDocs.reset();
		nTermDocs.clear();
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
	
}
