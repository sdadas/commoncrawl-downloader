package pl.sdadas.commoncrawl.process;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.commoncrawl.tools.FastTextLangDetector;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Sławomir Dadas
 */
public class CCWorker implements Callable<Void> {

    private final static Logger LOG = LoggerFactory.getLogger(CCWorker.class);

    private String path;

    private File tempDir;

    private File outputDir;

    private MultiLangCounter counter;

    private FastTextLangDetector ld;

    public CCWorker(String path, File tempDir, File outputDir, MultiLangCounter counter, FastTextLangDetector ld) {
        this.path = path;
        this.tempDir = tempDir;
        this.outputDir = outputDir;
        this.counter = counter;
        this.ld = ld;
    }

    @Override
    public Void call() throws Exception {
        String name = StringUtils.substringAfterLast(path, "/");
        File englishDir = new File(outputDir, "en");
        File englishFile = new File(englishDir, FilenameUtils.removeExtension(name) + ".txt");
        if(englishFile.exists()) {
            LOG.warn("File {} already exists, skipping processing", englishFile.getName());
            return null;
        }
        LOG.info("Startring part {}", path);
        CCDownloader downloader = new CCDownloader(path, tempDir);
        String result = downloader.call();
        CCWetReader reader = new CCWetReader(new File(result), outputDir, counter, ld);
        reader.call();
        LOG.info("Finished part {}", path);
        return null;
    }
}
