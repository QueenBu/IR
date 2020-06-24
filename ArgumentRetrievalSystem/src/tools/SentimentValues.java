package tools;

import java.util.Random;

public class SentimentValues {

    public static int termSentiment(String term) {
        return new Random().nextInt(2);
    }

}
