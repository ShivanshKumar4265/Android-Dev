package com.example.kit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView uploadNotice; //
    CardView uploadGalleryImage; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice = findViewById(R.id.addNotice);
        uploadGalleryImage = findViewById(R.id.addImage);


        uploadNotice.setOnClickListener(this);
        uploadGalleryImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.addNotice:
                    Intent AddNotice = new Intent(MainActivity.this,activity_UploadNotice.class);
                    startActivity(AddNotice);
                    break;
                case R.id.addImage:
                    Intent  AddGallery= new Intent(MainActivity.this,UploadImage.class);
                    startActivity(AddGallery);
                    break;

            }
    }
}