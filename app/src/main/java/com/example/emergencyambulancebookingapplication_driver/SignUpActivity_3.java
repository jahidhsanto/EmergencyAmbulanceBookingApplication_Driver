package com.example.emergencyambulancebookingapplication_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity_3 extends AppCompatActivity {

    private EditText mPresentAddr, mCompanyAddr, mNIDFront, mNIDBack, mProfilePhoto;
    private Button mNextBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;
    private String userID, mambulanceCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);

        mPresentAddr = findViewById(R.id.presentAddrId);
        mCompanyAddr = findViewById(R.id.companyAddrId);
        mNIDFront = findViewById(R.id.nidFrontId);
        mNIDBack = findViewById(R.id.nidBackId);
        mNextBtn = findViewById(R.id.nextBtnId_3);
        mLoginBtn = findViewById(R.id.signinTxtId);

        // DropDown Button
        {
            Spinner dropdownButton = findViewById(R.id.dropdownButton);

            // Create an array of options
            String[] options = {"Basic Life Support", "Patient Transport Service", "Advanced Life Support"};

            // Create an ArrayAdapter using the options array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);

            // Apply the adapter to the spinner
            dropdownButton.setAdapter(adapter);

            dropdownButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Handle the selected option
                    mambulanceCategory = options[position];
                    Toast.makeText(SignUpActivity_3.this, "Selected: " + mambulanceCategory, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle the case when nothing is selected
                }
            });

        }

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        // Initialize Cloud Firestore
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBarId);

        progressBar = findViewById(R.id.progressBarId);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String presentAddr = mPresentAddr.getText().toString().trim();
                String companyAddr = mCompanyAddr.getText().toString().trim();
                String nidFront = mNIDFront.getText().toString().trim();
                String nidBack = mNIDBack.getText().toString().trim();
                String ambulanceCategory = mambulanceCategory;

                if (TextUtils.isEmpty(presentAddr)) {
                    mPresentAddr.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(companyAddr)) {
                    mCompanyAddr.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(nidFront)) {
                    mNIDFront.setError("This field is Required.");
                    return;
                }
                if (TextUtils.isEmpty(nidBack)) {
                    mNIDBack.setError("This field is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Store in Firebase database
                userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                CollectionReference collectionReference = fStore.collection("drivers").document(userID).collection("profileInformation");
                // Create a new user with a first, middle, and last name
                Map<String, Object> user = new HashMap<>();

                user.put("presentAddr", presentAddr);
                user.put("companyAddr", companyAddr);
                user.put("nidFront", nidFront);
                user.put("nidBack", nidBack);
                user.put("ambulanceCategory", ambulanceCategory);

                // Add a new document with a generated ID
                collectionReference.document("profileInformation").set(user, SetOptions.merge()).addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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