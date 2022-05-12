package com.example.covid_19app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    EditText email,password, confirm_pass;
    ImageView covid_img;

    private boolean worker = false;

    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    private ArrayList<User> user_list = new ArrayList<>();
    private ArrayList<String> key_list = new ArrayList<>();

    private String wanted_amka;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        confirm_pass = findViewById(R.id.editTextTextPassword2);
        covid_img = findViewById(R.id.imageView);

        ref = database.getReference("Users");

        //Image Animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(covid_img, "scaleX", 1.0f, 1.4f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(covid_img, "scaleY", 1.0f, 1.4f);

        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);

        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet scaleAnim = new AnimatorSet();
        scaleAnim.setDuration(1000);
        scaleAnim.play(scaleX).with(scaleY);

        scaleAnim.start();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_list.clear();
                key_list.clear();
                for (DataSnapshot postSnap : snapshot.getChildren()){
                    User user = postSnap.getValue(User.class);
                    user_list.add(user);
                    key_list.add(postSnap.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });


    }

    //Sign-In method for existing Firebase users
    public void signin(View view) {
        try {
            if(password.getText().toString().trim().equals(confirm_pass.getText().toString().trim())) {
                mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Checks if the found user is a citizen or an employee and gets his/her AMKA as an intent extra for later use
                                worker = false;
                                wanted_amka = null;
                                for (User user : user_list){
                                    if (user.getEmail().equals(email.getText().toString().trim())){
                                        worker = user.isWorker();
                                        wanted_amka = key_list.get(user_list.indexOf(user));
                                        break;
                                    }
                                }
                                if(wanted_amka != null) {
                                    Intent intent;
                                    if (!worker) {
                                        intent = new Intent(Login.this, MainActivity.class);
                                    } else {
                                        intent = new Intent(Login.this, MainActivity2.class);
                                    }
                                    intent.putExtra("amka", wanted_amka); // AMKA will be needed for the covid case insertion to the DB
                                    startActivity(intent);
                                }
                                else{
                                    showMessage(getString(R.string.del_user_not_found));
                                }
                            } else {
                                showMessage(task.getException().getLocalizedMessage());
                            }
                        });
            }
            else{
                showMessage(getString(R.string.login_false_cred));
                }
        } catch (Exception e) {
            showMessage(getString(R.string.login_false_cred));
        }
    }

    //Change language
    public void changeLanguage(View view){
        final String[] listItems={getString(R.string.greek), getString(R.string.english)};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(Login.this);
        mBuilder.setTitle(getString(R.string.lang_title));
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if (i==0){
                LanguageHandler.setLocale(this, true);
            }
            if (i==1){
                LanguageHandler.setLocale(this, false);
            }
            dialogInterface.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Sign-Up method for new Firebase users
    public void signup(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}