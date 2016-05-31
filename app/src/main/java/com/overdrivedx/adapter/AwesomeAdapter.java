package com.overdrivedx.adapter;

/**
 * Created by babatundedennis on 6/17/15.
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;


public class AwesomeAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<Messages> mMessages;

    public AwesomeAdapter(Context context, ArrayList<Messages> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Messages message = (Messages) this.getItem(position);

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message.setText(message.getMessage());

        LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
        //check if it is a status message then remove background, and change text color.
        if(message.isStatusMessage()){
            holder.message.setBackgroundDrawable(null);
            lp.gravity = Gravity.LEFT;
            holder.message.setTextColor(Color.parseColor("#000000"));
        }
        else{
            //Check whether message is mine to show green background and align to right
            if(message.isMine()){
                holder.message.setBackgroundResource(R.drawable.purple);
                holder.message.setTextColor(Color.parseColor("#FFFFFF"));
                lp.gravity = Gravity.RIGHT;
            }
            //If not mine then it is from sender to show orange background and align to left
            else{
                holder.message.setBackgroundResource(R.drawable.grey);
                holder.message.setTextColor(Color.parseColor("#000000"));
                lp.gravity = Gravity.LEFT;
            }
            holder.message.setLayoutParams(lp);

        }
        return convertView;
    }

    private static class ViewHolder{
        TextView message;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}