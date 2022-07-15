package com.example.factsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);


        String[] myfacts = facts.getFacts();
        for(int i = 0; i < myfacts.length;i++){

            textView.append(myfacts[i]+ "\n\n");
        }
    }
}