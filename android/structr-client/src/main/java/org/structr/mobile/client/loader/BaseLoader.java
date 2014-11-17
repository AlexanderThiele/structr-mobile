package org.structr.mobile.client.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.net.BaseHTTPConnection;
import org.structr.mobile.client.util.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by alex on 17.11.14.
 */
public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

    private String mEntity;

    private String[] mQuery;
    private String mId;
    private String mView;
    private int mPage;
    private int mPageSize;
    private String mSort;
    private String mSortOrder;
    private boolean mInexactSearch;

    public BaseLoader(Context context, String entity) {
        super(context);
        this.mEntity = entity;
    }

    @Override
    public T loadInBackground() {

        //prepare
        if(mPage > 0 && mPageSize > 0){
            searchParams(new String[]{"page=" + mPage, "pageSize=" + mPageSize});
        }
        if(mSort != null && mSort.length() > 0
                && mSortOrder != null && mSortOrder.length() > 0){
            searchParams(new String[]{"sort=" + mSort, "order=" + mSortOrder});
        }
        if(mInexactSearch){
            searchParams("loose=1");
        }

        // build queryString
        String queryString = "";

        // search only for id if not null
        if(mId != null){
            queryString = "/"+mId;
        }else{ // else add all query params
            boolean firstElement = true;

            if(mQuery != null && mQuery.length > 0){
                for(String query : mQuery){

                    queryString += firstElement? "?" : "&";
                    queryString += query;

                    if(firstElement)
                        firstElement = false;
                }
            }
        }

        //build url
        String url = StructrConnector.getUri()
                + Constants.getRestUri()
                + "/"
                + mEntity
                ;

        if(mView != null && mView.length() > 0){
            url += "/"
                    + mView
                    ;
        }
        url += queryString;

        BaseHTTPConnection http = new BaseHTTPConnection();
        String response = http.get(url);

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject == null || jsonObject == JSONObject.NULL)
                return null;

            return parseSuccess(jsonObject.optString("result"));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(T data) {
        if(data == null){
            onError("Error parse is null");
        }else {
            onSuccess(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Search for a specific ID. This request will remove all query-search-parameters
     * @param id
     * @return
     */
    public void searchId(String id){
        if(id != null)
            this.mId = id;
    }

    /**
     * adds query Parameters to the request
     * @param queries
     * @return
     */
    public void searchParams(String... queries){
        int length = mQuery==null?0:mQuery.length;
        length += queries.length;
        if(length == 0)
            return;

        String[] newQueryArray = new String[length];
        int counter = 0;

        if(mQuery != null){
            for (String s : mQuery) {
                newQueryArray[counter] = s;
                counter++;
            }
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

        mQuery = newQueryArray;
    }

    /**
     * search with range parameters (f.e. latitude=[49 TO 50])
     * @param parameter to search
     * @param from value
     * @param to value
     * @return
     */
    public void searchFromTo(String parameter, double from, double to){
        this.searchParams(parameter +  "=[ " + from + " TO " + to + "]");
    }
    /**
     * search with range parameters (f.e. latitude=[49 TO 50])
     * @param parameter to search
     * @param from value
     * @param to value
     * @return
     */
    public void searchFromTo(String parameter, int from, int to){
        this.searchParams(parameter +  "=[ " + from + " TO " + to + "]");
    }

    /**
     * sets the custom view which can be defined inside the schema editor.
     * @param view
     * @return
     */
    public void setView(String view){
        if(view != null && view.length() > 0){
            this.mView = view;
        }
    }

    /**
     * Sets the page and page size for the request.
     * F.e.: page=1 & pageSize=10 : The Server now returns the first 10 entries.
     * @param currentPage
     * @param pageSize
     * @return
     */
    protected void setPageSize(int currentPage, int pageSize){
        if(currentPage > 0 && pageSize > 0){
            this.mPage = currentPage;
            this.mPageSize = pageSize;
        }
    }

    /**
     * sort the result in this query. sortOrder can be asc or desc
     * @param parameter
     * @param sortOrder
     * @return
     */
    public void sortResult(String parameter, String sortOrder){
        this.mSort = parameter;
        this.mSortOrder = sortOrder;
    }

    /**
     * sets the inexactsearch to true - this is the same behavior as set the search params to loose=1
     * @return
     */
    public void setInexactSearch(){
        this.mInexactSearch = true;
    }

    public abstract void onSuccess(T data);
    public abstract void onError(String msg);
    public abstract T parseSuccess(String data);
}
