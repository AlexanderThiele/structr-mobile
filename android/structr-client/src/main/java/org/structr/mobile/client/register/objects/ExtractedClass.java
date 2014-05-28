package org.structr.mobile.client.register.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.structr.mobile.client.register.EntityRegister;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by alex.
 */
public class ExtractedClass implements Cloneable{

    private final String TAG ="ExtractedClass";

    private String name;
    private Class<?> clazz;

    private Field[] fields;
    private String[] fieldNames;
    private String[] fieldTypes;

    private Object dataObject;

    public ExtractedClass(Class<?> clazz)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, Exception {

        this.name = clazz.getName();
        this.clazz = clazz;

        if(clazz.getFields().length > 0){
            fields = clazz.getFields();
            fieldNames = new String[fields.length];
            fieldTypes = new String[fields.length];

            Object clazzObj = clazz.getConstructors()[0].newInstance();

            for (int i=0; i < fieldNames.length; i++){
                fieldNames[i] = fields[i].getName();
                fieldTypes[i] = fields[i].getType().getName();

				/*
				if(fieldTypes[i].equals("String")){
					Object obj = fields[i].get(clazzObj);
					if(obj != null){
						fieldValues[i] = (String)obj;
					}
				}*/
                //TODO: add other types
            }
        }

		/*
		BeanInfo beanInfo = Introspector.getBeanInfo( clazz );
		beanInfo = Introspector.getBeanInfo(clazz.getSuperclass());
		if(beanInfo.getPropertyDescriptors().length > 0){
			for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() )
			System.out.println( pd.getDisplayName() + " : " +
					    pd.getPropertyType().getName() +
					      " " + pd.getDisplayName());
		}*/

    }

    public boolean setDataObject(Object obj){
        if(!obj.getClass().getName().equals(name)){
            return false;
        }

        this.dataObject = obj;

        return true;
    }

    public boolean setDataObjectValue(String name, Object val) {
        for(int i=0; i<fieldNames.length; i++){
            if(dataObject != null && fieldNames[i] != null && fieldNames[i].equals(name)){
                try {
                    fields[i].set(dataObject, val);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public Object getDataObjectValue(String name) {
        for(int i=0; i<fieldNames.length; i++){
            if(dataObject != null && fieldNames[i] != null && fieldNames[i].equals(name)){

                try {
                    return fields[i].get(dataObject);

                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    return null;
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ExtractedClass){
            return  o.hashCode() == hashCode();//( ((ExtractedClass)o).name.equals(this.name) && super.equals(o) );
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash += 31 * hash + this.name.hashCode();
        hash += 31 * hash + this.dataObject.hashCode();
        return hash;
    }

    public ExtractedClass copyObject(){
        try {
            ExtractedClass extc = (ExtractedClass) this.clone();
            extc.dataObject = null;

            return extc;

        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getClazzName(){
        return this.clazz.getSimpleName();
    }

    public JSONObject getJson(){
        if(dataObject == null){
            return new JSONObject();
        }
        JSONObject jsonObject = new JSONObject();
        for(int i=0; i<fieldNames.length; i++){
            try {

                jsonObject.put(fieldNames[i], fields[i].get(dataObject).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public Object buildObjectFromJson(JSONObject jsonObject){

        try {

            Object resultObject = clazz.getConstructors()[0].newInstance();

            for (int i=0; i < fields.length; i++){

                if(fieldTypes[i].equals("java.lang.String")){

                    try {
                        String value = jsonObject.getString(fieldNames[i]);
                        if(!value.equals("null"))
                            fields[i].set(resultObject, value);

                    } catch (JSONException e) {
                        Log.e(TAG, "No JSONString found for " + fieldNames[i] + " in " + jsonObject.toString());
                    }

                }else{
                    // No Primitive -> Custom Class

                    ExtractedClass customClazz;

                    // if Arraylist Type   n -> n or 1 -> n
                    if(fields[i].getType().getName().equals("java.util.ArrayList")){

                        ParameterizedType pType = (ParameterizedType)fields[i].getGenericType();

                        if(pType == null) {
                            throw new NullPointerException("ArrayList Type Error.");
                        }

                        Type[] arrayListTypes = pType.getActualTypeArguments();

                        if(arrayListTypes.length == 0){
                            throw new ArrayIndexOutOfBoundsException("ArrayList length is 0.");
                        }

                        Class<?> arrayListClazz = (Class<?>) arrayListTypes[0];

                        customClazz = EntityRegister.registerClass(arrayListClazz);

                        if(customClazz == null){
                            throw new NullPointerException("class is null but should not be null");
                        }

                        JSONArray insideJsonArray = null;

                        try {
                            insideJsonArray = jsonObject.getJSONArray(fieldNames[i]);
                        } catch (JSONException e) {
                            Log.e(TAG, "No JSONArray found for " + fieldNames[i] + " in " + jsonObject.toString());
                        }

                        ArrayList<Object> buildedArrayList = new ArrayList<Object>();

                        if(insideJsonArray != null && insideJsonArray.length() > 0){

                            for (int j=0; j < insideJsonArray.length(); j++){
                                JSONObject tmpJsonObject = insideJsonArray.optJSONObject(j);

                                if(tmpJsonObject != null){
                                    Object obj = customClazz.buildObjectFromJson(tmpJsonObject);
                                    if(obj != null) {
                                        buildedArrayList.add(obj);
                                    }else{
                                        Log.e(TAG, "Error render JSON for " + tmpJsonObject);
                                    }
                                }else{
                                    Log.e(TAG, "JSONObject from JSONArray is null for " + j + ": " + insideJsonArray);
                                }

                            }
                        }

                        fields[i].set(resultObject, buildedArrayList);

                    }else{

                        // normale Object  1 -> 1 or n -> 1


                        customClazz = EntityRegister.getClass(fieldNames[i]);

                        if(customClazz == null){

                            customClazz = EntityRegister.registerClass(fields[i].getType());


                            if(customClazz == null){
                                throw new NullPointerException("class is null but should not be null");
                            }
                            JSONObject insideJsonObject = null;

                            try {
                                insideJsonObject = jsonObject.getJSONObject(fieldNames[i]);
                            } catch (JSONException e) {
                                Log.e(TAG, "No JSONValue found for " + fieldNames[i] + " in " + jsonObject.toString());
                            }

                            if(insideJsonObject != null && insideJsonObject.length() > 0){
                                Object obj = customClazz.buildObjectFromJson(insideJsonObject);

                                if(obj != null){
                                    fields[i].set(resultObject, obj);

                                }else{
                                    Log.e(TAG, "Error create object for " + insideJsonObject.toString() + " in " + customClazz.toString());
                                }

                            }

                        }
                    }

                }

                //TODO: add types
                //TODO: handle array[]
            }

            return resultObject;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}