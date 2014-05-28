package org.structr.mobile.client.queries;

import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncGetListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.tasks.GetTask;

/**
 * Created by alex.
 */
public class StructrGetQuery {

    private final String TAG = "StructrGetQuery";

    private ExtractedClass getExtrC;
    private String[] query;


    public StructrGetQuery(ExtractedClass extrC, String... query){
        this.getExtrC = extrC;
        this.query = query;
    }


    /**
     * Executes the Request Asynchronous.
     * @param asyncGetListener
     */
    public void executeAsync(OnAsyncGetListener asyncGetListener){

        GetTask getTask = new GetTask(StructrConnector.getUri(), getExtrC, asyncGetListener);

        getTask.execute(query);
    }
}
