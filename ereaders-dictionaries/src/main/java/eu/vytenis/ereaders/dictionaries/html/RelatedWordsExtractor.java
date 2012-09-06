package eu.vytenis.ereaders.dictionaries.html;

import java.util.Set;

import eu.vytenis.ereaders.dictionaries.words.Word;

public interface RelatedWordsExtractor {
	
	Set<String> extract(Word word);

}
