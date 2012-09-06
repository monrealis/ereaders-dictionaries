package eu.vytenis.ereaders.dictionaries;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import eu.vytenis.ereaders.dictionaries.utils.CollatorComparator;
import eu.vytenis.ereaders.dictionaries.utils.IoUtils;
import eu.vytenis.ereaders.dictionaries.utils.StringUtils;
import eu.vytenis.ereaders.dictionaries.words.Word;

public class TsvToMobiPipeline {
	
	private File createOutputDir() {
		String dirName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		File dir = new File("etc/output", dirName);
		if (!dir.mkdirs()) {
			throw new IllegalStateException("Could not create dir " + dir);
		}
		return dir;
	}
	
	private void createTsvFile(List<Word> words, File outputFile) throws IOException {
		checkExistsFile(outputFile, false);
		List<String> lines = Word.getTsvLines(words);
		Collections.sort(lines, new CollatorComparator(new Locale("lt")));
		String text = StringUtils.toString("\n", lines);
		//System.out.println(text);
		IoUtils.transfer(new ByteArrayInputStream(text.getBytes("UTF-8")), new FileOutputStream(outputFile));
		checkExistsFile(outputFile, true);
	}
	
	private void fixOpf(File input, File output, File dtd) throws IOException {		
		checkExistsFile(input, true);
		checkExistsFile(dtd, true);
		checkExistsFile(output, false);
		
		File dtdCopy = new File(input.getParentFile(), dtd.getName());
		checkExistsFile(dtdCopy, false);		
		IoUtils.copy(dtd, new File(input.getParentFile(), dtd.getName()));
		checkExistsFile(dtdCopy, true);
		
		DOMResult dr = new DOMResult();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new StreamSource(input), dr);
		} catch (Exception e) {
			throw new IOException(e);
		}
		Document doc = (Document) dr.getNode();
		doc.getElementsByTagNameNS("Dublin Core", "Identifier").item(0).setTextContent("Anglų lietuvių žodynas");
		doc.getElementsByTagNameNS("", "h2").item(0).setTextContent("Anglų lietuvių žodynas");
		//doc.getElementsByTagNameNS("", "DictionaryInLanguage").item(0).setTextContent("en-en");
		//doc.getElementsByTagNameNS("", "DictionaryOutLanguage").item(0).setTextContent("lt-lt");
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(output));
		} catch (Exception e) {
			throw new IOException(e);
		}
		checkExistsFile(output, true);

	}
	
	private void checkExistsFile(File file, boolean mustExist) {
		if (file.exists() != mustExist) {
			throw new IllegalArgumentException();
		}
		if (mustExist && file.isDirectory()) {
			throw new IllegalArgumentException();
		}
	}
	
	public void process(List<Word> words) throws IOException {
		File execTab2opf = new File("etc/tab2opf.py");
		File execMobigen = new File("etc/mobigen.exe");
		checkExistsFile(execTab2opf, true);
		checkExistsFile(execMobigen, true);
		
		File outputDir = createOutputDir();		
		
		File tsvFile = new File(outputDir, "out.tab");
		createTsvFile(words, tsvFile);
		
		File opfFile = new File(outputDir, IoUtils.changeFileExtension(tsvFile.getName(), "opf"));
		checkExistsFile(opfFile, false);
		IoUtils.exec(new String[] {execTab2opf.getAbsolutePath(), "-utf", tsvFile.getAbsolutePath()}, outputDir);
		checkExistsFile(opfFile, true);
		
		File realOpfFile = new File(outputDir, IoUtils.changeFileExtension(tsvFile.getName(), "fixed.opf"));
		File dtd = new File("etc/oeb1.ent");
		fixOpf(opfFile, realOpfFile, dtd);
		
		String relativeOpf = realOpfFile.getAbsolutePath().substring(execMobigen.getParentFile().getAbsolutePath().length() + 1);
		File mobiFile = new File(outputDir, IoUtils.changeFileExtension(realOpfFile.getName(), "mobi"));
		checkExistsFile(mobiFile, false);
		IoUtils.exec(new String[] {execMobigen.getAbsolutePath(), relativeOpf}, execMobigen.getParentFile().getAbsoluteFile());
		checkExistsFile(mobiFile, true);
		
		File finalMobiFile = new File(outputDir, "anglu-lietuviu.mobi");
		checkExistsFile(finalMobiFile, false);
		IoUtils.copy(mobiFile, finalMobiFile);
		checkExistsFile(finalMobiFile, true);
	}

}
