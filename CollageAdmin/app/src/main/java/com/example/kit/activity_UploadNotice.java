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
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class activity_UploadNotice extends AppCompatActivity {
    private CardView uploadImage;
    private final int REQ  = 1;
    private Bitmap bitmap;
    private ImageView previewUpload;
    private EditText Title;
    private Button UploadNotice;
    private DatabaseReference reference;
    private StorageReference  storageReference;
    private ProgressDialog pd; //We have created this is to show progress of the current task using progress dialog
    String downloadUrl= ""; // if url not generated URl will be blank

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        uploadImage = findViewById(R.id.addImage);
        previewUpload = findViewById(R.id.NoticeImageView);
        Title = findViewById(R.id.NoticeTitle);
        UploadNotice = findViewById(R.id.ButtonUploadNotice);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference =  FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(this);  // Here we have initialized the the progress dialog

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        UploadNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Title.getText().toString().isEmpty()){
                    Title.setError("Empty");
                    Title.requestFocus();
                }
                else if(bitmap == null){
                    uploadData();
                }
                else{
                    uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        //Below two line will show  the progress of the task using ProgressDialog variable;
        pd.setMessage("Uploading....");
        pd.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //Here we are compressing the images
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;

        filePath = storageReference .child("Notice").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(activity_UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadUrl=String.valueOf(uri);
                                    uploadData();


                                }
                            });
                        }
                    });

                }
                else{
                    pd.dismiss();// when error occur while uploading the this will run
                    Toast.makeText(activity_UploadNotice.this,"Somnething Went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void uploadData() {
        //Used calender class to get the time and date for that particular moment


        reference = reference.child("Notice");// notice child k andar phle unique key hoga
        final String uniqueKey =reference.push().getKey();// In this we wil get the unique , where we can store all the data

        // To get the input from  the edit text . It wil takes the title for the notice
        String title = Title.getText().toString();

        // Calender class to get the Date, when the notice uploaded

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mm-yy");
        String date = currentDate.format(calForDate.getTime());

        // Calender class to get the Time, when the notice uploaded

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentDate.format(calForTime.getTime());

        /*Using class NoticeData the we have created we have created the variable lof that class to store all the data which is
         mention in the class constructor */
        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time,uniqueKey);

        /*
            1.Here we are storing data in firebase
            2.We also added SuccessListener to ensure that the file is uploaded
        */

        reference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();//If uploading done successfully
                Toast.makeText(activity_UploadNotice.this,"Successfully Uploaded",Toast.LENGTH_LONG).show();
            }


            // 3.And we also added addOnFailureListenerr to ensure that the file is uploaded

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();//If uploading failed
                Toast.makeText(activity_UploadNotice.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
            }
        });



    }

    // we have to open gallery so we will use Itent
    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

            previewUpload.setImageBitmap(bitmap);

        }
    }
}