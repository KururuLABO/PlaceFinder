package com.ossyria.placefinder.Helper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KururuLABO on 5/25/2015.
 */
public class JSONHelper {
    //region Method
    public static String getName(String pJson) {
        System.out.println("json : " + pJson);
        try {
            System.out.println("json : "+pJson);
            JSONObject json = new JSONObject(pJson);
            System.out.println("name : "+json.getString("name"));
            return json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getType(String pJson) {
        try {
            JSONObject json = new JSONObject(pJson);
            return json.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    //endregion
}
