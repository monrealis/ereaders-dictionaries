package eu.vytenis.ereaders.dictionaries.html;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.vytenis.ereaders.dictionaries.words.Word;

public class EngLitExtractor implements RelatedWordsExtractor {
	Pattern p = Pattern.compile(" pt iš ([a-zA-Z-]*)");
	
	public Set<String> extract(Word word) {
		Matcher m = p.matcher(word.getTranslation());
		Set<String> r = new HashSet<String>();
		while (m.find()) {
			String related = m.group(1);
			r.add(related);
			//System.out.println(word.getOriginal() + " " + related);
		}
		return r;
	}

}
