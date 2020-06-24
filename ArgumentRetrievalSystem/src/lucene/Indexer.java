package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tools.JSONDocument;
import tools.JSONParser;
import tools.newFileParser;

import java.io.IOException;
import java.nio.file.Paths;

public class Indexer {

    private IndexWriter writer;
    private String indexPath;
    private String jsonPath;

    public Indexer(String indexDirectoryPath, String jsonPath){
        this.indexPath = indexDirectoryPath;
        this.jsonPath = jsonPath;
    }

    public void createIndex(){
        openIndex();
        addDocuments();
        finish();
    }

    public void openIndex(){
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(new SentimentSimilarity());
            //rewrite index each time. for update use parameter:  IndexWriterConfig.OpenMode.CREATE_OR_APPEND
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(indexDirectory, iwc);
        }catch (IOException ex){
            System.err.println("Can not open Index");
            ex.printStackTrace();
        }
    }

    public void addDocuments(){
        newFileParser jp = new newFileParser(jsonPath);
        for(JSONDocument jsonDoc: jp.getJsonDocumentArrayList()){
            Document document = new Document();
            //TextField --> wird mit durchsucht
            //StringField --> wird NICHT mit durchsucht
            document.add( new TextField(LuceneConstants.CONTENTS, jsonDoc.getPremText().get(0),TextField.Store.YES));
            document.add( new StringField(LuceneConstants.STANCE, jsonDoc.getPremStance().get(0), TextField.Store.YES));
            document.add( new StringField(LuceneConstants.ID, jsonDoc.getId(), TextField.Store.YES));
            document.add( new TextField(LuceneConstants.CONCLUSION, jsonDoc.getConclusion(),TextField.Store.YES));
            document.add( new TextField(LuceneConstants.TOPIC, jsonDoc.getTopic(),TextField.Store.YES));
            document.add( new TextField(LuceneConstants.AUTHORNAME, jsonDoc.getAutName(), TextField.Store.YES));
            try{
                writer.addDocument(document);
            }catch (IOException ex){
                System.err.println("There is an IndexError");
                ex.printStackTrace();
            }
        }
    }

    public void finish(){
        try{
            writer.commit();
            writer.close();
        }catch (IOException ex){
            System.err.println("Error closing Index Writer");
            ex.printStackTrace();
        }
    }

}
