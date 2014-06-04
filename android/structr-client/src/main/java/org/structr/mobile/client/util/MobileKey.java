package org.structr.mobile.client.util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import org.structr.mobile.client.tasks.BaseTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alex.
 */
public class MobileKey {

    private static final String TAG = "MobileKey";

    private static String mobileKey;
    private static SharedPreferences prefs;
    private static String mobileKeySharedPrefKey;


    public static void generatePublicKey(SharedPreferences prefs, String mobileKeySharedPrefKey){

        MobileKey.prefs = prefs;
        MobileKey.mobileKeySharedPrefKey = mobileKeySharedPrefKey;

        MobileKeyAsyncTask mobileKeyAsyncTask = new MobileKeyAsyncTask();
        mobileKeyAsyncTask.execute();

    }

    public static void setMobileKey(String _mobileKey){
        MobileKey.mobileKey = _mobileKey;
    }
    public static String getMobileKey(){
        return mobileKey;
    }

    private static class MobileKeyAsyncTask extends AsyncTask<String, Void, String>{

        public MobileKeyAsyncTask(){
        }

        @Override
        protected String doInBackground(String... strings) {
            String uri = StructrConnector.getUri().toString()
                    + Constants.getRestUri()
                    + "/"
                    + "mobile_key"
                    ;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            HttpResponse response;

            //add credentials to header if available
            StructrConnector.addCredentialsToHeader(httpPost);

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("device_name", Build.MODEL);
                jsonObject.put("os", "Android api " + Build.VERSION.SDK_INT);

                StringEntity dataEntity = new StringEntity(jsonObject.toString());

                httpPost.setEntity(dataEntity);

                response = httpclient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_CREATED){

                    String[] splittedLocation = response.getFirstHeader("Location").getValue().split("/");


                    mobileKey = splittedLocation[splittedLocation.length-1];
                    return mobileKey;

                }else{
                    //closes the connection
                    response.getEntity().getContent().close();

                    Log.e(TAG, "Error generate Mobile Key: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String key) {
            if(key != null) {
                prefs.edit().putString(MobileKey.mobileKeySharedPrefKey, key).commit();
            }
        }
    }
}

