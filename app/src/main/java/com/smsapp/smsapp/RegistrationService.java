package com.smsapp.smsapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by pawim on 02.04.2016.
 */
public class RegistrationService {
    private static final String TAG = "RegistrationService";

    public String sendPostRequest(String urlAddress, HashMap<String, String> data) throws IOException  {

            URL url;
            try {
                url = new URL(urlAddress);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + urlAddress);
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }

            String body = bodyBuilder.toString();
            Log.v(TAG, "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();

            HttpURLConnection httpURLConnection = null;
            try {
                Log.e("URL", "> " + url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                OutputStream out = httpURLConnection.getOutputStream();
                out.write(bytes);
                out.close();
                int status = httpURLConnection.getResponseCode();
                if (status != 200) {
                    throw new IOException("Post failed with error code " + status);

                }
            } catch(IOException e){
                e.printStackTrace();
               return Variables.FAILURE;
            }
            if (httpURLConnection != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String response = bufferedReader.readLine();
                Log.i(TAG, "GCM Registration Token DONE: " + response);
                httpURLConnection.disconnect();

                return response;
            } else {
                Log.i(TAG, "GCM Registration Token FAILURE ");
                return Variables.FAILURE;
            }

    }
}
