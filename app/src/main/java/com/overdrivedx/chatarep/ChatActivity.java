package com.overdrivedx.chatarep;

import android.app.Dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.GsonBuilder;
import com.overdrivedx.adapter.AwesomeAdapter;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.Messages;
import com.overdrivedx.database.DatabaseHandler;
import com.overdrivedx.fragments.WaitFragment;


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
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements WaitFragment.OnFragmentInteractionListener{

    public static final String FUNCTION_ID = "function_id";
    public static final String USER_ID = "user_id";
    public static final String POPUP_MESSAGE = "popup_message";
    public static final String CLIENT_ID = "client_id";

    private String user_id;
    private String function_id;
    private String message;
    private String rep_name;
    private String client_id;

    public AbstractXMPPConnection _connection;

    private int chat_id;
    private int rep_id;

    private static Boolean global_error = false;

    private  FragmentTransaction fragmentTransaction;
    public WaitFragment waitFragment;
    private Handler mHandler;

    private Chat newChat;
    private ChatManager chatManager;

    private ArrayList<Messages> messages;
    private AwesomeAdapter adapter;
    private EditText text;
    private ListView lv;

    private Socket mSocket;{
        try {
            mSocket = IO.socket(com.overdrivedx.adapter.Constants.DOMAIN);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBarWithQuit();

        user_id = getIntent().getStringExtra(USER_ID);
        function_id = getIntent().getStringExtra(FUNCTION_ID);
        message = getIntent().getStringExtra(POPUP_MESSAGE);
        client_id = getIntent().getStringExtra(CLIENT_ID);

        text = (EditText) this.findViewById(R.id.text);

        messages = new ArrayList<Messages>();
        adapter = new AwesomeAdapter(this, messages);

        db = new DatabaseHandler(this);

        waitFragment = new WaitFragment();

        lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(adapter);

        mHandler = new Handler(){
        public void handleMessage (Message msg){
            super.handleMessage(msg);
            String aResponse = msg.getData().getString("message");

                if ((null != aResponse)) {
                    //getSupportFragmentManager().beginTransaction().remove(waitFragment).commit();
                    findViewById(R.id.chatfragment_container).setVisibility(View.INVISIBLE);

                    FullActionBar();

                    findViewById(R.id.chat_container).setVisibility(View.VISIBLE);

                    final Dialog dialog = new Dialog(ChatActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.setContentView(R.layout.startchat_popup_dialog);
                    dialog.show();

                    TextView tv = (TextView) dialog.findViewById(R.id.d_popup_message);
                    tv.setText(aResponse);

                    Button start_chat = (Button) dialog.findViewById(R.id.btn_start_chat);
                    start_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText ini = (EditText) dialog.findViewById(R.id.ini_message);
                            ini.getBackground().setColorFilter(Color.parseColor("#90088B"), PorterDuff.Mode.SRC_IN);
                            String ini_ms = ini.getText().toString();

                            sendCMessage(ini_ms);
                            addNewMessage(new Messages(ini_ms, true));
                            dialog.dismiss();

                        }
                    });

                }

             }
        };

        if(findViewById(R.id.chatfragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragmentTransaction = getSupportFragmentManager().beginTransaction();


            Bundle args = new Bundle();
            args.putString(WaitFragment.CLIENT_ID, client_id);
            waitFragment.setArguments(args);

            if (savedInstanceState == null) {
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.chatfragment_container, waitFragment).commitAllowingStateLoss();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new AddToQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            new AddToQueue().execute((Void[]) null);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                global_error = true;
                waitFragment.UpdateSplashMessage("Connection failed 1 of 2.");
                waitFragment.showEmailButton(true);
            }

        });
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //if(!global_error)
                    //waitFragment.UpdateSplashMessage("Connected 1 of 2.");
            }
        });

        mSocket.on("pushed", pushed);
        mSocket.on("rep_quit", rep_quit);

        mSocket.connect();
    }

    @Override
    public void onWaitFragmentInteraction(Uri uri) {

    }

    private class AddToQueue extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

           if (login()) {
                postData();
            }
            else{
                global_error = true;
                waitFragment.showEmailButton(true);
                waitFragment.UpdateSplashMessage("Unable to login to Chat Server");
            }

            return null;
        }
    }

    public Boolean login() {
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setResource(com.overdrivedx.adapter.Constants.RESOURCE);
        configBuilder.setServiceName(com.overdrivedx.adapter.Constants.SERVICE_NAME);
        configBuilder.setHost(com.overdrivedx.adapter.Constants.HOST);
        configBuilder.setPort(com.overdrivedx.adapter.Constants.PORT);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        _connection = new XMPPTCPConnection(configBuilder.build());

        try {
            _connection.setPacketReplyTimeout(15000);
            _connection.connect();

            if(user_id != null)
                _connection.login(user_id, "melody");
            else
                return false;

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

    public void postData() {
        Map<String, String> comment = new HashMap<String, String>();
        comment.put("user_id", user_id);
        comment.put("fid", function_id);
        comment.put("device", "Android");


        String json = new GsonBuilder().create().toJson(comment, Map.class);
        String res = makeRequest(Constants.ADD_TO_QUEUE_URL, json);

        try {
             parseRes(res);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String makeRequest(String uri, String json) {
        String responseString = null;

        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse response = new DefaultHttpClient().execute(httpPost);
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();

                out.close();
            } else {
                response.getEntity().getContent().close();
                //throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    private void parseRes(final String s) throws JSONException {
        if (s == null) {
            global_error = true;
            waitFragment.UpdateSplashMessage("Unable to contact server");
            waitFragment.showEmailButton(true);
        } else {

            JSONObject server_json = new JSONObject(s);
            Boolean error = Boolean.parseBoolean(server_json.getString("error"));

            if (!error) {
                chat_id = Integer.parseInt(server_json.getString("chat_id"));
                waitFragment.UpdateSplashMessage("Waiting for a Rep...");

            } else {
                Log.e(Constants.TAG, server_json.getString("status"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_chat, menu);
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
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();

        if(_connection != null)
            _connection.disconnect();

    }

    private Emitter.Listener pushed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                Log.v(Constants.TAG, data.toString());

                int emitted_chat_id = Integer.parseInt(data.getString("chat_id"));
                rep_id = Integer.parseInt(data.getString("rep_id"));
                rep_name = data.getString("rep_name");

                if(emitted_chat_id == chat_id ){
                    //ok start chatting
                    if(global_error == false){
                        if(message.length() <4 || message == null || message.equalsIgnoreCase("null")
                                || message.contains("null")){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.chatfragment_container).setVisibility(View.INVISIBLE);
                                    FullActionBar();
                                    findViewById(R.id.chat_container).setVisibility(View.VISIBLE);
                                }
                            });

                        }
                        else{
                            new PullTasksThread().start();
                        }
                    }
                    else{
                        waitFragment.UpdateSplashMessage("Unable to connect");
                        waitFragment.showEmailButton(true);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener rep_quit = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                int emitted_chat_id = Integer.parseInt(data.getString("chat_id"));

                if(emitted_chat_id == chat_id ){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
                            alertDialogBuilder.setTitle("Chat Ended");
                            // set dialog message
                            alertDialogBuilder
                                    .setMessage(rep_name + " has ended the chat.")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay!",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            endChat();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    private void sendCMessage(String s){
        try {
            if(_connection != null)
                chatManager = ChatManager.getInstanceFor(_connection);
                newChat = chatManager.createChat(rep_id + "@" + Constants.SERVICE_NAME, new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, final org.jivesoftware.smack.packet.Message message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(Constants.TAG, "inc:" + message.getBody());
                                addNewMessage(new Messages(message.getBody(), false));
                            }
                        });
                    }
                });
            newChat.sendMessage(s);
        }
        catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public class PullTasksThread extends Thread {
        public void run () {
            Message msgObj = mHandler.obtainMessage();
            Bundle b = new Bundle();
            String popmessage = "Hello " + db.getUserFirstName(user_id) + ". My name is " + rep_name  + ". To quickly assist you, please enter your " + message.toLowerCase();
            b.putString("message", popmessage);
            msgObj.setData(b);
            mHandler.sendMessage(msgObj);
        }
    }

    public void sendMessage(View v){
        String newMessage = text.getText().toString().trim();
        if(newMessage.length() > 0){
            text.setText("");
            addNewMessage(new Messages(newMessage, true));
            sendCMessage(newMessage);
            Log.v(Constants.TAG, newMessage);
        }
    }

    private void addNewMessage(Messages m){
        messages.add(m);
        adapter.notifyDataSetChanged();
        lv.setSelection(messages.size() - 1);
    }

    private void FullActionBar(){
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

        TextView rpN = (TextView) mCustomView.findViewById(R.id.x_rep_name);
        rpN.setText(rep_name);
        rpN.setVisibility(View.VISIBLE);

        TextView rpS = (TextView) mCustomView.findViewById(R.id.rep_status);
        rpS.setText("online");
        rpS.setVisibility(View.VISIBLE);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    public void xQuit(View v){
        endChat();
    }

    public void endChat(){
        mSocket.emit("user_quit", String.valueOf(chat_id));
        finish();
    }

    private void ActionBarWithQuit(){
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }
}
