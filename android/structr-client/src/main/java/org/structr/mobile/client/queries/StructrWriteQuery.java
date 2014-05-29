package org.structr.mobile.client.queries;

import android.util.Log;

import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncWriteListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.tasks.PostTask;

/**
 * Created by alex.
 */
public class StructrWriteQuery {

    private final String TAG = "StructrWriteQuery";

    private ExtractedClass extrC;
    private JSONObject jsonObject;

    public StructrWriteQuery (ExtractedClass extrC, Object obj){
        this.extrC = extrC;

        jsonObject = extrC.getJson(obj);

        if(jsonObject.length() == 0){
            Log.e(TAG, "Error write Query. JsonObject is empty.");
            throw new NullPointerException("Cannot create Json from Object");
        }
    }

    public void executeAsync(OnAsyncWriteListener asyncWriteListener){
        PostTask pt = new PostTask(StructrConnector.getUri(), extrC, jsonObject, asyncWriteListener );
        pt.execute();
    }

    public Object executeSync(){
        throw new UnsupportedOperationException();
    }
}
