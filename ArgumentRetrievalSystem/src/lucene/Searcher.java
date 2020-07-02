package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public Searcher(String indexPath) throws IOException {

        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(reader);
        indexSearcher.setSimilarity(new SentimentSimilarity());
        // TODO: query muss angepasst werden, sodass das Field LuceneConstants.SENTIMENT als Faktor in die Query einfließt
        queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());

    }

    public TopDocs search(String searchQuery) throws ParseException, IOException {
        query = queryParser.parse(searchQuery);
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void searchAndExplain(String searchQuery) throws ParseException, IOException {
        TopDocs hits = search(searchQuery);

        for ( int i = 0; i < hits.totalHits.value && i < hits.scoreDocs.length; i++ ) {
            final ScoreDoc scoreDoc = hits.scoreDocs[ i ];
            System.out.println(getDocument(scoreDoc));

            System.out.println(indexSearcher.explain(query, scoreDoc.doc) + "\n\n\n");

        }

    }
}
