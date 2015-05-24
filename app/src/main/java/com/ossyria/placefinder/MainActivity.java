package com.ossyria.placefinder;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private GoogleMap mMap;
    private LocationManager mLocationmgr;
    String[][] PlaceName; //[Name][type]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int index = 0;
        byte length = (byte)getResources().getStringArray(R.array.places).length;
        PlaceName = new String[2][length];
        for(String item : getResources().getStringArray(R.array.places)) {
            System.out.println("Item : " + item);
            PlaceName[0][index] = getName(item);
            PlaceName[1][index] = getType(item);
            index++;
        }

        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        //PlaceName = getResources().getStringArray(R.array.places);
        new GetPlacesTask(MainActivity.this, PlaceName[1][0].replace(" ","_").toLowerCase()).execute(); // Initialization

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PlaceName[0]); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new GetPlacesTask(MainActivity.this, PlaceName[1][position].replace(" ", "_").toLowerCase()).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private String getName(String pJson) {
        System.out.println("json : "+pJson);
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

    private String getType(String pJson) {
        try {
            JSONObject json = new JSONObject(pJson);
            return json.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void getCurrentLocaltion() {

    }

    private class GetPlacesTask extends AsyncTask<Void, Void, Result> {
        private Context context;
        private String FindType;

        public GetPlacesTask(Context pContext,String type) {
            this.context = pContext;
            this.FindType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMap.clear();
        }

        @Override
        protected Result doInBackground(Void... params) {
            OssyriaSearchEngine engine = new OssyriaSearchEngine(getResources().getString(R.string.api_placekey));
            Result result = engine.findPlaces(13.7380289, 100.3680477, FindType); //For testing to find hospital
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
