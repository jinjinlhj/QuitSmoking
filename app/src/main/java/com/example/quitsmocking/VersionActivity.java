package com.example.quitsmocking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VersionActivity  extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("버전정보");
        setContentView(R.layout.version);
    }
}
