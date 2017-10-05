package com.a_leonov.onesquare;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a_leonov.onesquare.fsq.FoursquareApp;
import com.a_leonov.onesquare.fsq.FsqVenue;
import com.a_leonov.onesquare.fsq.NearbyAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FoursquareApp mFsqApp;
    private ListView mListView;
    private NearbyAdapter mAdapter;
    private ArrayList<FsqVenue> mNearbyList;
    private ProgressDialog mProgress;

    public static final String CLIENT_ID = "GPBYVKP11KMUZFNLPUHZFN5SKFZYGL1EAUTTPARVVKEAGWWQ";
    public static final String CLIENT_SECRET = "RVTYEPM5K5XHKWQ4TUAUXQKOG0TFOD3TIUQPDVG23VPPLNIG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final TextView nameTv 		= (TextView) findViewById(R.id.tv_name);
        Button connectBtn 			= (Button) findViewById(R.id.b_connect);
        final EditText latitudeEt	= (EditText) findViewById(R.id.et_latitude);
        final EditText longitudeEt	= (EditText) findViewById(R.id.et_longitude);
        Button goBtn				= (Button) findViewById(R.id.b_go);
        mListView					= (ListView) findViewById(R.id.lv_places);

        mFsqApp 		= new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);

        mAdapter 		= new NearbyAdapter(this);
        mNearbyList		= new ArrayList<FsqVenue>();
        mProgress		= new ProgressDialog(this);

        mProgress.setMessage("Loading data ...");


        if (mFsqApp.hasAccessToken()) nameTv.setText("Connected as " + mFsqApp.getUserName());

        FoursquareApp.FsqAuthListener listener = new FoursquareApp.FsqAuthListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Connected as " + mFsqApp.getUserName(), Toast.LENGTH_SHORT).show();
                nameTv.setText("Connected as " + mFsqApp.getUserName());
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        };

        mFsqApp.setListener(listener);

        //get access token and user name from foursquare
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFsqApp.authorize();
            }
        });

        //use access token to get nearby places
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude  = latitudeEt.getText().toString();
                String longitude = longitudeEt.getText().toString();

                if (latitude.equals("") || longitude.equals("")) {
                    Toast.makeText(MainActivity.this, "Latitude or longitude is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                double lat	= Double.valueOf(latitude);
                double lon	= Double.valueOf(longitude);

                loadNearbyPlaces(lat, lon);
            }
        });
    }

    private void loadNearbyPlaces(final double latitude, final double longitude) {
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                int what = 0;

                try {

                    mNearbyList = mFsqApp.getNearby(latitude, longitude);
                } catch (Exception e) {
                    what = 1;
                    e.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what));
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgress.dismiss();

            if (msg.what == 0) {
                if (mNearbyList.size() == 0) {
                    Toast.makeText(MainActivity.this, "No nearby places available", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAdapter.setData(mNearbyList);
                mListView.setAdapter(mAdapter);
            } else {
                Toast.makeText(MainActivity.this, "Failed to load nearby places", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
