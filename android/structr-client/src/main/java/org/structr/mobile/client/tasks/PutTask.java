package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncListener;
import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.util.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alex.
 */
public class PutTask extends BaseTask {

    private final String TAG = "PutTask";

    private JSONObject jsonObject;
    private Object dataObject;
    private OnAsyncListener asyncListener;


    public PutTask(Uri baseUri, ExtractedClass extrC, JSONObject data, Object dataObject, OnAsyncListener asyncListener){
        super(baseUri, extrC);
        this.jsonObject = data;
        this.dataObject = dataObject;

        this.asyncListener = asyncListener;

    }

    @Override
    protected String doInBackground(String... strings) {

        String id = jsonObject.optString("id");
        if(id != null && id.length() > 0){

            jsonObject.remove("id");

            String uri = super.getUri()
                    + "/"
                    + id
                    ;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(uri);
            HttpResponse response;


            //add credentials to header if available
            StructrConnector.addCredentialsToHeader(httpPut);

            try{
                StringEntity dataEntity = new StringEntity(jsonObject.toString());

                httpPut.setEntity(dataEntity);
                httpPut.addHeader("content-type", super.contentType);

                response = httpclient.execute(httpPut);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                    return "created";

                }else{
                    //closes the connection
                    response.getEntity().getContent().close();

                    asyncListener.onAsyncError(statusLine.getReasonPhrase());
                    if(Constants.isLogging) {
                        Log.e(TAG, "AsyncTask Error: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
                    }

                    return null;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                asyncListener.onAsyncError(e.getMessage());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                asyncListener.onAsyncError(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                asyncListener.onAsyncError(e.getMessage());
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result == null){
            return;
        }

        asyncListener.onAsyncComplete(dataObject);
    }
}
