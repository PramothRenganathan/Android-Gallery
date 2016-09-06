package edu.cmu.idrift.ViewActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings;

import edu.cmu.idrift.HomeActivity;
import edu.cmu.idrift.Model.EventDao;
import edu.cmu.idrift.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends ActionBarActivity {
    private long rowID;

    private EditText nameEditText;
    private EditText dateTimEditText;
    private EditText locationEditText;
    private String addressText;
    private LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

// get action bar
       // ActionBar actionBar = getActionBar();
        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);


        nameEditText = (EditText) findViewById(R.id.nameEditText);
        dateTimEditText = (EditText) findViewById(R.id.dateTimEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);



        Bundle extras = getIntent().getExtras();


        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            nameEditText.setText(extras.getString("name"));
            dateTimEditText.setText(extras.getString("dateTime"));
            locationEditText.setText(extras.getString("location"));
        }

        Button saveEventButton = (Button) findViewById(R.id.saveEventButton);
        saveEventButton.setOnClickListener(saveEventButtonClicked);
    }


    View.OnClickListener saveEventButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (nameEditText.getText().length() != 0)
            {
                if (dateTimEditText.getText().length()==0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    String currentDateTime = sdf.format(new Date());

                    dateTimEditText.setText(currentDateTime);


                }
                if (locationEditText.getText().length()==0) {


                    //get current Location - Geocode

                    // Get a reference to the LocationManager object.
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    String networkProvider = mLocationManager.NETWORK_PROVIDER;
                    String gpsProvider = mLocationManager.GPS_PROVIDER;


                    Location lastKnownLocation = mLocationManager.getLastKnownLocation(networkProvider);

                    doReverseGeocoding(lastKnownLocation);


                    if (lastKnownLocation==null){

                        locationEditText.setText("Pittsburgh Racing Complex");
                        Log.d("no knownLocate", "Just String");
                    }
                    else if(lastKnownLocation!=null && addressText!=null) {
                         locationEditText.setText(addressText);
                         Log.d("Info",addressText);
                     }

                     else if(lastKnownLocation!=null && addressText==null) {
                             locationEditText.setText(lastKnownLocation.toString());
                             Log.d("[Not Null]AddressText",lastKnownLocation.toString());
                         }



                }
                AsyncTask<Object, Object, Object> saveEventTask = new AsyncTask<Object, Object, Object>()
                {
                    @Override
                    protected Object doInBackground(Object... params)
                    {

                        saveEvent();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object result)
                    {
                        finish();
                    }
                };

                saveEventTask.execute((Object[]) null);
            }
            else
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show();
            }
        }
    };


    private void saveEvent()
    {

        EventDao dbhelper = new EventDao(this);
        if (getIntent().getExtras() == null)
        {

            dbhelper.insertEvent(
                    nameEditText.getText().toString(),
                    dateTimEditText.getText().toString(),
                    locationEditText.getText().toString());

        }
        else
        {
            dbhelper.updateEvent(rowID,
                    nameEditText.getText().toString(),
                    dateTimEditText.getText().toString(),
                    locationEditText.getText().toString());

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
        return true;
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {



            case R.id.home:

                Intent listEvent = new Intent(AddEventActivity.this, HomeActivity.class);
                startActivity(listEvent);

                return true;
            case R.id.listEventItem:

                Intent itemList = new Intent(AddEventActivity.this, EventListActivity.class);
                startActivity(itemList);

                return true;





            default:
                return super.onOptionsItemSelected(item);
        }
        // return super.onOptionsItemSelected(item);

    }



    private void doReverseGeocoding(Location location) {
        // Since the geocoding API is synchronous and may take a while.  You don't want to lock
        // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
        (new ReverseGeocodingTask(this)).execute(new Location[] {location});
    }



    // Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }


// Since the geocoding API is synchronous and may take a while.  You don't want to lock
// up the UI thread.  Invoking reverse geocoding in an AsyncTask.
    // AsyncTask encapsulating the reverse-geocoding API.
    private class ReverseGeocodingTask extends AsyncTask<Location, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            Location loc = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("AddEventActivity",
                        "IO Exception in getFromLocation()");
                // Update address field with the exception.
                // Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
                Log.d("IOException", loc.getLatitude() + ", " + loc.getLongitude());
                return ("IO Exception trying to get address");

            }catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("AddEventActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }catch (Exception e3) {
                // Error message to post in the log
                String errorString = "Unkown Reason" +

                        " GPS code couldn't pass to address service";
                Log.e("AddEventActivity", errorString);
                e3.printStackTrace();
                return errorString;
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Format the first line of address (if available), city, and country name.
                 addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                // Update address field on UI.
                Log.d("Address Found", addressText);
                // Return the text
                return addressText;
            } else {
                Log.e("AddEventActivity","Address Not Found");
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            // Set activity indicator visibility to "gone"

            // Display the results of the lookup.
            locationEditText.setText(addressText);
            if (addressText!= null ) Log.d("addressText in Posting", addressText);
        }

    }
}

