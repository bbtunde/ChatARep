package com.overdrivedx.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.overdrivedx.adapter.Client;
import com.overdrivedx.adapter.Constants;
import com.overdrivedx.adapter.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babatundedennis on 6/13/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "cr_android";
    // Contacts table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_CLIENT = "client";

    //temporary tables
    private static final String TEMP_TABLE_USER = "temp_user";
    private static final String TEMP_TABLE_CLIENT = "temp_client";
    // Contacts User Columns names
    private static final String ID = "id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";

    private static final String CLIENT_ID ="id";
    private static final String CLIENT_NAME ="name";
    private static final String CLIENT_INDUSTRY ="industry";
    private static final String CLIENT_MESSAGE ="message";
    private static final String CLIENT_EMAIL ="email";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + ID + " VARCHAR PRIMARY KEY," + FIRST_NAME + " TEXT,"  + LAST_NAME + " TEXT," +  EMAIL + " TEXT)";

        String CREATE_CLIENT_TABLE = "CREATE TABLE "+ TABLE_CLIENT + "("
                + CLIENT_ID + " VARCHAR PRIMARY KEY," + CLIENT_NAME + " TEXT," + CLIENT_INDUSTRY + " TEXT,"+ CLIENT_MESSAGE + " TEXT,"+
                CLIENT_EMAIL + " TEXT)";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CLIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_CLIENT);
        db.execSQL("DROP TABLE " + TABLE_USER);

        onCreate(db);
        /*
        String TEMP_CREATE_CLIENT_TABLE = "CREATE TABLE "+ TEMP_TABLE_CLIENT + "("
                + CLIENT_ID + " VARCHAR PRIMARY KEY," + CLIENT_NAME + " TEXT," + CLIENT_INDUSTRY + " TEXT,"+ CLIENT_MESSAGE + " TEXT,"+
                CLIENT_EMAIL + " TEXT)";

        db.execSQL(TEMP_CREATE_CLIENT_TABLE);

        db.execSQL("INSERT INTO " + TEMP_TABLE_CLIENT + " SELECT " +  CLIENT_ID + ", "
                +  CLIENT_NAME + ", " +  CLIENT_INDUSTRY + ", " +  CLIENT_MESSAGE  +  "," + CLIENT_EMAIL +" FROM " + TABLE_CLIENT);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);

        String CREATE_CLIENT_TABLE = "CREATE TABLE "+ TABLE_CLIENT + "("
                + CLIENT_ID + " VARCHAR PRIMARY KEY," + CLIENT_NAME + " TEXT," + CLIENT_INDUSTRY + " TEXT,"+ CLIENT_MESSAGE + " TEXT,"+
                CLIENT_EMAIL + " TEXT)";

        db.execSQL(CREATE_CLIENT_TABLE);

        db.execSQL("INSERT INTO " + TABLE_CLIENT + " SELECT " +  CLIENT_ID + ", "
                +  CLIENT_NAME + ", " +  CLIENT_INDUSTRY + ", " +  CLIENT_MESSAGE + ", "  + CLIENT_EMAIL + " FROM " + TEMP_TABLE_CLIENT);

        db.execSQL("DROP TABLE " + TEMP_TABLE_CLIENT);
        */
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, user.getID());
        values.put(FIRST_NAME, user.getFirstName()); // Contact Name
        values.put(LAST_NAME, user.getLastName()); // Contact Phone Number
        values.put(EMAIL, user.getEmail()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, client.getID());
        values.put(CLIENT_NAME, client.getName()); // Contact Name
        values.put(CLIENT_INDUSTRY, client.getIndustry());
        values.put(CLIENT_MESSAGE, client.getMessage()); // Contact Phone Number
        values.put(CLIENT_EMAIL, client.getEmail()); // Contact Phone Number
        db.insert(TABLE_CLIENT, null, values);
        db.close(); // Closing database connection
    }

    public Boolean clientExists(String client_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ CLIENT_NAME +" FROM " + TABLE_CLIENT + " WHERE " + CLIENT_ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { client_id });
        int r = c.getCount();
        c.close();

        if(r == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public List<Client> getClient(String client_name) {
        List<Client> clientList = new ArrayList<Client>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLIENT +
                " WHERE " + CLIENT_NAME + " LIKE '%" + client_name + "%' ORDER BY "+ CLIENT_NAME +
                " DESC LIMIT 0,3";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Client client = new Client();
                client.setID(cursor.getString(0));
                client.setName(cursor.getString(1));
                client.setIndustry(cursor.getString(2));
                client.setMessage(cursor.getString(3));
                client.setEmail(cursor.getString(4));
                // Adding contact to list
                clientList.add(client);
            } while (cursor.moveToNext());
        }

        // return contact list
        return clientList;
    }

    public List<User> getUser() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " LIMIT 0,1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setID(cursor.getString(0));
                user.setFirstName(cursor.getString(1));
                user.setLastName(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                // Adding contact to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return contact list
        return userList;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIRST_NAME, user.getFirstName());
        values.put(LAST_NAME, user.getLastName());
        values.put(EMAIL, user.getEmail());
        // updating row
        return db.update(TABLE_USER, values, ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
    }

    public int updateClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLIENT_NAME, client.getName()); // Contact Name
        values.put(CLIENT_INDUSTRY, client.getIndustry());
        values.put(CLIENT_MESSAGE, client.getMessage()); // Contact Phone Number
        values.put(CLIENT_EMAIL, client.getEmail()); // Contact Phone Number
        // updating row
        return db.update(TABLE_CLIENT, values, CLIENT_ID + " = ?",
                new String[] { String.valueOf(client.getID()) });
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
        db.close();
    }

    public boolean userExists(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ FIRST_NAME +" FROM " + TABLE_USER + " WHERE " + ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { user_id });
        int r = c.getCount();
        c.close();

        if(r == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public String getClientID(String client_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ CLIENT_ID +" FROM " + TABLE_CLIENT + " WHERE " + CLIENT_NAME + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { client_name });
        String client_id = null;
        if (c.moveToFirst()) {
           client_id = c.getString(0);
        }

        return client_id;
    }

    public String getClientEmail(String client_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ CLIENT_EMAIL +" FROM " + TABLE_CLIENT + " WHERE " + CLIENT_NAME + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { client_id });
        String client_email = null;
        if (c.moveToFirst()) {
            client_email = c.getString(0);
        }

        return client_email;
    }

    public String getClientMessage(String client_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ CLIENT_MESSAGE +" FROM " + TABLE_CLIENT + " WHERE " + CLIENT_ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { client_id });
        String client_message = null;
        if (c.moveToFirst()) {
            client_message = c.getString(0);
        }

        return client_message;
    }

    public String getUserFirstName(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ FIRST_NAME +" FROM " + TABLE_USER + " WHERE " + ID + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[] { user_id });
        String first_name = null;
        if (c.moveToFirst()) {
            first_name = c.getString(0);
        }

        return first_name;
    }

    public void truncateClientTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_CLIENT);
        db.execSQL("VACUUM");
    }
}
