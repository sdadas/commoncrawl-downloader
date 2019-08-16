package pl.sdadas.commoncrawl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sdadas.commoncrawl.process.CCWorker;
import pl.sdadas.commoncrawl.process.MultiLangCounter;
import pl.sdadas.commoncrawl.tools.FastTextLangDetector;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Sławomir Dadas
 */
public class CommonCrawlApp {

    private final static Logger LOG = LoggerFactory.getLogger(CommonCrawlApp.class);


    public static void main(String[] args) throws Exception {
        File baseDir = new File(args[0]);
        File wetPaths = new File(baseDir, "wet.paths");
        File tempDir = new File(baseDir, "temp");
        File filteredDir = new File(baseDir, "filtered");
        ExecutorService service = Executors.newFixedThreadPool(4);
        FileUtils.forceMkdir(filteredDir);
        LineIterator iter = FileUtils.lineIterator(wetPaths, "UTF-8");
        FastTextLangDetector ld = new FastTextLangDetector();
        long limit = args.length > 1 ? Long.parseLong(args[1]) : MultiLangCounter.MAX_SIZE;
        MultiLangCounter counter = new MultiLangCounter(filteredDir, limit);
        LOG.info(counter.state());
        while(iter.hasNext()) {
            String line = StringUtils.strip(iter.next());
            service.submit(new CCWorker(line, tempDir, filteredDir, counter, ld));
        }
        service.shutdown();
    }
}
