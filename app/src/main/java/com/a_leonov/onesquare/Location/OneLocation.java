package com.a_leonov.onesquare.Location;

import android.app.Application;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by a_leonov on 15.01.2018.
 */

public class OneLocation extends LocationCallback implements Parcelable{

    private static final String TAG = OneLocation.class.getSimpleName();

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    Context context;

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 10;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public Boolean getmRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public void setmRequestingLocationUpdates(Boolean mRequestingLocationUpdates) {
        this.mRequestingLocationUpdates = mRequestingLocationUpdates;
    }

    public final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;

    public OneLocation(Object context){

        this.context = (Context) context;

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        mSettingsClient = LocationServices.getSettingsClient(this.context);
//        createGetLastLocationCallback();
//        createLocationCallback();
//        createLocationRequest();
        buildLocationSettingsRequest();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<OneLocation> CREATOR = new Parcelable.Creator<OneLocation>() {
        // распаковываем объект из Parcel
        public OneLocation createFromParcel(Parcel in) {
            Log.d(TAG, "createFromParcel");
            return new OneLocation(in);
        }

        public OneLocation[] newArray(int size) {
            return new OneLocation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(context, i);
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        mCurrentLocation = locationResult.getLastLocation();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }
//    }

//    private void createGetLastLocationCallback() {
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(mContext, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        mCurrentLocation = location;
//                    }
//                });
//
//    }

    private void buildLocationSettingsRequest() {


        // Location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        //Location setting request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        if (mLocationSettingsRequest != null) {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "All location settings are satisfied.");

                            //noinspection MissingPermission
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());

                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                            "location settings ");
                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
//                                    Toast.makeText(ac, errorMessage, Toast.LENGTH_LONG).show();
                                    mRequestingLocationUpdates = false;
                            }
                        }
                    });
        }
    }

    public String getmLastUpdateTime() {
        return mLastUpdateTime;
    }

    public void setmLastUpdateTime(String mLastUpdateTime) {
        this.mLastUpdateTime = mLastUpdateTime;
    }

    public void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
//                        setButtonsEnabledState();
                    }
                });
    }

}
