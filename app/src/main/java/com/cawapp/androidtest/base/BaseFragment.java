package com.cawapp.androidtest.base;


import android.support.v4.app.Fragment;
import android.widget.Toast;


public class BaseFragment extends Fragment {


    /**
     * Show Toast message
     * @param message the message
     */
    protected void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
