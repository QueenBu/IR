package execution;

import lucene.Indexer;
import lucene.LuceneConstants;
import lucene.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tools.CLIHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

import static execution.Main.inputDirectory;
import static execution.Main.outputDirectory;

public class Manager {
    private CLIHandler cli;
    private String indexPath = "index";
    private Searcher searcher;

    public void start() {
        readTopics();
        List<String> test = searcher.getOutput();
        for ( String y : test ) {
            System.out.println(y);
        }

        try {
            makeOutput();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void oldStart() {
        cli = new CLIHandler();
        while ( true ) {
            String query = cli.readUserInput("Please enter the phrase to be searched for! (empty input to cancel)");
            if ( query.isEmpty() ) {
                break;
            }
            try {
                search(query);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public void makeIndex() throws IOException {
        Indexer indexer = new Indexer(indexPath);
        indexer.createIndex();
        try {
            searcher = new Searcher(indexPath);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void search(String searchQuery) throws IOException, ParseException {
        TopDocs hits = searcher.search(searchQuery);
        System.out.println(hits.totalHits + " documents found.");
        for ( int i = 0; i < hits.scoreDocs.length; i++ ) {
            Document doc = searcher.getDocument(hits.scoreDocs[ i ]);
            System.out.println(i + 1 + ".\nText: " + doc.get(LuceneConstants.CONCLUSION) + "\nPremises: " + doc.get(LuceneConstants.PREMISES) + "\n");
        }

    }

    private void searchAndWriteToOutput(String searchQuery, int topicNumber) throws IOException, ParseException {
        searcher.searchAndWriteToOutput(searchQuery, topicNumber);
    }

    private void readTopics() {
        try {
            File inputFile = Paths.get(inputDirectory, "topics.xml").toFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("topic");

            for ( int i = 0; i < nList.getLength(); i++ ) {
                Node nNode = nList.item(i);
                if ( nNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode;
                    searchAndWriteToOutput(eElement.getElementsByTagName("title").item(0).getTextContent(),
                            Integer.parseInt(eElement.getElementsByTagName("num").item(0).getTextContent()));
                    //System.out.println("title : "+ eElement.getElementsByTagName("title").item(0).getTextContent());
                    //System.out.println("number : " + eElement.getElementsByTagName("num").item(0).getTextContent());
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void makeOutput() throws IOException {
        List<String> result = searcher.getOutput();
        File f = new File(outputDirectory + "/run.txt");
        f.createNewFile();
        try ( FileOutputStream fos = new FileOutputStream(f);
              BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)) ) {
            for ( String row : result ) {
                bw.write(row);
                bw.newLine();
            }
        }
    }
}
