package com.overdrivedx.adapter;

/**
 * Created by babatundedennis on 6/13/15.
 */
public class User {
    String _user_id,_first_name,_last_name, _email;

    // Empty constructor
    public User(){

    }
    // constructor
    public User(String id, String first_name, String last_name, String email){
        this._user_id = id;
        this._first_name = first_name;
        this._last_name = last_name;
        this._email = email;
    }

    // constructor
    public User(String first_name, String last_name, String email){
        this._first_name = first_name;
        this._last_name = last_name;
        this._email = email;
    }
    // getting ID
    public String getID(){
        return this._user_id;
    }

    // setting id
    public void setID(String id){
        this._user_id = id;
    }

    // getting name
    public String getFirstName(){
        return this._first_name;
    }

    // setting name
    public void setFirstName(String name){
        this._first_name = name;
    }

    // getting phone number
    public String getLastName(){
        return this._last_name;
    }

    public void setLastName(String name){
        this._last_name = name;
    }

    // setting phone number
    public void setEmail(String email){
        this._email = email;
    }
    // getting phone number
    public String getEmail(){
        return this._email;
    }

    // setting phone number

}
