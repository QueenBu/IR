package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;

public class SentimentValues {

    static HashMap<String, Integer> wordlist = new HashMap<String, Integer>();

    static {
        fillList();
    }

    /**
     * read the emotional words into a list
     */
    public static void fillList(){
        String csvFile = Paths.get("ArgumentRetrievalSystem", "corpus_files", "sentimentwords.csv").toAbsolutePath().normalize().toString();
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String [] temp = line.split(";");
                try {
                    int value = Integer.parseInt(temp[ 1 ]);
                    wordlist.put(temp[0], value);
                } catch (NumberFormatException ex) {
                    //skip
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * determine if a word is emotional
     * @param term a String that needs emotional evaluation
     * @return 1 for an emotional word 0 otherwise
     */
    public static int termSentiment(String term) {
        return wordlist.containsKey(term) ? 1 : 0;
    }

}

