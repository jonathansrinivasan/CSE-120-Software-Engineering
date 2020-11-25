package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dfost.Adapters.DisplayPageAdapter;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DisplayPage extends AppCompatActivity implements DisplayPageAdapter.OnNoteListener {

    // layout elements
    private TextView docTitleView, dateView, authorView, keywords;
    private Button backToSearchPage, backToListPage;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DisplayPageAdapter displayPageAdapter;

    private static JSONObject json;
    private JSONObject userInfo;
    private static JSONArray jAr;
    private int pos;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_page);

        docTitleView = (TextView) findViewById(R.id.docTitleView);
        dateView = (TextView) findViewById(R.id.dateView);
        authorView = (TextView) findViewById(R.id.authorView);
        keywords = (TextView) findViewById(R.id.keywords);
        recyclerView = (RecyclerView) findViewById(R.id.documentRecycler);
        backToSearchPage = (Button) findViewById(R.id.backToSearchPage);
        backToListPage = (Button) findViewById(R.id.backToListPage);
        // get the json object from the previous page
        Intent intent = getIntent();
        try {
            userInfo = RequestQueueSingleton.getInstance(this.getApplicationContext()).getUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            json = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.DOC_ID)));
            jAr = new JSONArray (intent.getStringExtra(Keys.USER_SUBSCRIBED_ARTICLES_ID));
            JSONObject header = (JSONObject) json.getJSONObject("header");
            docTitleView.setText(header.getString("title"));
            authorView.setText(header.getString("username"));
            dateView.setText(header.get("date").toString());
            keywords.setText(header.getJSONArray("keywords").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        try {
            Log.i("DisplayPage", json.getJSONArray("sections").toString());
            displayPageAdapter = new DisplayPageAdapter(json.getJSONArray("sections"), jAr, json.getJSONObject("header").getString("id") , json.getJSONObject("header").getString("username"), userInfo.getString("username"), this);
            recyclerView.setAdapter(displayPageAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // this is for the payment
    @Override
    public void onNoteClick(int position) throws JSONException, IOException {
        this.pos = position;
        Log.i("DisplayPage", "CLICKED");
        Intent intent = new Intent(this, PaymentConfirmation.class);
        // package the user, the doc id, and the section they want to access and start PaymentConfirmation
        intent.putExtra(Keys.USER_INFO_ID, RequestQueueSingleton.getInstance(this.getApplicationContext()).getUsername());
        intent.putExtra(Keys.REQUESTED_DOC_ID, json.getJSONObject("header").getString("id"));
        intent.putExtra(Keys.REQUESTED_DOC_SECTION_ID, Integer.toString(position));
        //intent.putExtra(DisplayListPage.USER_ACCESSIBLE_DOCS_ID, json.toString());
        startActivity(intent);
    }

    public void onBackToSearchPageClick(View view) throws IOException, JSONException {
        Intent intent = new Intent(this, SearchPage.class);
        final JSONObject userInfo = RequestQueueSingleton.getInstance(getApplicationContext()).getUserInfo();
        JSONObject temp = new JSONObject() {
            {
                put("username", userInfo.getString("username"));
                put ("password", HomePage.password); // WATCH OUT FOR THIS IF THIS ERRORS
            }
        };
        intent.putExtra(Keys.USER_INFO_ID, temp.toString());
        startActivity(intent);
    }

    public void onBackToListPageClick(View view) throws IOException, JSONException {
        Intent intent = new Intent(this, DisplayListPage.class);
        JSONArray lastQuery = RequestQueueSingleton.getInstance(this.getApplicationContext()).getLastResponseQuery();
        intent.putExtra(Keys.QUERY_RESPONSE_ID, lastQuery.toString());
        final JSONObject userInfo = RequestQueueSingleton.getInstance(getApplicationContext()).getUserInfo();
        JSONObject temp = new JSONObject() {
            {
                put("username", userInfo.getString("username"));
                put ("password", HomePage.password); // WATCH OUT FOR THIS IF THIS ERRORS
            }
        };
        intent.putExtra(Keys.USER_INFO_ID, temp.toString());
        startActivity(intent);
    }
}
