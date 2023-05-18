package com.sanat.complainmgt.network;

import com.sanat.complainmgt.model.ComplainModel;

public class APIs {
    String url = "http://172.16.1.131/helpme/ajoy/helpme/api/v1/";

    public String getLoginURL(String UserID, String Pass){
        String method = "login?";
        String params = "userID=" + UserID + "&password=" + Pass;
        return (url + method + params);
    }

    public String saveComplainURL(){
        String method = "saveComplain";
       // String params = "userID=" + model.getTitle() + "&password=" + model.getDescription();
        return (url + method);
    }

    public String getComplainsByUserIDURL(String UserID){
        String method = "getComplainsByUserID?";
        String params = "userID=" + UserID;
        return (url + method+params);
    }
}
