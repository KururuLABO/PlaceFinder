package com.ossyria.placefinder;

import java.util.ArrayList;

/**
 * Created by KururuLABO on 5/24/2015.
 * This class for multiple result return;
 */
public class Result {
    private String Status;
    private ArrayList<PlaceInfo> places;

    public String getStatus() {
        return Status;
    }
    public void setStatus(String pStatus) { this.Status = pStatus; }
    public ArrayList<PlaceInfo> getPlaces() {
        return places;
    }
    public void setPlaces(ArrayList<PlaceInfo> pPlaces) { this.places =  pPlaces; }


}
