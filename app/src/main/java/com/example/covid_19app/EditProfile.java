package com.example.covid_19app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    EditText name, surname, birthdate, address, city, email, amka;
    RadioButton male, female;
    RadioGroup rad_group;
    String amka_num, gender;
    boolean employee;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.editTextTextPersonName);
        surname = findViewById(R.id.editTextTextPersonName2);
        birthdate = findViewById(R.id.editTextDate);
        address = findViewById(R.id.editTextTextPersonName5);
        city = findViewById(R.id.editTextTextPersonName6);
        email = findViewById(R.id.editTextTextEmailAddress2);
        amka = findViewById(R.id.editTextNumber2);
        male = findViewById(R.id.radioButton);
        female = findViewById(R.id.radioButton2);
        rad_group = findViewById(R.id.radioGroup5);

        amka_num = getIntent().getStringExtra("amka");
        employee = getIntent().getBooleanExtra("employee", false);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()){
                    User user = postSnap.getValue(User.class);
                    if (postSnap.getKey().equals(amka_num)){
                        name.setText(user.getName());
                        surname.setText(user.getSurname());
                        birthdate.setText(user.getBirthdate());
                        address.setText(user.getAddress());
                        city.setText(user.getCity());
                        email.setText(user.getEmail());
                        amka.setText(amka_num);
                        if(user.getGender().equals(getString(R.string.register_gender_male))){
                            male.setChecked(true);
                            female.setChecked(false);
                            gender = getString(R.string.register_gender_male);
                        }
                        else{
                            male.setChecked(false);
                            female.setChecked(true);
                            gender = getString(R.string.register_gender_female);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });
    }

    public void editProfile(View view){
        try{
            if (!isEmpty(name) && !isEmpty(surname) && !isEmpty(address) && !isEmpty(city)) {
                User user = new User(name.getText().toString().trim(), surname.getText().toString().trim(), gender, birthdate.getText().toString().trim(), address.getText().toString().trim(), city.getText().toString().trim(), email.getText().toString().trim(), employee);
                mRef.child(amka_num).setValue(user);
                showMessage(getString(R.string.edit_profile_success));
                finish();
            }
        }
        catch (Exception e){
            showMessage(getString(R.string.error));
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