package pl.sdadas.commoncrawl.tools;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple tokenizer splitting sentences by dot and words by non alphanumeric characters.
 *
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
public class AlphanumericSplitter implements WordSplitter {

    @Override
    public List<String[]> split(String text) {
        if(StringUtils.isBlank(text)) return Collections.emptyList();
        StringBuilder builder = new StringBuilder();
        int length = text.length();
        List<String> results = new ArrayList<>();
        for (int offset = 0; offset < length;) {
            int codepoint = text.codePointAt(offset);
            if(Character.isLetterOrDigit(codepoint)) {
                builder.appendCodePoint(codepoint);
            } else {
                if(builder.length() > 0) results.add(builder.toString());
                if(!Character.isWhitespace(codepoint)) {
                    char[] chars = Character.toChars(codepoint);
                    results.add(new String(chars));
                }
                builder.setLength(0);
            }
            offset += Character.charCount(codepoint);
        }
        if(builder.length() > 0) {
            results.add(builder.toString());
        }
        return Collections.singletonList(results.toArray(new String[0]));
    }

    @Override
    public List<String> sentences(String text) {
        throw new NotImplementedException("AlphanumericSplitter doesn't support sentence level splitting");
    }
}
