package tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class JSONDocument {
    private String id;
    private String conclusion;
    private ArrayList<String> premText;
    private ArrayList<String> premStance;
    private String autName;
    private String topic;
    private ArrayList<HashMap<String, Object>> aspects;
    private HashMap<String, Object> sourceInfo;

    public JSONDocument() {
        premStance = new ArrayList<>();
        premText = new ArrayList<>();
        aspects = new ArrayList<>();
        sourceInfo = new HashMap<String, Object>();
    }

    public String getId() {
        return id;
    }

    public String getConclusion() {
        return conclusion;
    }

    public ArrayList<String> getPremText() {
        return premText;
    }

    public ArrayList<String> getPremStance() {
        return premStance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void setPremText(ArrayList<String> premText) {
        this.premText = premText;
    }

    public void setPremStance(ArrayList<String> premStance) {
        this.premStance = premStance;
    }

    public String getAutName() {
        return autName;
    }

    public void setAutName(String autName) {
        this.autName = autName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<HashMap<String, Object>> getAspects() {
        return aspects;
    }

    public void setAspects(ArrayList<HashMap<String, Object>> aspects) {
        this.aspects = aspects;
    }

    public HashMap<String, Object> getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(HashMap<String, Object> sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
}
