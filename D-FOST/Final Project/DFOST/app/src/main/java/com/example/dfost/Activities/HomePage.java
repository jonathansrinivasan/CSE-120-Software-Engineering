package com.example.dfost.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomePage extends AppCompatActivity {

    // declare activity's layout elements
    public static String username, password;
    private EditText loginSpace;
    private EditText passwordSpace;

    private Button loginButton;
    private Button createAccountButton;

    private ImageView imageView;

    public static final String JSON_ID = "JSON_ID";
    // TODO: should make this shared across the application, not just per class !
    private static RequestQueue queue;
    private static JsonObjectRequest jOR, jOR2;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // initialize layout elements
        loginSpace = (EditText) findViewById(R.id.loginSpace);
        passwordSpace = (EditText) findViewById(R.id.passwordSpace);

        loginButton = (Button) findViewById(R.id.loginButton);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        imageView = (ImageView) findViewById(R.id.imageView);

        try {
            queue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("HomePage", "Started Home Page");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void login(View view) throws JSONException, IOException {

        // please refer to local_server -> app.js for my example login endpoint
        final String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        final String localHostPort = ":3000";
        String loginEndpoint = "/login";
        String URL = localHostIP + localHostPort + loginEndpoint;
        if (loginSpace.getText() != null  && passwordSpace.getText() != null) { // so long as there's something in both the username and login
            final JSONObject json = new JSONObject();

            json.put("username", loginSpace.getText().toString());
            json.put("password", passwordSpace.getText().toString());

            //json object request
            jOR = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.get("access").equals("GRANTED")) {
                                    username = loginSpace.getText().toString();
                                    password = passwordSpace.getText().toString();
                                    //Log.i("HomePage", "Successfully logged in!");
                                    final Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                                    intent.putExtra(Keys.USER_INFO_ID, json.toString());
                                    RequestQueueSingleton.getInstance(getApplicationContext()).setUsername(username);
                                    String getUserInfoEndpoint = "/getUserInfo";
                                    String URL2 = localHostIP + localHostPort + getUserInfoEndpoint;
                                    jOR2 = new JsonObjectRequest(
                                            Request.Method.POST,
                                            URL2,
                                            new JSONObject("{\"username\": \"" + username + "\"}"),
                                            new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        RequestQueueSingleton.getInstance(getApplicationContext()).setUserInfo(response);
                                                        startActivity(intent);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                                    RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jOR2);
                                } else if (response.get("access").equals("DENIED")) {
                                    Toast.makeText(getApplicationContext(), "Incorrect Username/Password", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.i("HomePage", "wrong credentials");
                            Toast.makeText(getApplicationContext(), "Incorrect Username/Password", Toast.LENGTH_LONG).show();
                            //Objects.requireNonNull(error.getMessage())
                        }
                    });

            RequestQueueSingleton.getInstance(this).addToRequestQueue(jOR);


        } else {
            Toast.makeText(this, "Missing Username/Password Field!", Toast.LENGTH_LONG).show();
        }
    }

    public void createAccount(View view) {
        // TODO: Implement this fully
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        //intent.putExtra(JSON_ID, json.toString());
        startActivity(intent);
        //Toast.makeText(this, "createAccountTemp", Toast.LENGTH_LONG).show();
    }

}
