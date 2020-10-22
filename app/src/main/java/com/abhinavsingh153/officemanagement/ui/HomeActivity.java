package com.abhinavsingh153.officemanagement.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abhinavsingh153.officemanagement.R;
import com.abhinavsingh153.officemanagement.model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import static com.abhinavsingh153.officemanagement.util.Util.TASK_NOTE;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(TASK_NOTE).child(userId);

        toolbar = findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.fab_btn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your task app");
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        final View customView = inflater.inflate(R.layout.custominputfield , null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();

        final EditText title = customView.findViewById(R.id.title_edttxt);
        final EditText note = customView.findViewById(R.id.note_edttxt);
        Button saveButton = customView.findViewById(R.id.save_btn);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask(title , note, alertDialog);
            }
        });



        alertDialog.show();
    }

    private void saveTask(EditText title, EditText note, AlertDialog alertDialog) {

        String mTitle = title.getText().toString().trim();
        String mNote = note.getText().toString().trim();
        if (TextUtils.isEmpty(mTitle)){
            title.setError("Reqired Field...");
            return;
        }

        if (TextUtils.isEmpty(mNote)){
            note.setError("Reqired Field...");
            return;
        }

        // craete a random key
        String id = mDatabase.push().getKey();
        String date = DateFormat.getDateInstance().format(new Date());

        Log.d(TAG , id);
        Log.d(TAG , date);

        Data data = new Data(mTitle , mNote , date , id);

        mDatabase.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.task_inserted), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.dismiss();


    }
}