package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

public class SentimentValues {

    static Pattern sentimentWordsPattern;

    static {
        createRegEx();
    }

    /**
     * read the emotional words into a list
     */
    public static void createRegEx() {
        String csvPath =
                Paths.get("ArgumentRetrievalSystem", "corpus_files", "sentimentwords.csv").toAbsolutePath().normalize().toString();
        StringBuilder sentimentWordsBuilder = new StringBuilder();
        //sentimentWordsBuilder.append("[");
        try ( BufferedReader br = new BufferedReader(new FileReader(csvPath)) ) {
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
     * determine if a word is emotional
     * <p>
     * searches for a RegEx match
     *
     * @param docTerm a String that needs emotional evaluation
     * @return 1 for an emotional word 0 otherwise
     */
    public static int absoluteSentiment(String docTerm) {
        // W+  =  at least one non-word character which mean all character not a-z,A-Z,0-9,_
        return Arrays.stream(docTerm.split("\\W+")).mapToInt((docWord) ->
                sentimentWordsPattern.matcher(docWord).matches() ? 1 : 0
        ).sum();
    }

    public static int termCount(String docTerm) {
        return docTerm.split("\\W+").length;
    }

    public static double relativeSentiment(String docTerm) {
        return (double) absoluteSentiment(docTerm) / termCount(docTerm);
    }

}

