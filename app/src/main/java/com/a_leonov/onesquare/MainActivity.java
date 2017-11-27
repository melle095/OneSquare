package com.a_leonov.onesquare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.a_leonov.onesquare.data.FoursquareContract;
import com.a_leonov.onesquare.service.OneService;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private String selectedCategory;

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

    public String getCategory(){
        return selectedCategory;
    }

    @Override
    public void onCategorySelected(String category) {

        selectedCategory = category;
        Intent venueIntent = new Intent(this, OneService.class);
        venueIntent.putExtra(OneService.CITY_EXTRA, "Moscow, RU");
        venueIntent.putExtra(OneService.CATEGORY, FoursquareContract.CATEGORY_TOP_LEVEL_FOOD);

        startService(venueIntent);

        VenueListFragment venueListFragment = new VenueListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, venueListFragment)
                .addToBackStack(null)
                .commit();
    }
}
