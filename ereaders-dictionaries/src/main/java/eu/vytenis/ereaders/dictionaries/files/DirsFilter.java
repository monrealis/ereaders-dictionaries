package eu.vytenis.ereaders.dictionaries.files;

import java.io.File;
import java.io.FileFilter;

public class DirsFilter implements FileFilter {
	private boolean valid;

	public DirsFilter(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid(File file) {
		if (file.getName().equals(file.getName().toUpperCase())
				&& file.getName().length() == 1) {
			return false;
		}
		if (file.getName().contains("+") && file.getName().length() > 20) {
			return false;
		}
		
		if (file.getName().equals("nuorodos")) {
			return false;
		}
		return true;
	}

	public boolean accept(File pathname) {
		return isValid(pathname) ^ !valid;
	}

}
