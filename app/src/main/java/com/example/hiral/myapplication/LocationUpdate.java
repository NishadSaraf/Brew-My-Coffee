package com.example.hiral.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * LocationUpdate.java handles the UI appearing on Sign In
 *
 */
public class LocationUpdate extends AppCompatActivity{

    private final String LOCATION_UPDATE_TAG = "LocationUpdate";
    private EditText send_message;
    private Button send;
    private Button current_location_btn;
    private EditText editTextLocation;
    int Place_Picker_request=1;
    Editable message;
    LoginDataBaseAdapter loginData;
    String address;
    TextView current_location_tv;
    public double latitude,longitude,modalat,modalon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_update);

        /**
         * TODO: Define onClick Listener for "get_location" text box to get user's current location
         * Hint: Need to define IntentBuilder for PlacePicker built-in UI widget
         * Reference : https://developers.google.com/places/android-api/placepicker
         */
        editTextLocation = (EditText) findViewById(R.id.editText_current_location);
        editTextLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(LocationUpdate.this),Place_Picker_request);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(LocationUpdate.this,R.string.internal_error,Toast.LENGTH_SHORT).show();
                    Log.e(LOCATION_UPDATE_TAG,e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(LocationUpdate.this,R.string.internal_error,Toast.LENGTH_SHORT).show();
                    Log.e(LOCATION_UPDATE_TAG,e.getMessage());
                }
            }
        });


        current_location_tv = (TextView) findViewById(R.id.textView_current_location);
        current_location_btn = (Button) findViewById(R.id.button_current_location);
        current_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_location_tv.setText(address);
            }
        });



        /**
         * Do not edit the code below as it is dependent on server just fill the required snippets
         *
         */
        send_message = (EditText) findViewById(R.id.Send_Message);
        send = (Button) findViewById(R.id.Send_Button);
        loginData = new LoginDataBaseAdapter(this);
        loginData = loginData.open();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * OnClick event for send button gets username and location details
                 */
                message=send_message.getText();
                Bundle extras=getIntent().getExtras();
                String rx_username=extras.getString("tx_user_name");


                String rx_lat=loginData.getLat(rx_username);
                String rx_lon=loginData.getLng(rx_username);

                /**
                 * store in latitude , longitude variables to pass to json object
                 */
                modalat=Double.parseDouble(rx_lat);
                modalon=Double.parseDouble(rx_lon);

                try {

                    /**
                     * Creates a JSON object and uses toSend.put to send home, current location along with message
                     *Pass data as name/value pair where you cannot edit name written
                     *in " " ex:"home_lat" as this are hard coded on server side.
                     *You can change the variable name carrying value ex:modalat
                     */
                    JSONObject toSend = new JSONObject();
                    toSend.put("home_lat", modalat);
                    toSend.put("home_lon",modalon);
                    toSend.put("c_lat", latitude);
                    toSend.put("c_lon",longitude);
                    toSend.put("message",message);

                    /**
                     * Creates transmitter object to send data to server
                     */
                    JSONTransmitter transmitter = new JSONTransmitter();
                    transmitter.execute(new JSONObject[] {toSend});

                    /**
                     * Receives a message from the server which is displayed as toast
                     */
                    JSONObject output=transmitter.get();
                    String op=output.getString("message");
                    Toast.makeText(LocationUpdate.this,op, Toast.LENGTH_LONG).show();

                }
                //To handle exceptions
                catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * TODO: Define onActivityResult() method which would take Place_Picker_request
     * and extract current Latitude, Longitude and address string
     * Hint : Set the address String to "get_location" text box
     * Reference : https://developers.google.com/places/android-api/placepicker
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Place_Picker_request){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                LatLng latLng = place.getLatLng();
                this.latitude = latLng.latitude;
                this.longitude = latLng.longitude;
                this.address = place.getAddress().toString();
                editTextLocation.setText(this.address);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

