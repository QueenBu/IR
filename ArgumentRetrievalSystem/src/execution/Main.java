package execution;

public class Main {

    public static String getTestFile(String filename) {
        return "ArgumentRetrievalSystem/corpus_files/" + filename;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        HttpService hs = new HttpService();
        Ranker.rank(hs.getArguments("cookies")).forEach(System.out::println);
         */
        if(args.length != 2){

        }
        Manager manager = new Manager();
        manager.makeIndex(getTestFile("parliamentary.json"));
        manager.start();
    }

}
