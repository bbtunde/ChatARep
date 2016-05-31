package com.overdrivedx.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babatundedennis on 6/15/15.
 */
public class OpeningTimes {
    String title, popup_message, function_id, opening_times;

    public String getFunctionTitle() {
        return title;
    }

    public void setFunctionTitle(String title) {
        this.title = title;
    }

    public String getFunctionID(){
        return function_id;
    }

    public void setFunctionID(String fid){
        this.function_id = fid;
    }

    public String getOpeningTimes(){
        return opening_times;
    }

    public void setOpeningTimes(String op){
        this.opening_times = op;
    }

    public void setPopupMessage(String message) {
        this.popup_message = message;
    }

    public String getPopupMessage(){
        return popup_message;
    }
}
