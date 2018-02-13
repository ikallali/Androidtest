package com.cawapp.androidtest;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


import com.cawapp.androidtest.fragment.DetailFragment;
import com.cawapp.androidtest.fragment.MasterFragment;
import com.cawapp.androidtest.model.Item;


public class MainActivity extends AppCompatActivity implements MasterFragment.OnItemSelectedListener {

    boolean isDual;
    boolean isDetailActive;

    MasterFragment masterFragment;
    DetailFragment detailFragment;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("androidtest.isDetailActive", isDetailActive);
        getSupportFragmentManager().putFragment(savedInstanceState, "masterFragment", masterFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "detailFragment", detailFragment);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LOG","onCreate");

        //register the message for the connexion internet
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("connection-state-changed"));


        isDual = false;
        isDetailActive = false;

        //Determine rotation
        boolean isLandscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        //set Dual Mode for tablet in landscape mode
        if(getResources().getBoolean(R.bool.isTablet) && isLandscape) {
            isDual = true;
        }


        if(savedInstanceState != null){
            isDetailActive = savedInstanceState.getBoolean("androidtest.isDetailActive");

            masterFragment = (MasterFragment) getSupportFragmentManager().getFragment(savedInstanceState, "masterFragment");
            detailFragment = (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, "detailFragment");
        }
        else{
            masterFragment = new MasterFragment();
            detailFragment = new DetailFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.master_part, masterFragment);
        ft.replace(R.id.detail_part, detailFragment);

        ft.commit();




        //if is not dualMode, show only the active fragment...
        if(!isDual){
            if(isDetailActive){
                showDetail();
            } else {
                showMaster();
            }
        }

    }

    /**
     * Show the master fragment and hide other
     */
    void showMaster(){
        findViewById(R.id.master_part).setVisibility(View.VISIBLE);
        findViewById(R.id.detail_part).setVisibility(View.GONE);
        isDetailActive = false;
    }

    /**
     * Show the detail fragment and hide other
     */
    void showDetail(){
        findViewById(R.id.master_part).setVisibility(View.GONE);
        findViewById(R.id.detail_part).setVisibility(View.VISIBLE);
        isDetailActive = true;
    }

    /**
     * function to send the item from master fragment to detail fragement
     * @param item the item to send to the detail fragement
     */
    public void onItemSelected(Item item) {

        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_part);
        detailFragment.setItem(item);

        // If monoMode, open the detail fragement
        if (!isDual) {
            showDetail();
        }
    }

    @Override
    public void onBackPressed() {
        // on monoMode, overide the back button to open the master fragment
        if(!isDual && isDetailActive) {
            showMaster();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.quit)
                    .setMessage(R.string.really_quit)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }

    /**
     * Receiver for notify the connexion change
     * If online, do a loading of the data
     */
    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connexion_state = intent.getBooleanExtra("connexion.state",false);
            if(!masterFragment.isLoaded && connexion_state){
                masterFragment.loadData();
            }

        }
    };

    @Override
    protected void onDestroy() {
        // Destroy the Broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }


    /**
     * Return if the dualMode active
     * @return true or false
     */
    public boolean isDualMode(){
        return isDual;
    }


}


