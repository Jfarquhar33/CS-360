package com.example.farquharcs360project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this); // Initialize the database

        EditText editTextUsername = findViewById(R.id.userText);
        EditText editTextPassword = findViewById(R.id.passwordText);
        Button buttonLogin = findViewById(R.id.buttonLogIn);
        Button buttonRegister = findViewById(R.id.buttonCreate);

        buttonLogin.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (dbHelper.checkUser(username, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegister.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (dbHelper.addUser(username, password)) {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
