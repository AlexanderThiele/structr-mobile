package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncGetListener;
import org.structr.mobile.client.register.KnownObjects;
import org.structr.mobile.client.register.objects.ExtractedClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by alex on 16.05.14.
 */
public class GetTask extends BaseTask {

    private final String TAG = "GetTask";

    private OnAsyncGetListener asyncGetListener;
    private String id;
    private String view;

    public GetTask(Uri baseUri, ExtractedClass extrC, OnAsyncGetListener asyncGetListener){

        super(baseUri, extrC);
        this.asyncGetListener = asyncGetListener;

    }

    @Override
    protected String doInBackground(String... queryParams) {

        String queryString = "";

        // search only for id if not null
        if(id != null){

            queryString = "/"+id;

        }else{ // else add all query params

            boolean firstElement = true;

            if(queryParams != null && queryParams.length > 0){
                for(String query : queryParams){

                    queryString += firstElement? "?" : "&";
                    queryString += query;

                    if(firstElement)
                        firstElement = false;
                }
            }
        }

        String uri = super.getUri()
                + (view != null? "/" + view : "")
                + queryString;

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response;
        String responseString = null;


        //add credentials to header if available
        StructrConnector.addCredentialsToHeader(httpGet);

        try {
            response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            // TODO: handle status requests f.e. 400 Bad Request.
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                HttpEntity responseEntity = response.getEntity();
                responseEntity.writeTo(out);
                out.close();
                responseString = out.toString();

            } else{
                //Closes the connection.
                response.getEntity().getContent().close();

                asyncGetListener.onAsyncError(statusLine.getReasonPhrase());
                Log.e(TAG, "AsyncTask Error: " + statusLine.getStatusCode() + " " +statusLine.getReasonPhrase());

                return null;
                //throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            asyncGetListener.onAsyncError(e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
            asyncGetListener.onAsyncError(e.getMessage());

        }
        return responseString;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result == null)
            return;

        try {

            ArrayList<Object> resultList = new ArrayList<Object>();

            JSONObject jsonObject = new JSONObject(result);
            int resultSize = jsonObject.getInt("result_count");

            if(resultSize > 0 ){

                // first try to create Object if size == 1
                boolean singleObjectCreated = false;
                if(resultSize == 1){
                    JSONObject tryObject = jsonObject.optJSONObject("result");
                    if(tryObject != null){
                        Object  buildedObject = extrC.buildObjectFromJson(tryObject);

                        if(buildedObject != null){
                            resultList.add(buildedObject);
                            singleObjectCreated = true;
                        }
                    }
                }

                if(!singleObjectCreated) {
                    JSONArray dataArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject o = dataArray.getJSONObject(i);

                        Object buildedObject = extrC.buildObjectFromJson(o);

                        if (buildedObject != null) {
                            resultList.add(buildedObject);

                        } else {
                            Log.e(TAG, "failed build object from json. is null. " + i + ": " + o.toString());
                        }
                    }
                }

            }

            //clear known objects
            KnownObjects.clearObjects();

            //call async Complete
            asyncGetListener.onAsyncGetComplete(resultList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setIdSearch(String id){
        this.id = id;
    }

    public void setView(String view){
        this.view = view;
    }

}
