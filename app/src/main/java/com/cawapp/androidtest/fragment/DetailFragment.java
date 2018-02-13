package com.cawapp.androidtest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cawapp.androidtest.R;
import com.cawapp.androidtest.base.BaseFragment;
import com.cawapp.androidtest.model.Item;
import com.cawapp.androidtest.tools.HttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cawapp.androidtest.Settings.URL_DETAIL;


public class DetailFragment extends BaseFragment {

    LinearLayout bgLayout;
    TextView textView;
    ImageView imageView;

    Item item;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("androidtest.item", item);

        super.onSaveInstanceState(outState);
    }


    public static DetailFragment newInstance(Item item) {
        DetailFragment me = new DetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("param.item", item);
        me.setArguments(args);

        return me;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        bgLayout = getView().findViewById(R.id.bglayout);
        textView = getView().findViewById(R.id.name);
        imageView = getView().findViewById(R.id.image);

        resetContent();

        if( getArguments() != null){
            item = (Item)getArguments().getSerializable("param.item");
            showItem();
        }
        if (savedInstanceState != null) {
            item = (Item) savedInstanceState.getSerializable("androidtest.item");
            showItem();
        }

    }

    /**
     * Set the item from master and load content from ws
     * @param item
     */
    public void setItem(Item item){
        this.item = item;
        resetContent();
        loadDetail(item.getName());
    }

    /**
     * reset the view
     */
    void resetContent(){
        imageView.setImageResource(0);
        textView.setText("");
    }

    /**
     * set the data to the view
     */
    public void showItem(){

        if(item != null){
            textView.setText(item.getText());
            Picasso.with(getContext())
                    .load(item.getBigImage())
                    .into(imageView);
            bgLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Load the ws
     * @param id ID of the item
     */

    void loadDetail(String id){

        String url = URL_DETAIL+id;

        HttpClient httpClient = new HttpClient(getActivity());

        if(!httpClient.isConnected()){
            showToast(getString(R.string.not_connected));
            return;
        }

        httpClient.get(url, new HttpClient.OnRequestCallBack() {
                @Override
                public void onComplete(String responseData) {
                    try {
                        Log.d("LOG",responseData);
                        JSONObject jsonObj = new JSONObject(responseData);
                        item.setText(jsonObj.getString("text"));
                        item.setBigImage(jsonObj.getString("image"));
                        showItem();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d("LOG",message);
                }
            }
        );
    }
}
