package eu.vytenis.ereaders.dictionaries.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class IoUtils {

	public static void transfer(InputStream input, OutputStream output) throws IOException {
		int b;
		while ((b = input.read()) >= 0) {
			output.write(b);			
		}
		input.close();
		output.close();
	}
	
	public static void copy(File from, File to) throws IOException {
		if (!from.exists()) {
			throw new IllegalArgumentException();
		}
		if (from.isDirectory()) {
			throw new IllegalArgumentException();
		}
		
		IoUtils.transfer(new FileInputStream(from), new FileOutputStream(to));
		
	}
	
	public static void exec(String[] command, File dir) throws IOException {
		if (dir == null) {
			dir = new File(",");
		}
		long start = System.currentTimeMillis();
		Process p = Runtime.getRuntime().exec(command, new String[] {"DISPLAY=:0"}, dir);
		System.out.println(StringUtils.toString(" ", Arrays.asList(command)) + " (in " + dir.getAbsolutePath() + ")");
		try {
			int exitCode = p.waitFor();
			long end = System.currentTimeMillis();
			
			ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			IoUtils.transfer(p.getErrorStream(), errorStream);
			IoUtils.transfer(p.getInputStream(), outputStream);
			
			if (errorStream.toByteArray().length > 0) {
				//System.out.println("Errors:\n" + new String(errorStream.toByteArray()));
			}
			if (outputStream.toByteArray().length > 0) {
				//System.out.println("Output:\n" + new String(outputStream.toByteArray()));
			}			
			System.out.println("Exit code: " + exitCode + ", took " + (end - start) + "ms");
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}
	
	public static String changeFileExtension(String filename, String extension) {
		int idx = filename.lastIndexOf(".");
		if (idx < 0) {
			throw new IllegalArgumentException();
		}
		if (extension == null) {
			throw new IllegalArgumentException();
		}
		return filename.substring(0, idx + 1) + extension;
	}

}
