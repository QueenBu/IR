package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tools.JSONParser;

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
        JSONParser jp = new JSONParser(jsonPath);
        while(jp.next()){
            Document document = new Document();
            document.add( new TextField(LuceneConstants.CONTENTS, jp.getPremises().get(0).get("text"),TextField.Store.YES));
            document.add( new StringField(LuceneConstants.STANCE, jp.getPremises().get(0).get("stance"), TextField.Store.YES));
            document.add( new StringField(LuceneConstants.ID, jp.getId(), TextField.Store.YES));
            document.add( new TextField(LuceneConstants.CONCLUSION, jp.getConclusion(),TextField.Store.YES));
            document.add( new TextField(LuceneConstants.TOPIC, jp.getTopic(),TextField.Store.YES));

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
