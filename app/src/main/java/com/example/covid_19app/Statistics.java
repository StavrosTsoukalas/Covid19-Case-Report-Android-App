package com.example.covid_19app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

public class Statistics extends AppCompatActivity {
    CalendarView calendarView;
    String date_selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            if (dayOfMonth < 10) {
                if (month < 9) {
                    date_selected = "0" + dayOfMonth + "-0" + (month + 1) + "-" + year;
                }
                else{
                    date_selected = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
                }
            }
            else{
                if (month < 9) {
                    date_selected = dayOfMonth + "-0" + (month + 1) + "-" + year;
                }
                else{
                    date_selected = dayOfMonth + "-" + (month + 1) + "-" + year;
                }
            }
        });
    }

    public void sendToDataStatistics(View view){
        Intent intent = new Intent(Statistics.this, DataStatistics.class);
        intent.putExtra("dateSelected", date_selected);
        startActivity(intent);
    }
}