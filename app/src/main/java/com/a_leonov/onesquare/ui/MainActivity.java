package com.a_leonov.onesquare.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.a_leonov.onesquare.BuildConfig;
import com.a_leonov.onesquare.R;
import com.a_leonov.onesquare.Utils;
import com.a_leonov.onesquare.service.LocationUpdatesService;
import com.a_leonov.onesquare.service.OneService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.a_leonov.onesquare.Utils.FRAGMENT_LIST_TAG;
import static com.a_leonov.onesquare.Utils.REQUEST_PERMISSIONS_REQUEST_CODE;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedCategory;
    private String BUNDLE_CATEGORY = "category";
    private boolean locationAvailable = false;

    VenueListFragment listFragment;
    private Toolbar mToolbar;

    private Location mCurrentlocation;

    private VenueListFragment fragmentList;
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    private AdView mAdView;
    FragmentLocationListener fragmentLocationListener;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        listFragment = new VenueListFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, listFragment, FRAGMENT_LIST_TAG)
                .commit();

        if (!checkPermissions()) {
            requestPermissions();
        }

        myReceiver = new MyReceiver();

        OneService.startOneService(this, null);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationUpdatesService.class), this,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermissions()) {
            requestPermissions();
        }

        fragmentList = (VenueListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_TAG);

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        mService.removeLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(this);
            mBound = false;
        }
        super.onStop();
    }


    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);


        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                locationAvailable = true;

                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                locationAvailable = false;
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) iBinder;
        mService = binder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
        mBound = false;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrentlocation = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (mCurrentlocation != null) {
                OneService.startOneService(MainActivity.this, mCurrentlocation);
                Toast.makeText(MainActivity.this, Utils.getLocationText(mCurrentlocation),
                        Toast.LENGTH_SHORT).show();

                if (fragmentList!=null) {

                }
            }
        }
    }

    public interface FragmentLocationListener {
        void onLocationUpdate();
    }
}