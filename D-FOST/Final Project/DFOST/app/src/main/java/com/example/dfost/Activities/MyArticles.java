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

import com.example.dfost.Adapters.MyAdapter;
import com.example.dfost.Adapters.MyArticlesListAdapter;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyArticles extends AppCompatActivity implements MyArticlesListAdapter.OnNoteListener{

    private Button backToAccount;
    private TextView myArticles;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private MyArticlesListAdapter myArticlesListAdapter;
    private JSONArray jAR;

    private int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles);

        backToAccount = (Button) findViewById(R.id.backToAccount);
        myArticles = (TextView) findViewById(R.id.myArticles);

        recyclerView = (RecyclerView) findViewById(R.id.myArticlesRecycler);

        Intent intent = getIntent();

        try {
            jAR = new JSONArray(intent.getStringExtra(Keys.QUERY_RESPONSE_ID));
            if (jAR.length() >= 1 && jAR.get(0) != "no data") {
                layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                myArticlesListAdapter = new MyArticlesListAdapter(jAR, this);
                recyclerView.setAdapter(myArticlesListAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setBackToAccount(View view) throws IOException, JSONException {
        final JSONObject userInfo = RequestQueueSingleton.getInstance(getApplicationContext()).getUserInfo();
        Intent intent = new Intent(this, AccountPage.class);
        JSONObject temp = new JSONObject() {
            {
                put("username", userInfo.getString("username"));
                put ("password", HomePage.password); // WATCH OUT FOR THIS IF THIS ERRORS
            }
        };
        intent.putExtra(Keys.USER_INFO_ID, temp.toString());
        startActivity(intent);
    }

    @Override
    public void onNoteClick(int position) throws JSONException, IOException {
        this.pos = position;
        Intent intent = new Intent(this, MyArticlesDisplay.class);
        JSONObject json = (JSONObject) jAR.getJSONObject(pos);
        intent.putExtra(Keys.DOC_ID, json.toString());
        startActivity(intent);
    }

}
