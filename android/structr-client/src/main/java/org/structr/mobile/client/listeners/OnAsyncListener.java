package org.structr.mobile.client.listeners;

/**
 * Created by alex.
 */
public interface OnAsyncListener {


    public abstract void onAsyncComplete(Object result);

    public abstract void onAsyncError(String message);
}
