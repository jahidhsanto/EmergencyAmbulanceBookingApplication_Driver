package com.example.emergencyambulancebookingapplication_driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.util.HashMap;
import java.util.Map;

public class Driver_End extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    TextView destAddr, senderProfileName;
    ImageView senderProfileImage;
    Button endbtn;
    public static String userID, senderUserID = Driver_Accept.senderUserID;
    Dialog paymentDialog;

    String bookingId, paymentAmount;
    String userId = Driver_Accept.senderUserID;
    String driverId = userID;
    LatLng startLocation;
    LatLng endLocation;
    String time;
    String Date;
    String ambulanceCategory;
    String distance;
    String fare;
    String rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_end);

        destAddr = findViewById(R.id.destAddrId);
        senderProfileName = findViewById(R.id.senderProfileNameId);
        senderProfileImage = findViewById(R.id.senderProfileImageId);
        endbtn = findViewById(R.id.endbtnId);
        endbtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();         // for Authentication
        fStore = FirebaseFirestore.getInstance();   // for access Database
        storageReference = FirebaseStorage.getInstance().getReference();    // for image

        userID = fAuth.getCurrentUser().getUid();

        fetchInformation();

        paymentDialog = new Dialog(this);
    }

    private void fetchInformation() {
        // Fetch Request information From Firebase
        DocumentReference documentOfSender = fStore.collection("users").document(senderUserID).collection("tempRideInformation").document("tempRideInformation");
        documentOfSender.addSnapshotListener(Driver_End.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                // Fetch the 'dropOffLatLng' field
//                Map<String, Object> dropOffLatLng = (Map<String, Object>) documentSnapshot.get("dropOffLatLng");
//                // Fetch the 'latitude' and 'longitude' subfields
//                latitude = (double) dropOffLatLng.get("latitude");
//                longitude = (double) dropOffLatLng.get("longitude");
//                endLocation = new LatLng(latitude, longitude);
//                destAddr.setText(getLocationName(endLocation));

                bookingId = documentSnapshot.getString("bookingId");
                paymentAmount = String.valueOf(documentSnapshot.getLong("fare"));

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
    }


    @Override
    public void onClick(View view) {

        notifyUserOfEndRide();
    }

    private void notifyUserOfEndRide() {
        // Create a new document reference for additional data
        DocumentReference additionalDataRef = fStore.collection("users").document(senderUserID).collection("tempRideInformation").document("tempRideInformation");

        // Create a map with the additional data
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("status", "ENDED");

        // Store the additional data in the new document reference
        additionalDataRef.set(additionalData, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Be patience. Calculating Payment", Toast.LENGTH_SHORT).show();
                    paymentPopUp();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Failed to store additional data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void paymentPopUp() {
        paymentDialog.setContentView(R.layout.paymentpopup);
        paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentDialog.setCanceledOnTouchOutside(false);

        TextView paymentAmountDsp = paymentDialog.findViewById(R.id.paymentAmountDspId);
        paymentAmountDsp.setText(paymentAmount + " TK");

        paymentDialog.show();

        paymentDialog.findViewById(R.id.receivedbtnId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new document reference for additional data
                DocumentReference additionalDataRef = fStore.collection("users").document(senderUserID).collection("tempRideInformation").document("tempRideInformation");

                // Create a map with the additional data
                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("status", "PAID");

                // Store the additional data in the new document reference
                additionalDataRef.set(additionalData, SetOptions.merge())
                        .addOnSuccessListener(documentReference -> {
                            paymentDialog.dismiss(); // Dismiss the popup

                            Toast.makeText(Driver_End.this, "THANK YOU FOR SAVE OUR LIFE", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Driver_End.this, ProfileActivity.class));
                        })
                        .addOnFailureListener(error -> {
                            Toast.makeText(Driver_End.this, "Failed to store data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}