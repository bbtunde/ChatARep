package com.overdrivedx.chatarep;

/**
 * Created by babatundedennis on 1/31/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.overdrivedx.adapter.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class Splash extends Activity {
    private Request.Priority priority = Request.Priority.HIGH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /*
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(Constants.GET_CLIENTS);

        if(entry != null){
            try {
                parseRes(new String(entry.data, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }else{
        // Cached response doesn't exists. Make network call here
            }
        */
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,Constants.GET_CLIENTS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    parseRes(response.toString());
                }
                else{
                    parseRes(null);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                parseRes(null);
            }
        }){
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public Priority getPriority() {
                return priority;
            }

        };

        // Adding request to volley request queue
        jsonReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    private void parseRes(final String s) {
        TextView msg = (TextView) findViewById(R.id.splash_message);
        if (s == null) {
            msg.setText("Ouch! Network Error.\nSome functions might not work properly.");
        }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    i.putExtra("response", s);

                    startActivity(i);
                    finish();
                }
            }, 2000);


    }

}
