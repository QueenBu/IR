package execution;

import java.io.IOException;

public class Main {

    /**
     * must contain the .json files and the topics.xml which must be structured like this:<p>
     * <topics><topic><num>[number]</num><title>[title]</title></topic></topics>
     */
    public static String inputDirectory;

    /**
     * run.txt will be written to outputDirectory
     */
    public static String outputDirectory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ( args.length == 2 ) { // directory paths are supplied via command line on program start
            inputDirectory = args[ 0 ];
            outputDirectory = args[ 1 ];
        } else { // directories are not supplied -> use the development structures
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
