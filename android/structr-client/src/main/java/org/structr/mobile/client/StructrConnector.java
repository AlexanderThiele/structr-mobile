package org.structr.mobile.client;

import android.net.Uri;

import org.apache.http.client.methods.HttpRequestBase;
import org.structr.mobile.client.queries.StructrDeleteQuery;
import org.structr.mobile.client.queries.StructrGetQuery;
import org.structr.mobile.client.queries.StructrWriteQuery;
import org.structr.mobile.client.register.EntityRegister;
import org.structr.mobile.client.register.objects.ExtractedClass;

/**
 * Created by alex.
 */
public class StructrConnector {

    private static final String TAG = "StructrConnector";

    private static Uri uri;
    private static String userName;
    private static String password;


    public StructrConnector(){
    }

    /**
     * Connects to the structr server with the given uri.
     * @param uri server Uri
     */
    public static void connect(String uri){
        if(!uri.startsWith("http://"))
            uri = "http://" + uri;
        StructrConnector.uri = Uri.parse(uri);


        //TODO: security, ping query, get api key, user statistics
    }

    /**
     * connects to the structr server and adds credentials to all queries
     * @param uri server Uri
     * @param userName server username
     * @param password username password
     */
    public static void connect(String uri, String userName, String password){
        StructrConnector.connect(uri);
        StructrConnector.userName = userName;
        StructrConnector.password = password;
    }

    /**
     * add credentials to all queries
     * @param userName
     * @param password
     */
    public static void addCredentials(String userName, String password){
        StructrConnector.userName = userName;
        StructrConnector.password = password;
    }


    /**
     * Connects to the Structr Server. The request get all entities for the given class name and
     * generates a list of results.
     *
     * Add query elements to the request (f.e. "name=dortmund", "number=42");
     * Incorrect query names or not indexed elements return with error code 400 - Bad Request.
     *
     * @param clazz schema Class (f.e. Test.class)
     * @param query additional query params
     * @return StructrGetQuery Object for execution.
     */
    public static StructrGetQuery read(Class<?> clazz, String... query){
        ExtractedClass extrC = null;

        try{
            extrC = EntityRegister.registerClass(clazz);
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }

        if(extrC != null) {
            StructrGetQuery sgq = new StructrGetQuery(extrC, query);

            return sgq;
        }

        return null;
    }



    /**
     * Connects to the structr server and writes new data. Choose between asynchron or synchron
     * request.
     * Returns the new Object and server generated id
     *
     * @param object to send to the server
     * @return StructrWriteQuery Object
     */
    public static StructrWriteQuery write(Object object) {

        ExtractedClass extrC = null;

        try{
            extrC = EntityRegister.registerClass(object.getClass());
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }

        if(extrC != null){
            StructrWriteQuery swq = new StructrWriteQuery(extrC, object);

            return swq;
        }

        return null;
    }

    /**
     * simple write request if id is not null
     * @param object
     * @return
     */
    public static StructrWriteQuery update(Object object){

        return StructrConnector.write(object);

    }

    /**
     * Not yet Supported
     * @param object
     * @return
     */
    public static StructrDeleteQuery delete(Object object){

        ExtractedClass extrC = null;

        try{
            extrC = EntityRegister.registerClass(object.getClass());
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }

        if(extrC != null){
            StructrDeleteQuery swq = new StructrDeleteQuery(extrC, object);

            return swq;
        }

        return null;
    }

    /**
     * adds username and password to the request if available otherwise do nothing
     * @param baseRequestBase current request
     */
    public static void addCredentialsToHeader(HttpRequestBase baseRequestBase){
        if(userName != null && password != null){
            baseRequestBase.addHeader("X-user", StructrConnector.userName);
            baseRequestBase.addHeader("X-password", StructrConnector.password);
        }
    }

    /**
     * Returnes the current URI
     * @return current URI
     */
    public static Uri getUri(){
        return StructrConnector.uri;
    }

}
