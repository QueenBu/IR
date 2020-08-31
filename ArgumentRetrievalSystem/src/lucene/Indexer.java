package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tools.ArgumentsIterator;
import tools.JSONDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static execution.Main.inputDirectory;
import static lucene.LuceneConstants.INDEX_PATH;
import static tools.SentimentValues.relativeSentiment;


public class Indexer {

    private IndexWriter writer;

    public Indexer() {
    }

    /**
     * whole indexing flow, opening, reading, closing
     *
     * @throws IOException if input files cannot be opened
     */
    public void createIndex() throws IOException {
        openIndex();
        Files.walk(Paths.get(inputDirectory), 1).filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(".json")).forEach(filename -> {
            System.out.println(filename);
            addDocuments(filename);
        });
        finish();
    }

    /**
     * standard opening of lucene related indexing
     */
    private void openIndex() {
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(INDEX_PATH));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(new TFIDFSimilarity());
            //iwc.setSimilarity(new ClassicSimilarity());
            //rewrite index each time. for update use parameter:  IndexWriterConfig.OpenMode.CREATE_OR_APPEND
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(indexDirectory, iwc);
        } catch ( IOException ex ) {
            System.err.println("Can not open Index");
            ex.printStackTrace();
        }
    }

    /**
     * adds every document contained in the specified .JSON file to the index via iterating {@link ArgumentsIterator}
     *
     * @param filename where the .JSON file is located.
     */
    private void addDocuments(String filename) {
        try ( ArgumentsIterator ai = new ArgumentsIterator(filename) ) {
            while ( ai.hasNext() ) {
                JSONDocument jsonDoc = ai.next();
                Document document = new Document();

                String searchableString = jsonDoc.getPremTexts().stream().reduce(String::concat).orElse("");

                document.add(new StringField(LuceneConstants.ID, jsonDoc.getId(), StringField.Store.YES));

                document.add(new TextField(LuceneConstants.PREMISES, searchableString, Field.Store.YES));
                document.add(new TextField(LuceneConstants.CONCLUSION, jsonDoc.getConclusion(), TextField.Store.YES));
                if ( jsonDoc.getTopic() != null ) {
                    document.add(new TextField(LuceneConstants.TOPIC, jsonDoc.getTopic(), TextField.Store.YES));
                }
                if ( jsonDoc.getAutName() != null ) {
                    document.add(new StringField(LuceneConstants.AUTHORNAME, jsonDoc.getAutName(), StringField
                            .Store.YES));
                }

                document.add(new DoubleDocValuesField(
                        LuceneConstants.LENGTH_FACTOR,
                        // magic numbers are explained in the evaluation
                        Math.max(Math.log(searchableString.split("\\W+").length - 8), 1)
                        //Math.log(searchableString.split("\\W+").length)
                ));

                document.add(new DoubleDocValuesField(LuceneConstants.SENTIMENT,
                        relativeSentiment(jsonDoc.getSearchableText()))
                );

                try {
                    writer.updateDocument(new Term(LuceneConstants.ID, jsonDoc.getId()), document);
                } catch ( IOException ex ) {
                    System.err.println("There is an IndexError");
                    ex.printStackTrace();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * standard closing lucene related indexing
     */
    private void finish() {
        try {
            writer.commit();
            writer.close();
        } catch ( IOException ex ) {
            System.err.println("Error closing Index Writer");
            ex.printStackTrace();
        }
    }

}
