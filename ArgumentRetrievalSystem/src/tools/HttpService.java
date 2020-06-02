package tools;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ARSystem.Argument;
import org.json.*;
/**
 *
 * @author Maximilian Schmidt
 */
public class HttpService {

    public List<Argument> getArguments(String query){
        ArrayList<Argument> args = new ArrayList<>();
        try {
            URL url = new URL("https://www.args.me/api/v2/arguments?query=" + encodeValue(query));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String content = br.readLine();
            br.close();
            
            JSONObject json = new JSONObject(content);
            JSONArray json_args = json.getJSONArray("arguments");
            Iterator<Object> it = json_args.iterator();
            JSONObject json_arg;
            while(it.hasNext()){
                json_arg = (JSONObject) it.next();
                String id = json_arg.getString("id");
                String title = json_arg.getJSONObject("context").getString("discussionTitle");
                String sourceUrl = json_arg.getJSONObject("context").getString("sourceUrl");
                String conclusion = json_arg.getString("conclusion");
                String summary = json_arg.getString("summary");
                double score = json_arg.getJSONObject("explanation").getDouble("score");
                args.add(new Argument(id, title, sourceUrl, conclusion, summary, score));
            }
        } catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        return args;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

}
