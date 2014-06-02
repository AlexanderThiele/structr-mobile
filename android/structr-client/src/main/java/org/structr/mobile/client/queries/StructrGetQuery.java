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
    private String id;


    public StructrGetQuery(ExtractedClass extrC, String... query){
        this.getExtrC = extrC;
        this.query = query;
    }

    /**
     * Search for a specific ID. This request will remove all query-search-parameters
     * @param id
     * @return
     */
    public StructrGetQuery searchId(String id){
        if(id != null)
            this.id = id;
        return this;
    }

    /**
     * adds query Parameters to the request
     * @param queries
     * @return
     */
    public StructrGetQuery searchParams(String... queries){
        int length = query==null?0:query.length;
        length += queries.length;
        if(length == 0)
            return this;

        String[] newQueryArray = new String[length];
        int counter = 0;

        for(String s : query){
            newQueryArray[counter] = s;
            counter++;
        }

        for(String s : queries){
            newQueryArray[counter] = s;
            counter++;
        }

        query = newQueryArray;

        return this;
    }


    /**
     * Executes the Request Asynchronous.
     * @param asyncGetListener
     */
    public void executeAsync(OnAsyncGetListener asyncGetListener){

        GetTask getTask = new GetTask(StructrConnector.getUri(), getExtrC, asyncGetListener);
        if(id != null){
            getTask.setIdSearch(id);
            getTask.execute();
        }else{
            getTask.execute(query);
        }
    }
}
