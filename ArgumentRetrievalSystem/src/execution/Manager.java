package execution;

import lucene.Indexer;
import lucene.LuceneConstants;
import lucene.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.NodeList;
import tools.CLIHandler;


import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.io.IOException;
import java.util.List;

import static execution.Main.inputDirectory;
import static execution.Main.outputDirectory;

public class Manager {
    private CLIHandler cli;
    private String indexPath = "\\index";
    private Searcher searcher;

    public void start() {
        /*
        cli = new CLIHandler();
        while ( true ) {
            String query = cli.readUserInput("Please enter the phrase to be searched for! (empty input to cancel)");
            if ( query.isEmpty() ) {
                break;
            }
            try {
                readTopics();
                //searchAndExplain(query);
            } catch ( Exception e ) {
                e.printStackTrace();
            }


        }
       */
        readTopics();
        List<List<String>> test = searcher.getOutput();
        for(List<String> x: test){
            for(String y : x){
                System.out.println(y);
            }
        }
        try {
            makeOutput();
        } catch (IOException e) {
            e.printStackTrace();
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
        for ( ScoreDoc scoreDoc : hits.scoreDocs ) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("Text: " + doc.get(LuceneConstants.CONTENTS) + "\n Stance: " + doc.get(LuceneConstants.STANCE));
        }

    }

    private void searchAndExplain(String searchQuery, int topicNumber) throws IOException, ParseException {
        searcher.searchAndExplain(searchQuery, topicNumber);
    }

    private void readTopics(){
        try {
            File inputFile = new File(inputDirectory + "topic.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("topic");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    searchAndExplain(eElement.getElementsByTagName("title").item(0).getTextContent(), Integer.parseInt(eElement.getElementsByTagName("num").item(0).getTextContent()));
                    //System.out.println("title : "+ eElement.getElementsByTagName("title").item(0).getTextContent());
                    //System.out.println("number : " + eElement.getElementsByTagName("num").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeOutput() throws IOException {
        List<List<String>> result = searcher.getOutput();
        File f = new File(outputDirectory + "/run.txt");
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(List<String> row: result){
            bw.write(String.join(" ", row));
            bw.newLine();
        }
        bw.close();
    }
}
