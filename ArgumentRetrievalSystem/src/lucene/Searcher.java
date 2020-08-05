package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;
    List<List<String>> output;

    public static void main(String[] args0){

        try {
            Searcher s = new Searcher("\\index");
            System.out.println(s.output.get(0).get(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Searcher(String indexPath) throws IOException {

        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(reader);
        indexSearcher.setSimilarity(new TFIDFSimilarity());
        //indexSearcher.setSimilarity(new ClassicSimilarity());
        queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
        output = new ArrayList<>();
        output.add(new ArrayList<>(Arrays.asList("topic_number", "Q0", "arg_ids", "rank", "score", "method")));

    }

    public TopDocs search(String searchQuery) throws ParseException, IOException {
        query = FunctionScoreQuery.boostByValue(queryParser.parse(searchQuery),
                DoubleValuesSource.fromDoubleField(LuceneConstants.SENTIMENT));
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void searchAndExplain(String searchQuery, int topicNumber) throws ParseException, IOException {
        TopDocs hits = search(searchQuery);

        for ( int i = 0; i < hits.totalHits.value && i < hits.scoreDocs.length; i++ ) {
            final ScoreDoc scoreDoc = hits.scoreDocs[ i ];
            output.add(new ArrayList<>(Arrays.asList(Integer.toString(topicNumber), "Q0", getDocument(scoreDoc).get(LuceneConstants.ID), Integer.toString(i), Float.toString(scoreDoc.score), "method")));
            //System.out.println(getDocument(scoreDoc));
            //System.out.println(indexSearcher.explain(query, scoreDoc.doc) + "\n\n\n");

        }

    }

    public List<List<String>> getOutput() {
        return output;
    }
}
