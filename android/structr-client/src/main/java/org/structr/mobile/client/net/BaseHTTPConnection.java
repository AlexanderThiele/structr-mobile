package org.structr.mobile.client.net;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.structr.mobile.client.StructrConnector;

import java.io.IOException;

/**
 * Created by alex on 17.11.14.
 */
public class BaseHTTPConnection {

    public String get(String url){
        Log.e("URL", "" + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-user", StructrConnector.getUserName())
                .addHeader("X-password", StructrConnector.getPassword())
                .build();
        String responseString = null;
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                responseString = response.body().string();
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        return responseString;
    }
}
