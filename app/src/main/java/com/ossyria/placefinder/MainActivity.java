package com.ossyria.placefinder;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ossyria.placefinder.Helper.JSONHelper;
import com.ossyria.placefinder.Helper.OssyriaSearchEngine;
import com.ossyria.placefinder.Helper.Result;


public class MainActivity extends Activity {
    //region Variable
    private GoogleMap mMap;
    String[][] mPlaceName; //[Name][type]
    private LocationManager mLocationManager;
    private Location mUserLocation;
    private Spinner mSpinner;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region Intialzation GoogleMap
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        //endregion
        //region Intialzation Location Manager
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mUserLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        System.out.println("Lastnoiche : "+mUserLocation.getLatitude());
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mLocationListener); // Update every 1 seconds and 10 meters

        //mLocationManager.removeUpdates(mLocationListener);

        //endregion
        //region Initialzation Spinner Menu
        int index = 0;
        byte length = (byte)getResources().getStringArray(R.array.places).length;
        mPlaceName = new String[2][length];
        for(String item : getResources().getStringArray(R.array.places)) {
            //System.out.println("Item : " + item);
            mPlaceName[0][index] = JSONHelper.getName(item);
            mPlaceName[1][index] = JSONHelper.getType(item);
            index++;
        }
        mSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mPlaceName[0]); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerArrayAdapter);
        mSpinner.setSelection(0);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new GetPlacesTask(MainActivity.this, mPlaceName[1][position].replace(" ", "_").toLowerCase()).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //endregion
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationManager.removeUpdates(mLocationListener);
    }

    //region Location Listener Event
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Toast.makeText(MainActivity.this, "Location Chagned to : ", Toast.LENGTH_SHORT).show();
            //if(mUserLocation.getLongitude() != location.getLongitude() && mUserLocation.getLatitude() != location.getLatitude()) {
                mUserLocation = location;
                new GetPlacesTask(MainActivity.this, mPlaceName[1][mSpinner.getSelectedItemPosition()].replace(" ", "_").toLowerCase()).execute();
            //}
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}

    };
    //endregion
    //region AsyncTask
    private class GetPlacesTask extends AsyncTask<Void, Void, Result> {
        private Context context;
        private String FindType;

        public GetPlacesTask(Context pContext,String type) {
            this.context = pContext;
            this.FindType = type;
        }

        @Override
        protected Result doInBackground(Void... params) {
            OssyriaSearchEngine engine = new OssyriaSearchEngine(getResources().getString(R.string.api_placekey));
            Result result = engine.findPlaces(mUserLocation.getLatitude(), mUserLocation.getLongitude(), FindType); //For testing to find hospital
            return result;
        }

        @Override
        protected void onPostExecute(Result result) { //???????????? ?? Backgrounds ?????????
            super.onPostExecute(result);
            if(!result.getStatus().equals("OK")) {
                if(result.getStatus().equals("ZERO_RESULTS"))
                    Toast.makeText(this.context, "Sorry, Can't find " + mPlaceName[0][mSpinner.getSelectedItemPosition()] + " in range 5 km.", Toast.LENGTH_SHORT).show();
                if(result.getStatus().equals("REQUEST_DENIED"))
                    Toast.makeText(this.context, "Sorry, Can't request location. Please contact to administrator", Toast.LENGTH_SHORT).show();
                //Error ??????????????????????? ???? Over_query Limit
                return;
            }
            mMap.clear();
            for(int i = 0; i < result.getPlaces().size(); i++) {
                mMap.addMarker(new MarkerOptions()
                    .title(result.getPlaces().get(i).getName())
                    .position(new LatLng(result.getPlaces().get(i).getLatitude(),
                                         result.getPlaces().get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .snippet(result.getPlaces().get(i).getVicinity())
                );
            }

            mMap.addMarker(new MarkerOptions()
                            .title("You are Here")
                            .position(new LatLng(mUserLocation.getLatitude(),
                                    mUserLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_current))
            );
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    //.target(new LatLng(13.7380289, 100.3680477)) // Sets the center of the map to
                    .target(new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude())) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    //endregion
}
