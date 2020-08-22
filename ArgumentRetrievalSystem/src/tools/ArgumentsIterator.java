package tools;

import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Parses the json found with a given path-string on the fly.
 * Does not copy any information to heap which is why it is a) faster and b) does not crash.
 * Implements Iterator<JSONDocument> to provide an easy to use interface to walk through the json file.
 * Implements AutoCloseable to provide easy use inside a try-with-resources-statement.
 */
public class ArgumentsIterator implements Iterator<JSONDocument>, AutoCloseable {

    JsonReader reader;

    /**
     * initializes the streamed reader and skips the first two opening brackets + one jsonName
     *
     * @param jsonPath where the .json file is found
     * @throws IOException on any IOException thrown by {@link ArgumentsIterator#reader}
     */
    public ArgumentsIterator(String jsonPath) throws IOException {
        reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonPath)));
        reader.beginObject();
        if ( reader.nextName().equals("arguments") ) {
            //reader.skipValue(); // or: reader.nextName()->"arguments"
            reader.beginArray();
        }
    }

    /**
     * Checks with {@link ArgumentsIterator#reader}{@code .hasNext()} whether there is another json snippet coming.
     * It is only called inside the "arguments" array inside the given json file, after an array element has been
     * processed.
     * This way, one can read this method as: if there is another argument to be found, return true.
     *
     * @return true if there is another {@link JSONDocument} to be found in the file. false else and ony any
     * IOException.
     */
    @Override
    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Walks through the "arguments" array of the json file. Parses the information as a {@link JSONDocument} and
     * returns it.
     *
     * @return the next {@link JSONDocument} extracted from the next "arguments"-array element.
     */
    @Override
    public JSONDocument next() {
        JSONDocument jsonDocument = new JSONDocument();
        try {
            reader.beginObject();

            reader.skipValue(); // or: reader.nextName()->"id"
            jsonDocument.setId(reader.nextString());

            reader.skipValue(); // or: reader.nextName()->"conclusion"
            jsonDocument.setConclusion(reader.nextString());

            reader.skipValue(); // or: reader.nextName()->"premises"
            readPremises(jsonDocument);

            reader.skipValue(); // or: reader.nextName()->"context"
            readContext(jsonDocument);

            reader.endObject();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return jsonDocument;
    }

    /**
     * Is called in {@link ArgumentsIterator#next()} and writes the "premises"
     * information into the given jsonDocument.
     *
     * @param jsonDocument to write "premises" info into
     * @throws IOException on any IOException thrown by {@link ArgumentsIterator#reader}.
     */
    private void readPremises(JSONDocument jsonDocument) throws IOException {
        reader.beginArray();

        while ( reader.hasNext() ) {
            reader.beginObject();

            reader.skipValue(); // or: reader.nextName()->"text"
            jsonDocument.addPremText(reader.nextString());

            reader.skipValue(); // or: reader.nextName()->"stance"
            reader.skipValue(); //jsonDocument.addPremStance(reader.nextString()); would read the stance

            reader.skipValue(); // or: reader.nextName()->"annotations"
            readAnnotations(jsonDocument);

            reader.endObject();
        }

        reader.endArray();
    }

    /**
     * Commented out code can be used (and extended) if annotations need to be added.
     *
     * @param jsonDocument to write on currently.
     * @throws IOException on any operation on {@link JsonReader}-methods
     */
    private void readAnnotations(JSONDocument jsonDocument) throws IOException {

        // Current state would just print information inside "annotations".
        /*
        reader.beginArray();

        while ( reader.hasNext() ) {
            // its always empty? sout never showed anything on any source file
            System.out.println("annotations: " + reader.peek());
            reader.skipValue();
        }
        reader.endArray();
        */

        reader.skipValue(); // skips whole array
    }

    /**
     * Method extracted for clean code. Is called in {@link ArgumentsIterator#next()} and writes the "context"
     * information into the given jsonDocument.
     *
     * @param jsonDocument to write "context" info into
     * @throws IOException on any IOException thrown by {@link ArgumentsIterator#reader}.
     */
    private void readContext(JSONDocument jsonDocument) throws IOException {
        reader.beginObject();

        while ( reader.hasNext() ) {
            switch ( reader.nextName() ) {
                case "author":
                    jsonDocument.setAutName(reader.nextString());
                    break;
                case "topic":
                    jsonDocument.setTopic(reader.nextString());
                    break;
                default: // every other source info is skipped
                    reader.skipValue();
            }
        }

        reader.endObject();
    }

    /**
     * closes the {@link ArgumentsIterator#reader}
     *
     * @throws IOException if {@code ArgumentsIterator#reader#close()} throws an Exception
     */
    public void close() throws IOException {
        // Last two "closing operations" aren't needed since the resource does not get blocked if the closing parts of
        // the json object or json array aren't read in. Less error-prone.
        //reader.endArray();
        //reader.endObject();

        reader.close();
    }
}
