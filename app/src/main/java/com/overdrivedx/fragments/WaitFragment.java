package com.overdrivedx.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.overdrivedx.chatarep.R;
import com.overdrivedx.database.DatabaseHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WaitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String CLIENT_ID = "client_id";

    // TODO: Rename and change types of parameters
    private String client_id;

    private OnFragmentInteractionListener mListener;

    TextView splash;
    Button email;
    ProgressBar pb;
    DatabaseHandler db;
    Activity activity;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WaitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaitFragment newInstance(String param1, String param2) {
        WaitFragment fragment = new WaitFragment();
        Bundle args = new Bundle();
        //args.putString(CLIENT_ID, client_id);
        fragment.setArguments(args);
        return fragment;
    }

    public WaitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            client_id = getArguments().getString(CLIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wait, container, false);
        splash = (TextView) v.findViewById(R.id.splash_message);
        pb = (ProgressBar) v.findViewById((R.id.progressBar));
        db = new DatabaseHandler(getActivity());

        email = (Button) v.findViewById((R.id.email_button));

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onWaitFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onWaitFragmentInteraction(Uri uri);
    }

    public void UpdateSplashMessage(final String s) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                splash.setText(s);
            }
        });
    }

    public void showEmailButton(Boolean show){
        if(show){
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    email.setVisibility(View.VISIBLE);

                    pb.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


}