package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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

public class AccountPage extends AppCompatActivity {

    // layout elements
    private Button paidArticlesButton;
    private Button myArticlesButton;
    private Button createAccountButton;
    private Button backToSearchButton;
    private Button logOutButton;

    private ImageView userPicture;

    private TextView userName;

    private JSONObject userInfo;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        // initialize lay-out elements
        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.USER_INFO_ID)));
            Log.i("HomePage", userInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        paidArticlesButton = (Button) findViewById(R.id.paidArticlesButton);
        myArticlesButton = (Button) findViewById(R.id.myArticlesButton);
        createAccountButton = (Button) findViewById(R.id.createDocButton);
        backToSearchButton = (Button) findViewById(R.id.backtoSearch);
        logOutButton = (Button) findViewById(R.id.logOutButton);

        userPicture = (ImageView) findViewById(R.id.userPicture);

        userName = (TextView) findViewById(R.id.usernameField);

        try {
            userName.setText(RequestQueueSingleton.getInstance(this.getApplicationContext()).getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // for the paidArticles button
    public void paidArticles(View view) {
        // TODO: Implement this fully, get rid of Toast
        Toast.makeText(this, "PaidArticlesTemp", Toast.LENGTH_LONG).show();
    }

    // for the myArticles button
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void myArticles(View view) throws IOException, JSONException {
        // TODO: Implement this fully, get rid of Toast
        Toast.makeText(this, "MyArticlesTemp", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MyArticles.class);
        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String findMyDocumentsEndpoint = "/findMyDocuments";
        String URL = localHostIP + localHostPort + findMyDocumentsEndpoint;
        final JSONObject tempObject = new JSONObject() {
            {
                put("username", RequestQueueSingleton.getInstance(getApplicationContext()).getUsername());
            }
        };
        JSONArray tempArray = new JSONArray() {
            {
                put(tempObject);
            }
        };
        JsonArrayRequest jAR = new JsonArrayRequest(
                Request.Method.POST,
                URL,
                tempArray,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Intent intent = new Intent(getApplicationContext(), MyArticles.class);
                        intent.putExtra(Keys.QUERY_RESPONSE_ID, response.toString());
                        try {
                            RequestQueueSingleton.getInstance(getApplicationContext()).setLastResponseQuery(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jAR);
    }

    // for the createDocument Button
    public void createDocumentButon(View view) {
        // TODO: Implement this fully, get rid of Toast
        //Toast.makeText(this, "EditAccountTemp", Toast.LENGTH_LONG).show();
        Log.i("AccountPage", userInfo.toString());
        Intent intent = new Intent(this, CreateDocument.class);
        intent.putExtra(Keys.USER_INFO_ID, userInfo.toString());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void backToSearchOnClick(View view) throws JSONException, IOException {
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



    // for the log out button
    public void logOut(View view) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

}
