package com.example.enjoymadrid.serviceslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.enjoymadrid.models.TouristicPoint;
import com.example.enjoymadrid.models.repositories.DictionaryRepository;
import com.example.enjoymadrid.services.DictionaryLoadService;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

@Service
public class DictionaryLoadServiceLogic implements DictionaryLoadService {
	
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoadService.class);
		
	// Stop words
	private static final String[] STOP_WORDS = new String[] {
			"a", "actualmente", "adelante", "además", "afirmó", "agregó", "ahora", "ahí", "al", "algo", "alguna", "algunas", "alguno", "algunos", "algún", "alrededor", "ambos", "ampleamos", "ante", "anterior", "antes", "apenas", 
			"aproximadamente", "aquel", "aquellas", "aquellos", "aqui", "aquí", "arriba", "aseguró", "así", "atras", "aunque", "ayer", "añadió", "aún", "bajo", "bastante", "bien", "buen", "buena", "buenas", "bueno", "buenos", 
			"cada", "casi", "cerca", "cierta", "ciertas", "cierto", "ciertos", "cinco", "comentó", "como", "con", "conocer", "conseguimos", "conseguir", "considera", "consideró", "consigo", "consigue", "consiguen", "consigues", 
			"contra", "cosas", "creo", "cual", "cuales", "cualquier", "cuando", "cuanto", "cuatro", "cuenta", "cómo", "da", "dado", "dan", "dar", "de", "debe", "deben", "debido", "decir", "dejó", "del", "demás", "dentro", "desde", 
			"después", "dice", "dicen", "dicho", "dieron", "diferente", "diferentes", "dijeron", "dijo", "dio", "donde", "dos", "durante", "e", "ejemplo", "el", "ella", "ellas", "ello", "ellos", "embargo", "empleais", "emplean", 
			"emplear", "empleas", "empleo", "en", "encima", "encuentra", "entonces", "entre", "era", "erais", "eramos", "eran", "eras", "eres", "es", "esa", "esas", "ese", "eso", "esos", "esta", "estaba", "estabais", "estaban", 
			"estabas", "estad", "estada", "estadas", "estado", "estados", "estais", "estamos", "estan", "estando", "estar", "estaremos", "estará", "estarán", "estarás", "estaré", "estaréis", "estaría", "estaríais", "estaríamos", 
			"estarían", "estarías", "estas", "este", "estemos", "esto", "estos", "estoy", "estuve", "estuviera", "estuvierais", "estuvieran", "estuvieras", "estuvieron", "estuviese", "estuvieseis", "estuviesen", "estuvieses", "by", 
			"estuvimos", "estuviste", "estuvisteis", "estuviéramos", "estuviésemos", "estuvo", "está", "estábamos", "estáis", "están", "estás", "esté", "estéis", "estén", "estés", "ex", "existe", "existen", "explicó", "expresó", 
			"fin", "fue", "fuera", "fuerais", "fueran", "fueras", "fueron", "fuese", "fueseis", "fuesen", "fueses", "fui", "fuimos", "fuiste", "fuisteis", "fuéramos", "fuésemos", "gran", "grandes", "gueno", "ha", "haber", "habida", 
			"habidas", "habido", "habidos", "habiendo", "habremos", "habrá", "habrán", "habrás", "habré", "habréis", "habría", "habríais", "habríamos", "habrían", "habrías", "habéis", "había", "habíais", "habíamos", "habían", "habías", 
			"hace", "haceis", "hacemos", "hacen", "hacer", "hacerlo", "haces", "hacia", "haciendo", "hago", "han", "has", "hasta", "hay", "haya", "hayamos", "hayan", "hayas", "hayáis", "he", "hecho", "hemos", "hicieron", "hizo", "hoy", 
			"hube", "hubiera", "hubierais", "hubieran", "hubieras", "hubieron", "hubiese", "hubieseis", "hubiesen", "hubieses", "hubimos", "hubiste", "hubisteis", "hubiéramos", "hubiésemos", "hubo", "igual", "incluso", "indicó", 
			"informó", "intenta", "intentais", "intentamos", "intentan", "intentar", "intentas", "intento", "ir", "junto", "la", "lado", "largo", "las", "le", "les", "llegó", "lleva", "llevar", "lo", "los", "luego", "lugar", "manera", 
			"manifestó", "mayor", "me", "mediante", "mejor", "mencionó", "menos", "mi", "mientras", "mio", "mis", "misma", "mismas", "mismo", "mismos", "modo", "momento", "mucha", "muchas", "mucho", "muchos", "muy", "más", "mí", "mía", 
			"mías", "mío", "míos", "nada", "nadie", "ni", "ninguna", "ningunas", "ninguno", "ningunos", "ningún", "no", "nos", "nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nueva", "nuevas", "nuevo", "nuevos", 
			"nunca", "o", "ocho", "os", "otra", "otras", "otro", "otros", "para", "parece", "parte", "partir", "pasada", "pasado", "pero", "pesar", "poca", "pocas", "poco", "pocos", "podeis", "podemos", "poder", "podria", "podriais", 
			"podriamos", "podrian", "podrias", "podrá", "podrán", "podría", "podrían", "poner", "por", "por qué", "porque", "posible", "primer", "primera", "primero", "primeros", "principalmente", "propia", "propias", "propio", "propios", 
			"próximo", "próximos", "pudo", "pueda", "puede", "pueden", "puedo", "pues", "que", "quedó", "queremos", "quien", "quienes", "quiere", "quién", "qué", "realizado", "realizar", "realizó", "respecto", "sabe", "sabeis", "sabemos", 
			"saben", "saber", "sabes", "se", "sea", "seamos", "sean", "seas", "segunda", "segundo", "según", "seis", "ser", "seremos", "será", "serán", "serás", "seré", "seréis", "sería", "seríais", "seríamos", "serían", "serías", "seáis", 
			"señaló", "si", "sido", "siempre", "siendo", "siete", "sigue", "siguiente", "sin", "sino", "sobre", "sois", "sola", "solamente", "solas", "solo", "solos", "somos", "son", "soy", "su", "sus", "suya", "suyas", "suyo", "suyos", 
			"sí", "sólo", "tal", "también", "tampoco", "tan", "tanto", "te", "tendremos", "tendrá", "tendrán", "tendrás", "tendré", "tendréis", "tendría", "tendríais", "tendríamos", "tendrían", "tendrías", "tened", "teneis", "tenemos", 
			"tener", "tenga", "tengamos", "tengan", "tengas", "tengo", "tengáis", "tenida", "tenidas", "tenido", "tenidos", "teniendo", "tenéis", "tenía", "teníais", "teníamos", "tenían", "tenías", "tercera", "ti", "tiempo", "tiene", 
			"tienen", "tienes", "toda", "todas", "todavía", "todo", "todos", "total", "trabaja", "trabajais", "trabajamos", "trabajan", "trabajar", "trabajas", "trabajo", "tras", "trata", "través", "tres", "tu", "tus", "tuve", "tuviera", 
			"tuvierais", "tuvieran", "tuvieras", "tuvieron", "tuviese", "tuvieseis", "tuviesen", "tuvieses", "tuvimos", "tuviste", "tuvisteis", "tuviéramos", "tuviésemos", "tuvo", "tuya", "tuyas", "tuyo", "tuyos", "tú", "ultimo", "un", 
			"una", "unas", "uno", "unos", "usa", "usais", "usamos", "usan", "usar", "usas", "uso", "usted", "va", "vais", "valor", "vamos", "van", "varias", "varios", "vaya", "veces", "ver", "verdad", "verdadera", "verdadero", "vez", 
			"vosotras", "vosotros", "voy", "vuestra", "vuestras", "vuestro", "vuestros", "y", "ya", "yo", "él", "éramos", "ésta", "éstas", "éste", "éstos", "última", "últimas", "último", "últimos" 
	};
	
	private final DictionaryRepository dictionaryRepository;
	
	public DictionaryLoadServiceLogic(DictionaryRepository dictionaryRepository) {
		this.dictionaryRepository = dictionaryRepository;
	}

	@Override
	public void loadTerms(TouristicPoint point) {
		// Get title & description from point
		StringBuilder text = new StringBuilder(point.getName());
		text.append(' ');
		text.append(parseHtml(point.getDescription()));
		
		// Create stemmer for the terms to be stored (Snowball stemmer)
		SnowballStemmer snowballStemmer = new SnowballStemmer(ALGORITHM.SPANISH);			
		
		// Tokenize string, lowercase tokens, filter symbols/stop words & stemming
		List<String> resultTerms = analyze(text.toString(), new StandardAnalyzer(StopFilter.makeStopSet(STOP_WORDS))); 
		// Remove numbers & words of 1 character length
		resultTerms = resultTerms.stream()
				.filter(term -> !term.matches("[0-9]+") && term.length() > 1)
				.map(term -> snowballStemmer.stem(term).toString())
				.collect(Collectors.toList());
		
		System.out.println("Done!");
	}
	
	/**
	 * Tokenize & filter
	 * 
	 * @param text Text to tokenize and analyze
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
