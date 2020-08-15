package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Pattern;

public class SentimentValues {

    static Pattern sentimentWordsPattern;

    static {
        createRegEx();
    }

    public static void createRegEx() {
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

    private static int absoluteSentiment(String[] docTerms) {
        return Arrays.stream(docTerms).mapToInt((docWord) ->
                sentimentWordsPattern.matcher(docWord).matches() ? 1 : 0
        ).sum();
    }

    public static double relativeSentiment(String docTerm) {
        // W+  =  at least one non-word character which mean all character not a-z,A-Z,0-9,_
        String[] docTerms = docTerm.split("\\W+");
        return (double) absoluteSentiment(docTerms) / docTerms.length;
        //return 1;
    }

}

