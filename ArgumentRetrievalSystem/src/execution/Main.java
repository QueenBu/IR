package execution;

import java.nio.file.Paths;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        HttpService hs = new HttpService();
        Ranker.rank(hs.getArguments("cookies")).forEach(System.out::println);
         */
        Manager manager = new Manager();
        manager.makeIndex(Paths.get("ArgumentRetrievalSystem", "corpus_files", "parliamentary.json").toAbsolutePath().normalize().toString());
        manager.start();
    }

}
