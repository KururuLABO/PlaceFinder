package com.ossyria.placefinder.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by KururuLABO on 5/24/2015.
 */
public class PlaceInfo {
    private String mId;
    private String mName;
    private String mVicinity;
    private Double mLatitude;
    private Double mLongitude;

    public String getId() {
        return mId;
    }
    public void setId(String pId) {
        this.mId = pId;
    }
    public Double getLatitude() {
        return mLatitude;
    }
    public void setLatitude(Double pLatitude) {
        this.mLatitude = pLatitude;
    }
    public Double getLongitude() {
        return mLongitude;
    }
    public void setLongitude(Double pLongitude) {
        this.mLongitude = pLongitude;
    }
    public String getName() {
        return mName;
    }
    public void setName(String pName) {
        this.mName = pName;
    }
    public String getVicinity() {
        return mVicinity;
    }
    public void setVicinity(String pVicinity) {
        this.mVicinity = pVicinity;
    }

    static PlaceInfo GetPlaceInfo(JSONObject json) {
        try {
            PlaceInfo result = new PlaceInfo();
            JSONObject geometry = (JSONObject) json.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setName(json.getString("name"));
            result.setVicinity(json.getString("vicinity"));
            result.setId(json.getString("id"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(PlaceInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + mId + ", name=" + mName + ", latitude=" + mLatitude + ", longitude=" + mLongitude + '}';
    }
}
