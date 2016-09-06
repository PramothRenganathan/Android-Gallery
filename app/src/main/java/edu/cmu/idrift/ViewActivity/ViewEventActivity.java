package edu.cmu.idrift.ViewActivity;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import com.google.android.gms.maps.model.CameraPosition;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.cmu.idrift.Model.EventDao;
import edu.cmu.idrift.PermissionUtils;
import edu.cmu.idrift.R;


//AppCompbatActivity
public class ViewEventActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private long rowID;
    private static final String TAG = "LocationService";
    private TextView nameTextView;


    private TextView dateTimeTextView;
    private TextView locationTextView;
    private boolean isUseStoredTokenKey = false;
    private boolean isUseWebViewForAuthentication = false;


    private GoogleMap googleMap;
    private double latitude, longitude;
    private double target_latitude, target_longitude;
    private GoogleApiClient mLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // get action bar
        ActionBar actionBar = getActionBar();

        // Enabling Up / Back navigation
//        actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(EventListActivity.ROW_ID);

        nameTextView = (TextView) findViewById(R.id.nameTextView);

        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        //Log.d(TAG, "finding MapFragement");

        //mapFragment.getMapAsync(this);


        try {
            // Loading map
            setUpMapIfNeeded();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);


        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            double myLatitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLatitude();
            double myLongitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLongitude();

            Log.d(TAG, String.valueOf(myLatitude));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("ME").snippet("My location").icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            String msg = "Location = " + LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            googleMap.setMyLocationEnabled(true);
        }
    }


    /**
     * function to load map. If map is not created it will create it for you
     * */

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();


            //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            if (googleMap != null) {
                Log.d(TAG, "Google map is not null");
                setUpMap();
            }

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    @Override
    public boolean onMyLocationButtonClick() {
       // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            Toast.makeText(this, "check permision", Toast.LENGTH_SHORT).show();

        } else if (googleMap != null) {
            String msg = "Location = " + LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
        }
        return false;

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadEventTask().execute(rowID);
        setUpMapIfNeeded();

        buildGoogleApiClient();
        mLocationClient.connect();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {

        latitude = 40.443504;
        longitude = -79.941571;

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(14).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("CMU").snippet("Carnegie Mellon University"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        //enableMyLocation();

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);

        if ((googleMap.getMyLocation()) == null) {
            Log.d(TAG, "no my location on google map");
        }
        ;


        //  googleMap.addMarker(new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("ME").snippet("My location"));

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */


    protected synchronized void buildGoogleApiClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new GoogleApiClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
*/

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {


        if (mLocationClient != null && mLocationClient.isConnected()) {

            Log.d(TAG, "old Show my Location");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);


            } else if (googleMap != null) {
                // Access to the location has been granted to the app.
                //googleMap.setMyLocationEnabled(true);
                double myLatitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLatitude();
                double myLongitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLongitude();

                Log.d(TAG, String.valueOf(myLatitude)+","+String.valueOf(myLongitude));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("ME").snippet("My location").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                String msg = "Location = " + LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
            //double myLatitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLatitude();
            // double myLongitude = LocationServices.FusedLocationApi.getLastLocation(mLocationClient).getLongitude();

            // Log.d(TAG, String.valueOf(myLatitude));
            //googleMap.addMarker(new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("ME").snippet("My location").icon(BitmapDescriptorFactory
            // .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            // String msg = "Location = " + LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
            // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        //Log.i("Location Changed = ", location.toString());
    }


    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d(TAG, "old onConnected");

         if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, this);  // LocationListener
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */

    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }




    private class LoadEventTask extends AsyncTask<Long, Object, Cursor>
    {

        EventDao dbConnector= new EventDao(ViewEventActivity.this);
        @Override
        protected Cursor doInBackground(Long... params)
        {
            dbConnector.open();
            return dbConnector.getOneEvent(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);
            result.moveToFirst();

            int nameIndex = result.getColumnIndex("name");
            int dateTimeIndex = result.getColumnIndex("dateTime");
            int locationIndex = result.getColumnIndex("location");

            nameTextView.setText(result.getString(nameIndex));
            dateTimeTextView.setText(result.getString(dateTimeIndex));
            locationTextView.setText(result.getString(locationIndex));

            Log.d(TAG, result.getString(locationIndex));
            result.close();
            dbConnector.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.view_event, menu);
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.view_event, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        return super.onOptionsItemSelected(item);
*/

        switch (item.getItemId())
        {
            case R.id.home :
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, EventListActivity.class));
                return true;

            case R.id.editItem:

                Intent addEvent = new Intent(this, AddEventActivity.class);

                addEvent.putExtra(EventListActivity.ROW_ID, rowID);
                addEvent.putExtra("name", nameTextView.getText());

                addEvent.putExtra("dateTime", dateTimeTextView.getText());
                addEvent.putExtra("location", locationTextView.getText());
                startActivity(addEvent);

                return true;

            case R.id.deleteItem:
                deleteEvent();
                return true;


            /*case R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, EventListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;*/


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteEvent()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventActivity.this);

        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);

        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final EventDao dbConnector =  new EventDao(ViewEventActivity.this);

                        AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Long... params)
                            {
                                dbConnector.deleteEvent(params[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                finish();
                            }
                        };


                        deleteTask.execute(new Long[] { rowID });
                    }
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }
}
