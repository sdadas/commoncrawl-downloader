package pl.sdadas.commoncrawl;

import com.beust.jcommander.JCommander;
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
        Params params = new Params();
        JCommander.newBuilder().addObject(params).build().parse(args);
        File wetPaths = new File(params.getDir(), "wet.paths");
        ExecutorService service = Executors.newFixedThreadPool(params.getThreads());
        FileUtils.forceMkdir(params.getFilteredDir());
        LineIterator iter = FileUtils.lineIterator(wetPaths, "UTF-8");
        FastTextLangDetector ld = new FastTextLangDetector();
        MultiLangCounter counter = new MultiLangCounter(params.getFilteredDir(), params.getSizeLimit());
        LOG.info(counter.state());
        while(iter.hasNext()) {
            String line = StringUtils.strip(iter.next());
            service.submit(new CCWorker(line, params, counter, ld));
        }
        service.shutdown();
    }
}
