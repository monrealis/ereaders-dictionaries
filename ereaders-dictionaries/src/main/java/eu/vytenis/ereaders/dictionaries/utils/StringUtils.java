package eu.vytenis.ereaders.dictionaries.utils;

public class StringUtils {

	public static String toString(String separator, Iterable<String> items) {
		StringBuilder b = new StringBuilder();
		boolean first = true;
		for (String item : items) {
			if (item == null) {
				continue;
			}
			if (!first) {
				b.append(separator);
			}
			b.append(item);
			first = false;
		}
		return b.toString();		
		
	}

}
