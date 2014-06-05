package org.structr.mobile.client.queries;

import android.util.Log;

import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.tasks.PostTask;
import org.structr.mobile.client.tasks.PutTask;

/**
 * Created by alex.
 */
public class StructrWriteQuery {

    private final String TAG = "StructrWriteQuery";


    private WriteActions action = WriteActions.CREATE_NEW;

    private ExtractedClass extrC;
    private JSONObject jsonObject;
    private Object dataObject;


    public StructrWriteQuery (ExtractedClass extrC, Object dataObject){
        this.extrC = extrC;
        this.dataObject = dataObject;

        jsonObject = extrC.getJsonFromObject(dataObject);

        if(jsonObject.length() == 0){
            Log.e(TAG, "Error write Query. JsonObject is empty.");
            throw new NullPointerException("Cannot create Json from Object");
        }

        //if data on server exist
        if(jsonObject.optString("id").length() > 0){

            action = WriteActions.UPDATE_DATA;
        }
    }

    public void executeAsync(OnAsyncListener asyncListener){

        if(action == WriteActions.CREATE_NEW) {
            PostTask pt = new PostTask(StructrConnector.getUri(), extrC, jsonObject, dataObject, asyncListener);
            pt.execute();

        }else if(action == WriteActions.UPDATE_DATA){

            PutTask pt = new PutTask(StructrConnector.getUri(), extrC, jsonObject, dataObject, asyncListener);
            pt.execute();
        }
    }

    /**
     * Executes the request Synchronous! But beware: this request fails if you are on the main Thread.
     *
     * @param getJsonObject if true returns JSONObject instead of DataObject
     * @return DataObject or JSONObject
     */
    public Object executeSync(boolean getJsonObject){

        if(action == WriteActions.CREATE_NEW){
            PostTask pt = new PostTask(StructrConnector.getUri(), extrC, jsonObject, dataObject, null);
            String id = pt.syncHttpRequest();
            if(id != null){
                pt.addIdToObject(id);

                if(getJsonObject){
                    return  pt.getJsonObject();
                }else{
                    return pt.getDataObject();
                }

            }else{
                Log.e(TAG, "Error generating sync ID");
            }
        }
        return null;
    }

    // ENUM

    private enum WriteActions{
        CREATE_NEW, UPDATE_DATA
    }
}
