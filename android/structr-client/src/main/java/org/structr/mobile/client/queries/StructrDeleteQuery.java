package org.structr.mobile.client.queries;

import android.util.Log;

import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.tasks.DeleteTask;
import org.structr.mobile.client.util.Constants;

/**
 * Created by alex.
 */
public class StructrDeleteQuery {

    private final String TAG = "StructrDeleteQuery";

    private ExtractedClass extrC;
    private String id;
    private Object dataObject;


    public StructrDeleteQuery(ExtractedClass extrC, Object dataObject){
        this.extrC = extrC;
        this.dataObject = dataObject;

        JSONObject jsonObject = extrC.getJsonFromObject(dataObject);

        if(jsonObject.length() == 0){
            if(Constants.isLogging) {
                Log.e(TAG, "Error write Query. JsonObject is empty.");
            }
            throw new NullPointerException("Cannot create Json from Object");
        }

        id = jsonObject.optString("id");

        //if id is empty
        if(id != null && id.length() == 0){
            throw new NullPointerException("Id in object is null or empty");
        }
    }

    public void executeAsync(OnAsyncListener asyncListener){
        DeleteTask dt = new DeleteTask(StructrConnector.getUri(), extrC, id, dataObject, asyncListener);
        dt.execute();
    }
}
