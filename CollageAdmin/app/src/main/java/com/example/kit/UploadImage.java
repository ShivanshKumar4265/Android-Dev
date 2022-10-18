package com.example.kit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;

public class UploadImage extends AppCompatActivity {

//Here we Declared all the widgets for the activity with its variable name
    private CardView selectImage;
    private Spinner imageCategory;
    private Button uploadImage;
    private ImageView galleryImageView;
//------------------------------------------------------------
    private String category; // In this variable we will store the category which will be selected by  admin
    private final int REQ  = 1;
    private Bitmap bitmap;

    /*  A bitmap is an object , which is an instance of the Bitmap Class.
        This class represents a 2d coordinate system
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
// Here we found every element that we are going to use in the UploadImage activity

        selectImage = findViewById(R.id.addImage);
        imageCategory = findViewById(R.id.ImageCategory);
        uploadImage = findViewById(R.id.ButtonGalleryImage);
        galleryImageView = findViewById(R.id.GalleryImageView);



//Here we are creating string array for the different category. Variable as categoryList

        String[] categoryList = {"Select Category", "E-Cell","SDC","T&P","Other Clubs"};

/*
    In Android, Adapter is a bridge between UI component and data source that helps us to fill data in UI component.
    It holds the data and send the data to an Adapter view then view can takes
    the data from the adapter view and shows the data on different views like as ListView, GridView, Spinner etc.

    --> Here we are setting the adapter to the SPINNER so, we can add the values of the string arrays
        in the spinner(As the Adapter class definition see)
 */

        imageCategory.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,categoryList)
        );




// Here we are setting "setOnItemSelectedListener" on  spinner element
       imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               category = imageCategory.getSelectedItem().toString();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });



/*
    Here we are adding functionality to the the card view to open the gallery to select the images
    for uploading purpose
 */
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openGallery(); method will invoke the gallery and its body in on line 86
                openGallery();
            }
        });

    }


    private void openGallery() {
        Intent pickImage = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(pickImage, REQ);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ&& resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

           galleryImageView.setImageBitmap(bitmap);

        }
    }

}