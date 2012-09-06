package eu.vytenis.ereaders.dictionaries.words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Word {
	private String original;
	private String translation;
	
	private Set<String> relatedOriginals = new HashSet<String>();
	
	public Word(String original, String translation) {
		this.original = original;
		this.translation = translation;
	}
	
	public String getOriginal() {
		return original;
	}
	public String getTranslation() {
		return translation;
	}
	
	public void setTranslation(String translation) {
		this.translation = translation;
	}
	
	public Set<String> getRelatedOriginals() {
		return relatedOriginals;
	}
	
	public String toTsv() {
		if (original.contains("\t") || translation.contains("\t")) {
			throw new IllegalStateException();
		}
		return original + "\t" + translation;
	}
	
	public static List<String> getTsvLines(Iterable<Word> words) {
		List<String> r = new ArrayList<String>();
		for (Word w : words) {
			r.add(w.toTsv());
		}
		return r;
	}
	
	public static Map<String, Word> toOriginalMap(Iterable<Word> words) {
		Map<String, Word> r = new HashMap<String, Word>();
		for (Word w : words) {
			r.put(w.getOriginal(), w);
		}
		return r;
	}
	
	public static void updateTranslationWithRelatedWords(Iterable<Word> words) {
		Map<String, Word> wordsByOriginal = toOriginalMap(words);
		Map<String, String> updatedByOriginal = new HashMap<String, String>();
		for (Word w : words) {
			for (String ro : w.getRelatedOriginals()) {
				Word rw = wordsByOriginal.get(ro);
				if (rw != null) {
					String translation = (updatedByOriginal.get(w.getOriginal()) != null
							? updatedByOriginal.get(w.getOriginal())
							:  w.getTranslation()) + " " + (rw.getOriginal() + ": " + rw.getTranslation());
					updatedByOriginal.put(w.getOriginal(), translation);
					//System.out.println(w.getOriginal() + ": " + ro + " updated");
				} else {
					System.out.println(w.getOriginal() + ": " + ro + " not found");
				}
			}
		}
		for (Map.Entry<String, String> e : updatedByOriginal.entrySet()) {
			wordsByOriginal.get(e.getKey()).setTranslation(e.getValue());
			
		}
		
		
	}

}
