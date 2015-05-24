package com.ossyria.placefinder;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private GoogleMap mMap;
    private LocationManager mLocationmgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        new GetPlacesTask(MainActivity.this).execute();


        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.places)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);


    }

    private void getCurrentLocaltion() {

    }

    private class GetPlacesTask extends AsyncTask<Void, Void, Result> {
        private Context context;

        public GetPlacesTask(Context pContext) {
            this.context = pContext;
        }

        @Override
        protected Result doInBackground(Void... params) {
            OssyriaSearchEngine engine = new OssyriaSearchEngine(getResources().getString(R.string.api_placekey));
            Result result = engine.findPlaces(13.7380289, 100.3680477, "hospital"); //For testing to find hospital
            return result;
        }

        @Override
        protected void onPostExecute(Result result) { //???????????? ?? Backgrounds ?????????
            super.onPostExecute(result);
            if(!result.getStatus().equals("OK")) {
                Toast.makeText(this.context, "Error : " + result.getStatus(), Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i = 0; i < result.getPlaces().size(); i++) {
                mMap.addMarker(new MarkerOptions()
                    .title(result.getPlaces().get(i).getName())
                    .position(new LatLng(result.getPlaces().get(i).getLatitude(),
                                         result.getPlaces().get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .snippet(result.getPlaces().get(i).getVicinity())
                );



            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(13.7380289, 100.3680477)) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }
    }



}
