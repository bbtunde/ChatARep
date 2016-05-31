package com.overdrivedx.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;

/**
 * Created by babatundedennis on 6/28/15.
 */
public class DropDownAdapter extends ArrayAdapter<String> {

    Context mContext;
    int layoutResourceId;
    String data[] = null;

    public DropDownAdapter(Context mContext, int layoutResourceId, String[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
             */
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            //MyObject objectItem = data[position];

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
            textViewItem.setText(data[position]);



        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }

    public class MyObject {

        public String objectName;

        // constructor for adding sample data
        public MyObject(String objectName){

            this.objectName = objectName;
        }

    }
}
