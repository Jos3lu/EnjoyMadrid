package com.example.enjoymadrid.serviceslogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.services.DictionaryLoadService;
import com.example.enjoymadrid.services.DictionaryService;

@Service
public class DictionaryLoadServiceLogic implements DictionaryLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoadService.class);
		
	// Stop words
	private static final String[] STOP_WORDS = new String[] {
			"a","actualmente","adelante","ademas","además","afirmó","agregó","ahi","ahora","ahí","al","algo","alguna","algunas","alguno","algunos","algún","alli","allí","alrededor","ambos", "ante","anterior", "antes","apenas","aproximadamente",
			"aquel","aquella","aquellas","aquello","aquellos","aqui","aquél","aquélla","aquéllas","aquéllos","aquí","arriba","aseguró","asi","así","atrás","aun","aunque","ayer","añadió","aún","b","bajo","bastante","bien","breve","buen","buena",
			"buenas","bueno","buenos","c","cada","casi","cerca","cierta","ciertas","cierto","ciertos","cinco","claro","comentó","como","con","conmigo","conocer","conseguimos","conseguir","considera","consideró","consigo","consigue","consiguen",
			"consigues","contigo","contra","cosas","creo","cual","cuales","cualquier","cuando","cuanta","cuantas","cuanto","cuantos","cuatro","cuenta","cuál","cuáles","cuándo","cuánta","cuántas","cuánto","cuántos","cómo","d","da","dado","dan",
			"dar","de","debajo","debe","deben","debido","decir","dejó","del","delante","demasiado","demás","dentro","deprisa","desde","despacio","despues","después","detras","detrás","dia","dias","dice","dicen","dicho","dieron","diferente",
			"diferentes","dijeron","dijo","dio", "disfrutar","donde","dos","durante","día","días","dónde","e","ejemplo","el","ella","ellas","ello","ellos","embargo","empleais","emplean","emplear","empleas","empleo","en","encima","encuentra",
			"enfrente","enseguida","entonces","entre","era","eramos","eran","eras","eres","es","esa","esas","ese","eso","esos","esta","estaba","estaban","estado","estados","estais","estamos","estan","estar","estará","estas","este","esto",
			"estos","estoy","estuvo","está","están","ex","excepto","existe","existen","explicó","expresó","f","fin","final","fue","fuera","fueron","fui","fuimos","g","general","gran","grandes","gueno","h","ha","haber","habia","habla","hablan",
			"habrá","había","habían","hace","haceis","hacemos","hacen","hacer","hacerlo","haces","hacia","haciendo","hago","han","hasta","hay","haya","he","hecho","hemos","hicieron","hizo","horas","hoy","hubo","i","igual","incluso","indicó",
			"informo","informó","intenta","intentais","intentamos","intentan","intentar","intentas","intento","ir","j","junto","k","l","la","lado","largo","las","le","lejos","les","llegó","lleva","llevar","lo","los","luego","lugar","m","mal",
			"manera","manifestó","mas","mayor","me","mediante","medio","mejor","mencionó","menos","menudo","mi","mia","mias","mientras","mio","mios","mis","misma","mismas","mismo","mismos","modo","momento","mucha","muchas","mucho","muchos",
			"muy","más","mí","mía","mías","mío","míos","n","nada","nadie","ni","ninguna","ningunas","ninguno","ningunos","ningún","no","nos","nosotras","nosotros","nuestra","nuestras","nuestro","nuestros","nueva","nuevas","nuevo","nuevos",
			"nunca","o","ocho","os","otra","otras","otro","otros","p","pais","para","parece","parte","partir","pasada","pasado","peor","pero","pesar","poca","pocas","poco","pocos","podeis","podemos","poder","podria","podriais","podriamos",
			"podrian","podrias","podrá","podrán","podría","podrían","poner","por","porque","posible","primer","primera","primero","primeros","principalmente","pronto","propia","propias","propio","propios","proximo","próximo","próximos","pudo",
			"pueda","puede","pueden","puedo","pues","q","que","quedó","queremos","quien","quienes","quiere","quiza","quizas","quizá","quizás","quién","quiénes","qué","r","raras","realizado","realizar","realizó","repente","respecto","s","sabe",
			"sabeis","sabemos","saben","saber","sabes","salvo","se","sea","sean","segun","segunda","segundo","según","seis","ser","sera","será","serán","sería","señaló","si","sido","siempre","siendo","siete","sigue","siguiente","sin","sino",
			"sobre","sois","sola","solamente","solas","solo","solos","somos","son","soy","su","supuesto","sus","suya","suyas","suyo","sé","sí","sólo","t","tal","tambien","también","tampoco","tan","tanto","tarde","te","temprano","tendrá","tendrán",
			"teneis","tenemos","tener","tenga","tengo","tenido","tenía","tercera","ti","tiempo","tiene","tienen","toda","todas","todavia","todavía","todo","todos","total","trabaja","trabajais","trabajamos","trabajan","trabajar","trabajas","trabajo",
			"tras","trata","través","tres","tu","tus","tuvo","tuya","tuyas","tuyo","tuyos","tú","u","ultimo","un","una","unas","uno","unos","usa","usais","usamos","usan","usar","usas","uso","usted","ustedes","v","va","vais","valor","vamos","van",
			"varias","varios","vaya","veces","ver","verdad","verdadera","verdadero","vez","vosotras","vosotros","voy","vuestra","vuestras","vuestro","vuestros","w","x","y","ya","yo","z","él","ésa","ésas","ése","ésos","ésta","éstas","éste","éstos",
			"última","últimas","último","últimos"
	};

	// Term frequencies
	private static Map<String, Map<TouristicPoint, AtomicInteger>> termFreq = Collections
			.synchronizedMap(new HashMap<>());
	// Term frequencies in collection
	private static Map<String, AtomicInteger> termFreqCollection = Collections.synchronizedMap(new HashMap<>());
	// Max term frequencies
	private static ConcurrentHashMap<TouristicPoint, AtomicInteger> maxTermFreq = new ConcurrentHashMap<>();
	// Total number of documents/touristic points
	private static AtomicInteger totalDocs = new AtomicInteger();
	// Number of documents where the term t appears
	private static Map<String, AtomicInteger> nTermDocs = Collections.synchronizedMap(new HashMap<>());
	// Length of the document D in words
	private static ConcurrentHashMap<TouristicPoint, AtomicInteger> docsLength = new ConcurrentHashMap<>();
	// Length of the collection C in words
	private static AtomicLong collectionLength = new AtomicLong();
		
	// Dependency injection
	private final DictionaryService dictionaryService;
	private final PNormModelServiceLogic pNormModelServiceLogic;
	
	public DictionaryLoadServiceLogic(DictionaryService dictionaryService, PNormModelServiceLogic pNormModelServiceLogic) {
		this.dictionaryService = dictionaryService;
		this.pNormModelServiceLogic = pNormModelServiceLogic;
	}

	@Override
	public void loadTerms(TouristicPoint point) {
		// Get title & description from point
		StringBuilder text = new StringBuilder(point.getName());
		text.append(' ');
		text.append(parseHtml(point.getDescription()));
						
		// Tokenize string, lowercase tokens, filter symbols/stop words & stemming
		List<String> resultTerms = this.dictionaryService.analyze(text.toString(), new StandardAnalyzer(StopFilter.makeStopSet(STOP_WORDS))); 
		// Remove numbers & words of 1 character length, then group by frequency in Map
		Map<String, Long> termFreqDocs = resultTerms.stream()
				.filter(term -> term.matches("[a-z]+") && term.length() > 1)
				.map(term -> this.dictionaryService.stem(term))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				
		// Total terms in text/document		
		int docLength = text.toString().replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+").length;
		// Doc -> doc length
		docsLength.put(point, new AtomicInteger(docLength));
		// Add terms count to collection
		collectionLength.addAndGet(docLength);
		// Add doc to total
		totalDocs.incrementAndGet();
				
		// Add term frequencies & max term frequency in document
		AtomicInteger maxFreq = new AtomicInteger();
		for (Map.Entry<String, Long> entry : termFreqDocs.entrySet()) {
			// Term -> (Document -> Frequency)
			synchronized (termFreq) {
				Map<TouristicPoint, AtomicInteger> docTermFreq = termFreq.getOrDefault(entry.getKey(), new HashMap<>());
				docTermFreq.put(point, new AtomicInteger(entry.getValue().intValue()));
				termFreq.put(entry.getKey(), docTermFreq);
			}
			// Term -> Frequency collection
			synchronized (termFreqCollection) {
				AtomicInteger termFreqCollectionValue = termFreqCollection.getOrDefault(entry.getKey(), new AtomicInteger());
				termFreqCollectionValue.getAndAdd(entry.getValue().intValue());
				termFreqCollection.put(entry.getKey(), termFreqCollectionValue);
			}
			// Get max term from document
			if (entry.getValue() > maxFreq.get()) maxFreq.set(entry.getValue().intValue());
			// Increase occurrences of the term in documents
			synchronized (nTermDocs) {
				AtomicInteger termOcurrences = nTermDocs.getOrDefault(entry.getKey(), new AtomicInteger());
				termOcurrences.incrementAndGet();
				nTermDocs.put(entry.getKey(), termOcurrences);
			}
		}
		
		// Set max term frequency in document
		maxTermFreq.put(point, maxFreq);
	}
	
	@Override
	public void calculateScoreTerms() {
		// Model to use for documents score
		this.pNormModelServiceLogic.calculateScore(termFreq, maxTermFreq, totalDocs, nTermDocs);
		
		// Reset variables
		termFreq.clear();
		maxTermFreq.clear();
		totalDocs.set(0);
		nTermDocs.clear();
		docsLength.clear();
		collectionLength.set(0);
		
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
