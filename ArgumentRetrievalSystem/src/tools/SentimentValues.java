package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SentimentValues {

    /**
     * ein kompiliertes Pattern, um fuer ein Dokument zu ueberpruefen, wie viele "sentiment words" es enthaelt
     */
    private static Pattern sentimentWordsPattern;

    static {
        createRegEx();
    }

    /**
     * initialisiert das Pattern
     */
    private static void createRegEx() {
        //String csvPath = Paths.get("sentimentwords.csv").toAbsolutePath().normalize().toString();
        StringBuilder sentimentWordsBuilder = new StringBuilder();
        //sentimentWordsBuilder.append("[");
        try ( BufferedReader br = new BufferedReader(new InputStreamReader(SentimentValues.class.getResource(
                "sentimentwords.csv").openStream())) ) {
            br.lines().forEach((line) ->
                    sentimentWordsBuilder
                            .append(line.split(";")[ 0 ].replaceAll("\\*", ".*"))
                            .append("|")
            );
            sentimentWordsBuilder.deleteCharAt(sentimentWordsBuilder.length() - 1);
            //sentimentWordsBuilder.append("]");
            sentimentWordsPattern = Pattern.compile(sentimentWordsBuilder.toString());
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * zaehlt die "sentiment words" in einem Dokument
     *
     * @param docTerms Inhalt des Dokuments
     * @return Anzahl der erkannten "sentiment words"
     */
    private static long absoluteSentiment(String[] docTerms) {
        return Arrays.stream(docTerms).parallel().filter((String docWord) -> sentimentWordsPattern.matcher(docWord).matches()).count();
    }

    /**
     * gibt einen relativen "sentiment Wert" fuer ein Dokument
     *
     * @param documentContent Inhalt des Dokuments
     * @return absolute Anzahl der "sentiment words" geteilt durch die Gesamtzahl der Woerter im Dokument
     */
    public static double relativeSentiment(String documentContent) {
        // W+  =  at least one non-word character which mean all character not a-z,A-Z,0-9,_
        String[] docTerms = documentContent.split("\\W+");
        return (double) absoluteSentiment(docTerms) / docTerms.length;
    }

}

