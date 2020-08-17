package execution;

import tools.CLIHandler;

import java.io.*;
import java.net.URISyntaxException;

public class Main {

    public static String inputDirectory;
    public static String outputDirectory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws URISyntaxException, FileNotFoundException {
        inputDirectory =
                new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        outputDirectory = inputDirectory;
        try {
            Manager manager = new Manager();
            manager.makeIndex();
            manager.readTopics();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
