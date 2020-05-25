package argumentretrivalsystem;

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
        hs.getArguments("cookies");
        
    }

}
