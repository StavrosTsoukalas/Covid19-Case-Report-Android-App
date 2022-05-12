package com.example.covid_19app;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.covid_19app.databinding.ActivityCovidCasesMapBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CovidCasesMap extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<CovidCase> covidCases_list = new ArrayList<>();

    private GoogleMap mMap;
    private ActivityCovidCasesMapBinding binding;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("CovidCases");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                covidCases_list.clear();
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    for (DataSnapshot postSnap2 : postSnap.getChildren()) {
                        CovidCase covidCase = postSnap2.getValue(CovidCase.class);
                        covidCases_list.add(covidCase);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage(error.getMessage());
            }
        });

        binding = ActivityCovidCasesMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Here the Markers of the covid cases recorded on the DB are being initialized and placed on the map
        try {
            LatLng coords;

            //Iterate through the ArrayList for each event
            for (CovidCase covidCase : covidCases_list) {
                coords = new LatLng(covidCase.getLatitude(), covidCase.getLongitude());
                float pinColor = BitmapDescriptorFactory.HUE_ROSE;
                mMap.addMarker(new MarkerOptions()
                        .position(coords)
                        .icon(BitmapDescriptorFactory.defaultMarker(pinColor))
                        .title(getString(R.string.case_timestamp) + " " + covidCase.getTimestamp())
                        .snippet(getString(R.string.case_humidity) + " " + covidCase.getHumidity() + ", " + getString(R.string.case_amb_temperature) + " " + covidCase.getAmbient_temperature()));
            }

            //Finally, zoom on the first covidCase Marker
            coords = new LatLng(covidCases_list.get(0).getLatitude(), covidCases_list.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 7.0f));

        }catch (Exception e){
            showMessage(getString(R.string.no_cases));
        }
    }

    //Simple method showing a message through Toast
    void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}