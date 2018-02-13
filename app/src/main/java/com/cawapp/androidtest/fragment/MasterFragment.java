package com.cawapp.androidtest.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cawapp.androidtest.MainActivity;
import com.cawapp.androidtest.Settings;
import com.cawapp.androidtest.R;
import com.cawapp.androidtest.adapter.MasterAdapter;
import com.cawapp.androidtest.base.BaseFragment;
import com.cawapp.androidtest.model.Item;
import com.cawapp.androidtest.tools.HttpClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.cawapp.androidtest.Settings.URL_MASTER;


public class MasterFragment extends BaseFragment {
    final String TAG = "MasterFragment";

    OnItemSelectedListener mItemSelectedListener;

    public interface OnItemSelectedListener {
        public void onItemSelected(Item item);
    }

    MainActivity activity;

    private ArrayList<Item> itemsList = new ArrayList<>();
    public boolean isLoaded = false;
    int selectedIdx;
    boolean isLoading = false;


    RecyclerView recyclerView;
    MasterAdapter adapter;
    MasterAdapter.ItemClickListener itemClickListener;

    LinearLayout errorPanel;
    HttpClient httpClient;






    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("androidtest.itemsList", itemsList);
        outState.putSerializable("androidtest.selectedIdx", selectedIdx);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_master, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (MainActivity) context;
            try {
                mItemSelectedListener = (OnItemSelectedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
            }

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        httpClient = new HttpClient(getActivity());



        recyclerView = getView().findViewById(R.id.recycler_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        itemClickListener = new MasterAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position) {



                itemsList.get(selectedIdx).setSelected(false);
                adapter.notifyItemChanged(selectedIdx);

                selectedIdx = position;

                itemsList.get(position).setSelected(true);
                adapter.notifyItemChanged(position);

                if(!httpClient.isConnected()){
                    showToast(getString(R.string.not_connected));
                    errorPanel.setVisibility(View.VISIBLE);
                    return;
                }

                mItemSelectedListener.onItemSelected(itemsList.get(position));
            }
        };

        errorPanel = getView().findViewById(R.id.error_panel);
        errorPanel.setVisibility(View.GONE);

        Button btnRetry = getView().findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });


        if (savedInstanceState != null) {
            itemsList = (ArrayList<Item>) savedInstanceState.getSerializable("androidtest.itemsList");
            selectedIdx = savedInstanceState.getInt("androidtest.selectedIdx");
            adapter = new MasterAdapter(getContext(), itemsList,  itemClickListener);
            adapter.activeSelection(activity.isDualMode());
            recyclerView.setAdapter(adapter);
        } else {
            loadData();
        }
    }


    public void loadData(){
        if(isLoading){
            return;
        }

        if(!httpClient.isConnected()){
            showToast(getString(R.string.not_connected));
            errorPanel.setVisibility(View.VISIBLE);
            return;
        }
        errorPanel.setVisibility(View.GONE);

        isLoading = true;

        httpClient.get(URL_MASTER, new HttpClient.OnRequestCallBack() {
            @Override
            public void onComplete(String responseData) {
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    itemsList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        Item item = new Item(jObj.getString("name"), jObj.getString("image"));
                        itemsList.add(item);
                    }
                    adapter = new MasterAdapter(getContext(), itemsList,  itemClickListener);
                    adapter.activeSelection(activity.isDualMode());
                    recyclerView.setAdapter(adapter);
                    isLoaded = true;
                    isLoading = false;

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast(getString(R.string.error_occurred));
                    Log.d(TAG,"error : json malformed...");
                    errorPanel.setVisibility(View.VISIBLE);
                    isLoading = false;
                }

            }

            @Override
            public void onFailure(String message) {
                showToast(message);
                isLoading = false;
                errorPanel.setVisibility(View.VISIBLE);
            }
        });

    }

}
