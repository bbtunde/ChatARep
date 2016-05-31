package com.overdrivedx.chatarep;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.ClientFragment;
import com.overdrivedx.fragments.WaitFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SearchResultsActivity extends AppCompatActivity implements
        WaitFragment.OnFragmentInteractionListener,
        ClientFragment.OnFragmentInteractionListener{

    private String client_id;
    private String client_name;

    private FragmentTransaction fragmentTransaction;
    private WaitFragment waitFragment;
    private getClientDetails gCD;
    private boolean gCDfinished = false;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        db = new DatabaseHandler(this);

        if (findViewById(R.id.searchfragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragmentTransaction = getSupportFragmentManager().beginTransaction();

            waitFragment = new WaitFragment();
            Bundle args = new Bundle();
            args.putString(WaitFragment.CLIENT_ID, client_id);
            waitFragment.setArguments(args);

            if (savedInstanceState == null) {
                //fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.searchfragment_container, waitFragment).commitAllowingStateLoss();
            }
        }

        client_id = getIntent().getStringExtra("client_id");
        client_name = getIntent().getStringExtra("client_name");
        setTitle(client_name);
        ActionBarWithQuit();

        gCD = new getClientDetails();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            gCD.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            gCD.execute((Void[]) null);

    }

    @Override
    public void onClientFragmentInteraction(Uri uri) {

    }

    private class getClientDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if(client_id != null){
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                        Constants.GET_CLIENT + "?client_id=" + client_id, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            //Log.e(Constants.TAG, response.toString());
                            parseRes(response.toString());
                        }
                        else{
                            waitFragment.UpdateSplashMessage("Unable to fetch opening times.");
                            waitFragment.showEmailButton(true);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waitFragment.UpdateSplashMessage("A network error has occurred.");
                        waitFragment.showEmailButton(true);
                    }
                }){

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                jsonReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(jsonReq);
            }
            else{
                waitFragment.UpdateSplashMessage("Unable to find company details.");
                waitFragment.showEmailButton(true);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
             gCDfinished = true;
        }
    }

    private void parseRes(String s) {
        ClientFragment newFragment = new ClientFragment();
        Bundle args = new Bundle();
        args.putString(ClientFragment.CLIENT_JSON, s);
        args.putString(ClientFragment.CLIENT_ID, client_id);
        newFragment.setArguments(args);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.searchfragment_container, newFragment);
        //fragmentTransaction.remove(waitFragment);
        fragmentTransaction.addToBackStack(newFragment.getClass().getName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWaitFragmentInteraction(Uri uri) {

    }

    @Override
    public void onResume(){
        super.onResume();
        //View dV = getWindow().getDecorView();
        //int uiOpts = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //dV.setSystemUiVisibility(uiOpts);

       // android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void ActionBarWithQuit(){
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);

        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onBackPressed() {
       do_back();
    }

    public void xQuit(View v){
       do_back();

    }

    private void do_back(){
        if(gCD.getStatus() == AsyncTask.Status.FINISHED && gCDfinished)
            finish();
        else
            gCD.cancel(true);
        finish();
    }

    public void sendEmail(View v){
        String email = db.getClientEmail(client_id);
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n Sent From ChatRep");

        startActivity(Intent.createChooser(emailIntent, "Send Email"));
        finish();
    }
}
