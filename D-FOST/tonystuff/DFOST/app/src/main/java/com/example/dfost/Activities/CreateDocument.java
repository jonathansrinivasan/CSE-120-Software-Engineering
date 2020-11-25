package com.example.dfost.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dfost.Adapters.CreateDocumentAdapter;
import com.example.dfost.R;
import com.example.dfost.Singletons.Keys;
import com.example.dfost.Singletons.RequestQueueSingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class CreateDocument extends AppCompatActivity {
    private static final int PERMISSION_ID = 27;

    // declare layout elements
    private EditText docTitle;
    private EditText tags;

    private TextView dateView;
    // additional stuff for the dateView
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private TextView userName;
    private TextView locationView;

    private Button sendButton;
    private Button addSectionButton;
    private Button deleteSectionButton;
    private Button backToAccountButton;

    // recyclerview stuff
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CreateDocumentAdapter createDocumentAdapter;

    // json object for the user information, passed by the intent
    private JSONObject userInfo;

    // some private member variables for document meta data
    private JSONObject documentInfo;
    private ArrayList<JSONObject> sectionInfo;


    // location stuff
    private FusedLocationProviderClient fusedLocationClient;

    private float latitude, longitude;
    private String climateTag;

    // setting up POST request for create document endpoint
    private RequestQueue queue; // do I even need this
    private JSONObject document;
    private JsonObjectRequest sendDocumentRequest;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document);

        // initialize layout elements
        docTitle = (EditText) findViewById(R.id.editTitle);
        tags = (EditText) findViewById(R.id.Tags);

        dateView = (TextView) findViewById(R.id.dateView);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateView.setText("Date: " + dateFormat.format(calendar.getTime())); // yyyy-MM-dd

        userName = (TextView) findViewById(R.id.userName);
        locationView = (TextView) findViewById(R.id.locationID);

        sendButton = (Button) findViewById(R.id.sendDoc);
        addSectionButton = (Button) findViewById(R.id.addSection);
        deleteSectionButton = (Button) findViewById(R.id.deleteSection);
        backToAccountButton = (Button) findViewById(R.id.backToAccountPage);
        recyclerView = (RecyclerView) findViewById(R.id.docSections);

        // initialize the location info
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        Intent intent = getIntent();

        // grab the user info
        try {
            userInfo = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.USER_INFO_ID)));
            // grab the username for the display
            userName.setText("Author: " + userInfo.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sectionInfo = new ArrayList<JSONObject>();
        documentInfo = new JSONObject();
        document = new JSONObject();
        // TODO: replace this
        //sectionInfo.add(new JSONObject());

        // set up the recyclerview stuff
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        createDocumentAdapter = new CreateDocumentAdapter(sectionInfo);
        recyclerView.setAdapter(createDocumentAdapter);
    }

    // methods for getting last location
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    // DecimalFormat will truncate all of our locations to a certain format
                                    DecimalFormat df = new DecimalFormat("##.##");
                                    df.setRoundingMode(RoundingMode.DOWN); // don't automatically round the value up

                                    // get the longitude and latitude of the user
                                    longitude = Float.parseFloat(df.format(location.getLongitude()));
                                    latitude = Float.parseFloat(df.format(location.getLatitude()));
                                    try {
                                        climateTag = RequestQueueSingleton.getInstance(getApplicationContext()).getClimateTag(longitude, latitude);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    locationView.setText("Tag: " + climateTag);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            if (lastLocation == null) {
                // handle error here
            } else {
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN); // don't automatically round the value up
                longitude = Float.parseFloat(df.format(lastLocation.getLongitude()));
                latitude = Float.parseFloat(df.format(lastLocation.getLatitude()));
                try {
                    climateTag = RequestQueueSingleton.getInstance(getApplicationContext()).getClimateTag(longitude, latitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locationView.setText("Tag: " + climateTag);
            }
        }
    };



    // method for adding a section
    public void onAddSectionClick(View view) {
        sectionInfo.add(new JSONObject());
        createDocumentAdapter.notifyItemInserted(sectionInfo.size() - 1);
    }


    // method for sending a section
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSendDocClick(View view) throws JSONException, IOException {
        // prepare the document, then the POST request, then add it to the queue

        // first prepare the header
        documentInfo.put("id", "N/A");

        if (docTitle.getText().toString().length() == 0) {
            Toast.makeText(this, "Can't send doc; no title!", Toast.LENGTH_LONG).show();
            return;
        }
        documentInfo.put("title", docTitle.getText().toString());
        documentInfo.put("username", userInfo.getString("username"));

        // grab the keywords
        String[] keywords = tags.getText().toString().toLowerCase().split(",\\s*");
        documentInfo.put("keywords", new JSONArray(keywords));

        JSONObject locationInfo = new JSONObject();
        locationInfo.put("latitude", latitude);
        locationInfo.put("longitude", longitude);
        locationInfo.put("climate_tag", climateTag);
        documentInfo.put("location", locationInfo);

        documentInfo.put("date", dateFormat.format(calendar.getTime()));

        document.put("header", documentInfo);
        if (sectionInfo.isEmpty()) {
            Toast.makeText(this, "Can't send doc; no sections!", Toast.LENGTH_LONG).show();
            return;
        }
        JSONArray sectionArray = new JSONArray();
        for (int i = 0; i < createDocumentAdapter.getItemCount(); i++) {
            Log.i("CreateDocument", Integer.toString(i));
            View view0 = recyclerView.getChildAt(i);
            EditText sectionTitleView = (EditText) view0.findViewById(R.id.sectionTitle);
            String sectionTitle = sectionTitleView.getText().toString();

            EditText sectionContentView = (EditText) view0.findViewById(R.id.sectionContent);
            String sectionContent = sectionContentView.getText().toString();
            sectionInfo.get(i).put("section_title", sectionTitle);
            sectionInfo.get(i).put("section_content", sectionContent);
            sectionArray.put(i, sectionInfo.get(i));
            //Log.i("CreateDocument", sectionTitle);
        }
        document.put("sections", sectionArray);

        // POST request stuff
        String localHostIP = "http://10.0.2.2"; // local android ip, reroutes to localhost
        String localHostPort = ":3000";
        String insertCreatedDocEndpoint = "/insertCreatedDoc";
        String URL = localHostIP + localHostPort + insertCreatedDocEndpoint;

        sendDocumentRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                document,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("CreateDocument", response.toString());
                        Toast.makeText(getApplicationContext(), "Document sent successfully!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), AccountPage.class);
                        try {
                            final JSONObject userInfo = RequestQueueSingleton.getInstance(getApplicationContext()).getUserInfo();
                            JSONObject temp = new JSONObject() {
                                {
                                    put("username", userInfo.getString("username"));
                                    put ("password", HomePage.password); // WATCH OUT FOR THIS IF THIS ERRORS
                                }
                            };
                            intent.putExtra(Keys.USER_INFO_ID, temp.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
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

        RequestQueueSingleton.getInstance(this).addToRequestQueue(sendDocumentRequest);
    }

    public void onDeleteSectionClick(View view) {
        if (!sectionInfo.isEmpty()) {
            sectionInfo.remove(sectionInfo.size() - 1);
            createDocumentAdapter.notifyItemRemoved(sectionInfo.size());
        } else {
            Toast.makeText(getApplicationContext(), "No sections to delete!", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToAccountClick(View view) throws JSONException, IOException {
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



    // permission stuff
    // Permission check methods
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                requestNewLocationData();
            }
        }
    }

    // end of permission check methods
}
