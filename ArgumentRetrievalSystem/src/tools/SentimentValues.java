package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class SentimentValues {

    static HashMap<String, Integer> sentimentWordMap = new HashMap<String, Integer>();

    static {
        fillList();
    }

    /**
     * read the emotional words into a list
     */
    public static void fillList() {
        String csvFile =
                Paths.get("ArgumentRetrievalSystem", "corpus_files", "sentimentwords.csv").toAbsolutePath().normalize().toString();
        String line = "";

        try ( BufferedReader br = new BufferedReader(new FileReader(csvFile)) ) {

            while ( (line = br.readLine()) != null ) {
                String[] temp = line.split(";");
                try {
                    int value = Integer.parseInt(temp[ 1 ]);
                    sentimentWordMap.put(temp[ 0 ].replaceAll("\\*", ".*"), value);
                } catch ( NumberFormatException ex ) {
                    //skip
                }
            }

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
        final String[] docWords = docTerm.split("\\W+");
        int sum = 0;
        for ( String docWord : docWords ) {
            for ( String sentimentWord : sentimentWordMap.keySet() ) {
                if ( docWord.matches(sentimentWord) ) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public static int termSentimentWithoutPatternMatching(String term) {
        return sentimentWordMap.containsKey(term) ? 1 : 0;
    }

    public static int termCount(String docTerm) {
        return docTerm.split("\\W+").length;
    }

    public static double relativeSentiment(String docTerm) {
        return (double) absoluteSentiment(docTerm) / termCount(docTerm);
    }

}

