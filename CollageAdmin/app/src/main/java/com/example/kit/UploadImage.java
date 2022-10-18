package com.example.kit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    private Bitmap bitmap; // In This variable the image is stored which we are going to upload
    private ProgressDialog pd; // ProgressDialog while uploading

    private DatabaseReference reference;
    private StorageReference  storageReference;
    String downloadUrl= "";

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

        pd = new ProgressDialog(this);// Initialized progress dialog

        reference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference =  FirebaseStorage.getInstance().getReference().child("Gallery");

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

// Here we are invoking the functionality of ImageUpload button by setting OnClickListener

       uploadImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               // Here we are checking if image is selected or not(Image is stored in bitmap variable)

               if(bitmap == null){
                   Toast.makeText(UploadImage.this,"Upload Image",Toast.LENGTH_LONG).show();

                // Checking whether user selected the category or not

               }else if(imageCategory.equals("Select Category")){

                   Toast.makeText(UploadImage.this,"Invalid Category",Toast.LENGTH_LONG).show();
               }
               else{
                // user will get the message while uploading the image in progress dialog
                   pd.setMessage("Uploading....");
                   pd.show();
                   // A method to upload image to the firebase
                   uploadImageGallery();

               }
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

    private void uploadImageGallery() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //Here we are compressing the images
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;

        filePath = storageReference .child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadUrl=String.valueOf(uri); // We are storing the url of the image
                                    uploadDataImage(); // Method to upload image


                                }
                            });
                        }
                    });

                }
                else{
                    pd.dismiss();// when error occur while uploading the this will run
                    Toast.makeText(UploadImage.this,"Something Went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void uploadDataImage() {

            reference = reference.child(category); // The selected category folder will created(once) and image
                                                    // uploaded to the same

//      push() function A DatabaseReference pointing to the new location

            final String uniqueKEY = reference.push().getKey();

            reference.child(uniqueKEY).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this,"Hurray Successfully uploaded",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(UploadImage.this,"Something Went wrong",Toast.LENGTH_LONG).show();

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