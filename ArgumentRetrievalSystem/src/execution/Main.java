package execution;

import tools.HttpService;
import ARSystem.Ranker;

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
        manager.start();
    }

}
