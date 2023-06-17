package com.example.emergencyambulancebookingapplication_driver;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity_1 extends AppCompatActivity {

    private EditText mEmail, mPhone, mPassword01, mPassword02;
    private Button mNextBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        mEmail = findViewById(R.id.emailId);
        mPhone = findViewById(R.id.phoneId);
        mPassword01 = findViewById(R.id.pass01Id);
        mPassword02 = findViewById(R.id.pass02Id);
        mNextBtn = findViewById(R.id.nextBtnId_1);
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
                String email = mEmail.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String password01 = mPassword01.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password01)) {
                    mPassword01.setError("Password is Required.");
                    return;
                }
                if (password01.length() < 6) {
                    mPassword01.setError("Password Must be >= 6 Characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password01).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity_1.this, "User Created", Toast.LENGTH_SHORT).show();


                            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            CollectionReference collectionReference = fStore.collection("drivers")
                                    .document(userID)
                                    .collection("profileInformation");
                            // Create a new user with a first, middle, and last name
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("phone", phone);

                            // Add a new document with a generated ID
                            collectionReference.document("profileInformation").set(user).addOnSuccessListener(documentReference -> {
                                // Document added successfully
                                startActivity(new Intent(getApplicationContext(), SignUpActivity_2.class));
                                Log.d("TAG", "onSuccess: user Profile is created for " + userID);

                            }).addOnFailureListener(e -> {
                                // Error adding document
                                Log.e("TAG", "Error adding document", e);
                            });
                        } else {
                            Toast.makeText(SignUpActivity_1.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
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