package pl.sdadas.commoncrawl.process;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author Sławomir Dadas
 */
public class CCDownloader implements Callable<String> {

    private final static Logger LOG = LoggerFactory.getLogger(CCDownloader.class);

    private final static String BASE_URL = "https://commoncrawl.s3.amazonaws.com/";

    private final String url;

    private final File dest;

    public CCDownloader(String path, File outputDir) {
        this.url = BASE_URL + StringUtils.removeStart(path, "/");
        this.dest = outputDir;
    }

    @Override
    public String call() throws Exception {
        String fileName = StringUtils.substringAfterLast(url, "/");
        File output = downloadFile(fileName);
        return output.getAbsolutePath();
    }

    private File downloadFile(String fileName) {
        try {
            File output = new File(dest, fileName);
            File temp = new File(dest, fileName + "_temp");
            if(output.exists()) {
                LOG.warn("File {} already exists, skipping download", fileName);
                return output;
            }
            LOG.info("Downloading {} to {}", url, output.getAbsolutePath());
            Connection.Response response = Jsoup.connect(url).ignoreContentType(true)
                    .maxBodySize(0).timeout(Integer.MAX_VALUE).execute();
            BufferedInputStream is = response.bodyStream();
            FileUtils.copyInputStreamToFile(is, temp);
            temp.renameTo(output);
            return output;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
