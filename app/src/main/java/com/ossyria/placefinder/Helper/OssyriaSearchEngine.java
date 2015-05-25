package com.ossyria.placefinder.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by KururuLABO on 5/24/2015.
 */
public class OssyriaSearchEngine {
    //region Variable
    private String API_KEY;
    //endregion
    //region Constructor
    public OssyriaSearchEngine(String key) {
        this.API_KEY = key;
    }
    //endregion
    //region Getter
    public String getAPI_KEY(){
        return API_KEY;
    }
    //endregion
    //region Method
    public Result findPlaces(double pLatitude, double pLongitude, String pType) {
        String url = BuildUrl(pLatitude,pLongitude,pType);
        System.out.println("URL : "+url);
        try {
            String plainText = getPlainTextFromURL(url);

            JSONObject json = new JSONObject(plainText); //แปลงเป็น json object
            JSONArray jsonarray = json.getJSONArray("results");
            ArrayList<PlaceInfo> placeList = new ArrayList<PlaceInfo>();
            for(int i=0; i < jsonarray.length(); i++) {
                try {
                    PlaceInfo place = PlaceInfo.GetPlaceInfo((JSONObject) jsonarray.get(i));
                    placeList.add(place);
                } catch (Exception e) { }
            }
            Result result = new Result();
            result.setPlaces(placeList);
            String Status = json.getString("status");
            result.setStatus(Status);
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex); //ถ้าไม่สามารถแปลง PlainText จาก url ที่เรียกข้อได้แล้ว error ให้ทำการ เก็บ log
        }
        return null;
    }

    //ตัวอย่าง url แบบเต็มๆ
    //https://maps.googleapis.com/maps/api/place/search/json?location=13.7380289,100.3680477&types=hospital&radius=100000&key=AIzaSyCVp-2nnXkTGCpSdpxTPgwSDUW3b-1ErDY&sensor=false
    private String BuildUrl(double pLatitude, double pLongitude, String pType) {
        StringBuilder resultURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
        resultURL.append("&location=");
        resultURL.append(Double.toString(pLatitude));
        resultURL.append(",");
        resultURL.append(Double.toString(pLongitude));
        resultURL.append("&radius=5000"); //กำหนดรัศมีเพียงแค่ 5 กิโลเมตร // Radius = meter : 5000 meters = 5km
        if(!pType.equals("")) //ถ้า Type ไม่ว่างให้เพิ่มเข้าไปด้วย
            resultURL.append("&types="+pType);
        resultURL.append("&sensor=false&key=" + API_KEY);

        return resultURL.toString();
    }

    private String getPlainTextFromURL(String pUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(pUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    //endregion
}
