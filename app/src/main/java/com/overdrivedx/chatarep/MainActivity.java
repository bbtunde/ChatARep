package com.overdrivedx.chatarep;

import android.support.v4.app.DialogFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.overdrivedx.adapter.Client;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.TabsPagerAdapter;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.DeleteAccountFragment;
import com.overdrivedx.fragments.SearchFragment;
import com.overdrivedx.fragments.SettingsFragment;
import com.overdrivedx.views.BlurImageView;
import com.overdrivedx.views.SlidingTabLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    private DatabaseHandler db;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private BlurImageView iv_background;
    private SlidingTabLayout tabs;
    //private VideoView myVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        db = new DatabaseHandler(this);

        //iv_background = (BlurImageView)findViewById(R.id.iv_birdie);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager(),Constants.Titles,Constants.Titles.length);

        viewPager.setAdapter(mAdapter);


        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

            @Override
            public int getDividerColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs.setViewPager(viewPager);

        try {
            String res =  getIntent().getStringExtra("response");
            if(res != null)
                parseJsonFeed(new JSONObject(res));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        myVideoView = (VideoView) findViewById(R.id.video_view);

        try {
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
            myVideoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0f, 0f);
                myVideoView.start();
            }
        });
        */
        //changeBackground();
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            Boolean error = Boolean.valueOf(response.getString("error"));
            if(!error) {
                JSONArray feedArray = response.getJSONArray("client_list");
                Log.v(Constants.TAG, feedArray.toString());

                db.truncateClientTable();
                // remove all the content from the client table
                // then add new clients
                // reason for this is that the client json will change
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                        db.addClient(new Client(
                                feedObj.getString("id"),
                                feedObj.getString("name"),
                                feedObj.getString("industry"),
                                feedObj.getString("message"),
                                feedObj.getString("email")
                        ));

                }

            }
            else{
                this.finish();
                Log.e(Constants.TAG, "Unable to parse");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchFragmentInteraction(Uri uri) {

    }

    @Override
    public void onResume(){
        super.onResume();
        //View dV = getWindow().getDecorView();
        //int uiOpts = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //dV.setSystemUiVisibility(uiOpts);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void changeBackground(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.leopard));
        //Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cook,size.x,size.y, true));

        //fill the background ImageView with the resized image
        iv_background.setImageBitmap(bmp);
        iv_background.setScaleType(ImageView.ScaleType.CENTER_CROP);

    }


    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    DeleteAccountFragment newFragment;

    public void deleteAccount(View v){

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        newFragment = DeleteAccountFragment.newInstance();
        newFragment.show(ft, "dialog");
    }


}
