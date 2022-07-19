package com.example.calculator;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    EditText first, second;
    TextView output;
    Button add, mul, sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        first = findViewById(R.id.FirstNumber);
        second = findViewById(R.id.SecondNumber);
        output = findViewById(R.id.Output);
        add = findViewById(R.id.Add);
        mul = findViewById(R.id.Multiply);
        sub = findViewById(R.id.Subtraction);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Adding...", Toast.LENGTH_SHORT).show();
                int num1,num2,ans;
                num1 = Integer.parseInt(first.getText().toString());
                num2 = Integer.parseInt(second.getText().toString());
                ans = num1 + num2;
                output.setText("Your sum is " + ans );
            }
        });

        mul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Multiplying...", Toast.LENGTH_SHORT).show();
                int num1,num2,ans;
                num1 = Integer.parseInt(first.getText().toString());
                num2 = Integer.parseInt(second.getText().toString());
                ans = num1 * num2;
                output.setText("Your multiplication is " + ans );
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Subtractin...", Toast.LENGTH_SHORT).show();
                int num1,num2,ans;
                num1 = Integer.parseInt(first.getText().toString());
                num2 = Integer.parseInt(second.getText().toString());
                ans = num1 - num2;
                output.setText("Your Subtraction is " + ans );
            }
        });
    }


}