package com.overdrivedx.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.OpeningTimes;
import com.overdrivedx.adapter.OpeningTimesAdapter;
import com.overdrivedx.adapter.User;
import com.overdrivedx.chatarep.ChatActivity;
import com.overdrivedx.chatarep.R;
import com.overdrivedx.database.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ClientFragment extends Fragment{

    public static final String CLIENT_JSON = "json";
    public static final String CLIENT_ID = "client_id";
    private String json, client_id;
    private Context context;
    private ArrayList<OpeningTimes> myList = new ArrayList<OpeningTimes>();
    private OnFragmentInteractionListener mListener;
    private DatabaseHandler db;
    public ClientFragment() {}
    Activity activity;
    private String doftW ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(activity);

        if(getArguments() != null) {
            json = getArguments().getString(CLIENT_JSON);
            client_id = getArguments().getString(CLIENT_ID);
            Log.e(Constants.TAG, json);
        }
        else{
            Log.e(Constants.TAG, " error no client id");
        }
        Log.v(Constants.TAG, json);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client, container, false);
        ListView lvDetail = (ListView) v.findViewById(R.id.lView);

        TextView tv = (TextView)v.findViewById(R.id.client_desc);
        Log.v(Constants.TAG, db.getClientMessage(client_id));
        tv.setText(db.getClientMessage(client_id));

        try {
            getDataInList(new JSONObject(json));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        lvDetail.setAdapter(new OpeningTimesAdapter(context, myList));
        lvDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getUserID() == null){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Oops! It seems you have not filled out your profile details. Go to settings.");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.create().show();

                }
                else {
                    OpeningTimes op = myList.get(position);
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra(ChatActivity.FUNCTION_ID, op.getFunctionID());
                    i.putExtra(ChatActivity.USER_ID, getUserID());
                    i.putExtra(ChatActivity.POPUP_MESSAGE, op.getPopupMessage());
                    i.putExtra(ChatActivity.CLIENT_ID, client_id);
                    startActivity(i);
                }
            }
        });
        return v;
    }

    private String getUserID(){
        DatabaseHandler db = new DatabaseHandler(context);
        List<User> user = db.getUser();
        int rowCount = user.size();
        String user_id = null;
        if(rowCount ==1) {
            for (User record : user) {
                user_id = record.getID();
            }
        }
        return user_id;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onClientFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onClientFragmentInteraction(Uri uri);
    }

    private void getDataInList(JSONObject response) {

        try {
            Boolean error = Boolean.valueOf(response.getString("error"));
            if(!error) {
                JSONArray feedArray = response.getJSONArray("functions");
                Log.v(Constants.TAG, feedArray.toString());

                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    OpeningTimes ot = new OpeningTimes();
                    ot.setFunctionID(feedObj.getString("id"));
                    ot.setFunctionTitle(feedObj.getString("title"));
                    ot.setPopupMessage(feedObj.getString("popup_message"));
                    ot.setOpeningTimes(feedObj.getString(dayOftheWeek()));
                    myList.add(ot);
                }
            }
            else{
                Log.e(Constants.TAG, "Unable to parse");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String dayOftheWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String ff = "";

        if(day == Calendar.SUNDAY){
            ff = "sun";
        }
        else if(day == Calendar.MONDAY) {
            ff = "mon";
        }
        else if(day == Calendar.TUESDAY) {
            ff = "tue";
        }
        else if(day == Calendar.WEDNESDAY) {
            ff = "wed";
        }
        else if(day == Calendar.THURSDAY) {
            ff = "thu";
        }
        else if(day == Calendar.FRIDAY) {
            ff = "fri";
        }
        else if(day == Calendar.SATURDAY) {
            ff = "sat";
        }

        return ff;
    }

}
