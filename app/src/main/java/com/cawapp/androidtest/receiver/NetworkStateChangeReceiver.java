package com.cawapp.androidtest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;



public class NetworkStateChangeReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        boolean state = false;
        Log.d("app","Network connectivity change");
        if(intent.getExtras()!=null) {
            NetworkInfo networkInfo=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                state = true;
            } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
                state = false;
            }
        }
        sendMessage(state);
    }

    /**
     * Send message to the activity for notify the connexion change state...
     * @param state
     */

    private void sendMessage(boolean state) {
        Intent intent = new Intent("connection-state-changed");
        intent.putExtra("connexion.state", state);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
