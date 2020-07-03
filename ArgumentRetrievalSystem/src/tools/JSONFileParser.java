package tools;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class JSONFileParser {

    private ArrayList<JSONDocument> jsonDocumentArrayList = new ArrayList<>();

    public JSONFileParser(String jsonPath) {
        process(jsonPath);
    }

    public ArrayList<JSONDocument> getJsonDocumentArrayList() {
        return jsonDocumentArrayList;
    }

    public void process(String jsonPath) {
        File jsonFile = new File(jsonPath);
        JsonFactory jsonfactory = new JsonFactory();
        try {
            JsonParser parser = jsonfactory.createParser(jsonFile);
            while ( !parser.isClosed() ) {
                JSONDocument jd = new JSONDocument();
                JsonToken jsonToken = parser.nextToken();
                while ( !(parser.isClosed() || JsonToken.FIELD_NAME.equals(jsonToken)) ) {
                    jsonToken = parser.nextToken();
                }
                if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                    String fieldName = parser.getCurrentName();
                    jsonToken = parser.nextToken();
                    if ( "id".equals(fieldName) ) {
                        jd.setId(parser.getValueAsString());
                        while ( true ) {
                            while ( !JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                jsonToken = parser.nextToken();
                            }
                            if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                fieldName = parser.getCurrentName();
                                jsonToken = parser.nextToken();
                                if ( "conclusion".equals(fieldName) ) {
                                    jd.setConclusion(parser.getValueAsString());
                                }
                                if ( "premises".equals(fieldName) ) {
                                    while ( !JsonToken.END_ARRAY.equals(jsonToken) ) {
                                        jsonToken = parser.nextToken();
                                        if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                            fieldName = parser.getCurrentName();
                                            jsonToken = parser.nextToken();
                                            if ( "text".equals(fieldName) ) {
                                                ArrayList<String> temp = jd.getPremTexts();
                                                temp.add(parser.getValueAsString());
                                                jd.setPremTexts(temp);
                                            }
                                            if ( "stance".equals(fieldName) ) {
                                                ArrayList<String> temp = jd.getPremStances();
                                                temp.add(parser.getValueAsString());
                                                jd.setPremStances(temp);
                                            }
                                            if ( "annotations".equals(fieldName) ) {
                                                do {
                                                    jsonToken = parser.nextToken();
                                                    if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                                        fieldName = parser.getCurrentName();
                                                        jsonToken = parser.nextToken();
                                                    }
                                                } while ( !JsonToken.END_ARRAY.equals(jsonToken) );
                                                jsonToken = parser.nextToken();
                                            }
                                        }
                                    }
                                    jsonToken = parser.nextToken();
                                }
                                if ( "aspects".equals(fieldName) ) {
                                    int i = 0;
                                    while ( !JsonToken.END_ARRAY.equals(jsonToken) ) {
                                        jsonToken = parser.nextToken();
                                        if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                            fieldName = parser.getCurrentName();
                                            jsonToken = parser.nextToken();
                                            if ( "name".equals(fieldName) ) {
                                                HashMap<String, Object> hm = new HashMap<>();
                                                hm.put("name", parser.getValueAsString());
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.add(hm);
                                                jd.setAspects(temp);
                                            }
                                            if ( "weight".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("weight", parser.getValueAsInt());
                                                jd.setAspects(temp);
                                            }
                                            if ( "normalizedWeight".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("normalizedWeight", parser.getValueAsDouble());
                                                jd.setAspects(temp);
                                            }
                                            if ( "rank".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("rank", parser.getValueAsInt());
                                                jd.setAspects(temp);
                                                i++;
                                            }
                                        }
                                    }
                                    jsonToken = parser.nextToken();
                                }
                                if ( "author".equals(fieldName) ) {
                                    jd.setAutName(parser.getValueAsString());
                                }
                                if ( "mode".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("mode", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTitle".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTitle", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceText".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceText", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "date".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("date", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextConclusionStart".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextConclusionStart", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextConclusionEnd".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextConclusionEnd", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextPremiseStart".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextPremiseStart", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextPremiseEnd".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextPremiseEnd", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "topic".equals(fieldName) ) {
                                    jd.setTopic(parser.getValueAsString());
                                    break;
                                }
                            }
                        }
                    }
                    if ( "arguments".equals(fieldName) ) {
                        while ( !JsonToken.END_ARRAY.equals(jsonToken) ) {
                            while ( !JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                jsonToken = parser.nextToken();
                            }
                            if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                fieldName = parser.getCurrentName();
                                jsonToken = parser.nextToken();
                                if ( "id".equals(fieldName) ) {
                                    jd.setId(parser.getValueAsString());
                                }
                                if ( "conclusion".equals(fieldName) ) {
                                    jd.setConclusion(parser.getValueAsString());
                                }
                                if ( "premises".equals(fieldName) ) {
                                    while ( !JsonToken.END_ARRAY.equals(jsonToken) ) {
                                        jsonToken = parser.nextToken();
                                        if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                            fieldName = parser.getCurrentName();
                                            jsonToken = parser.nextToken();
                                            if ( "text".equals(fieldName) ) {
                                                ArrayList<String> temp = jd.getPremTexts();
                                                temp.add(parser.getValueAsString());
                                                jd.setPremTexts(temp);
                                            }
                                            if ( "stance".equals(fieldName) ) {
                                                ArrayList<String> temp = jd.getPremStances();
                                                temp.add(parser.getValueAsString());
                                                jd.setPremStances(temp);
                                            }
                                            if ( "annotations".equals(fieldName) ) {
                                                do {
                                                    jsonToken = parser.nextToken();
                                                    if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                                        fieldName = parser.getCurrentName();
                                                        jsonToken = parser.nextToken();
                                                    }
                                                } while ( !JsonToken.END_ARRAY.equals(jsonToken) );
                                                jsonToken = parser.nextToken();
                                            }
                                        }
                                    }
                                    jsonToken = parser.nextToken();
                                }
                                if ( "aspects".equals(fieldName) ) {
                                    int i = 0;
                                    while ( !JsonToken.END_ARRAY.equals(jsonToken) ) {
                                        jsonToken = parser.nextToken();
                                        if ( JsonToken.FIELD_NAME.equals(jsonToken) ) {
                                            fieldName = parser.getCurrentName();
                                            jsonToken = parser.nextToken();
                                            if ( "name".equals(fieldName) ) {
                                                HashMap<String, Object> hm = new HashMap<>();
                                                hm.put("name", parser.getValueAsString());
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.add(hm);
                                                jd.setAspects(temp);

                                            }
                                            if ( "weight".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("weight", parser.getValueAsInt());
                                                jd.setAspects(temp);
                                            }
                                            if ( "normalizedWeight".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("normalizedWeight", parser.getValueAsDouble());
                                                jd.setAspects(temp);
                                            }
                                            if ( "rank".equals(fieldName) ) {
                                                ArrayList<HashMap<String, Object>> temp = jd.getAspects();
                                                temp.get(i).put("rank", parser.getValueAsInt());
                                                jd.setAspects(temp);
                                                i++;
                                            }
                                        }
                                    }
                                    jsonToken = parser.nextToken();
                                }
                                if ( "author".equals(fieldName) ) {
                                    jd.setAutName(parser.getValueAsString());
                                }
                                if ( "mode".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("mode", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTitle".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTitle", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceText".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceText", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "date".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("date", parser.getValueAsString());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextConclusionStart".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextConclusionStart", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextConclusionEnd".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextConclusionEnd", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextPremiseStart".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextPremiseStart", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "sourceTextPremiseEnd".equals(fieldName) ) {
                                    HashMap<String, Object> temp = jd.getSourceInfo();
                                    temp.put("sourceTextPremiseEnd", parser.getValueAsInt());
                                    jd.setSourceInfo(temp);
                                }
                                if ( "topic".equals(fieldName) ) {
                                    jd.setTopic(parser.getValueAsString());
                                    break;
                                }
                            }
                        }
                    }
                }
                if ( jd.getId() != null ) {
                    jsonDocumentArrayList.add(jd);
                }
            }
        } catch ( JsonParseException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
