package com.overdrivedx.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;

/**
 * Created by babatundedennis on 6/27/15.
 */
public class SettingsProfileAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;
    private final String[] icons;
    private
    static class ViewHolder {
        public TextView text;
        public TextView image;
        public TextView rightArrow;
    }

    public SettingsProfileAdapter(Activity context, String[] icons, String[] names) {
        super(context, R.layout.fragment_settings, names);
        this.context = context;
        this.names = names;
        this.icons= icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.settings_list, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.firstLine);
           // viewHolder.image = (TextView) rowView.findViewById(R.id.icon);
            viewHolder.rightArrow = (TextView) rowView.findViewById(R.id.rightArrow);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.text.setText(names[position]);

        //holder.image.setText(icons[position]);
        //holder.image.setTypeface(Typeface.createFromAsset(context.getAssets(), "icomoon.ttf"));
        holder.rightArrow.setText(String.valueOf((char) 0xe600));
        holder.rightArrow.setTypeface(Typeface.createFromAsset(context.getAssets(), "icomoon.ttf"));

        return rowView;
    }
}