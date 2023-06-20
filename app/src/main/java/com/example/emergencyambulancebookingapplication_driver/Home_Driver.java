package com.example.emergencyambulancebookingapplication_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Home_Driver extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView profileName, ambulanceCategory, ambulanceNumber, hospitalName;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private Switch toggleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);

        profileName = findViewById(R.id.senderProfileNameId);
        ambulanceCategory = findViewById(R.id.ambulanceCategoryId);
        ambulanceNumber = findViewById(R.id.ambulanceNumberId);
        hospitalName = findViewById(R.id.hospitalNameId);

        fAuth = FirebaseAuth.getInstance();         // for Authentication
        fStore = FirebaseFirestore.getInstance();   // for store information

        userId = fAuth.getCurrentUser().getUid();


        // Fetch Data From Firebase
        DocumentReference documentReference = fStore.collection("drivers").document(userId).collection("profileInformation").document("profileInformation");
        documentReference.addSnapshotListener(Home_Driver.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                profileName.setText(documentSnapshot.getString("fullName"));
                ambulanceCategory.setText(documentSnapshot.getString("ambulanceCategory"));
                ambulanceNumber.setText("KA - 051624");
                hospitalName.setText(documentSnapshot.getString("companyName"));
            }
        });


        toggleSwitch = findViewById(R.id.toggleSwitchId);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ToggleSwitch is turned ON Store in Firebase database
                    CollectionReference collectionReference = fStore.collection("drivers");
                    Map<String, Object> user = new HashMap<>();

                    user.put("status", "ONLINE");

                    // Add a new document with a generated ID
                    collectionReference.document(userId).set(user, SetOptions.merge()).addOnSuccessListener(documentReference -> {
                        // Document added successfully
                        Log.d("TAG", "onSuccess: user Profile is ONLINE for " + userId);
                    }).addOnFailureListener(e -> {
                        // Error adding document
                        Log.e("TAG", "Error adding document", e);
                    });
                } else {
                    // ToggleSwitch is turned OFF Store in Firebase database
                    CollectionReference collectionReference = fStore.collection("drivers");
                    Map<String, Object> user = new HashMap<>();

                    user.put("status", "OFFLINE");

                    // Add a new document with a generated ID
                    collectionReference.document(userId).set(user, SetOptions.merge()).addOnSuccessListener(documentReference -> {
                        // Document added successfully
                        Log.d("TAG", "onSuccess: user Profile is ONLINE for " + userId);
                    }).addOnFailureListener(e -> {
                        // Error adding document
                        Log.e("TAG", "Error adding document", e);
                    });
                }
            }
        });

        // Navigation Bar ======================

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation Bar ======================


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_homeId) {
            startActivity(new Intent(this, Home_Driver.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (item.getItemId() == R.id.nav_profileId) {
            startActivity(new Intent(this, ProfileSetting.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (item.getItemId() == R.id.nav_ridehistoryId) {
            startActivity(new Intent(this, RideHistory.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (item.getItemId() == R.id.nav_logoutId) {
            fAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            // ToggleSwitch is turned OFF Store in Firebase database
            CollectionReference collectionReference = fStore.collection("drivers");
            Map<String, Object> user = new HashMap<>();

            user.put("status", "OFFLINE");

            // Add a new document with a generated ID
            collectionReference.document(userId).set(user, SetOptions.merge()).addOnSuccessListener(documentReference -> {
                // Document added successfully
                Log.d("TAG", "onSuccess: user Profile is ONLINE for " + userId);
            }).addOnFailureListener(e -> {
                // Error adding document
                Log.e("TAG", "Error adding document", e);
            });
        }
    }
}