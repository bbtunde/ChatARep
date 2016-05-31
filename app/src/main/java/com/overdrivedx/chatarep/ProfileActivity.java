package com.overdrivedx.chatarep;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.User;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.utils.EmailValidator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity {
    private String user_id,first_name, last_name, email;
    private AbstractXMPPConnection _connection;
    DatabaseHandler db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new DatabaseHandler(this);
        context = this;

        setUserDetails();

        EditText firstName = (EditText) findViewById(R.id.first_name);
        EditText lastName = (EditText) findViewById(R.id.last_name);
        EditText _email = (EditText) findViewById(R.id.email);

        if(email != null){
            _email.setText(email);
        }
        if(last_name != null){
            lastName.setText(last_name);
        }
        if(first_name != null){
            firstName.setText(first_name);
        }
        //new user, so set user id else user_id already set at setUserDetails
        if(user_id == null) {
            Long tsLong = System.currentTimeMillis() / 1000;
            user_id = tsLong.toString();
        }

        Button save_profile = (Button) findViewById(R.id.profile_save_button);
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                EditText firstName = (EditText) findViewById(R.id.first_name);
                EditText lastName = (EditText) findViewById(R.id.last_name);
                EditText _email = (EditText) findViewById(R.id.email);

                first_name = firstName.getText().toString();
                last_name = lastName.getText().toString();
                email = _email.getText().toString();

                if (new EmailValidator().validate(email) || last_name.length() > 2 || first_name.length() > 2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        new saveProfile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    else
                        new saveProfile().execute((Void[]) null);
                }
                else {

                }
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_profile, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
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

    private Boolean setUserDetails(){
        List<User> user = db.getUser();
        int rowCount = user.size();

        if(rowCount ==1) {
            for (User record : user) {
                user_id = record.getID();
                email = record.getEmail();
                last_name = record.getLastName();
                first_name = record.getFirstName();
            }
            return true;
        }
        else{
            Log.v(Constants.TAG, "no results");
            return false;
        }
    }

    private class saveProfile extends AsyncTask<Void, String, Void>{
        ProgressDialog pDialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Saving ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);

            pDialog.show();
            super.onPreExecute();

        }

        @Override
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

        protected void onProgressUpdate(String... progUpdate) {
            pDialog.setMessage(progUpdate.toString());
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // dismiss dialog
            pDialog.dismiss();
        }

    }

    private Boolean saveProfileLocal(String server_response) throws JSONException {
        JSONObject server_json = new JSONObject(server_response);
        Boolean error = Boolean.parseBoolean(server_json.getString("error"));

        if (!error) {
            if(db.userExists(user_id)){
                db.updateUser(new User(user_id, first_name, last_name, email));

            }
            else {
                db.addUser(new User(user_id, first_name, last_name, email));

            }

            return true;
        }
        else {

        }


        return false;
    }

    private boolean saveToServer(){
        Map<String, String> comment = new HashMap<String, String>();
        comment.put("first_name", first_name);
        comment.put("last_name", last_name);
        comment.put("email", email);
        comment.put("id", user_id);

        String json = new GsonBuilder().create().toJson(comment, Map.class);
        try {
            HttpPost httpPost = new HttpPost(Constants.ADD_USER);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse response = new DefaultHttpClient().execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);

                Log.v(Constants.TAG, out.toString());
                try {
                    if (saveProfileLocal(out.toString())) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                out.close();


            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean createUserOnChatServer() {

        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setResource(Constants.RESOURCE);
        configBuilder.setServiceName(Constants.SERVICE_NAME);
        configBuilder.setHost(Constants.HOST);
        configBuilder.setPort(Constants.PORT);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        _connection = new XMPPTCPConnection(configBuilder.build());

        try {
            _connection.setPacketReplyTimeout(15000);
            _connection.connect();
            AccountManager accountManager = AccountManager.getInstance(_connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(user_id, "melody");
            return true;

        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(_connection != null)
            _connection.disconnect();
    }

    @Override
    public void onPause(){
        super.onPause();

    }
}
