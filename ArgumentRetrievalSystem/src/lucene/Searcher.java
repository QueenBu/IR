package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    IndexSearcher indexSearcher;
    MultiFieldQueryParser qp2;
    List<String> output;

    public static void main(String[] args0) {

        try {
            Searcher s = new Searcher("\\index");
            System.out.println(s.output.get(0));
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public Searcher(String indexPath) throws IOException {

        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(reader);
        indexSearcher.setSimilarity(new TFIDFSimilarity());
        //indexSearcher.setSimilarity(new ClassicSimilarity());

        qp2 = new MultiFieldQueryParser(new String[]{ LuceneConstants.CONCLUSION, LuceneConstants.PREMISES },
                new StandardAnalyzer());

        output = new ArrayList<>();
        output.add("topic_number Q0 arg_ids rank score method");

    }

    public TopDocs search(String searchQuery) throws ParseException, IOException {
        Query query =
                FunctionScoreQuery.boostByValue(
                        FunctionScoreQuery.boostByValue(
                                qp2.parse(searchQuery),
                                DoubleValuesSource.fromDoubleField(LuceneConstants.SENTIMENT)
                        ),
                        DoubleValuesSource.fromDoubleField(LuceneConstants.LENGTH_FACTOR)
                );
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void searchAndWriteToOutput(String searchQuery, int topicNumber) throws ParseException, IOException {
        TopDocs hits = search(searchQuery);

        for ( int i = 0; i < hits.totalHits.value && i < hits.scoreDocs.length; i++ ) {
            final ScoreDoc scoreDoc = hits.scoreDocs[ i ];
            output.add(topicNumber + " Q0 " + getDocument(scoreDoc).get(LuceneConstants.ID) + " " + i + " " + scoreDoc.score + " tf/idf+doc_length_ln");
            //System.out.println(getDocument(scoreDoc));
            //System.out.println(indexSearcher.explain(query, scoreDoc.doc) + "\n\n\n");

        }

    }

    public List<String> getOutput() {
        return output;
    }
}
