package com.example.dfost.Singletons;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.dfost.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RequestQueueSingleton {
    private static RequestQueueSingleton instance;
    private RequestQueue requestQueue;
    private ArrayList<ArrayList<String>> koppenDataset;
    private ArrayList<String> stopWords;
    private static Context ctx;
    private String username;
    private JSONObject userInfo;
    private JSONArray lastResponseQuery;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private RequestQueueSingleton(Context context) throws IOException {
        ctx = context;
        requestQueue = getRequestQueue();
        koppenDataset = getKoppenDataset();
        stopWords = getStopWords();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static synchronized RequestQueueSingleton getInstance(Context context) throws IOException {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<ArrayList<String>> getKoppenDataset() {
        if (koppenDataset == null) {
            koppenDataset = new ArrayList<ArrayList<String>>();
            InputStream is = ctx.getResources().openRawResource(R.raw.koppen);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int i = 0;

                while ((line = br.readLine()) != null) {
                    koppenDataset.add(new ArrayList<String>());
                    String[] splitText = line.split("\t");
                    for (String ss : splitText)
                        koppenDataset.get(i).add(ss);
                    i++;
                }
                if (koppenDataset.size() == 85795) Log.i("SearchPage", "Parsed successfully");
            } catch (Exception e) {

            }
        }
        return koppenDataset;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> getStopWords() {
        if (stopWords == null) {
            stopWords = new ArrayList<String>();
            InputStream is1 = ctx.getResources().openRawResource(R.raw.stoptext);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is1))) {
                String line;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line);
                }
            } catch (Exception e) {

            }
        }
        return stopWords;
    }

    public void setUserInfo(JSONObject userInfo) throws JSONException {
        this.userInfo = new JSONObject(userInfo.toString());
    }
    public void setLastResponseQuery(JSONArray lastQuery) throws JSONException {
        this.lastResponseQuery = new JSONArray(lastQuery.toString());
    }
    public JSONArray getLastResponseQuery() {
        return this.lastResponseQuery;
    }

    public JSONObject getUserInfo() {
        return this.userInfo;
    }

    public boolean containsStopword(String word) {
        return stopWords.contains(word);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getClimateTag(float longitude, float latitude) {
        int i = 1;
        while (i < koppenDataset.size()) {
            float lon = Float.parseFloat(koppenDataset.get(i).get(0));
            float lat = Float.parseFloat(koppenDataset.get(i).get(1));
            if (Math.abs(Math.abs(longitude) - Math.abs(lon)) <= 0.5f && Math.abs(Math.abs(latitude) - Math.abs(lat)) <= 0.5f) {
                return koppenDataset.get(i).get(2);
            }
            i++;
        }
        return "N/A";
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
