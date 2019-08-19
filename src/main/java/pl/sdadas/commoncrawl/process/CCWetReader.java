package pl.sdadas.commoncrawl.process;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.commoncrawl.tools.FastTextLangDetector;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

/**
 * @author Sławomir Dadas
 */
public class CCWetReader implements Callable<String> {

    private final static Logger LOG = LoggerFactory.getLogger(CCWetReader.class);

    private final File file;

    private final File destDir;

    private final MultiLangCounter counter;

    private final FastTextLangDetector ld;

    public CCWetReader(File file, File destDir, MultiLangCounter counter, FastTextLangDetector ld) {
        this.file = file;
        this.destDir = destDir;
        this.counter = counter;
        this.ld = ld;
    }

    @Override
    public String call() {
        String outputFileName = FilenameUtils.removeExtension(file.getName()) + ".txt";
        String tempFileName = outputFileName + "_temp";
        File englishDir = new File(destDir, "en");
        File englishFile = new File(englishDir, outputFileName);
        if(englishFile.exists()) {
            LOG.warn("File {} already exists, skipping processing", englishFile.getName());
            return englishFile.getAbsolutePath();
        }

        WetDocumentCleaner cleaner = new WetDocumentCleaner();
        Map<String, File> files;
        try(BufferedReader reader = createReader(); MultiLangWriter writer = new MultiLangWriter(destDir, tempFileName)) {
            LineIterator iter = IOUtils.lineIterator(reader);
            if(iter.hasNext()) {
                String line = StringUtils.strip(iter.next());
                if(!line.equalsIgnoreCase("WARC/1.0"))  {
                    throw new IllegalArgumentException("Invalid warc format in file " + file.getName());
                }
                CCWetDocumentReader documentReader = new CCWetDocumentReader(iter);
                while(documentReader.hasNext()) {
                    WetDocument doc = documentReader.next();
                    String text = doc.getBuilder().toString();
                    String lang = ld.predict(text);
                    doc.setLang(lang);
                    text = cleaner.apply(doc);
                    String uri = doc.getMeta().get(WetDocument.TARGET_URI);
                    if(StringUtils.isNotBlank(uri) && StringUtils.isNotBlank(lang) && !counter.aboveThreshold(lang)) {
                        //writer.println("__URL: " + uri);
                        counter.add(lang, text.length());
                        writer.write(lang, text);
                    }
                }
            }
            files = writer.getFiles();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex);
        }

        for (File file : files.values()) {
            File renamed = new File(file.getParent(), file.getName().replace("_temp", ""));
            file.renameTo(renamed);
        }
        file.delete();
        return englishFile.getAbsolutePath();
    }

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
    }

    private class CCWetDocumentReader implements Iterator<WetDocument> {

        private final LineIterator iter;

        public CCWetDocumentReader(LineIterator iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public WetDocument next() {
            WetDocument result = new WetDocument();
            boolean meta = true;
            while(iter.hasNext()) {
                String line = StringUtils.strip(iter.next());
                if(line.equalsIgnoreCase("WARC/1.0")) {
                    return result;
                }
                if(meta && StringUtils.isBlank(line)) {
                    meta = false;
                } else if(meta) {
                    String[] split = StringUtils.split(line, ":", 2);
                    result.getMeta().put(split[0], StringUtils.strip(split[1]));
                } else {
                    if(StringUtils.length(line) > 50) {
                        result.getBuilder().append(line).append('\n');
                    }
                }
            }
            return result;
        }
    }

}
