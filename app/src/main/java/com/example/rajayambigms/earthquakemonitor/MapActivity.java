package com.example.rajayambigms.earthquakemonitor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap googleMap;
    MapView mapView;
    View view;
    float lati = 0f;
    float longt = 0f;
    String city = "";
    String title, desc = "";
    int index;

    TextView place_txt;
    TextView type_txt;
    TextView mag_txt;
    TextView time_txt;

    Handler handler;
    ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lati = 40.489247f;
        longt = -74.044502f;
        city = "New York";
        title = "Liberty Statue";
        desc = "Yes!! we are here";

        handler = new Handler();


        Bundle getBundle = null;
        getBundle = this.getIntent().getExtras();
        String type = getBundle.getString("txtView2");
        String time = getBundle.getString("txtView4");
        String mag =  getBundle.getString("txtView3");
        String place = getBundle.getString("txtView1");
        index = getBundle.getInt("index");


        place_txt =(TextView)findViewById(R.id.place_txt);
        type_txt =(TextView)findViewById(R.id.type_txt);
        mag_txt =(TextView)findViewById(R.id.mag_txt);
        time_txt =(TextView)findViewById(R.id.time_txt);

        place_txt.setText("Location:"+place);
        type_txt.setText("Type:"+type);
        mag_txt.setText("Magnitude:"+mag);
        time_txt.setText("Time:"+time);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1); // 1 is requestCode
                return;

            }
        }

        mapView = (MapView)findViewById(R.id.mapView);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);

                updateWeatherData();
            }
        });

        ImageView img = (ImageView) findViewById(R.id.imageViewBack);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMapobj) {

        MapsInitializer.initialize(this);
        googleMap = googleMapobj;
        googleMapobj.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMapobj.addMarker(new MarkerOptions().position(new LatLng(lati, longt)).title(title).snippet(desc));
        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(lati, longt)).zoom(4).bearing(0).tilt(45).build();
        googleMapobj.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void updateWeatherData() {
        new Thread() {
            public void run() {
                final JSONObject json = EarthquakeRemoteFetch.getJSON();
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            System.out.println("no data found");

                        }
                    });
                } else {

                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {



            for (int i = 0; i <= 7; i++) {

                if(i == index)
                {
                    JSONObject listDetails = json.getJSONArray("features").getJSONObject(i);

                    JSONObject mainDetails = listDetails.getJSONObject("properties");

                    String mag = mainDetails.getString("mag");
                    String place = mainDetails.getString("place").toLowerCase(Locale.US);

                    DateFormat df = DateFormat.getDateTimeInstance();

                    String time = df.format(new Date(mainDetails.getLong("time") * 1000));

                    String type = mainDetails.getString("type");

                    System.out.println("RajayJson: " + mag + "Time:" + time + "Tempr:" + place + type);


                    place_txt.setText("Location:"+place);
                    type_txt.setText("Type:"+type);
                    mag_txt.setText("Magnitude:"+mag);
                    time_txt.setText("Time:"+time);

                    spinner.setVisibility(View.GONE);

                }
                else
                {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(MapActivity.this,"SORRY!!! NO DATA FOUND",Toast.LENGTH_LONG);
                }

            }


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"PERMISSION_DENIED",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"PERMISSION_GRANTED",Toast.LENGTH_SHORT).show();
                    // permission granted do something
                }
                break;
        }
    }

}
