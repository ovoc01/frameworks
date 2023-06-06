package etu2074.framework.utilities;

import java.util.HashMap;
import java.util.Map;

public  class Utilities {
    public static HashMap<String,String> matchingType=new HashMap<>();
    public static void setUp_matchingType(){
        matchingType.put("int","java.lang.Integer");
        matchingType.put("double","java.lang.Double");
        matchingType.put("float","java.lang.Float");
        matchingType.put("boolean","java.lang.Boolean");
        matchingType.put("java.lang.Boolean","java.lang.Boolean");
        matchingType.put("java.lang.String","java.lang.String");
        matchingType.put("java.lang.Integer","java.lang.Integer");
        matchingType.put("java.lang.Double","java.lang.Double");
        matchingType.put("java.lang.Float","java.lang.Float");
    }
    public static String createSetter(String field) {
        return "set" + capitalizeFirstLetter(field);
    }
    public static String capitalizeFirstLetter(String input) {
        String var10000 = input.substring(0, 1).toUpperCase();
        return var10000 + input.substring(1);
    }
}
