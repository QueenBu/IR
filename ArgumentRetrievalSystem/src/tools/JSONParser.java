package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;


public class JSONParser {
    private String file_path;
    private Iterator<Object> json_iterator;
    private JSONObject arg;

    public JSONParser(String file_path) {
        this.file_path = file_path;
        String source_data = FileParser.parseFile(file_path);
        JSONObject json = new JSONObject(source_data);
        JSONArray json_args = json.getJSONArray("arguments");
        this.json_iterator = json_args.iterator();
    }

    public boolean next() {
        if (json_iterator.hasNext()){
            arg = (JSONObject) json_iterator.next();
            return true;
        } else {
            return false;
        }
    }
    
    public String getId(){
        return arg.getString("id");
    }
    
    public String getConclusion(){
        return arg.getString("conclusion");
    }
    
    // Arraylist mit allen Premissen als Map<String, String> (Schlüssel: stance, text)
    public ArrayList<HashMap<String, String>> getPremises(){
        ArrayList<HashMap<String, String>> premises = new ArrayList<>();
        Iterator<Object> it = arg.getJSONArray("premises").iterator();
        JSONObject json_premise;
        while(it.hasNext()){
            HashMap<String, String> premise = new HashMap<>();
            json_premise = (JSONObject) it.next();
            premise.put("stance", json_premise.getString("stance"));
            premise.put("text", json_premise.getString("text"));
            premises.add(premise);
        }
        return premises;
    }
    
    // Map<String, String> mit allen Autordaten (Schlüssel: name, imageUrl, organization, role)
    public HashMap<String, String> getAuthorData(){
        HashMap<String, String> author_data = new HashMap<>();
        author_data.put("name", arg.getJSONObject("context").getString("author"));
        //author_data.put("imageUrl", arg.getJSONObject("context").getString("authorImage"));
        //author_data.put("organization", arg.getJSONObject("context").getString("authorOrganization"));
        //author_data.put("role", arg.getJSONObject("context").getString("authorRole"));
        return author_data;
    }
    
    // Arraylist mit allen Aspekten als Map<String, Object> (Schlüssel: name -> String, weight -> int, normalizedWeight -> double, rank -> int)
    public ArrayList<HashMap<String, Object>> getAspects(){
        ArrayList<HashMap<String, Object>> aspects = new ArrayList<>();
        Iterator<Object> it = arg.getJSONObject("context").getJSONArray("aspects").iterator();
        JSONObject json_aspect;
        while(it.hasNext()){
            HashMap<String, Object> aspect = new HashMap<>();
            json_aspect = (JSONObject) it.next();
            aspect.put("name", json_aspect.getString("name"));
            aspect.put("weight", json_aspect.getInt("weight"));
            aspect.put("normalizedWeight", json_aspect.getDouble("normalizedWeight"));
            aspect.put("rank", json_aspect.getInt("rank"));
            aspects.add(aspect);
        }
        return aspects;
    }
    
    // Map<String, Object> mit allen Quelleinformationen (Schlüssel: id -> String, title -> String, text -> String, date -> String, conclusionStart -> int, conclusionEnd -> int, premiseStart -> int, premiseEnd -> int)
    public HashMap<String, Object> getSourceInformation() {
        HashMap<String, Object> source_information = new HashMap<>();
        source_information.put("id", arg.getJSONObject("context").getString("sourceId"));
        source_information.put("title", arg.getJSONObject("context").getString("sourceTitle"));
        source_information.put("text", arg.getJSONObject("context").getString("sourceText"));
        source_information.put("date", arg.getJSONObject("context").getString("date"));
        source_information.put("conclusionStart", arg.getJSONObject("context").getInt("sourceTextConclusionStart"));
        source_information.put("conclusionEnd", arg.getJSONObject("context").getInt("sourceTextConclusionEnd"));
        source_information.put("premiseStart", arg.getJSONObject("context").getInt("sourceTextPremiseStart"));        
        source_information.put("premiseEnd", arg.getJSONObject("context").getInt("sourceTextPremiseEnd"));
        return source_information;
    }
    
    public String getTopic(){
        return arg.getJSONObject("context").getString("topic");
    }
}
