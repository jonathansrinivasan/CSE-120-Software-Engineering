package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.dfost.Adapters.MyAdapter;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class DisplayListPage extends AppCompatActivity implements MyAdapter.OnNoteListener {

    // declare layout elements

    private RecyclerView recyclerView;
    private ImageButton imageButton;
    private TextView textView;
    private TextView noData;
    private Button backToSearchPage;

    // recycleview stuff
    private LinearLayoutManager layoutManager;
    private MyAdapter myAdapter;
    private JSONArray jAR;
    public static final String PAGE_ID = "PAGE_ID";
    public static final String USER_ACCESSIBLE_DOCS_ID = "USER_KEYWORD_ID";

    private int pos;
    // request for user stuff
    private static RequestQueue queue;
    private static JsonArrayRequest userKeywordsRequest;
    private JSONArray userKeywordsList, userRequest = new JSONArray();
    private JSONObject userObject;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_page);

        //initializing layout elements
        textView = (TextView) findViewById(R.id.textView);
        noData = (TextView) findViewById(R.id.noData);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        backToSearchPage = (Button) findViewById(R.id.backToSearch);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        try {
             jAR = new JSONArray(intent.getStringExtra(Keys.QUERY_RESPONSE_ID));

            if (jAR.length() >= 1 && jAR.get(0) != "no data") {
                // recyclerview stuff
                //Log.i("DisplayListPage", "TOPKEK");
                noData.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                myAdapter = new MyAdapter(jAR, this);
                recyclerView.setAdapter(myAdapter);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noData.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String getAllowedUserDocsEndpoint = "/getAllowedUserDocs";
        String URL = localHostIP + localHostPort + getAllowedUserDocsEndpoint;

        try {
            userObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.USER_INFO_ID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userRequest.put(userObject);

        userKeywordsRequest = new JsonArrayRequest(
                Request.Method.POST,
                URL,
                userRequest,
                new Response.Listener<JSONArray>() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.i("DisplayListPage", response.toString());
                        try {
                            userKeywordsList = response;
                            Intent intent = new Intent(getApplicationContext(), DisplayPage.class);
                            Log.i("DisplayListPage", "Contents of response: " + userKeywordsList.toString());
                            JSONObject json = (JSONObject) jAR.getJSONObject(pos);
                            Log.i("DisplayListPage", json.toString());
                            intent.putExtra(Keys.DOC_ID, json.toString());
                            intent.putExtra(Keys.USER_SUBSCRIBED_ARTICLES_ID, userKeywordsList.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("DisplayListPage", "ERRORED");
                        Log.i("DisplayListPage", Objects.requireNonNull(error.getMessage()));
                    }
                }
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNoteClick(int position) throws JSONException, IOException {
        this.pos = position;
        //Log.i("DisplayListPage", json.toString());
        Log.i("DisplayListPage", "Attempting to send request for user info...");
        RequestQueueSingleton.getInstance(this).addToRequestQueue(userKeywordsRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onBacktoSearch(View view) throws IOException, JSONException {
        Intent intent = new Intent(this, SearchPage.class);
        final JSONObject userInfo = RequestQueueSingleton.getInstance(this.getApplicationContext()).getUserInfo();
        JSONObject temp = new JSONObject() {
            {
                put("username", userInfo.getString("username"));
                put("password", HomePage.password);
            }
        };
        intent.putExtra(Keys.USER_INFO_ID, temp.toString());
        startActivity(intent);
    }
}
