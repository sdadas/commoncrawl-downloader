package pl.sdadas.commoncrawl;

import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sławomir Dadas
 */
public class Params implements Serializable {

    @Parameter(description = "Directory containing wet.paths file", required = true)
    private String dir;

    @Parameter(names = {"-l", "-languages"}, description = "Comma separated list of languages to include")
    private String languages;

    @Parameter(names = {"-t", "-threads"}, description = "Number of worker threads")
    private Integer threads = 1;

    @Parameter(names = {"-limit", "-limit-bytes"}, description = "Maximum number of bytes for a single language corpus")
    private Long sizeLimit = -1L;

    private Set<String> languagesSet;

    public File getDir() {
        return new File(dir);
    }

    public File getTempDir() {
        return new File(getDir(), "temp");
    }

    public File getFilteredDir() {
        return new File(getDir(), "filtered");
    }

    public Set<String> getLanguages() {
        if(this.languagesSet == null) {
            if (StringUtils.isNotBlank(this.languages)) {
                Iterable<String> iter = Splitter.on(',').trimResults().split(languages);
                this.languagesSet = Sets.newHashSet(iter);
            } else {
                this.languagesSet = new HashSet<>();
            }
        }
        return this.languagesSet;
    }

    public Integer getThreads() {
        return threads;
    }

    public Long getSizeLimit() {
        return sizeLimit;
    }
}
