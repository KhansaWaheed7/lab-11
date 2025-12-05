package com.example.lab11;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone;
    private Button buttonSave;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        initializeFirebase();

        initializeViews();
        setupClickListener();
    }

    private void initializeFirebase() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSave = findViewById(R.id.buttonSave);
    }

    private void setupClickListener() {
        buttonSave.setOnClickListener(v -> saveProfileData());
    }

    private void saveProfileData() {
        // Get input values
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to data map
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("phone", phone);
        data.put("updatedAt", System.currentTimeMillis());

        // Get reference to current user's data
        DatabaseReference userReference = databaseReference.child(currentUser.getUid());

        // Update data in Firebase
        userReference.updateChildren(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to previous activity
                    } else {
                        Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
