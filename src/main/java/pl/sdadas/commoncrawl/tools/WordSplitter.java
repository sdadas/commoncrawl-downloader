package pl.sdadas.commoncrawl.tools;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
public interface WordSplitter {

    default String[] flatSplit(String text) {
        List<String[]> result = split(text);
        return result.stream().flatMap(Arrays::stream).toArray(String[]::new);
    }

    List<String[]> split(String text);

    List<String> sentences(String text);
}
