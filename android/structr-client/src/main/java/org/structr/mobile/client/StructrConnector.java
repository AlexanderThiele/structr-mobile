package org.structr.mobile.client;

import android.net.Uri;

import org.structr.mobile.client.queries.StructrDeleteQuery;
import org.structr.mobile.client.queries.StructrGetQuery;
import org.structr.mobile.client.queries.StructrUpdateQuery;
import org.structr.mobile.client.queries.StructrWriteQuery;
import org.structr.mobile.client.register.EntityRegister;
import org.structr.mobile.client.register.objects.ExtractedClass;

/**
 * Created by alex.
 */
public class StructrConnector {

    private static final String TAG = "StructrConnector";

    private static Uri uri;


    public StructrConnector(){
    }

    /**
     * Connects to the structr server with the given uri.
     * @param uri
     */
    public static void connect(String uri){
        if(!uri.startsWith("http://"))
            uri = "http://" + uri;
        StructrConnector.uri = Uri.parse(uri);


        //TODO: security, ping query, get api key, user statistics
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
            extrC = EntityRegister.read(clazz);
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

        //TODO when object id is not null: update old data

        ExtractedClass extrC = null;

        try{
            extrC = EntityRegister.read(object.getClass());
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
     * Not yet Supported
     * @param object
     * @return
     */
    public static StructrUpdateQuery update(Object object){
        throw new UnsupportedOperationException("Not yet Supported");

    }

    /**
     * Not yet Supported
     * @param object
     * @return
     */
    public static StructrDeleteQuery delete(Object object){
        throw new UnsupportedOperationException("Not yet Supported");
    }

    /**
     * Returnes the current URI
     * @return current URI
     */
    public static Uri getUri(){
        return StructrConnector.uri;
    }

}
