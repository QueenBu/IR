package execution;

import java.io.IOException;

public class Main {

    public static String inputDirectory;
    public static String outputDirectory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        HttpService hs = new HttpService();
        Ranker.rank(hs.getArguments("cookies")).forEach(System.out::println);
         */
        if ( args.length == 2 ) {
            inputDirectory = args[ 0 ];
            outputDirectory = args[ 1 ];
        } else {
            inputDirectory = "ArgumentRetrievalSystem/corpus_files/";
            outputDirectory = "ArgumentRetrievalSystem/output_files/";
        }
        try {
            Manager manager = new Manager();
            manager.makeIndex();
            manager.start();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
