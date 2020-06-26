package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class SentimentValues {

    static HashMap<String, Integer> wordlist = new HashMap<String, Integer>();


    /**
     * read the emotional words into a list
     */
    public static void fillList(){
        String csvFile = "C:\\Users\\SutonToch\\Documents\\Uni\\IR\\ArgumentRetrievalSystem\\corpus_files\\sentimentwords.csv";
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String [] temp = line.split(";");
                int value = Integer.parseInt(temp[1]);
                wordlist.put(temp[0], value);
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
        //i know we need to call this method somewhere else but i don't know where yet
        fillList();
        if(wordlist.containsKey(term)){
            return 1;
        } else{
            return 0;
        }
    }

}

