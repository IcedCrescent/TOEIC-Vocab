package com.example.trungspc.toiecvocab.activities;

import android.content.Context;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;

public class ReviewActivity extends AppCompatActivity {

    PowerManager.WakeLock fullWakeLock;
    PowerManager.WakeLock partialWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toast.makeText(this, "Review activity on create", Toast.LENGTH_LONG).show();
    }

}
