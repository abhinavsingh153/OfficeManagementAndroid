 package com.abhinavsingh153.officemanagement.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinavsingh153.officemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

 public class LoginActivity extends AppCompatActivity {

     private TextView signUp;

     private EditText email;
     private EditText password;
     private Button loginButton;
     private ProgressDialog progressDialog;

     private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        signUp = findViewById(R.id.signup_txt);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , RegistrationActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Field required...");
                    return;
                }
                if (TextUtils.isEmpty(mPass)){
                    password.setError("Field required...");
                    return;
                }

                progressDialog.setMessage("Processing...");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail , mPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext() , HomeActivity.class));
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });
    }

     private void init() {

         email = findViewById(R.id.email_login);
         password = findViewById(R.id.password_login);

         loginButton = findViewById(R.id.login_button);
         progressDialog = new ProgressDialog(this);

         mAuth = FirebaseAuth.getInstance();
     }
 }