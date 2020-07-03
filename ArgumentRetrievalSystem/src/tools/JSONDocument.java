package tools;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONDocument {
    private String id;
    private String conclusion;
    private ArrayList<String> premTexts;
    private ArrayList<String> premStances;
    private String autName;
    private String topic;
    /**
     * not used, can be filled in {@link ArgumentsIterator}#readContext(JSONDocument)
     */
    private ArrayList<HashMap<String, Object>> aspects;
    /**
     * not used, can be filled in {@link ArgumentsIterator}#readContext(JSONDocument)
     */
    private HashMap<String, Object> sourceInfo;

    public int getPremisesCount() {
        return premTexts.size();
    }

    public JSONDocument() {
        premStances = new ArrayList<>();
        premTexts = new ArrayList<>();
        aspects = new ArrayList<>();
        sourceInfo = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getConclusion() {
        return conclusion;
    }

    public ArrayList<String> getPremTexts() {
        return premTexts;
    }

    public ArrayList<String> getPremStances() {
        return premStances;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void addPremText(String premText) {
        premTexts.add(premText);
    }

    public void setPremTexts(ArrayList<String> premTexts) {
        this.premTexts = premTexts;
    }

    public void addPremStance(String premStance) {
        premStances.add(premStance);
    }

    public void setPremStances(ArrayList<String> premStances) {
        this.premStances = premStances;
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

    @Override
    public String toString() {
        return "JSONDocument{" +
                "id='" + id + '\'' +
                ", conclusion='" + conclusion + '\'' +
                ", premTexts=" + premTexts +
                ", premStances=" + premStances +
                ", autName='" + autName + '\'' +
                ", topic='" + topic + '\'' +
                ", aspects=" + aspects +
                ", sourceInfo=" + sourceInfo +
                '}';
    }
}
