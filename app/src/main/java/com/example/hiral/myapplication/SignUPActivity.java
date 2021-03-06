package com.example.hiral.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deven Bawale and Nishad Saraf on 11/22/2016.
 */
public class SignUPActivity extends LoginActivity
{
    // UI references
    EditText editTextUserName,editTextPassword,editTextConfirmPassword,latitude,longitude;
    Button btnCreateAccount,btnHomeLocation;
    LoginDataBaseAdapter loginDataBaseAdapter;
    public GoogleMap mMap;
    Location mprovider;
    private String userName;
    private String password;
    private String confirmPassword;
    private String lat;
    private String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        // retrieve stored bundles if any
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userName = intent.getStringExtra(userName);
            password = intent.getStringExtra(password);
            confirmPassword = intent.getStringExtra(confirmPassword);
            lat = intent.getStringExtra(lat);
            lng = intent.getStringExtra(lng);
        }

        // Creates object for LoginDataBaseAdapter to gain access to database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        // link all the UI components to java objects
        editTextUserName = (EditText)findViewById(R.id.editText_username);
        editTextPassword = (EditText)findViewById(R.id.editText_password);
        editTextConfirmPassword = (EditText)findViewById(R.id.editText_confirm_password);
        latitude = (EditText)findViewById(R.id.editText_latitude);
        longitude = (EditText)findViewById(R.id.editText_longitude);
        btnHomeLocation = (Button)findViewById(R.id.button_set_home_location);
        btnCreateAccount = (Button)findViewById(R.id.button_create_acc);

        // Since Android 6.0 some permissions are considered as "dangerous" (FINE_LOCATION is one of them).
        // To protect the user, permissions have to be authorized at runtime, so the user know if it's related to his action.
        ActivityCompat.requestPermissions(SignUPActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        btnHomeLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // LocationManager is the main class through which application can access location services.
                // a reference can be obtained from calling the getSystemService() method.
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {

                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        String lat = String.valueOf(location.getLatitude());
                    }
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    public void onProviderEnabled(String provider) {}
                    public void onProviderDisabled(String provider) {}

                };
                // get the location from gps provider
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                String locationProvider = LocationManager.GPS_PROVIDER;
                mprovider = locationManager.getLastKnownLocation(locationProvider);
                if (mprovider != null && !mprovider.equals("")) {
                    // if permission is granted then set the EditText views with respective values of latitude and longitude
                    if (ActivityCompat.checkSelfPermission(SignUPActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUPActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = mprovider;
                    if (location != null) {
                        latitude.setText(String.valueOf(location.getLatitude()));
                        longitude.setText( String.valueOf(location.getLongitude()));
                    }
                    else
                        Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userName=editTextUserName.getText().toString();
                password=editTextPassword.getText().toString();
                confirmPassword=editTextConfirmPassword.getText().toString();
                lat=latitude.getText().toString();
                lng=longitude.getText().toString();

                //check if the username is of valid email address format
                Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
                Matcher mat = pattern.matcher(userName);
                if(!mat.matches()){
                    Toast.makeText(getApplicationContext(),"Username should be a valid email address",Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if any of the fields are vacant
                if(userName.equals("")||password.equals("")||confirmPassword.equals("")||lat.equals("")||lng.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Field Vacant", Toast.LENGTH_LONG).show();
                    return;
                }
                // check if both password matches
                if(!password.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    // Save the Data in Database
                    loginDataBaseAdapter.insertEntry(userName, password,lat,lng);
                    Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUPActivity.this,LoginActivity.class);
                    startActivity(intent);

                }

            }
        });
    }

    // fires when there is a change in device configuration to save data across these changes
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("username", userName);
        savedInstanceState.putString("password", password);
        savedInstanceState.putString("confirm_password", confirmPassword);
        savedInstanceState.putString("latitude", lat);
        savedInstanceState.putString("longitude", lng);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}
