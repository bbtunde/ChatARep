package com.overdrivedx.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.SettingsGeneralAdapter;
import com.overdrivedx.adapter.SettingsProfileAdapter;
import com.overdrivedx.chatarep.AboutActivity;
import com.overdrivedx.chatarep.ProfileActivity;
import com.overdrivedx.chatarep.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Activity activity;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        final ListView profilelistview = (ListView) v.findViewById(R.id.listviewProfile);
        final ListView generallistview = (ListView) v.findViewById(R.id.listviewGeneral);

        String[] profilevalues = new String[] {
                "Edit Profile",
                "Delete",
        };

        String[] profileicon = new String[] {
                String.valueOf((char) 0xe96e),
                String.valueOf((char) 0xe971),
        };

        String[] generalvalues = new String[] {
                "About",
                "Tell a Friend",
                "Feedback & Support",
                "Help"
        };

        String[] generalicon = new String[] {
                String.valueOf((char) 0xe96e),
                String.valueOf((char) 0xe971),
                String.valueOf((char) 0xe971)
        };


        final SettingsProfileAdapter adapter = new SettingsProfileAdapter(getActivity(),profileicon,profilevalues);
        profilelistview.setAdapter(adapter);

        profilelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
             if(position == 0){
                 startActivity(new Intent(activity, ProfileActivity.class));
             }
             else if(position == 1){
                 FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                 Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                 if (prev != null) {
                     ft.remove(prev);
                 }
                 ft.addToBackStack(null);

                 DeleteAccountFragment newFragment = DeleteAccountFragment.newInstance();
                 newFragment.show(ft, "dialog");
             }

            }

        });

        final SettingsGeneralAdapter generaladapter = new SettingsGeneralAdapter(getActivity(),generalicon,generalvalues);
        generallistview.setAdapter(generaladapter);

        generallistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if(position == 0){
                    startActivity(new Intent(activity, AboutActivity.class));
                }
                else if(position == 1){
                    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("sms_body","Hey, start using ChatRep to chat with companies! Download now " + Constants.DOWNLOAD_LINK);
                    startActivity(smsIntent);
                }
                else if(position == 2){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", Constants.SUPPORT_EMAIL, null));
                    startActivity(Intent.createChooser(emailIntent, "Contact ChatRep"));
                }
                else if(position == 3){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP));
                    startActivity(browserIntent);
                }
            }

        });
        return v;
    }

    private void addProfileListView(){

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSettingsFragmentInteraction(uri);
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
        public void onSettingsFragmentInteraction(Uri uri);
    }

}
