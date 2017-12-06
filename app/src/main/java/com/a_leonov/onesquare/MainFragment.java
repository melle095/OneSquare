package com.a_leonov.onesquare;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.a_leonov.onesquare.data.FoursquareContract;

public class MainFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        Button btn_coffee   = rootView.findViewById(R.id.button_cat_coffee);
        Button btn_bars     = rootView.findViewById(R.id.button_cat_bars);
        Button btn_bistros  = rootView.findViewById(R.id.button_cat_bistros);
        Button btn_clubs    = rootView.findViewById(R.id.button_cat_clubs);

        btn_coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCategorySelected(FoursquareContract.CATEGORY_FOOD);
            }
        });

        btn_bars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCategorySelected(FoursquareContract.CATEGORY_NIGHTLIFE);
            }
        });

        btn_bistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCategorySelected(FoursquareContract.CATEGORY_Entertainment);
            }
        });

        btn_clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCategorySelected(FoursquareContract.CATEGORY_OUTDOOR);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onCategorySelected(String category);
    }
}
