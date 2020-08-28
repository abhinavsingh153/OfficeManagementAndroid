package com.abhinavsingh153.officemanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {

    CountryCodePicker ccp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ccp = findViewById(R.id.countyCodePicker);


    }

}