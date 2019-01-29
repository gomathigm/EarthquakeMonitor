package com.example.rajayambigms.earthquakemonitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String[] TimeDataArray = new String[10];
    String[] PlaceDataArray = new String[10];
    String[] MagDataArray = new String[10];
    String[] TypeDataArray = new String[10];
    String[] errorSoon = new String[100];

    String magData;
    String placeData;
    String timeData;
    String typeData;

    Handler handler;
    ListView listView;
    CurrentEarthquakeAdapter currentEarthquakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         listView = (ListView) findViewById(R.id.mainActivity_listView);

        PlaceDataArray = new String[]{"One", "Two", "Three", "Four", "Five", "six", "seven"};
        TimeDataArray = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        MagDataArray = new String[]{"0", "0", "0", "0", "0", "0", "0"};
        TypeDataArray = new String[]{"Temp", "Temp", "Temp", "Temp", "Temp", "Temp", "Temp"};

        currentEarthquakeAdapter = new CurrentEarthquakeAdapter(this, PlaceDataArray, TimeDataArray, TypeDataArray,MagDataArray);
        listView.setAdapter(currentEarthquakeAdapter);

        handler = new Handler();

        updateWeatherData();


        //new yourDataTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            magData = MagDataArray[i];
            placeData = PlaceDataArray[i];
            timeData = TimeDataArray[i];
            typeData = TypeDataArray[i];


                Bundle bundle = new Bundle();
                bundle.putString("txtView1", placeData);
                bundle.putString("txtView2", typeData);
                bundle.putString("txtView3", magData);
                bundle.putString("txtView4", timeData);
                bundle.putInt("index",i);
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                }
            });

        }


    @Override
    protected void onResume() {
        super.onResume();
        currentEarthquakeAdapter.notifyDataSetChanged();
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
                JSONObject listDetails = json.getJSONArray("features").getJSONObject(i);

                JSONObject mainDetails = listDetails.getJSONObject("properties");

                String mag = mainDetails.getString("mag");
                String place = mainDetails.getString("place").toLowerCase(Locale.US);

                DateFormat df = DateFormat.getDateTimeInstance();

                String time = df.format(new Date(mainDetails.getLong("time") * 1000));

                String type = mainDetails.getString("type");

                System.out.println("RajayJson: " + mag + "Time:" + time + "Tempr:" + place + type);


                TypeDataArray[i] = type;
                PlaceDataArray[i] = place;
                TimeDataArray[i] = time;
                MagDataArray[i] =mag;
                currentEarthquakeAdapter.notifyDataSetChanged();

            }


        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
           updateWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
