package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.structr.mobile.client.listeners.OnAsyncWriteListener;
import org.structr.mobile.client.register.objects.ExtractedClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex.
 */
public class PostTask extends BaseTask{

    private final String TAG = "PostTask";

    private Uri baseUri;
    private ExtractedClass extrC;
    private JSONObject jsonObject;

    private OnAsyncWriteListener asyncWriteListener;


    public PostTask(Uri baseUri, ExtractedClass extrC, JSONObject data, OnAsyncWriteListener asyncWriteListener){

        this.baseUri = baseUri;
        this.extrC = extrC;
        this.jsonObject = data;
        this.asyncWriteListener = asyncWriteListener;

    }

    @Override
    protected String doInBackground(String... strings) {

        String uri = buildBaseUri(baseUri, extrC);

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        HttpResponse response;
        String responseString = null;

        try {
            StringEntity dataEntity = new StringEntity(jsonObject.toString());
            // TODO dataEntity.setContentType();
            httpPost.setEntity(dataEntity);

            response = httpclient.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_CREATED){

                String[] splittedLocation = response.getFirstHeader("Location").getValue().split("/");
                String id = splittedLocation[splittedLocation.length-1];

                // location = http://structr.org:8082/structr/rest/Test/cad0df04b16143bf8b61824e1e21257e

                responseString = id;

            }else{
                //closes the connection
                response.getEntity().getContent().close();

                asyncWriteListener.onAsyncError(statusLine.getReasonPhrase());
                Log.e(TAG, "AsyncTask Error: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());

                return null;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            asyncWriteListener.onAsyncError(e.getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            asyncWriteListener.onAsyncError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            asyncWriteListener.onAsyncError(e.getMessage());
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result == null)
            return;

        try {
            jsonObject.put("id", result);

            Object resultObj = extrC.buildObjectFromJson(jsonObject);

            asyncWriteListener.onAsyncWriteComplete(resultObj);
            return;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONObject Error: put id to object");
        }

        asyncWriteListener.onAsyncError("Error trying to generate object.");
    }
}
