package com.example.rajayambigms.earthquakemonitor;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import  com.example.rajayambigms.earthquakemonitor.EarthquakeDataItems;

import java.util.List;

public class CurrentEarthquakeAdapter extends ArrayAdapter<String> {

    Context context;
    int resoureId;
    String weatherData[];
    String weatherTimeData[];
    String weatherDescData[];
    String magDescData[];
    int[] images;

    List<EarthquakeDataItems> dataItems = null;


    public CurrentEarthquakeAdapter(Context context, String[] wData, String[] tData, String[] dData,String [] mag) {
        super(context, R.layout.currentitemsrow, wData);

        this.weatherDescData = dData;
        this.weatherTimeData = tData;
        this.weatherData = wData;
        this.context = context;
        this.magDescData = mag;

    }

    static class dataHolder {
        ImageView imgview;
        TextView dataTxt;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        dataHolder holder = null;
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            // convertView = inf.inflate(resoureId,parent);
            convertView = inf.inflate(R.layout.currentitemsrow, null);

        }

        TextView dataTimeTxt = (TextView) convertView.findViewById(R.id.timeHourly_txtview);
        TextView dataDescTxt = (TextView) convertView.findViewById(R.id.descHourly_txtview);
        TextView dataWeatherTxt = (TextView) convertView.findViewById(R.id.weatherHourly_txtview);
        TextView dataMagTxt = (TextView) convertView.findViewById(R.id.mag_txt);
        dataTimeTxt.setText(weatherTimeData[position]);
        dataDescTxt.setText(weatherDescData[position]);
        dataWeatherTxt.setText(weatherData[position]);
        dataMagTxt.setText(magDescData[position]);
        String datMag = magDescData[position];
        float mag = Float.parseFloat(datMag);
        if(mag >= 0.0 && mag <= 0.9)
        {
            dataMagTxt.setTextColor(Color.parseColor("#FF1BD824")); //FFD81B60 // FF1BD824
        }
        if(mag >= 1.0 && mag <= 9.9)
        {
            dataMagTxt.setTextColor(Color.parseColor("#FFD81B60")); //FFD81B60 // FF1BD824
        }
       // imgView.setImageResource(images[position]);


        return convertView;
    }
}
