package com.example.emergencyambulancebookingapplication_driver;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileSetting extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    StorageReference storageReference;

    private ImageView mProfileImage;
    private TextView mProfileName, mNumberOfRide, mNumberOfTrip, mGender, mDOBId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        // Navigation Bar ======================
        {
            Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
            setSupportActionBar(toolbar);

            getSupportActionBar().setTitle("Profile");

            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
        // Navigation Bar ======================

        fAuth = FirebaseAuth.getInstance();         // for Authentication
        fAuth = FirebaseAuth.getInstance();         // for Authentication
        fStore = FirebaseFirestore.getInstance();   // for store information
        storageReference = FirebaseStorage.getInstance().getReference();    // for store image

        mProfileImage = findViewById(R.id.profileImageId);
        mProfileName = findViewById(R.id.profileNameId);
        mNumberOfRide = findViewById(R.id.numberOfRideId);
        mNumberOfTrip = findViewById(R.id.numberOfTripId);
        mGender = findViewById(R.id.genderId);
        mDOBId = findViewById(R.id.dobId);

        userId = fAuth.getCurrentUser().getUid();


        // show user Profile Image
        showProfileImage();

        // Fetch Data From Firebase
        showOtherInformation();
    }

    private void showOtherInformation() {
        DocumentReference documentReference = fStore.collection("drivers").document(userId).collection("profileInformation").document("profileInformation");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                mProfileName.setText(documentSnapshot.getString("fullName"));
                mNumberOfRide.setText(documentSnapshot.getString("phone"));
            }
        });
    }

    // show user Profile Image
    private void showProfileImage() {
        StorageReference profileRef = storageReference.child("drivers/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfileImage);
            }
        });

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
        }
    }
}
