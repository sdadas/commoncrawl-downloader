package pl.sdadas.commoncrawl.process;

import cz.jirutka.unidecode.Unidecode;
import org.apache.commons.lang3.StringUtils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class WetDocumentCleaner implements Function<WetDocument, String> {

    private static final Unidecode UNIDECODE = Unidecode.toAscii();

    private static final Pattern URL_PATTERN = createUrlPattern();

    private static Pattern createUrlPattern() {
        String regex = "(?:^|[\\W])((ht|f)tp(s?)://|www\\.)(([\\w\\-]+\\.){1,}?" +
                "([\\w\\-.~]+/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    public String apply(WetDocument doc) {
        String text = doc.getBuilder().toString();
        BreakIterator iter = BreakIterator.getSentenceInstance(doc.getLocale());
        iter.setText(text);
        List<String> sentences = new ArrayList<>();
        int start = iter.first();
        for(int end=iter.next(); end != BreakIterator.DONE; start=end, end=iter.next()) {
            sentences.add(text.substring(start, end));
        }
        text = sentences.stream().filter(this::filterSentence).collect(Collectors.joining("\t"));
        text = StringUtils.strip(text.replaceAll("[\r\n\t]+", "%%TAB%%"));
        text = text.replaceAll("\\p{C}", "");
        text = text.replace("%%TAB%%", "\t") + "\n";
        return text;
    }

    private boolean filterSentence(String sentence) {
        String unidecoded = UNIDECODE.decode(sentence);
        int alphanum = 0;
        int alpha = 0;
        int whitespace = 0;
        int other = 0;
        int length = unidecoded.length();
        for (int offset = 0; offset < length;) {
            int codepoint = unidecoded.codePointAt(offset);
            if(Character.isLetterOrDigit(codepoint)) {
                alphanum++;
                if(Character.isAlphabetic(codepoint)) alpha++;
            }
            else if(Character.isWhitespace(codepoint)) whitespace++;
            else other++;
            offset += Character.charCount(codepoint);
        }
        double textFraction = ((double) alphanum + (double) whitespace) / (double) length;
        double alphaFraction = (double) alpha / (double) length;
        double otherPerChar = (double) length / (double) other;
        if(textFraction < 0.7 || alphaFraction < 0.1) return false;
        if(length > 20 && otherPerChar < 5) return false;
        if(containsUrl(unidecoded)) return false;
        return true;
    }

    private boolean containsUrl(String sentence) {
        Matcher matcher = URL_PATTERN.matcher(sentence);
        return matcher.find();
    }
}
