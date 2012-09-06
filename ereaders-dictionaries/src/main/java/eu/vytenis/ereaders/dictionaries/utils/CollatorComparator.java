package eu.vytenis.ereaders.dictionaries.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class CollatorComparator implements Comparator<String> {
	private Collator collator;
	public CollatorComparator(Locale locale) {
		collator = Collator.getInstance(locale);
	}

	public int compare(String o1, String o2) {
		if (o1 != null && o2 != null) {
			return collator.compare(o1, o2);
		} else if (o1 == null && o2 == null) {
			return 0;
		} else {
			return o1 == null ? -1 : 1;
		}
	}
	
}