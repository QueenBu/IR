package tools;

import java.util.ArrayList;

public class JSONDocument {
    private String id;
    private String conclusion;
    private final ArrayList<String> premTexts;
    private String autName;
    private String topic;

    public JSONDocument() {
        premTexts = new ArrayList<>();
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

    public void setId(String id) {
        this.id = id;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public void addPremText(String premText) {
        premTexts.add(premText);
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

    public String getSearchableText() {
        StringBuilder sb = new StringBuilder(conclusion);
        premTexts.forEach(sb::append);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "JSONDocument{" +
                "id='" + id + '\'' +
                ", conclusion='" + conclusion + '\'' +
                ", premTexts=" + premTexts +
                ", autName='" + autName + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
