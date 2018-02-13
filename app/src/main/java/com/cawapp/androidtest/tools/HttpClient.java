package com.cawapp.androidtest.tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.cawapp.androidtest.R;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpClient  {

    private Activity activity;


    public HttpClient(Activity activity) {
        this.activity = activity;
    }

    public interface OnRequestCallBack{
        void onComplete(String responseData);
        void onFailure(String message);
    }

    public void get(String url, final OnRequestCallBack callback){
        new MyAsyncTask(activity, url, callback).execute();
    }

    private static class MyAsyncTask extends  AsyncTask<Void, Void, String> {

        OkHttpClient client = new OkHttpClient();
        Request request;
        WeakReference<Activity> actRef;
        OnRequestCallBack callback;


        MyAsyncTask(Activity activity, String url, OnRequestCallBack callback) {
            request = new okhttp3   .Request
                    .Builder()
                    .url(url)
                    .build();

            this.callback = callback;
            actRef = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(Void... params) {

            final Activity activity = actRef.get();
            try {
                final Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.d("LOG","error "+response.code());
                    if (callback != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(activity.getString(R.string.error_occurred) + " (" + response.code() + ")");
                            }
                        });

                    }
                    return null;
                }
                return response.body().string();
            } catch (final Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(activity.getString(R.string.error_occurred) + "("+e.getMessage()+")");
                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (callback != null && s != null) {
                callback.onComplete(s);
            }
            else{
                Log.d("LOG","httpCallback not defined...");
            }
        }
    }

    /**
     * Determine if the device is connected or not
     * @return
     */
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }
        return false;
    }


}
