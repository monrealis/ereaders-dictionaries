package eu.vytenis.ereaders.dictionaries.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import eu.vytenis.ereaders.dictionaries.words.Word;

public class HtmlParser {
	private RelatedWordsExtractor extractor;
	
	public void setExtractor(RelatedWordsExtractor extractor) {
		this.extractor = extractor;
	}
	
	public RelatedWordsExtractor getExtractor() {
		return extractor;
	}

	public Word parse(final File dir) throws IOException {
		File[] files = dir.listFiles(new FilenameFilter() {
			
			public boolean accept(File file, String name) {
				return name.matches("index.*html");
			}
		});
		if (files == null || files.length == 0) {
			//System.out.println("Skipped " + dir);
			return null;			
		}
		File htmlFile = files[files.length - 1];
		FileInputStream fis = new FileInputStream(htmlFile);
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		fis.close();
		String text = new String(bytes);
		

		Document doc = Jsoup.parse(text);
		Elements keys = doc.select("h2");
		Elements values = doc.select("h2+p");
		Element key = keys.iterator().next();
		Element value = values.iterator().next();
		
		String original = key.text();
		String translation = value.text();

		Word w = new Word(original, translation);
		if (extractor != null) {
			Set<String> related = extractor.extract(w);
			w.getRelatedOriginals().addAll(related);
		}
		return w;
	}

}
