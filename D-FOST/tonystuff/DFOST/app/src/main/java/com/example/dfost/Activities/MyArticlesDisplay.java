package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dfost.Adapters.DisplayPageAdapter;
import com.example.dfost.Adapters.MyArticleDisplayAdapter;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class MyArticlesDisplay extends AppCompatActivity {

    private TextView docTitleView, dateView, authorView, keywords;
    private Button backToSearchPage, backToListPage;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyArticleDisplayAdapter displayPageAdapter;

    private JSONObject json;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles_display);

        docTitleView = (TextView) findViewById(R.id.docTitleView);
        dateView = (TextView) findViewById(R.id.dateView);
        authorView = (TextView) findViewById(R.id.authorView);
        keywords = (TextView) findViewById(R.id.keywords);
        recyclerView = (RecyclerView) findViewById(R.id.documentRecycler);
        backToSearchPage = (Button) findViewById(R.id.backToSearchPage);
        backToListPage = (Button) findViewById(R.id.backToListPage);

        Intent intent = getIntent();
        try {
            json = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.DOC_ID)));
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
            displayPageAdapter = new MyArticleDisplayAdapter(json.getJSONArray("sections"));
            recyclerView.setAdapter(displayPageAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onBackToSearchPageClick(View view) throws JSONException, IOException {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onBackToListPageClick(View view) throws IOException, JSONException {
        Intent intent = new Intent(this, MyArticles.class);
        JSONArray lastQuery = RequestQueueSingleton.getInstance(this.getApplicationContext()).getLastResponseQuery();
        intent.putExtra(Keys.QUERY_RESPONSE_ID, lastQuery.toString());
        startActivity(intent);
    }

}
