package com.a_leonov.onesquare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.a_leonov.onesquare.service.OneService;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        MainFragment mainFragment = new MainFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, mainFragment)
                .commit();

    }

    @Override
    public void onCategorySelected(String category) {
        Intent venueIntent = new Intent(this, OneService.class);
        venueIntent.putExtra(OneService.CITY_EXTRA, "Moscow, RU");
        venueIntent.putExtra(OneService.CATEGORY, category);

        startService(venueIntent);
    }
}
