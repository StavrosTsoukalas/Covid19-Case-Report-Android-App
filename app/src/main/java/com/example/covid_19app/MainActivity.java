package com.example.covid_19app;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    FirebaseAuth mAuth;
    ImageView covid_img;
    Button covid_confirm_btn;
    AlertDialog.Builder builder, builder2;
    TextView textView;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef;

    private String amka, humidity, amb_temp;

    private SensorManager sensorManager;

    private Sensor humiditySensor, amb_tempSensor;

    private boolean isHumidityAvail, isTempAvail;
    private boolean pressed = false;

    private long maxid = 0;

    private TextToSpeech tts;

    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String timestamp;

    DecimalFormat df = new DecimalFormat("#.#");

    LocationManager locationManager;

    private final double[] location_cords = new double[2];

    static final int REQUEST_LOCATION_CODE = 23;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS = 0;
    private static final long MIN_TIME_BETWEEN_UPDATES = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        amka = getIntent().getStringExtra("amka");
        mRef = db.getReference("CovidCases").child(amka);

        covid_img = findViewById(R.id.imageView6);
        covid_confirm_btn = findViewById(R.id.button7);
        textView = findViewById(R.id.textView6);

        builder = new AlertDialog.Builder(this);
        builder2 = new AlertDialog.Builder(this);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });

        if(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            isHumidityAvail = true;
        }
        else{
            Toast.makeText(this, getString(R.string.humidity_unavail), Toast.LENGTH_SHORT).show();
            isHumidityAvail = false;
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            amb_tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isTempAvail = true;
        }
        else{
            Toast.makeText(this, getString(R.string.amb_temp_unavail), Toast.LENGTH_SHORT).show();
            isTempAvail = false;
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, MainActivity.this);
        }
    }

    //Simple method showing an alert dialog box with two buttons: one for Confirmation and one for cancelling and closing the AlertDialog
    public void covid19_case(View view){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE);
        }
        else{
            pressed = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, MainActivity.this);
            if (covid_confirm_btn.getVisibility() == View.INVISIBLE) {
                showMessage(getString(R.string.wait));
            }
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(covid_img, "scaleX", 1.0f, 1.3f, 1.0f)
                            .setDuration(800),
                    ObjectAnimator.ofFloat(covid_img, "scaleY", 1.0f, 1.3f, 1.0f)
                            .setDuration(800)
            );
            set.start();
        }
    }

    public void covid19_confirmation(View view){
        builder.setMessage(R.string.covid19_confirm_msg)
                .setTitle(R.string.covid19_confirm_title)
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.covid19_confirm_title, (dialog, which) -> {
                    if(location_cords[0] != 0 && location_cords[1] != 0) {
                        if (isHumidityAvail && humidity != null) {
                            CovidCase covidCase;
                            if (isTempAvail && amb_temp != null) {
                                covidCase = new CovidCase(timestamp, humidity, amb_temp, location_cords[0], location_cords[1]);
                            } else {
                                covidCase = new CovidCase(timestamp, humidity, "-", location_cords[0], location_cords[1]);
                            }
                            mRef.child("Case" + maxid).setValue(covidCase);
                        } else {
                            CovidCase covidCase;
                            if (isTempAvail && amb_temp != null) {
                                covidCase = new CovidCase(timestamp, "-", amb_temp, location_cords[0], location_cords[1]);
                            } else {
                                covidCase = new CovidCase(timestamp, "-", "-", location_cords[0], location_cords[1]);
                            }
                            mRef.child("Case" + maxid).setValue(covidCase);
                        }
                        covid_confirm_btn.setVisibility(View.INVISIBLE);
                        pressed = false;
                        showMessage(getString(R.string.covid19_case_confirmed_msg));
                    }
                    else{
                        pressed = false;
                        covid_confirm_btn.setVisibility(View.INVISIBLE);
                        showMessage(getString(R.string.location_not_avail));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sendToStatistics(View view){
        Intent intent = new Intent(MainActivity.this, Statistics.class);
        startActivity(intent);
    }

    public void editProfile(View view){
        Intent intent = new Intent(MainActivity.this, EditProfile.class);
        intent.putExtra("amka", amka);
        intent.putExtra("employee", false);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, MainActivity.this);
            }
        }
    }

    public void signout(View view){
        locationManager.removeUpdates(this);
        mAuth.signOut();
        finish();
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        locationManager.removeUpdates(this);
        if(isHumidityAvail){
            sensorManager.unregisterListener(this, humiditySensor);
        }
        if(isTempAvail){
            sensorManager.unregisterListener(this, amb_tempSensor);
        }
        finish();
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                humidity = df.format(event.values[0]) + "%";
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                amb_temp = df.format(event.values[0]) + "°C";
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isHumidityAvail){
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(isTempAvail){
            sensorManager.registerListener(this, amb_tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isHumidityAvail){
            sensorManager.unregisterListener(this, humiditySensor);
        }
        if(isTempAvail){
            sensorManager.unregisterListener(this, amb_tempSensor);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Date date = new Date();
        timestamp = sdfDate.format(date);
        location_cords[0] = location.getLatitude();
        location_cords[1] = location.getLongitude();
        if (pressed) {
            if (covid_confirm_btn.getVisibility() == View.INVISIBLE) {
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(covid_confirm_btn, "alpha", 0f, 1.0f).setDuration(1000);
                fadeAnim.start();
                covid_confirm_btn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_CODE);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_IN_METERS, MainActivity.this);
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if(covid_confirm_btn.getVisibility() == View.VISIBLE) {
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(covid_confirm_btn, "alpha", 1.0f, 0f).setDuration(1000);
            fadeAnim.start();
            covid_confirm_btn.setVisibility(View.INVISIBLE);
        }
        locationManager.removeUpdates(this);
    }

    //Simple method showing an alert dialog box with two buttons: one for Text-To-Speech functionality and one to close the AlertDialog
    public void showHelp(View view){
        builder2.setMessage(R.string.covid_case_show_help_msg)
                .setTitle(R.string.info)
                .setNegativeButton("OK", (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.tts, (dialog, which) -> tts = new TextToSpeech(getApplicationContext(), status -> {
                    if(status==TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.ENGLISH);
                        tts.setSpeechRate(1.0f);
                        tts.speak(getResources().getString(R.string.info), TextToSpeech.QUEUE_ADD, null);
                        tts.speak(getResources().getString(R.string.covid_case_show_help_msg), TextToSpeech.QUEUE_ADD, null);
                    }
                }));
        AlertDialog dialog = builder2.create();
        dialog.show();
    }
}