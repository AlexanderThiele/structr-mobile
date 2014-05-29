package org.structr.mobile.client.register;

import org.structr.mobile.client.register.objects.ExtractedClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex.
 */
public class EntityRegister {

    private static Map<String, ExtractedClass> classesMap = new HashMap<String, ExtractedClass>();
    //private static Map<String, ExtractedClass> objectMap = new HashMap<String, ExtractedClass>();

    public static ExtractedClass registerClass(Class<?> clazz){

        String name = clazz.getName();

        if(!classesMap.containsKey(name)){

            try{
                ExtractedClass extrC = new ExtractedClass(clazz);

                classesMap.put(name, extrC);
                return extrC;

            }catch (InstantiationException ex){
                ex.printStackTrace();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            ExtractedClass extrC = classesMap.get(name);
            if(extrC == null)
                throw new NullPointerException("extrC is null but should not be null");

            return extrC;
        }

        return null;
    }

    /*public static ExtractedClass write(Object object){
        String objName = object.getClass().getName() + object.toString();
        ExtractedClass extrC = null;

        // if not already in map
        if(!objectMap.containsKey(objName)){

            // if in classesMap
            if(classesMap.containsKey(object.getClass().getName())){
                ExtractedClass tmpextrC = classesMap.get(object.getClass().getName());
                if(tmpextrC != null){
                    extrC = tmpextrC.copyObject();
                }else{
                    throw new NullPointerException("Error: Extracted class is null but should not be null.");
                }

            }else{
                // if first time with Class
                extrC = EntityRegister.registerClass(object.getClass()).copyObject();
            }
            objectMap.put(objName, extrC);

        }else{
            extrC = objectMap.get(objName);
        }

        if(extrC == null){
            throw new NullPointerException("extrC is null but should not be null.");
        }

        //set Data
        if(extrC.setDataObject(object)){
            return extrC;
        }

        return null;
    }*/

    /*public static ExtractedClass read(Class<?> clazz){

        ExtractedClass extrC = null;

        //if class already registered
        if(classesMap.containsKey(clazz.getName())) {
            extrC = classesMap.get(clazz.getName());
        }else{
            // if first time with Class
            extrC = EntityRegister.registerClass(clazz);
        }

        if(extrC == null){
            throw new NullPointerException("extrC is null but should not be null.");
        }

        return extrC;
    }*/

    public static ExtractedClass getClass(String name){
        if(classesMap.containsKey(name)){

            return classesMap.get(name);

        }else{
            return null;
        }
    }
}
