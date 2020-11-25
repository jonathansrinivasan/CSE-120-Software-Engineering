package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class LoadQueryPage extends AppCompatActivity {
    public static final String RESPONSE_ID = "RESPONSE_ID";

    // layout elements
    private TextView debugInfo;

    // member variables
    private static RequestQueue queue;
    private boolean debugging = false; // TODO: change this accordingly
    private JSONObject json;
    private JSONArray jarray;
    private JSONObject userInfo;

    private static JsonArrayRequest jAR;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_query_page);

        // debug log
        Log.i("LoadQueryPage", "Loaded QueryPage");

        //initialize layout elements
        debugInfo = (TextView) findViewById(R.id.DebugView);
        if (debugging) debugInfo.setVisibility(View.VISIBLE);
        else debugInfo.setVisibility(View.INVISIBLE);

        //initializing member variables
        Intent intent = getIntent();

        try {
            userInfo = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.USER_INFO_ID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // POST request to /findDocuments endpoint of local server

        try {
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String findDocEndpoint = "/findDocuments";
        String URL = localHostIP + localHostPort + findDocEndpoint;
        Log.i("LoadQueryPage", URL);

        // initialize test JSON object

        try {
            json = new JSONObject(Objects.requireNonNull(intent.getStringExtra(SearchPage.JSON_OBJ_ID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // we want to return a json array so we're kind of forced to package our json object into an array : (
        jarray = new JSONArray();
        jarray.put(json);

        jAR = new JsonArrayRequest(
                Request.Method.POST,
                URL,
                jarray,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.i("LoadQueryPage", response.get(0).toString());
                            Intent intent = new Intent(getApplicationContext(), DisplayListPage.class);
                            intent.putExtra(Keys.USER_INFO_ID, userInfo.toString());
                            RequestQueueSingleton.getInstance(getApplicationContext()).setLastResponseQuery(response);
                            intent.putExtra(Keys.QUERY_RESPONSE_ID, response.toString());
                            startActivity(intent);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Should probably handle this
                        Log.i("LoadQueryPage", error.getMessage());
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LoadQueryPage", "Started QueryPage");
        try {
            RequestQueueSingleton.getInstance(this).addToRequestQueue(jAR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
