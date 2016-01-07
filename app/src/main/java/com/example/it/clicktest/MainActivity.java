package com.example.it.clicktest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView tDate, tTemp, tHumidity, tPressure, tWind, tCloud;
    EditText etCity;
    GisAsyncTask gisTask;
    private static final String TAG = "forecast debug "; // тег для  лога
    public static final String CHANNEL = "GIS_SERVICE";
    public static final String INFO = "INFO";
    public static int city = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity      = (EditText) findViewById(R.id.city);

        tDate       = (TextView) findViewById(R.id.date);
        tTemp       = (TextView) findViewById(R.id.temp);
        tPressure   = (TextView) findViewById(R.id.pressure);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onClick(View v)
    {
        Toast.makeText(this, "получаем данные о погоде", Toast.LENGTH_SHORT).show();
        city = Integer.parseInt(etCity.getText().toString());
        if (city < 0)
        {
            city = 21;
        }
        gisTask  = new GisAsyncTask();
        gisTask.execute();
    }

    private class GisAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String aVoid) {
            Intent i = new Intent(CHANNEL);
            i.putExtra(INFO, aVoid);
            sendBroadcast(i);
            Log.d(TAG, aVoid); // пишем в лог всю строку, что скачали с сайта
            try {
                JSONObject gis = new JSONObject(aVoid);
                JSONArray forecast = gis.getJSONArray("gis");
                JSONObject weather = forecast.getJSONObject(0);
                String temp = weather.getString("temp");
                String date = weather.getString("date");
                String pressure = weather.getString("pressure");

                Log.d(TAG, temp);
                Log.d(TAG, date);
                Log.d(TAG, pressure);

                tDate.setText(date);
                tTemp.setText(temp);
                tPressure.setText(pressure);
            }
            catch (JSONException e)
            {}
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            try {
                URL url = new URL("http://icomms.ru/inf/meteo.php?tid="+city);
                Scanner in = new Scanner((InputStream) url.getContent());
                result = "{\"gis\":" + in.nextLine() + "}";
            } catch (Exception e) {
                result = "не удалось загрузить информацию о погоде" + e.toString();
            }
            return result;
        }
    }
}
