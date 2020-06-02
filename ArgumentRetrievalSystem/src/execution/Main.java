package execution;

import ARSystem.HttpService;
import ARSystem.Ranker;

/**
 *
 * @author Maximilian Schmidt
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HttpService hs = new HttpService();
        Ranker.rank(hs.getArguments("cookies")).forEach(System.out::println);
        
    }

}
