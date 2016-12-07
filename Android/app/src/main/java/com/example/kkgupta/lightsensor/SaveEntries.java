package com.example.kkgupta.lightsensor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by kkgupta on 8/12/2016.
 */
public class SaveEntries {



    SaveEntries(JSONObject json) throws IOException {

        try
        {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "CropDetail.csv";
            String filePath = baseDir + File.separator + fileName;
            Log.d("a","======================================================================="+filePath);

            File f = new File(filePath );
            CSVWriter writer;

            if(!f.exists()){
                f.createNewFile();
                writer = new CSVWriter(new FileWriter(filePath,true));
                Log.d("a","=======================================================================100");
               // writer = new CSVWriter(new FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\r\n");
                Log.d("a","=======================================================================101");
                String[] data = {"Name","Time","Crop","lat","lng"};
                writer.writeNext(data);
                writer.close();

            }

            //  writer = new CSVWriter(new FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\r\n");
            writer = new CSVWriter(new FileWriter(filePath,true));
            String[] data = {json.get("Name").toString(),json.get("time").toString(),json.get("Crop").toString(),
                        json.get("lat").toString(),json.get("lng").toString()};



            writer.writeNext(data);
            writer.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

