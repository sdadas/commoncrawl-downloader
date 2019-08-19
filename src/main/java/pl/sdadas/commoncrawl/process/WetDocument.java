package pl.sdadas.commoncrawl.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sławomir Dadas
 */
public class WetDocument implements Serializable {

    public final static String TARGET_URI = "WARC-Target-URI";

    public final static String CONTENT_TYPE = "Content-Type";

    public final static String CONTENT_LENGTH = "Content-Length";

    private Map<String, String> meta;

    private StringBuilder builder;

    private String lang;

    private Locale locale;

    public WetDocument() {
        this.meta = new HashMap<>();
        this.builder = new StringBuilder();
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(StringBuilder builder) {
        this.builder = builder;
    }

    public Locale getLocale() {
        return new Locale(lang);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
