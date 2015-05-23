package com.ossyria.placefinder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        new GetPlacesTask(MainActivity.this).execute();
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




        }
    }

}
