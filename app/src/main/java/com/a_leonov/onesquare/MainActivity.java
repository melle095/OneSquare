package com.a_leonov.onesquare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.a_leonov.onesquare.service.OneService;


public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button connect_btn = (Button) findViewById(R.id.b_connect);

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOneSquare();
            }
        });
    }
    void startOneSquare() {
        Intent alarmIntent = new Intent(this, OneService.class);
        alarmIntent.putExtra(OneService.CITY_EXTRA, "Moscow, RU");
        startService(alarmIntent);
    }
}
