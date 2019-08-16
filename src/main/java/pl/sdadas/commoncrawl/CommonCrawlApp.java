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
        if(args.length < 2) {
            LOG.info("usage: java -jar commoncrawl-downloader.jar [directory:str] [num_threads:int] [lang_byte_limit:int]");
            System.exit(0);
        }
        File baseDir = new File(args[0]);
        File wetPaths = new File(baseDir, "wet.paths");
        File tempDir = new File(baseDir, "temp");
        File filteredDir = new File(baseDir, "filtered");
        ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(args[1]));
        FileUtils.forceMkdir(filteredDir);
        LineIterator iter = FileUtils.lineIterator(wetPaths, "UTF-8");
        FastTextLangDetector ld = new FastTextLangDetector();
        long limit = args.length > 2 ? Long.parseLong(args[2]) : MultiLangCounter.MAX_SIZE;
        MultiLangCounter counter = new MultiLangCounter(filteredDir, limit);
        LOG.info(counter.state());
        while(iter.hasNext()) {
            String line = StringUtils.strip(iter.next());
            service.submit(new CCWorker(line, tempDir, filteredDir, counter, ld));
        }
        service.shutdown();
    }
}
