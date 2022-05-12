package com.example.covid_19app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    FirebaseAuth mAuth;
    String amka;

    AlertDialog.Builder builder;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();

        amka = getIntent().getStringExtra("amka");

        builder = new AlertDialog.Builder(this);
    }

    public void sendToCovidMap(View view){
        Intent intent = new Intent(MainActivity2.this, CovidCasesMap.class);
        startActivity(intent);
    }

    public void sendToDeleteUserMenu(View view){
        Intent intent = new Intent(MainActivity2.this, DeleteUserMenu.class);
        startActivity(intent);
    }

    public void sendToStatistics(View view){
        Intent intent = new Intent(MainActivity2.this, Statistics.class);
        startActivity(intent);
    }

    public void editProfile(View view){
        Intent intent = new Intent(MainActivity2.this, EditProfile.class);
        intent.putExtra("amka", amka);
        intent.putExtra("employee", true);
        startActivity(intent);
    }

    //Simple method showing an alert dialog box with two buttons: one for Text-To-Speech functionality and one to close the AlertDialog
    public void showHelp(View view){
        builder.setMessage(R.string.worker_help_msg)
                .setTitle(R.string.info)
                .setNegativeButton("OK", (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.tts, (dialog, which) -> tts = new TextToSpeech(getApplicationContext(), status -> {
                    if(status==TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.ENGLISH);
                        tts.setSpeechRate(1.0f);
                        tts.speak(getResources().getString(R.string.info), TextToSpeech.QUEUE_ADD, null);
                        tts.speak(getResources().getString(R.string.worker_help_msg), TextToSpeech.QUEUE_ADD, null);
                    }
                }));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void signout(View view){
        mAuth.signOut();
        finish();
    }

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        finish();
    }
}