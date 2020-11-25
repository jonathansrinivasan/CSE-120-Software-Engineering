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
import com.android.volley.toolbox.Volley;
import com.example.dfost.R;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignUp extends AppCompatActivity {
    private EditText usernameField;
    private EditText passwordField;
    private EditText nameField;

    private Button registerUserButton;

    private ImageView imageView;

    public static final String JSON_ID = "JSON_ID";

    private static RequestQueue queue;
    private static JsonObjectRequest jOR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize layout elements
        usernameField = (EditText) findViewById(R.id.usernameField);
        nameField = (EditText) findViewById(R.id.nameField);
        passwordField = (EditText) findViewById(R.id.passwordField);

        registerUserButton = (Button) findViewById(R.id.registerUserButton);

        imageView = (ImageView) findViewById(R.id.imageView);

        //queue = Volley.newRequestQueue(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SignUp", "Started SignUp Page");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(View view) throws JSONException, IOException {
        // please refer to local_server -> app.js for my example login endpoint
        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String loginEndpoint = "/register";
        String URL = localHostIP + localHostPort + loginEndpoint;
        if (usernameField.getText() != null && nameField.getText() != null && passwordField.getText() != null){
            final JSONObject json = new JSONObject();

            json.put("username", usernameField.getText());
            json.put("password", passwordField.getText());
            json.put("name", nameField.getText());

            //json object request
            jOR = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("SignUp", "Successfully Registered!");
                            try {
                                RequestQueueSingleton.getInstance(getApplicationContext()).setUsername(json.getString("username"));
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                            intent.putExtra(JSON_ID, json.toString());
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("SignUp", "Account already exists");
                            //Objects.requireNonNull(error.getMessage())
                        }
                    });

            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jOR);


        } else {
            Toast.makeText(this, "Missing Fields!", Toast.LENGTH_LONG).show();
        }

    }
}