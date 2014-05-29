package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import org.structr.mobile.client.listeners.OnAsyncGetListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alex on 16.05.14.
 */
public class GetTask extends BaseTask {

    private final String TAG = "GetTask";

    private OnAsyncGetListener asyncGetListener;

    public GetTask(Uri baseUri, ExtractedClass extrC, OnAsyncGetListener asyncGetListener){

        super(baseUri, extrC);
        this.asyncGetListener = asyncGetListener;

    }

    @Override
    protected String doInBackground(String... queryParams) {

        String queryString = "";
        boolean firstElement = true;

        if(queryParams != null && queryParams.length > 0){
            for(String query : queryParams){

                queryString += firstElement? "?" : "&";
                queryString += query;

                if(firstElement)
                    firstElement = false;
            }
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        String uri = super.getUri() + queryString;

        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();

            // TODO: handle status requests f.e. 400 Bad Request.
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
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

                JSONArray dataArray = jsonObject.getJSONArray("result");

                for (int i=0; i < dataArray.length(); i++){
                    JSONObject o = dataArray.getJSONObject(i);

                    Object  buildedObject = extrC.buildObjectFromJson(o);

                    if(buildedObject != null){
                        resultList.add(buildedObject);

                    }else{
                        Log.e(TAG, "failed build object from json. is null. " + i + ": " + o.toString());
                    }
                }

            }

            asyncGetListener.onAsyncGetComplete(resultList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
