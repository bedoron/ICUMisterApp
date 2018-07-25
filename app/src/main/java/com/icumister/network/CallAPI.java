package com.icumister.network;

import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CallAPI extends AsyncTask<byte[], String, Map<String, List<String>>> {

    private static String TAG = "CallPI";

    private final String urlString;
    private final Map<String, String> requestProperties;

    public CallAPI(String urlString) {
        this(urlString, null);
    }

    public CallAPI(String urlString, @Nullable Map<String, String> requestProperties) {
        this.urlString = urlString;
        this.requestProperties = requestProperties == null ? Collections.<String, String>emptyMap() : requestProperties;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Map<String, List<String>> doInBackground(byte[]... params) {
        byte[] data = params[0]; //data to post

        try {
            // Defined URL  where to send data
            URL url = new URL(urlString);

            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            for (Map.Entry<String, String> e : requestProperties.entrySet()) {
                conn.setRequestProperty(e.getKey(), e.getValue());
            }
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);//define connection timeout
            conn.setReadTimeout(5000);//define read timeout
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            return conn.getHeaderFields();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}
