package execution;

import lucene.Indexer;
import lucene.LuceneConstants;
import lucene.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import tools.CLIHandler;

import java.io.IOException;

public class Manager {
    private CLIHandler cli;
    private String indexPath = "\\index";
    //private HttpService httpService;

    public void makeIndex(String filepath){
        Indexer indexer = new Indexer(indexPath, filepath);
        indexer.createIndex();
    }
    public void search(String searchQuery) throws IOException, ParseException {
         Searcher searcher = new Searcher(indexPath);
         TopDocs hits = searcher.search(searchQuery);
         System.out.println(hits.totalHits + " documents found.");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("Text: " + doc.get(LuceneConstants.CONTENTS) + "\n Stance: " +doc.get(LuceneConstants.STANCE));
            }

    }


    public void start() {
        cli = new CLIHandler();
        //httpService = new HttpService();

        while ( true ) {
            String query = cli.readUserInput("Please enter the phrase to be searched for! (empty input to cancel)");
            if (query.isEmpty()){
                break;
            }
            try {
                search(query);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

/*

            List<Argument> rankedArguments = Ranker.rank(httpService.getArguments(query));
            for ( int index = 0; index < rankedArguments.size() && index < 10; index++ ) {
                cli.writeToCL(rankedArguments.get(index));
            }


 */
        }
    }

}
