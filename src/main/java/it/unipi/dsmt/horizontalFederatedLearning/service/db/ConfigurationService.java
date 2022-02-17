package it.unipi.dsmt.horizontalFederatedLearning.service.db;

import java.util.HashMap;

public class ConfigurationService {
    private final static LevelDB db;

    static {
        db = LevelDB.getInstance();
    }

    // for general configuration parameters
    public static void insertGeneral(HashMap<String, String> values){
        HashMap<String, String> map = new HashMap<>();
        String prefixKey = "Conf:general:";
        for(String key: values.keySet()){
            String value = values.get(key);
            map.put(prefixKey + key, value);
        }
        db.putBatchValues(map);
    }

    // for algorithms' specific configuration parameters
    public static void insertSpecific(HashMap<String, String> values, String algorithm){
        HashMap<String, String> map = new HashMap<>();
        String prefixKey = "Conf:" + algorithm + ":";
        for(String key: values.keySet()){
            String value = values.get(key);
            map.put(prefixKey + key, value);
        }
        db.putBatchValues(map);
    }

    // for general configuration parameters
    public static HashMap<String, String> retrieveGeneral(){
        HashMap<String, String> map = db.findByPrefix("Conf:general:");
        HashMap<String, String> attributeValues = new HashMap<>();
        for(String key: map.keySet()){
            String attribute = key.split(":")[2];
            String value = map.get(key);
            attributeValues.put(attribute, value);
        }
        return attributeValues;
    }

    // for algorithms' specific configuration parameters
    public static HashMap<String, String> retrieveSpecific(String algorithm){
        HashMap<String, String> map = db.findByPrefix("Conf:" + algorithm + ":");
        HashMap<String, String> attributeValues = new HashMap<>();
        for(String key: map.keySet()){
            String attribute = key.split(":")[2];
            String value = map.get(key);
            attributeValues.put(attribute, value);
        }
        return attributeValues;
    }
}
