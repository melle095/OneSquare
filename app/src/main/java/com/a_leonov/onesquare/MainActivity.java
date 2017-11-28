package com.a_leonov.onesquare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.a_leonov.onesquare.service.OneService;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, LocationListener {

    private String selectedCategory;
    LocationManager locationManager;
    Location lastKnownLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        MainFragment mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, mainFragment)
                .commit();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


    }

    public String getCategory() {
        return selectedCategory;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            lastKnownLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.d(getClass().getSimpleName(), "Error get GPS permission: " + e.getMessage().toString());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onCategorySelected(String category) {

        selectedCategory = category;

        VenueListFragment venueListFragment = new VenueListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, venueListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLocationChanged(Location location) {

        Intent venueIntent = new Intent(this, OneService.class);
        venueIntent.putExtra(OneService.lat, String.valueOf(location.getLatitude()));
        venueIntent.putExtra(OneService.lon, String.valueOf(location.getLongitude()));

        startService(venueIntent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
