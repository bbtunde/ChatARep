package com.overdrivedx.adapter;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by babatundedennis on 6/15/15.
 */
public class OpeningTimesAdapter extends BaseAdapter{
    ArrayList<OpeningTimes> myList = new ArrayList<OpeningTimes>();
    LayoutInflater inflater;
    Context context;

    public OpeningTimesAdapter(Context context, ArrayList<OpeningTimes> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public OpeningTimes getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.client_opening_times, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        OpeningTimes currentListData = getItem(position);
        mViewHolder.name.setText(currentListData.getFunctionTitle());
        String open = currentListData.getOpeningTimes();
        Log.e(Constants.TAG, open);

        if(open.contains("-")) {
            String opening_time = open.split("-")[0];
            String closing_time = open.split("-")[1];

            String text=convertToTime(opening_time).toLowerCase() + " - " + convertToTime(closing_time).toLowerCase() ;
            /*
            if(dateToNow(opening_time)){
                text = "Opens at " + convertToTime(opening_time).toLowerCase();
            }
            else{
                if(dateToNow(closing_time)){
                    text = "Closes at " + convertToTime(closing_time).toLowerCase();
                }
                else{
                    text = "Closed";
                }
            }
            */
            mViewHolder.opening.setText(text);
        }
        else{
            mViewHolder.opening.setText("Closed");
        }

        return convertView;
    }


    private class MyViewHolder {
        TextView name, opening;

        public MyViewHolder(View item) {
            name = (TextView) item.findViewById(R.id.function);
            opening= (TextView) item.findViewById(R.id.opening_time);
        }
    }

    private String convertToTime(String t){
        final String NEW_FORMAT = "hh:mm aa";
        final String OLD_FORMAT = "hh:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        return sdf.format(d);
    }

    private boolean dateToNow(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date d = sdf.parse(dateString);
            long diff = new Date().getTime() - d.getTime();

            if((diff/1000) > 0){
                Log.v(Constants.TAG, "diff: " + diff/1000 + "time is true");
                return true;
            }
            else{
                Log.v(Constants.TAG, "diff: " + diff/1000 + "time is false");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}