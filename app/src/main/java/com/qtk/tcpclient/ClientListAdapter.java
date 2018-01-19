package com.qtk.tcpclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jack on 2018-01-19.
 */

public class ClientListAdapter extends BaseAdapter {
    private ArrayList<String> listItems;
    private LayoutInflater layoutInflater;

    public ClientListAdapter(Context context, ArrayList<String> arrayList){
        listItems = arrayList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return listItems.size();
    }

    @Override
    public Object getItem(int i){
        return null;
    }

    @Override
    public long getItemId(int i){
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = layoutInflater.inflate(R.layout.list_item, null);
        }

        String stringItem = listItems.get(position);
        if(stringItem!=null){
            TextView itemName = view.findViewById(R.id.list_item_text_view);
            if(itemName != null){
                itemName.setText(stringItem);
            }
        }

        return view;
    }
}
