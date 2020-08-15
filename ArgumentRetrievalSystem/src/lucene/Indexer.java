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
import static tools.SentimentValues.relativeSentiment;

/*import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;*/

public class Indexer {

    private IndexWriter writer;
    private final String indexPath;

    public Indexer(String indexDirectoryPath) {
        this.indexPath = indexDirectoryPath;
    }

    public void createIndex() throws IOException {
        openIndex();
        Files.walk(Paths.get(inputDirectory), 1).filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(".json")).forEach(filename -> {
            System.out.println(filename);
            addDocuments(filename);
        });
        finish();
    }

    public void openIndex() {
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
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

    public void addDocuments(String filename) {
        try ( ArgumentsIterator ai = new ArgumentsIterator(filename) ) {
            //List<String> ids = new ArrayList<>();
            while ( ai.hasNext() ) {
                JSONDocument jsonDoc = ai.next();
                Document document = new Document();

                String searchableString = jsonDoc.getPremTexts().stream().reduce(String::concat).orElse("");

                document.add(new TextField(LuceneConstants.PREMISES, searchableString, Field.Store.YES));

                document.add(new DoubleDocValuesField(
                        LuceneConstants.LENGTH_FACTOR,
                        // magic numbers are explained in the evaluation
                        10. / (1 + 30 * Math.pow(Math.E, -0.1 * searchableString.split("\\W+").length))
                        //Math.log(searchableString.split("\\W+").length)
                ));

                /*document.add(new StringField(LuceneConstants.STANCE, jsonDoc.getPremStances().get(i),
                        TextField.Store.YES)
                );*/

                document.add(new DoubleDocValuesField(LuceneConstants.SENTIMENT,
                        relativeSentiment(jsonDoc.getSearchableText()))
                );
                //ids.add(jsonDoc.getId());
                document.add(new StringField(LuceneConstants.ID, jsonDoc.getId(), StringField.Store.YES));
                document.add(new TextField(LuceneConstants.CONCLUSION, jsonDoc.getConclusion(), TextField.Store.YES));
                if ( jsonDoc.getTopic() != null ) {
                    document.add(new TextField(LuceneConstants.TOPIC, jsonDoc.getTopic(), TextField.Store.YES));
                }
                if ( jsonDoc.getAutName() != null ) {
                    document.add(new TextField(LuceneConstants.AUTHORNAME, jsonDoc.getAutName(), TextField
                            .Store.YES));
                }

                //System.out.println(jsonDoc.getTopic() + " <-> " + findTopic(jsonDoc.getPremText().get(0)));
                //findTopic(jsonDoc.getPremText().get(0));
                try {
                    writer.updateDocument(new Term(LuceneConstants.ID, jsonDoc.getId()), document);
                    //writer.addDocument(document);
                } catch ( IOException ex ) {
                    System.err.println("There is an IndexError");
                    ex.printStackTrace();
                }
            }
            /*Set<String> lel = new HashSet<>();
            Set<String> duplicates = ids.stream().filter(n -> !lel.add(n)).collect(Collectors.toSet());
            duplicates.forEach(System.out::println);*/
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
/*
    public void addDocuments_old() {
        JSONFileParser jp = new JSONFileParser(jsonPath);
        for ( JSONDocument jsonDoc : jp.getJsonDocumentArrayList() ) {
            for ( int i = 0; i < jsonDoc.getPremTexts().size(); i++ ) {
                System.out.println(i);

                Document document = new Document();
                //TextField --> wird mit durchsucht
                //StringField --> wird NICHT mit durchsucht
                document.add(
                        new TextField(LuceneConstants.CONTENTS, jsonDoc.getPremTexts().get(i), TextField.Store.YES)
                );
                document.add(
                        new StringField(LuceneConstants.STANCE, jsonDoc.getPremStances().get(i), TextField.Store.YES)
                );
                document.add(
                        new DoubleDocValuesField(LuceneConstants.SENTIMENT,
                                relativeSentiment(jsonDoc.getPremTexts().get(i)))
                );

                document.add(new StringField(LuceneConstants.ID, jsonDoc.getId(), TextField.Store.YES));
                document.add(new TextField(LuceneConstants.CONCLUSION, jsonDoc.getConclusion(), TextField.Store.YES));
                document.add(new TextField(LuceneConstants.TOPIC, jsonDoc.getTopic(), TextField.Store.YES));
                document.add(new TextField(LuceneConstants.AUTHORNAME, jsonDoc.getAutName(), TextField.Store.YES));

                //System.out.println(jsonDoc.getTopic() + " <-> " + findTopic(jsonDoc.getPremText().get(0)));
                //findTopic(jsonDoc.getPremText().get(0));
                try {
                    writer.addDocument(document);
                } catch ( IOException ex ) {
                    System.err.println("There is an IndexError");
                    ex.printStackTrace();
                }
            }
        }
    }
*/

    public String findTopic(String text) {/*
        text = text.toLowerCase();
        TreeMap<String, Integer> topics = new TreeMap<>();
        try {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document doc = builder.build(new File(execution.Main.getTestFile("policy_agendas_english.xml")));
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
        return top_topic;*/
        return "";
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
