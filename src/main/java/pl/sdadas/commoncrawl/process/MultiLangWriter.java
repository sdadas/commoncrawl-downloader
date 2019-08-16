package pl.sdadas.commoncrawl.process;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
public class MultiLangWriter implements AutoCloseable {

    private final Map<String, PrintWriter> writers;

    private final Map<String, File> files;

    private final File basePath;

    private final String outputFileName;

    public MultiLangWriter(File basePath, String outputFileName) {
        this.basePath = basePath;
        this.outputFileName = outputFileName;
        this.writers = new HashMap<>();
        this.files = new HashMap<>();
    }

    public void write(String lang, String text) throws IOException {
        PrintWriter writer = getWriter(lang);
        writer.write(text);
    }

    private PrintWriter getWriter(String lang) throws IOException {
        PrintWriter res = writers.get(lang);
        if(res == null) {
            File langDir = new File(basePath, lang);
            langDir.mkdirs();
            File file = new File(langDir, outputFileName);
            res = new PrintWriter(file, "UTF-8");
            writers.put(lang, res);
            files.put(lang, file);
        }
        return res;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    @Override
    public void close() throws Exception {
        for (PrintWriter value : writers.values()) {
            try {
                value.close();
            } catch (Exception ex) {
                /* DO NOTHING */
            }
        }
    }
}
