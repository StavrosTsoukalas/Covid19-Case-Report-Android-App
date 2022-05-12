package com.example.covid_19app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataStatistics extends AppCompatActivity {
    String date_selected;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference mRef = db.getReference();

    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy");

    TextView cases_num_label, cases_num, gender_label, gender_male, gender_female, gender_male_num;
    TextView gender_female_num, ages_label, ages_kid, ages_adult, ages_elder, ages_kid_num, ages_adult_num, ages_elder_num;

    View divider1, divider2, panel;

    int covid_cases, covid_males, covid_females, covid_kids, covid_adults, covid_elders, males, females, kids, adults, elders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_statistics);

        date_selected = getIntent().getStringExtra("dateSelected");

        String curr_date = formatter.format(date);

        cases_num_label = findViewById(R.id.textView11);
        cases_num = findViewById(R.id.textView12);
        gender_label = findViewById(R.id.textView13);
        gender_male = findViewById(R.id.textView14);
        gender_female = findViewById(R.id.textView15);
        gender_male_num = findViewById(R.id.textView16);
        gender_female_num = findViewById(R.id.textView17);
        ages_label = findViewById(R.id.textView18);
        ages_kid = findViewById(R.id.textView19);
        ages_adult = findViewById(R.id.textView20);
        ages_elder = findViewById(R.id.textView21);
        ages_kid_num = findViewById(R.id.textView22);
        ages_adult_num = findViewById(R.id.textView23);
        ages_elder_num = findViewById(R.id.textView24);
        divider1 = findViewById(R.id.divider9);
        divider2 = findViewById(R.id.divider10);
        panel = findViewById(R.id.view);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.child("CovidCases").getChildren()){
                    String case_key = postSnap.getKey();
                    for (DataSnapshot postSnapCases : postSnap.getChildren()){
                        CovidCase covidCase = postSnapCases.getValue(CovidCase.class);
                        try {
                            String case_date = covidCase.getTimestamp().split("\\s+")[0];
                            if (case_date.equals(date_selected)) {
                                covid_cases += 1;
                                for (DataSnapshot snapshot1 : snapshot.child("Users").getChildren()){
                                    if (case_key.equals(snapshot1.getKey())){
                                        User user = snapshot1.getValue(User.class);

                                        //Checks the user's gender
                                        if (user.getGender().equals(getString(R.string.register_gender_male))){
                                            covid_males += 1;
                                        }
                                        else{
                                            covid_females += 1;
                                        }

                                        //Checks in what age group the user belongs
                                        if (Integer.parseInt(curr_date) - Integer.parseInt(user.getBirthdate().split("/")[2]) < 18){
                                            covid_kids += 1;
                                        }
                                        else if (Integer.parseInt(curr_date) - Integer.parseInt(user.getBirthdate().split("/")[2]) < 60){
                                            covid_adults += 1;
                                        }
                                        else{
                                            covid_elders += 1;
                                        }
                                    }
                                }
                            }
                        }
                        catch (Exception e){
                            showMessage(e.getLocalizedMessage());
                        }
                    }
                }

                float covid_cases_f = covid_cases;
                //Calculates the percentages and set them to their corresponding TextViews
                if (covid_cases != 0){
                    panel.setVisibility(View.INVISIBLE);
                    cases_num.setText(String.valueOf(covid_cases));
                    males = Math.round((covid_males/covid_cases_f)*100);
                    females = Math.round((covid_females/covid_cases_f)*100);
                    gender_male_num.setText(males + "%");
                    gender_female_num.setText(females + "%");
                    kids = Math.round((covid_kids/covid_cases_f)*100);
                    adults = Math.round((covid_adults/covid_cases_f)*100);
                    elders = Math.round((covid_elders/covid_cases_f)*100);
                    ages_kid_num.setText(kids + "%");
                    ages_adult_num.setText(adults + "%");
                    ages_elder_num.setText(elders + "%");
                }
                else{
                    cases_num_label.setText(getString(R.string.no_cases));
                    cases_num.setVisibility(View.INVISIBLE);
                    gender_label.setVisibility(View.INVISIBLE);
                    gender_male.setVisibility(View.INVISIBLE);
                    gender_female.setVisibility(View.INVISIBLE);
                    gender_male_num.setVisibility(View.INVISIBLE);
                    gender_female_num.setVisibility(View.INVISIBLE);
                    ages_label.setVisibility(View.INVISIBLE);
                    ages_kid.setVisibility(View.INVISIBLE);
                    ages_adult.setVisibility(View.INVISIBLE);
                    ages_elder.setVisibility(View.INVISIBLE);
                    ages_kid_num.setVisibility(View.INVISIBLE);
                    ages_adult_num.setVisibility(View.INVISIBLE);
                    ages_elder_num.setVisibility(View.INVISIBLE);
                    divider1.setVisibility(View.INVISIBLE);
                    divider2.setVisibility(View.INVISIBLE);
                    panel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}