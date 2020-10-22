package com.abhinavsingh153.officemanagement.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abhinavsingh153.officemanagement.R;
import com.abhinavsingh153.officemanagement.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.abhinavsingh153.officemanagement.util.Util.TASK_NOTE;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Data> taskList = new ArrayList<>();

    //update input field
    private EditText titleUp;
    private EditText noteUp;
    private Button deleteButton;
    private Button updateButton;

    //variables
    private String title;
    private String note;
    private String post_key;

    FirebaseRecyclerOptions<Data> options;
    FirebaseRecyclerAdapter<Data , TaskViewHolder> adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your task app");

        loadTasks();
    }

    private void loadTasks(){


         options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mDatabase, Data.class)
                .build();

         adapter = new FirebaseRecyclerAdapter<Data, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, final int position, @NonNull final Data data) {
                holder.mNote.setText(data.getNote());
                holder.mTitle.setText(data.getTitle());
                holder.mDate.setText(data.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        title = data.getTitle();
                        note = data.getNote();

                        showUpdateAlertDialog();

                    }
                });

            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data , parent , false);
                return new TaskViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(TASK_NOTE).child(userId);

        toolbar = findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.fab_btn);

        recyclerView = findViewById(R.id.recycerview);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
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


    private void showUpdateAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        final View customView = inflater.inflate(R.layout.customupdatefield , null);
        builder.setView(customView);
        final AlertDialog alertDialog = builder.create();

        titleUp = customView.findViewById(R.id.title_update_edttxt);
        noteUp = customView.findViewById(R.id.note_update_edttxt);
        updateButton = customView.findViewById(R.id.update_btn);
        deleteButton = customView.findViewById(R.id.delete_btn);

        titleUp.setText(title);
        titleUp.setSelection(title.length());

        noteUp.setText(note);
        noteUp.setSelection(note.length());


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = titleUp.getText().toString().trim();
                note = noteUp.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title , note , mDate , note);

                mDatabase.child(post_key).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        Toast.makeText(HomeActivity.this,getResources().getString(R.string.task_updated) , Toast.LENGTH_SHORT).show();

                        else
                            Toast.makeText(HomeActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });

                //saveTask(titleUp , noteUp, alertDialog);

                alertDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete task and update recyclerview
                mDatabase.child(post_key).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(HomeActivity.this, getResources().getString(R.string.task_removed), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(HomeActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.dismiss();
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

    @Override
    protected void onStart() {
        super.onStart();

        if (adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView mTitle;
        TextView mNote;
        TextView mDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.title_txt);
            mNote = itemView.findViewById(R.id.note_txt);
            mDate = itemView.findViewById(R.id.date_txt);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(this , LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}