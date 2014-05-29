package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.util.Constants;

/**
 * Created by alex.
 */
public abstract class BaseTask extends AsyncTask<String, Void, String> {

    protected String contentType = "application/json;charset=UTF-8";

    protected Uri baseUri;
    protected ExtractedClass extrC;

    public BaseTask(Uri baseUri, ExtractedClass extrC){
        this.baseUri = baseUri;
        this.extrC = extrC;
    }


    public String getUri(){

        String url = "";
        url += baseUri
                + Constants.getRestUri()
                + "/"
                + extrC.getClazzName()
                ;

        return url;
    }

}
