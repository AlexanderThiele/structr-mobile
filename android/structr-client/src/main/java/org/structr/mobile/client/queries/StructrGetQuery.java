package org.structr.mobile.client.queries;

import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncGetListener;
import org.structr.mobile.client.register.KnownObjects;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.tasks.GetTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by alex.
 */
public class StructrGetQuery {

    private final String TAG = "StructrGetQuery";

    private ExtractedClass getExtrC;
    private String[] query;
    private String id;
    private String view;
    private int page;
    private int pageSize;
    private String sort;
    private String sortOrder;
    private boolean inexactSearch;


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
            try {
                String[] split = s.split("=");
                if(split.length == 2) {
                    newQueryArray[counter] = split[0] + "=" + URLEncoder.encode(split[1], "UTF-8");
                    counter++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        query = newQueryArray;

        return this;
    }


    /**
     * sets the custom view which can be defined inside the schema editor.
     * @param view
     * @return
     */
    public StructrGetQuery setView(String view){

        if(view != null && view.length() > 0){
            this.view = view;
        }

        return this;
    }


    /**
     * Sets the page and page size for the request.
     * F.e.: page=1 & pageSize=10 : The Server now returns the first 10 entries.
     * @param currentPage
     * @param pageSize
     * @return
     */
    public StructrGetQuery setPageSize(int currentPage, int pageSize){

        if(page > 0 && pageSize > 0){
            this.page = currentPage;
            this.pageSize = pageSize;
        }

        return this;
    }

    /**
     * sort the result in this query. sortOrder can be asc or desc
     * @param parameter
     * @param sortOrder
     * @return
     */
    public StructrGetQuery sortResult(String parameter, String sortOrder){

        this.sort = parameter;
        this.sortOrder = sortOrder;

        return this;
    }

    /**
     * sets the inexactsearch to true - this is the same behavior as set the search params to loose=1
     * @return
     */
    public StructrGetQuery setInexactSearch(){
        this.inexactSearch = true;
        return this;
    }


    /**
     * Executes the Request Asynchronous.
     * @param asyncGetListener
     */
    public void executeAsync(OnAsyncGetListener asyncGetListener){

        KnownObjects.clearObjects();

        GetTask getTask = new GetTask(StructrConnector.getUri(), getExtrC, asyncGetListener);

        if(page > 0 && pageSize > 0){
            this.searchParams(new String[]{"page=" + page,"pageSize=" + pageSize});
        }
        if(sort != null && sort.length() > 0
                && sortOrder != null && sortOrder.length() > 0){
            this.searchParams(new String[]{"sort=" + sort, "order=" + sortOrder});
        }
        if(inexactSearch){
            this.searchParams("loose=1");
        }

        if(view != null && view.length() > 0){
            getTask.setView(view);
        }
        if(id != null){
            getTask.setIdSearch(id);
            getTask.execute();
        }else{
            getTask.execute(query);
        }
    }
}
