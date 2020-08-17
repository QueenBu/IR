package execution;

import tools.CLIHandler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static String inputDirectory;
    public static String outputDirectory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws URISyntaxException {
        /*
        HttpService hs = new HttpService();
        Ranker.rank(hs.getArguments("cookies")).forEach(System.out::println);
         */
        if ( args.length == 2 ) {
            inputDirectory = args[ 0 ];
            outputDirectory = args[ 1 ];
        } else {
            CLIHandler cli = new CLIHandler();
            String input = cli.readUserInput("for execution via jar enter something, empty for execution via IDE");
            inputDirectory = !input.equals("") ?
                    new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() :
                    "ArgumentRetrievalSystem/corpus_files/";
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
