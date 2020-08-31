package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static lucene.LuceneConstants.INDEX_PATH;

public class Searcher {

    IndexSearcher indexSearcher;
    /**
     * Parses search Strings to queries which search in {@link LuceneConstants#CONCLUSION} and
     * {@link LuceneConstants#PREMISES}
     */
    MultiFieldQueryParser qp2;
    /**
     * collects the tira compatible output lines in a list
     */
    List<String> output;

    public Searcher() throws IOException {

        indexSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH))));
        indexSearcher.setSimilarity(new TFIDFSimilarity());
        //indexSearcher.setSimilarity(new ClassicSimilarity());

        qp2 = new MultiFieldQueryParser(
                new String[]{ LuceneConstants.CONCLUSION, LuceneConstants.PREMISES },
                new StandardAnalyzer()
        );

        output = new ArrayList<>();
        output.add("topic_number Q0 arg_ids rank score method");

    }

    /**
     * standard searching function
     * parses the searchQuery String to a {@link org.apache.lucene.search.Query}, starts the search and handles the
     * command line feedback.
     *
     * @param searchQuery to be searched for
     * @return an object containing the search results
     * @throws ParseException if the query failed to parse
     * @throws IOException    if the index files couldnt be searched
     */
    public TopDocs search(String searchQuery) throws ParseException, IOException {
        Query query =
                FunctionScoreQuery.boostByValue(
                        FunctionScoreQuery.boostByValue(
                                // standard query parsing
                                qp2.parse(searchQuery),
                                // boosting by sentiment value
                                DoubleValuesSource.fromDoubleField(LuceneConstants.SENTIMENT)
                        ),
                        // boosting by length factor
                        DoubleValuesSource.fromDoubleField(LuceneConstants.LENGTH_FACTOR)
                );
        System.out.println(query);
        TopDocs hits = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
        System.out.println(hits.totalHits + " documents found.");
        for ( int i = 0; i < hits.scoreDocs.length; i++ ) {
            Document doc = getDocument(hits.scoreDocs[ i ]);
            System.out.println(i + 1 + ".\nText: " + doc.get(LuceneConstants.CONCLUSION) + "\nPremises: " + doc.get(LuceneConstants.PREMISES) + "\n" /*+ indexSearcher.explain(query, hits.scoreDocs[ i ].doc)*/);
        }
        return hits;
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    /**
     * standard search + tira compatible search output
     *
     * @param searchQuery to be searched for
     * @param topicNumber in the given topics.xml
     * @throws ParseException if thrown in {@link Searcher#search(String searchQuery)}
     * @throws IOException    if thrown in {@link Searcher#search(String searchQuery)}
     */
    public void searchAndAddToOutput(String searchQuery, String topicNumber) throws ParseException, IOException {
        TopDocs hits = search(searchQuery);

        for ( int i = 0; i < hits.totalHits.value && i < hits.scoreDocs.length; i++ ) {
            final ScoreDoc scoreDoc = hits.scoreDocs[ i ];
            output.add(topicNumber + " Q0 " + getDocument(scoreDoc).get(LuceneConstants.ID) + " " + i + " " + scoreDoc.score + " tf/idf+ln+sentiment+speedup");
            //System.out.println(getDocument(scoreDoc));
            //System.out.println(indexSearcher.explain(query, scoreDoc.doc) + "\n\n\n");

        }

    }

    public List<String> getOutput() {
        return output;
    }
}
