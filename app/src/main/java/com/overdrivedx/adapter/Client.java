package com.overdrivedx.adapter;

/**
 * Created by babatundedennis on 6/13/15.
 */
public class Client{
    String _client_id,_name,_message, _email, _industry;

    // Empty constructor
    public Client(){

    }
    // constructor
    public Client(String id, String name, String industry, String message, String email){
        this._client_id = id;
        this._name = name;
        this._message = message;
        this._email = email;
        this._industry = industry;
    }

    // constructor
    public Client(String name, String industry, String message,  String email){
        this._name = name;
        this._message = message;
        this._email = email;
        this._industry = industry;
    }
    // getting ID
    public String getID(){
        return this._client_id;
    }

    // setting id
    public void setID(String id){
        this._client_id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }
    // setting name
    public void setIndustry(String industry){
        this._industry= industry;
    }

    public String getIndustry(){
        return this._industry;
    }

    public void setMessage(String message){
        this._message = message;
    }

    // getting phone number
    public String getMessage(){
        return this._message;
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
