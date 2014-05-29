package org.structr.mobile.client.listeners;

/**
 * Created by alex.
 */
public interface OnAsyncWriteListener<T> {


    public abstract void onAsyncWriteComplete(T result);

    public abstract void onAsyncError(String message);
}
