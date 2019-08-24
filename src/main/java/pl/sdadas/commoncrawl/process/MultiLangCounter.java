package pl.sdadas.commoncrawl.process;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
public class MultiLangCounter {

    public final static long MAX_SIZE = 10073741824L; // 10 GB

    private Map<String, AtomicLong> counts;

    private final long limit;

    public MultiLangCounter(File outputDir) {
        this(outputDir, MAX_SIZE);
    }

    public MultiLangCounter(File outputDir, long limit) {
        this.counts = new ConcurrentHashMap<>();
        this.limit = limit;
        initStartingSizes(outputDir);
    }

    private void initStartingSizes(File outputDir) {
        File[] files = outputDir.listFiles();
        if(files == null) return;
        for (File dir : files) {
            if(!dir.isDirectory() || !StringUtils.isAlphanumeric(dir.getName())) continue;
            long size = FileUtils.sizeOfDirectory(dir);
            add(dir.getName(), size);
        }
    }

    public void add(String lang, long length) {
        count(lang).addAndGet(length);
    }

    public boolean aboveThreshold(String lang) {
        long val = count(lang).get();
        return limit >= 0 && val > limit;
    }

    private synchronized AtomicLong count(String lang) {
        AtomicLong res = counts.get(lang);
        if(res == null) {
            res = new AtomicLong();
            counts.put(lang, res);
        }
        return res;
    }

    public String state() {
        return StringUtils.join(counts);
    }
}
