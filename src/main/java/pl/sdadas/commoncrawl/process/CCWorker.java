package pl.sdadas.commoncrawl.process;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.commoncrawl.Params;
import pl.sdadas.commoncrawl.tools.FastTextLangDetector;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Sławomir Dadas
 */
public class CCWorker implements Callable<Void> {

    private final static Logger LOG = LoggerFactory.getLogger(CCWorker.class);

    private final String path;

    private final MultiLangCounter counter;

    private final FastTextLangDetector ld;

    private final Params params;

    public CCWorker(String path, Params params, MultiLangCounter counter, FastTextLangDetector ld) {
        this.path = path;
        this.counter = counter;
        this.ld = ld;
        this.params = params;
    }

    @Override
    public Void call() throws Exception {
        String name = StringUtils.substringAfterLast(path, "/");
        File markerDir = new File(params.getFilteredDir(), "markers");
        File markerFile = new File(markerDir, FilenameUtils.removeExtension(name) + ".txt");
        if(markerFile.exists()) {
            LOG.warn("File {} already exists, skipping processing", markerFile.getName());
            return null;
        }
        LOG.info("Startring part {}", path);
        CCDownloader downloader = new CCDownloader(path, params.getTempDir());
        String result = downloader.call();
        CCWetReader reader = new CCWetReader(new File(result), params, counter, ld);
        reader.call();
        markerDir.mkdirs();
        markerFile.createNewFile();
        LOG.info("Finished part {}", path);
        return null;
    }
}
