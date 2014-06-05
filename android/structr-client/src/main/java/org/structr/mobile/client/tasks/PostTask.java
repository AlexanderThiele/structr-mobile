package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncListener;
import org.structr.mobile.client.register.objects.ExtractedClass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by alex.
 */
public class PostTask extends BaseTask{

    private final String TAG = "PostTask";

    private JSONObject jsonObject;
    private Object dataObject;

    private OnAsyncListener asyncListener;

    private ArrayList<Object> objectsToCreateFirst;


    public PostTask(Uri baseUri, ExtractedClass extrC, JSONObject data, Object dataObject, OnAsyncListener asyncListener){
        super(baseUri, extrC);
        this.jsonObject = data;
        this.dataObject = dataObject;
        this.asyncListener = asyncListener;

    }

    @Override
    protected String doInBackground(String... strings) {

        // TODO check for inner objects and do syncRequests


        return syncHttpRequest(strings);
    }

    public String syncHttpRequest(String... strings){

        String uri = super.getUri();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        HttpResponse response;
        String responseString = null;

        //add credentials to header if available
        StructrConnector.addCredentialsToHeader(httpPost);

        try {
            StringEntity dataEntity = new StringEntity(jsonObject.toString());

            httpPost.setEntity(dataEntity);
            httpPost.addHeader("content-type", super.contentType);

            response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_CREATED){

                String[] splittedLocation = response.getFirstHeader("Location").getValue().split("/");
                String id = splittedLocation[splittedLocation.length-1];

                responseString = id;

            }else{
                //closes the connection
                response.getEntity().getContent().close();

                if(asyncListener != null)
                    asyncListener.onAsyncError(statusLine.getReasonPhrase());

                Log.e(TAG, "AsyncTask Error: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());

                return null;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if(asyncListener != null)
                asyncListener.onAsyncError(e.getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            if(asyncListener != null)
                asyncListener.onAsyncError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            if(asyncListener != null)
                asyncListener.onAsyncError(e.getMessage());
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result == null)
            return;

        // add id to object
        if(!addIdToObject(result)){
            asyncListener.onAsyncError("Error trying to generate object.");
        }


    }

    public boolean addIdToObject(String id){
        try {
            jsonObject.put("id", id);

            if(!extrC.setValueToObject("id", dataObject, id)){
                Log.e(TAG, "ERROR SETTING ID TO OBJECT");
            }

            if(asyncListener != null)
                asyncListener.onAsyncComplete(dataObject);

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONObject Error: put id to object");
        }

        return false;
    }

    public Object getDataObject(){
        return dataObject;
    }

    public JSONObject getJsonObject(){
        return jsonObject;
    }
}
