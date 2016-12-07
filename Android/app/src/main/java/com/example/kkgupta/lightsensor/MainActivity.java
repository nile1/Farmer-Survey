package com.example.kkgupta.lightsensor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaActionSound;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.StreamHandler;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Spinner spinner2;
    private Button btnSubmit;
    String name;
    EditText txtname;
    private Handler mHandler;
    float SensorReading;
    String date;
    String line = "";
    private ProgressBar spinner;
    File external = Environment.getExternalStorageDirectory();
    String sdcardPath = external.getPath();
    File file = new File(sdcardPath + "/CropDetail.txt");
    public Context c;
    double lat, lng;
    private LocationManager service;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        //Criteria criteria = new Criteria();
        //provider = service.getBestProvider(criteria, false);
        c = MainActivity.this;
        Log.d("a", "=======================================================================1");
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        mHandler = new Handler();

        txtname = (EditText) findViewById(R.id.editText2);


        addListenerOnButton();


        if (!isNetworkAvailable()) {
            Toast.makeText(MainActivity.this, "No internet access quit-", Toast.LENGTH_LONG).show();
            Log.d("a", "=======================================================================2(a)");
        }

    }

    public void addListenerOnButton() {

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        //Log.d("a","=======================================================================4");
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("a", "=======================================================================5");
                spinner2 = (Spinner) findViewById(R.id.spinner2);
                name = txtname.getText().toString();
                date = Datetime();
                Log.d("a", "=======================================================================6");
                if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = service.getLastKnownLocation(service.PASSIVE_PROVIDER);



                // Initialize the location fields
                if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else {
                    lat =-1;
                    lng = -1;
                }

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d("a", "=======================================================================8");
                        MediaActionSound sound = new MediaActionSound();
                        sound.play(MediaActionSound.SHUTTER_CLICK);


                        mHandler.postDelayed(new Runnable() {
                            public void run() {

                                spinner.setVisibility(View.VISIBLE);
                                Log.d("a", "=======================================================================9");
                                JSONObject JsonObj = new JSONObject();
                                try {
                                    JsonObj.put("Name", name);
                                    JsonObj.put("time", String.valueOf(date));
                                    JsonObj.put("Crop", spinner2.getSelectedItem());
                                    JsonObj.put("lat", lat);
                                    JsonObj.put("lng", lng);
                                    //new SaveEntries(JsonObj);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (JsonObj.length() > 0) {
                                    try {
                                        new SaveEntries(JsonObj);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (isNetworkAvailable())
                                        new sendJsonDataToServer().execute(String.valueOf(JsonObj));
                                    else
                                        System.exit(0);
                                }
                            }
                        }, 10);
                    }
                }, 1);
            }

        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
         lat = (int) (location.getLatitude());
         lng = (int) (location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class sendJsonDataToServer extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://10.6.3.227:3000/users/SendData");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Toast.makeText(MainActivity.this, "Connection Failed1", Toast.LENGTH_SHORT).show();
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i("a", JsonResponse);
                return "1";

            } catch (IOException e) {
                // Toast.makeText(MainActivity.this,"Connection Failed2",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                System.exit(0);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("a", "Error closing stream3", e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            //  Log.d("a","======================================================================="+s);


            spinner.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Data send successful", Toast.LENGTH_SHORT).show();
            System.exit(0);


        }
    }

    public static String Datetime()
    {
        Calendar c = Calendar .getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mms");
        return df.format(c.getTime());



    }





}
