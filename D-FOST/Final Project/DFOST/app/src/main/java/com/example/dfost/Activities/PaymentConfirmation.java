package com.example.dfost.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PaymentConfirmation extends AppCompatActivity {

    private TextView paymentView;
    private Button sendRequest;
    private Intent intent;

    private JsonObjectRequest jOR, pageRedirectRequest;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        paymentView = (TextView) findViewById(R.id.purchaseInfo);
        sendRequest = (Button) findViewById(R.id.sendRequest);

        intent = getIntent();
        paymentView.setText("Confirming purchase details: User " + intent.getStringExtra(Keys.USER_INFO_ID) + " purchasing section " + intent.getStringExtra(Keys.REQUESTED_DOC_SECTION_ID) + " of document #" + intent.getStringExtra(Keys.REQUESTED_DOC_ID));
        JSONObject temp = new JSONObject();
        try {
            temp.put("username", intent.getStringExtra(Keys.USER_INFO_ID));
            temp.put("doc_id", intent.getStringExtra(Keys.REQUESTED_DOC_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String sectionRequestRedirectEndpoint = "/sectionRequestRedirect";
        String URL = localHostIP + localHostPort + sectionRequestRedirectEndpoint;

        pageRedirectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                temp,
                new Response.Listener<JSONObject>() {
                    // TODO: Implement
                    @Override
                    public void onResponse(JSONObject response) {
                        // get the user sections array, and the doc info
                        Intent intent = new Intent(getApplicationContext(), DisplayPage.class);
                        try {
                            intent.putExtra(Keys.USER_SUBSCRIBED_ARTICLES_ID, response.getJSONArray("subscribedArticles").toString());
                            intent.putExtra(Keys.DOC_ID, response.getJSONObject("docInfo").toString());
                        } catch (JSONException e) {
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

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendRequest(View view) throws JSONException, IOException {
        JSONObject temp = new JSONObject();
        temp.put("username", intent.getStringExtra(Keys.USER_INFO_ID));
        temp.put("doc_section_id",intent.getStringExtra(Keys.REQUESTED_DOC_SECTION_ID));
        temp.put("doc_id", intent.getStringExtra(Keys.REQUESTED_DOC_ID));

        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String sectionRequestEndpoint = "/sectionRequest";
        String URL = localHostIP + localHostPort + sectionRequestEndpoint;

        jOR = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                temp,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("PaymentConfirmation", response.toString());
                        Toast.makeText(getApplicationContext(), "Section purchase successful, redirecting you to page", Toast.LENGTH_LONG).show();
                        try {
                            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(pageRedirectRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jOR);
    }
}
