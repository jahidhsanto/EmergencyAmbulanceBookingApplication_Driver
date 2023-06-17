package com.example.emergencyambulancebookingapplication_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity_2 extends AppCompatActivity {

    private EditText mCompanyName, mDriverName, mFatherName, mDob, mNIDNumber;
    private Button mNextBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        mCompanyName = findViewById(R.id.companyNameId);
        mDriverName = findViewById(R.id.driverNameId);
        mFatherName = findViewById(R.id.fatherNameId);
        mDob = findViewById(R.id.dobId);
        mNIDNumber = findViewById(R.id.nidNumberId);
        mNextBtn = findViewById(R.id.nextBtnId_2);
        mLoginBtn = findViewById(R.id.signinTxtId);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        // Initialize Cloud Firestore
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBarId);

        progressBar = findViewById(R.id.progressBarId);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyName = mCompanyName.getText().toString().trim();
                String driverName = mDriverName.getText().toString().trim();
                String fatherName = mFatherName.getText().toString().trim();
                String dob = mDob.getText().toString().trim();
                String nidNumber = mNIDNumber.getText().toString().trim();

                if (TextUtils.isEmpty(companyName)) {
                    mCompanyName.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(driverName)) {
                    mDriverName.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(fatherName)) {
                    mFatherName.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    mDob.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(nidNumber)) {
                    mNIDNumber.setError("This field is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Store in Firebase database
                userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                CollectionReference collectionReference = fStore.collection("drivers").document(userID).collection("profileInformation");
                // Create a new user with a first, middle, and last name
                Map<String, Object> user = new HashMap<>();

                user.put("companyName", companyName);
                user.put("fullName", driverName);
                user.put("fatherName", fatherName);
                user.put("dob", dob);
                user.put("nidNumber", nidNumber);

                // Add a new document with a generated ID
                collectionReference.document("profileInformation").set(user, SetOptions.merge()).addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), SignUpActivity_3.class));
                    Log.d("TAG", "onSuccess: user Profile is created for " + userID);

                }).addOnFailureListener(e -> {
                    // Error adding document
                    Log.e("TAG", "Error adding document", e);
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });

    }
}