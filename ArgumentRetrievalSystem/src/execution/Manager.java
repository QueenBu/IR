package execution;

import lucene.Indexer;
import lucene.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tools.CLIHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

import static execution.Main.inputDirectory;
import static execution.Main.outputDirectory;

/**
 * initializing, starting, running, closing of the Indexer and Searcher and general program flow.
 */
public class Manager {
    /**
     * interface to realise searching and handling results
     */
    private Searcher searcher;

    /**
     * indexes the given files
     *
     * @throws IOException if writing the index fails.
     */
    public void makeIndex() throws IOException {
        Indexer indexer = new Indexer();
        indexer.createIndex();
        try {
            searcher = new Searcher();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * standard start method.<p>
     * <p>
     * tira compatible output
     */
    public void start() {
        searchTopics();
        List<String> output = searcher.getOutput();
        for ( String trec_line : output ) {
            System.out.println(trec_line);
        }

        try {
            writeOutput();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * program flow for debugging and testing, human readable output
     */
    public void oldStart() {
        CLIHandler cli = new CLIHandler();
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

    /**
     * searches for the given query.
     *
     * @param searchQuery to be searched for
     * @throws IOException    if {@link Searcher#search(String searchQuery)} throws it
     * @throws ParseException if {@link Searcher#search(String searchQuery)} throws it
     */
    private void search(String searchQuery) throws IOException, ParseException {
        TopDocs hits = searcher.search(searchQuery);
    }

    /**
     * Sequentially searches for the topics given in topics.xml
     */
    private void searchTopics() {
        try {
            File inputFile = Paths.get(inputDirectory, "topics.xml").toFile();
            org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("topic");

            for ( int i = 0; i < nList.getLength(); i++ ) {
                Node nNode = nList.item(i);
                if ( nNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode;
                    searcher.searchAndAddToOutput(
                            eElement.getElementsByTagName("title").item(0).getTextContent(),
                            eElement.getElementsByTagName("num").item(0).getTextContent()
                    );
                    //System.out.println("title : "+ eElement.getElementsByTagName("title").item(0).getTextContent());
                    //System.out.println("number : " + eElement.getElementsByTagName("num").item(0).getTextContent());
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * writes the output read by {@link Manager#searchTopics()} to run.txt
     *
     * @throws IOException if the file couldnt be written.
     */
    private void writeOutput() throws IOException {
        List<String> result = searcher.getOutput();
        File f = Paths.get(outputDirectory, "run.txt").toFile();
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
