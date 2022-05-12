package com.example.covid_19app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText name, surname, birthdate, address, city, email, amka, password, pass_conf;
    RadioButton male, female;
    RadioGroup rad_group;

    FirebaseAuth mAuth;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference("Users");

    boolean gender_male = false;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.editTextTextPersonName);
        surname = findViewById(R.id.editTextTextPersonName2);
        birthdate = findViewById(R.id.editTextDate);
        address = findViewById(R.id.editTextTextPersonName5);
        city = findViewById(R.id.editTextTextPersonName6);
        email = findViewById(R.id.editTextTextEmailAddress2);
        amka = findViewById(R.id.editTextNumber2);
        password = findViewById(R.id.editTextTextPassword3);
        pass_conf = findViewById(R.id.editTextTextPassword4);
        male = findViewById(R.id.radioButton);
        female = findViewById(R.id.radioButton2);
        rad_group = findViewById(R.id.radioGroup5);
    }

    //Sign-Up method for new Firebase users
    public void signup(View view) {
        try {
            // Checking if fields are not empty and some of them correct
            if (!isEmpty(name) && !isEmpty(surname) && !isEmpty(birthdate) && !isEmpty(address) && !isEmpty(city) && !isEmpty(email) && (!isEmpty(amka) && amka.getText().length() == 11) && !isEmpty(password) && !isEmpty(pass_conf) && !(rad_group.getCheckedRadioButtonId() == -1)) {
                if (password.getText().toString().trim().equals(pass_conf.getText().toString().trim())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (male.isChecked()) {
                                        gender_male = true;
                                        gender = getString(R.string.register_gender_male);
                                    } else if (female.isChecked()) {
                                        gender_male = false;
                                        gender = getString(R.string.register_gender_female);
                                    }
                                    User new_user = new User(name.getText().toString().trim(), surname.getText().toString().trim(), gender, birthdate.getText().toString().trim(), address.getText().toString().trim(), city.getText().toString().trim(), email.getText().toString().trim(), false);
                                    mRef.child(amka.getText().toString()).setValue(new_user);
                                    showMessage(getString(R.string.register_success));
                                    finish();
                                } else {
                                    showMessage(task.getException().getLocalizedMessage());
                                }
                            });
                } else {
                    showMessage(getString(R.string.register_false_conf_pass));
                }
            } else {
                showMessage(getString(R.string.register_not_complete));
            }
        } catch (Exception e) {
            showMessage(getString(R.string.register_not_complete));
            e.printStackTrace();
        }
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //Method to check if an EditText is empty
    private boolean isEmpty(EditText edittext) {
        return edittext.getText().toString().trim().length() == 0;
    }
}