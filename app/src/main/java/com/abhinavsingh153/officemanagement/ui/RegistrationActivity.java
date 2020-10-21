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
import android.widget.TextView;
import android.widget.Toast;

import com.abhinavsingh153.officemanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

  private EditText email;
  private EditText password;
  private Button btn_reg;
  private  TextView login_txt;
  private ProgressDialog progressDialog;

  private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this , LoginActivity.class));
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
            }
        });

    }

    private void registerUser() {

        String mEmail = email.getText().toString().trim();
        String mPass = password.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)){
            email.setError("Required field..");
            return;
        }
        if (TextUtils.isEmpty(mPass)){
            password.setError("Required field..");
            return;
        }

        //show progress dialog
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        //this will create user with given  email and password
        mAuth.createUserWithEmailAndPassword(mEmail , mPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext() ,HomeActivity.class ));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);

        btn_reg = findViewById(R.id.reg_button);
        login_txt = findViewById(R.id.login_txt);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
    }

}