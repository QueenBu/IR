package lucene;

import java.io.File;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import tools.JSONDocument;
import tools.JSONFileParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import static tools.SentimentValues.relativeSentiment;

public class Indexer {

    private IndexWriter writer;
    private String indexPath;
    private String jsonPath;

    public Indexer(String indexDirectoryPath, String jsonPath) {
        this.indexPath = indexDirectoryPath;
        this.jsonPath = jsonPath;
    }

    public void createIndex() {
        openIndex();
        addDocuments();
        finish();
    }

    public void openIndex() {
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(new SentimentSimilarity());
            //rewrite index each time. for update use parameter:  IndexWriterConfig.OpenMode.CREATE_OR_APPEND
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(indexDirectory, iwc);
        } catch ( IOException ex ) {
            System.err.println("Can not open Index");
            ex.printStackTrace();
        }
    }

    public void addDocuments() {
        JSONFileParser jp = new JSONFileParser(jsonPath);
        for ( JSONDocument jsonDoc : jp.getJsonDocumentArrayList() ) {
            Document document = new Document();
            //TextField --> wird mit durchsucht
            //StringField --> wird NICHT mit durchsucht
            document.add(new TextField(LuceneConstants.CONTENTS, jsonDoc.getPremText().get(0), TextField.Store.YES));
            document.add(new StringField(LuceneConstants.STANCE, jsonDoc.getPremStance().get(0), TextField.Store.YES));
            // TODO speedup here, it takes way too long
            document.add(new DoublePoint(LuceneConstants.SENTIMENT, relativeSentiment(jsonDoc.getPremText().get(0))));

            document.add(new StringField(LuceneConstants.ID, jsonDoc.getId(), TextField.Store.YES));
            document.add(new TextField(LuceneConstants.CONCLUSION, jsonDoc.getConclusion(), TextField.Store.YES));
            document.add(new TextField(LuceneConstants.TOPIC, jsonDoc.getTopic(), TextField.Store.YES));
            document.add(new TextField(LuceneConstants.AUTHORNAME, jsonDoc.getAutName(), TextField.Store.YES));
            System.out.println(jsonDoc.getTopic() + " <-> " + findTopic(jsonDoc.getPremText().get(0)));
            //findTopic(jsonDoc.getPremText().get(0));    
            try {
                writer.addDocument(document);
            } catch ( IOException ex ) {
                System.err.println("There is an IndexError");
                ex.printStackTrace();
            }
        }
    }
    
    public String findTopic(String text){
        text = text.toLowerCase();
        TreeMap<String, Integer> topics = new TreeMap<>();
        try {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document doc = builder.build(new File("policy_agendas_english.xml"));
            XMLOutputter fmt = new XMLOutputter();
            
            Element dictionary = doc.getRootElement();
            List cnodes = (List) dictionary.getChildren();
            for(Object cnode : cnodes){
                String cnode_value = ((Element) cnode).getAttributeValue("name").toLowerCase();
                topics.put(cnode_value, 0);
                List pnodes = (List) ((Element) cnode).getChildren();
                for(Object pnode : pnodes){
                    String pnode_value = ((Element) pnode).getAttributeValue("name").toLowerCase();
                    int offset = text.indexOf(pnode_value);
                    while(offset != -1){
                        topics.replace(cnode_value, topics.get(cnode_value) + 1);
                        offset = text.indexOf(pnode_value, offset + 1);
                    }
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(topics);
        
        String top_topic = "";
        int top_frequency = 0;
        for(Entry<String, Integer> e : topics.entrySet()){
            String topic = e.getKey();
            int frequency = e.getValue();
            if (frequency > top_frequency){
                top_topic = topic;
                top_frequency = frequency;
            }
        }
        return top_topic;
    }

    public void finish() {
        try {
            writer.commit();
            writer.close();
        } catch ( IOException ex ) {
            System.err.println("Error closing Index Writer");
            ex.printStackTrace();
        }
    }

}
