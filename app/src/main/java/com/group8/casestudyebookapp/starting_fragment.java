package com.group8.casestudyebookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class starting_fragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_fragment);
    }

    //call main activity
    public void callMainActivity(View v) {
        //create intent
        //start activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}