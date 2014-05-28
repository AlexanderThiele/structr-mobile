package org.structr.mobile.client.listeners;

import java.util.ArrayList;

/**
 * Created by alex.
 */
public interface OnAsyncGetListener<T>{

    public abstract void onAsyncGetComplete(ArrayList<T> results);

    public abstract void onAsyncError(String message);
}
