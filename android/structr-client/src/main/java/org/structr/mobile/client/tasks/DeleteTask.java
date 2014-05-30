package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.structr.mobile.client.StructrConnector;
import org.structr.mobile.client.listeners.OnAsyncListener;
import org.structr.mobile.client.register.objects.ExtractedClass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alex.
 */
public class DeleteTask extends BaseTask {

    private final String TAG = "DeleteTask";

    private String id;
    private Object dataObject;
    private OnAsyncListener asyncListener;


    public DeleteTask(Uri baseUri, ExtractedClass extrC, String id, Object dataObject, OnAsyncListener asyncListener){
        super(baseUri, extrC);
        this.id = id;
        this.dataObject = dataObject;

        this.asyncListener = asyncListener;

    }

    @Override
    protected String doInBackground(String... strings) {


        if(id != null && id.length() > 0){

            String uri = super.getUri()
                    + "/"
                    + id
                    ;

            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(uri);
            HttpResponse response;

            //add credentials to header if available
            StructrConnector.addCredentialsToHeader(httpDelete);

            try{

                response = httpclient.execute(httpDelete);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                    return "created";

                }else{
                    //closes the connection
                    response.getEntity().getContent().close();

                    asyncListener.onAsyncError(statusLine.getReasonPhrase());
                    Log.e(TAG, "AsyncTask Error: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());

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
