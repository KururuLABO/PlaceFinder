package com.ossyria.placefinder.Helper;

import java.util.ArrayList;

/**
 * Created by KururuLABO on 5/24/2015.
 * This class for multiple result return;
 */
public class Result {
    private String mStatus;
    private ArrayList<PlaceInfo> mPlaces;

    public String getStatus() {
        return mStatus;
    }
    public void setStatus(String pStatus) { this.mStatus = pStatus; }
    public ArrayList<PlaceInfo> getPlaces() {
        return mPlaces;
    }
    public void setPlaces(ArrayList<PlaceInfo> pPlaces) { this.mPlaces =  pPlaces; }


}
