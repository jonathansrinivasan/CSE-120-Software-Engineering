package com.example.dfost.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class SearchPage extends AppCompatActivity {
    private static final int PERMISSION_ID = 24;
    // TODO: Pass Dates as well
    public static final String LAT_ID = "LAT_ID", LONG_ID = "LONG_ID", CLIMATE_ID = "CLIMATE_ID", KEYWORDS_ID = "KEYWORDS_ID",
            FIRST_DATE_ID = "FIRST_DATE_ID", SECOND_DATE_ID = "SECOND_DATE_ID", JSON_OBJ_ID = "JSON_OBJ_ID";

    //layout elements
    private TextView locInfoTextView;

    private SearchView searchView;

    private ImageButton userPageButton;
    private Button searchButton;

    private DatePicker firstDatePicker, secondDatePicker;

    //location element(s)
    private FusedLocationProviderClient fusedLocationClient;

    //member variables
    private float longitude, latitude;
    private String climateTag;
    private JSONObject test2;
    private JSONObject userInfo;
    // methods
    // activity constructor
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(Objects.requireNonNull(intent.getStringExtra(Keys.USER_INFO_ID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // initialize layout elements
        locInfoTextView = (TextView) findViewById(R.id.LocationTextBox);

        searchView = (SearchView) findViewById(R.id.SearchView);

        userPageButton = (ImageButton) findViewById(R.id.UserButton);
        searchButton = (Button) findViewById(R.id.SearchButton);

        firstDatePicker = (DatePicker) findViewById(R.id.FirstDatePicker);
        secondDatePicker = (DatePicker) findViewById(R.id.SecondDatePicker);

        // location initialization
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        try {
            // @ JONATHAN LOOK AT THIS
            test2 = RequestQueueSingleton.getInstance(this.getApplicationContext()).getUserInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNewLocationData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestNewLocationData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("SearchPage", "SearchPage paused.");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("SearchPage", "SearchPage stopped.");
    }

    // gets the last location
    private void getLastLocation() { // https://www.androdocs.com/java/getting-current-location-latitude-longitude-in-android-using-java.html courtesy of this website
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
                                    Log.i("SearchPageActivity", "Location was null. Requesting new location data...");
                                    requestNewLocationData();
                                } else {
                                    // DecimalFormat will truncate all of our locations to a certain format
                                    Log.i("SearchPageActivity", "Location was found.");
                                    DecimalFormat df = new DecimalFormat("##.##");
                                    df.setRoundingMode(RoundingMode.DOWN); // don't automatically round the value up

                                    // get the longitude and latitude of the user
                                    longitude = Float.parseFloat(df.format(location.getLongitude()));
                                    latitude = Float.parseFloat(df.format(location.getLatitude()));
                                    Log.i("SearchPageActivity", "Longitude: " + longitude);
                                    Log.i("SearchPageActivity", "Latitude: " + latitude);
                                    try {
                                        climateTag = RequestQueueSingleton.getInstance(getApplicationContext()).getClimateTag(longitude, latitude);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    locInfoTextView.setText("Latitude: " + latitude + "; Longitude: " + longitude + "; ClimateTag: " + climateTag);
                                    //longLatTextBox.setText("Longitude: " + longitude + "; Latitude: " + latitude);
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

    @SuppressLint("MissingPermission")
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
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location lastLocation = locationResult.getLastLocation();
            if (lastLocation == null) {
                Log.i("SearchPage", "onLocationResult still couldnt' find another location.");
            } else {
                Log.i("SearchPage", "onLocationResult found the last location.");
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN); // don't automatically round the value up

                // get the longitude and latitude of the user
                longitude = Float.parseFloat(df.format(lastLocation.getLongitude()));
                latitude = Float.parseFloat(df.format(lastLocation.getLatitude()));
                try {
                    climateTag = RequestQueueSingleton.getInstance(getApplicationContext()).getClimateTag(longitude, latitude);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locInfoTextView.setText("Latitude: " + latitude + "; Longitude: " + longitude + "; ClimateTag: " + climateTag);
            }

        }
    };

    //button stuff
    public void goToUserProfile(View view) {
        //TODO: Fully implement this and replace the filler toast
        //Toast.makeText(this, "UserProfileTemp", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AccountPage.class);
        Log.i("SearchPage", userInfo.toString());
        intent.putExtra(Keys.USER_INFO_ID, userInfo.toString());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void searchButton(View view) throws JSONException, IOException, ParseException {
        //Toast.makeText(this, "SearchButtonTemp", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoadQueryPage.class);
        intent.putExtra(Keys.USER_INFO_ID, userInfo.toString());
        // get all the data that you want to pass


        // first check if the dates are correct
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(firstDatePicker.getYear() + "-" + firstDatePicker.getMonth() + "-" + firstDatePicker.getDayOfMonth());
        Date d2 = sdf.parse(secondDatePicker.getYear() + "-" + secondDatePicker.getMonth() + "-" + secondDatePicker.getDayOfMonth());
        if (d1.after(d2)) {
            Toast.makeText(this, "Invalid date selection", Toast.LENGTH_LONG).show();
            return;
        }

        // check if youre searching anything
        if (searchView.getQuery().toString().isEmpty()) {
            Toast.makeText(this, "Not searching for anything!", Toast.LENGTH_LONG).show();
            return;
        }


        // TODO: Wrap all parameters into a JSON object, comment out the old implementation

        JSONObject jO = new JSONObject();

        // keywords first
        JSONArray jA1 = new JSONArray();

        // TODO: REPLACE THIS
        String[] keywords = searchView.getQuery().toString().split("\\s");
        for (String keyword : keywords) {
            if (!RequestQueueSingleton.getInstance(this.getApplicationContext()).containsStopword(keyword.toLowerCase())) {
                jA1.put(keyword.toLowerCase());
            }
        }
        jO.put("keywords", jA1);
        // date next
        JSONArray jA2 = new JSONArray();
        jA2.put(firstDatePicker.getYear() + "-" + (firstDatePicker.getMonth() +1) + "-" + firstDatePicker.getDayOfMonth());
        jA2.put(secondDatePicker.getYear() + "-" + (secondDatePicker.getMonth()+1) + "-" + secondDatePicker.getDayOfMonth());
        jO.put("date", jA2);

        // climate tag
        jO.put("climate_tag", climateTag);

        // store it into an intent
        intent.putExtra(JSON_OBJ_ID, jO.toString());
        Log.i("SearchPageActivity", "Attempting to start LoadQueryPage");
        startActivity(intent);
    }


    // Permission check methods //
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
