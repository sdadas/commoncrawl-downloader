package pl.sdadas.commoncrawl.tools;

import com.mayabot.mynlp.fasttext.FastText;
import com.mayabot.mynlp.fasttext.FloatStringPair;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sławomir Dadas
 */
public class FastTextLangDetector {

    private static final Logger LOG = LoggerFactory.getLogger(FastTextLangDetector.class);

    private final FastText model;

    private final WordSplitter splitter;

    public FastTextLangDetector() {
        this.model = loadModel();
        this.splitter = new AlphanumericSplitter();
    }

    private FastText loadModel() {
        File file = new File("lid.176.bin");
        if(!file.exists()) {
            try {
                downloadModel();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        try(InputStream is = new FileInputStream(file)) {
            return FastText.loadFasttextBinModel(is);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void downloadModel() throws IOException {
        String url = "https://dl.fbaipublicfiles.com/fasttext/supervised-models/lid.176.bin";
        LOG.info("Downloading FastText language detection model from {}", url);
        Connection.Response response = Jsoup.connect(url).ignoreContentType(true)
                .maxBodySize(0).timeout(Integer.MAX_VALUE).execute();
        BufferedInputStream is = response.bodyStream();
        File file = new File("lid.176.bin");
        FileUtils.copyInputStreamToFile(is, file);
    }

    public String predict(String text) {
        String[] words = this.splitter.flatSplit(text);
        List<FloatStringPair> predict = this.model.predict(Arrays.asList(words), 1);
        String label = predict.get(0).second;
        return label.replace("__label__", "");
    }
}
