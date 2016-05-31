package com.overdrivedx.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.overdrivedx.adapter.Client;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.DropDownAdapter;
import com.overdrivedx.chatarep.MainActivity;
import com.overdrivedx.chatarep.R;
import com.overdrivedx.chatarep.SearchResultsActivity;
import com.overdrivedx.custom.CAutoCompleteView;
import com.overdrivedx.database.DatabaseHandler;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CAutoCompleteView actv;
    DropDownAdapter myAdapter;
    String[] item = new String[] {" "};
    DatabaseHandler db;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       final View v = inflater.inflate(R.layout.fragment_search, container, false);
        actv = (CAutoCompleteView) v.findViewById(R.id.autoCompleteTextView1);
        //actv.getBackground().clearColorFilter();
        int color = Color.parseColor("#ffffff");

        //actv.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        final TextView txt = (TextView)v.findViewById(R.id.searchicon);
        txt.setText(String.valueOf((char) 0xe986));

        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "icomoon.ttf"));
        actv.setThreshold(3);


        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item = getItemsFromDb(s.toString());

                // update the adapater
                myAdapter.notifyDataSetChanged();
                myAdapter = new DropDownAdapter(getActivity(), R.layout.autocomplete_drop_down, item);
                actv.setAdapter(myAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView tx = (TextView)v.findViewById(R.id.noresult);

                if (s.length() > 2) {

                    if (!actv.isPopupShowing()) {
                        tx.setText("No results");

                    }
                    else{
                        tx.setText("");
                    }

                    return;
                }
                else{
                    tx.setText("");
                }
            }
        });

        myAdapter = new DropDownAdapter(getActivity(), R.layout.autocomplete_drop_down, item);
        actv.setAdapter(myAdapter);

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                Bundle bundle = new Bundle();

                String ClientID = db.getClientID(arg0.getItemAtPosition(arg2).toString());
                String ClientName = arg0.getItemAtPosition(arg2).toString();


                bundle.putString("client_id", ClientID);
                bundle.putString("client_name", ClientName);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSearchFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        public void onSearchFragmentInteraction(Uri uri);
    }

    public String[] getItemsFromDb(String searchTerm){
        List<Client> clientList = db.getClient(searchTerm);
        int rowCount = clientList.size();
        String[] item = new String[rowCount];
        int x = 0;
        for (Client record : clientList) {
            item[x] = record.getName();
            x++;
        }
        return item;
    }
}
