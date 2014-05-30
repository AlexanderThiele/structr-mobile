package org.structr.mobile.client.register;

import java.util.HashMap;

/**
 * Created by alex.
 */
public class KnownObjects {

    private static HashMap<String, Object> knownObjects = new HashMap<String, Object>();

    public static boolean isKnown(String key){
        return knownObjects.containsKey(key);
    }

    public static void putObject(String key, Object object){
        knownObjects.put(key, object);
    }

    public static Object getObject(String key){
        return knownObjects.get(key);
    }

    public static void clearObjects(){
        knownObjects.clear();
    }
}
