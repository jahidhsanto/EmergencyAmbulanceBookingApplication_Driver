package com.example.emergencyambulancebookingapplication_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Driver_Accept extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    public static String userID, senderUserID;
    private String driverProfileName, driverContactNumber, hospitalName;
    private StorageReference storageReference;
    private LatLng startLocation, endLocation;
    double latitude, longitude;
    TextView estimatedDistance, amountOfFare, pickAddr, destAddr, senderProfileName;
    ImageView senderProfileImage;
    Button acceptbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accept);

        estimatedDistance = findViewById(R.id.estimatedDistanceId);
        amountOfFare = findViewById(R.id.amountOfFareId);
        pickAddr = findViewById(R.id.pickAddrId);
        destAddr = findViewById(R.id.destAddrId);
        senderProfileName = findViewById(R.id.senderProfileNameId);
        senderProfileImage = findViewById(R.id.senderProfileImageId);
        acceptbtn = findViewById(R.id.acceptbtnId);
        acceptbtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();         // for Authentication
        fStore = FirebaseFirestore.getInstance();   // for access Database
        storageReference = FirebaseStorage.getInstance().getReference();    // for image

        userID = fAuth.getCurrentUser().getUid();

        fetchInformation();


    }

    private void fetchInformation() {
        // Fetch Request information From Firebase
        DocumentReference documentOfSender = fStore.collection("users").document(senderUserID).collection("tempRideInformation").document("tempRideInformation");
        documentOfSender.addSnapshotListener(Driver_Accept.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                estimatedDistance.setText(String.valueOf(documentSnapshot.getLong("distance")));
                amountOfFare.setText(String.valueOf(documentSnapshot.getLong("fare")));

//                // Fetch the 'pickUpLatLng' field
//                Map<String, Object> pickUpLatLng = (Map<String, Object>) documentSnapshot.get("dropOffLatLng");
//                // Fetch the 'latitude' and 'longitude' subfields
//                latitude = (double) pickUpLatLng.get("latitude");
//                longitude = (double) pickUpLatLng.get("longitude");
//                startLocation = new LatLng(latitude, longitude);
//                pickAddr.setText(getLocationName(startLocation));
//
//                // Fetch the 'dropOffLatLng' field
//                Map<String, Object> dropOffLatLng = (Map<String, Object>) documentSnapshot.get("dropOffLatLng");
//                // Fetch the 'latitude' and 'longitude' subfields
//                latitude = (double) dropOffLatLng.get("latitude");
//                longitude = (double) dropOffLatLng.get("longitude");
//                endLocation = new LatLng(latitude, longitude);
//                destAddr.setText(getLocationName(endLocation));


                // fetch the sender profile name
                DocumentReference parentDocRef = documentOfSender.getParent().getParent().collection("profileInformation").document("profileInformation");
                parentDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot parentDocument = task.getResult();
                        senderProfileName.setText(parentDocument.getString("fName"));
                    }
                });
            }
        });

        // show user Profile Image
        StorageReference profileRef = storageReference.child("users/" + senderUserID + "/profile.jpg");
        profileRef.getDownloadUrl().
                addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(senderProfileImage);
                    }
                });

        // fetch the own profile
        DocumentReference documentOfDriver = fStore.collection("drivers").document(userID).collection("profileInformation").document("profileInformation");
        documentOfDriver.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot parentDocument = task.getResult();
                driverProfileName = parentDocument.getString("fullName");
                driverContactNumber = parentDocument.getString("phone");
                hospitalName = parentDocument.getString("companyName");
            }
        });


    }

    private String getLocationName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(Driver_Accept.this);

        try {
            // Get the addresses for the given latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String locationName = address.getAddressLine(0); // Get the location name

                return locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.acceptbtnId) {
            DocumentReference bookingRef = fStore.collection("drivers").document(userID);

            // Update the driver status to "BOOKED"
            bookingRef.update("status", "BOOKED")
                    .addOnSuccessListener(result -> {
                        Toast.makeText(this, "Booking confirmed successfully", Toast.LENGTH_SHORT).show();
                        sendToUser();
                    })
                    .addOnFailureListener(error -> {
                        Toast.makeText(this, "Failed to confirm booking: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void sendToUser() {
        // Create a new document reference for additional data
        DocumentReference additionalDataRef = fStore.collection("users").document(senderUserID).collection("tempRideInformation").document("tempRideInformation");

        // Create a map with the additional data
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("driverProfileName", driverProfileName);
        additionalData.put("driverContactNumber", driverContactNumber);
        additionalData.put("ambulanceNumber", "ambulanceNumber");
//        additionalData.put("hospitalName", hospitalName);

        // Store the additional data in the new document reference
        additionalDataRef.set(additionalData, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Additional data stored successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Failed to store additional data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}