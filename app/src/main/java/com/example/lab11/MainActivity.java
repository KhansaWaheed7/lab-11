package com.example.lab11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWelcome, textViewUserData;
    private Button buttonEditProfile, buttonLogout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        initializeFirebase();

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Check if user is logged in
        checkUserAuthentication();
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUser = firebaseAuth.getCurrentUser();
    }

    private void initializeViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewUserData = findViewById(R.id.textViewUserData);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonLogout = findViewById(R.id.buttonLogout);
    }

    private void setupClickListeners() {
        // Edit Profile Button
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout Button
        buttonLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            checkUserAuthentication();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUserAuthentication() {
        if (currentUser != null) {
            // User is signed in
            showUserData();
            textViewWelcome.setText("Welcome, " + currentUser.getEmail());
        } else {
            // No user is signed in, redirect to Login Activity
            redirectToLogin();
        }
    }

    private void showUserData() {
        if (currentUser != null) {
            DatabaseReference userRef = databaseReference.child(currentUser.getUid());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get user data from Firebase
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        // Display user data
                        String userInfo = "Name: " + (name != null ? name : "Not set") + "\n" +
                                "Email: " + (email != null ? email : "Not set") + "\n" +
                                "Phone: " + (phone != null ? phone : "Not set");

                        textViewUserData.setText(userInfo);
                    } else {
                        textViewUserData.setText("No profile data found. Please edit your profile.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in when activity starts
        currentUser = firebaseAuth.getCurrentUser();
        checkUserAuthentication();
    }
}