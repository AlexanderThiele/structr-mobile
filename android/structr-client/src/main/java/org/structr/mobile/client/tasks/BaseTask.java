package org.structr.mobile.client.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import org.structr.mobile.client.register.objects.ExtractedClass;
import org.structr.mobile.client.util.Constants;

/**
 * Created by alex.
 */
public abstract class BaseTask extends AsyncTask<String, Void, String> {


    public String buildBaseUri(Uri baseUri, ExtractedClass extrC){

        String url = "";

        url += baseUri
                + Constants.getRestUri()
                + "/"
                + extrC.getClazzName()
                ;

        return url;
    }

}
