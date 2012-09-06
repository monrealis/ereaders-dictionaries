package eu.vytenis.ereaders.dictionaries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.vytenis.ereaders.dictionaries.files.DirsFilter;
import eu.vytenis.ereaders.dictionaries.html.EngLitExtractor;
import eu.vytenis.ereaders.dictionaries.html.HtmlParser;
import eu.vytenis.ereaders.dictionaries.words.Word;

public class Main {

	public static List<Word> parseWords(File baseDir) {
		if (!baseDir.exists()) {
			throw new IllegalArgumentException();
		}
		if (!baseDir.isDirectory()) {
			throw new IllegalArgumentException();
		}
		
		//System.out.println(Arrays.asList(base.listFiles(new WordsFilter(false))));
		File[] dirs = baseDir.listFiles(new DirsFilter(true));
		HtmlParser p = new HtmlParser();
		p.setExtractor(new EngLitExtractor());
		List<Word> words = new ArrayList<Word>();
		for (File dir : dirs) {
			try {
				Word w = p.parse(dir);
				if (w == null) {
					continue;
				}
				words.add(w);
			} catch (Exception e) {
				//System.out.println("Skipped: " + dir);
			}
		}
		Word.updateTranslationWithRelatedWords(words);
		return words;		
	}
	
	
	public static void main(String[] args) throws Exception {
		File baseDir = new File("../anglu-lietuviu");
		List<Word> words = parseWords(baseDir);		
		
		
		TsvToMobiPipeline pipeline = new TsvToMobiPipeline();
		pipeline.process(words);		
		
		System.err.println(words.size() + " words ");
		//System.out.println(Arrays.asList(base.listFiles(new WordsFilter(false))));

	}

}
