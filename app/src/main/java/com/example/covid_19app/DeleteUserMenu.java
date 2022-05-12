package com.example.covid_19app;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class DeleteUserMenu extends AppCompatActivity {
    AlertDialog.Builder builder;
    private TextToSpeech tts;

    TextView amka_field;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference();

    private FirebaseAuth mAuth;

    private ArrayList<String> amka_list = new ArrayList<>();

    private boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user_menu);

        builder = new AlertDialog.Builder(this);

        amka_field = findViewById(R.id.editTextNumber3);

        mAuth = FirebaseAuth.getInstance();

        mRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amka_list.clear();
                for (DataSnapshot postSnap : snapshot.getChildren()){
                    String amka_num = postSnap.getKey();
                    amka_list.add(amka_num);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });
    }

    public void deleteUser(View view){
        builder.setMessage(R.string.covid19_confirm_msg)
                .setTitle(R.string.covid19_confirm_title)
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.covid19_confirm_title, (dialog, which) -> {
                    String amka = amka_field.getText().toString().trim();
                    if(amka.length() == 11){
                        for (String tmp_amka : amka_list){
                            if (amka.equals(tmp_amka)){
                                found = true;
                                try {
                                    mRef.child("Users").child(tmp_amka).removeValue();
                                    mRef.child("CovidCases").child(tmp_amka).removeValue();
                                    showMessage(getString(R.string.del_user_success));
                                    break;
                                }
                                catch (Exception e){
                                    showMessage(e.getLocalizedMessage());
                                }
                            }
                            found = false;
                        }
                        if (!found){
                            showMessage(getString(R.string.del_user_not_found));
                        }
                    }
                    else{
                        showMessage(getString(R.string.del_user_amka_empty));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Simple method showing an alert dialog box with two buttons: one for Text-To-Speech functionality and one to close the AlertDialog
    public void showHelp(View view){
        builder.setMessage(R.string.delete_user_help_msg)
                .setTitle(R.string.info)
                .setNegativeButton("OK", (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.tts, (dialog, which) -> tts = new TextToSpeech(getApplicationContext(), status -> {
                    if(status==TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.ENGLISH);
                        tts.setSpeechRate(1.0f);
                        tts.speak(getResources().getString(R.string.info), TextToSpeech.QUEUE_ADD, null);
                        tts.speak(getResources().getString(R.string.delete_user_help_msg), TextToSpeech.QUEUE_ADD, null);
                    }
                }));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}