package com.overdrivedx.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;
import com.overdrivedx.utils.EmailValidator;


public class DeleteAccountFragment extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static DeleteAccountFragment newInstance() {
        DeleteAccountFragment  f = new DeleteAccountFragment ();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("num", "");
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delete_account, container, false);
        TextView red_caution = (TextView)v.findViewById(R.id.del_desc);

        TextView xclose = (TextView)v.findViewById(R.id.xclose);

        xclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        red_caution.setText(String.valueOf((char) 0xe601));
        red_caution.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "icomoon.ttf"));


        Button del = (Button) v.findViewById(R.id.delete_account);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {


            }

        });


        return v;
    }

    /*
    private class deleteAccount extends AsyncTask<Void, String, Void>{
        ProgressDialog pDialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Deleting ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);

            pDialog.show();
            super.onPreExecute();

        }
    /*    @Override
        protected Void doInBackground(Void... params) {
            //create user.
            if(!db.userExists(user_id)) {
                if(createUserOnChatServer()){
                    if(saveToServer()){
                        publishProgress("Profile Saved");
                    }
                    else{
                        publishProgress("Unable to save");
                    }
                }
                else{
                    publishProgress("Unable to save");
                }
            }
            else{
                if(saveToServer())
                    publishProgress("Profile Saved");
                else
                    publishProgress("Unable to save");
            }
            return null;
        }
     */
}