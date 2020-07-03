package tools;

import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ArgumentsIterator implements Iterator<JSONDocument>, AutoCloseable {

    JsonReader reader;

    public ArgumentsIterator(String jsonPath) throws IOException {
        reader = new JsonReader(new InputStreamReader(new FileInputStream(jsonPath)));
        reader.beginObject();
        reader.skipValue(); // or: reader.nextName()->"arguments"
        reader.beginArray();
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.hasNext();
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

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

    private void readPremises(JSONDocument jsonDocument) throws IOException {
        reader.beginArray();

        while ( reader.hasNext() ) {
            reader.beginObject();

            reader.skipValue(); // or: reader.nextName()->"text"
            jsonDocument.addPremText(reader.nextString());

            reader.skipValue(); // or: reader.nextName()->"stance"
            jsonDocument.addPremStance(reader.nextString());

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

    public void close() throws IOException {
        reader.close();
    }


    public static void main(String[] args) {
        try {
            ArgumentsIterator ai = new ArgumentsIterator("ArgumentRetrievalSystem/corpus_files/parliamentary.json");
            int i = 0;
            while ( ai.hasNext() ) {
                //ai.next();
                System.out.println(i++ + " " + ai.next());
            }
            ai.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
