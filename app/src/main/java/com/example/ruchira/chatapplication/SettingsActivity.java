package com.example.ruchira.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //from activity
    private CircleImageView mDisplayImage;
    private TextView mname,mstatus;
    private Button mstatusbtn,mImageButton;
    private static final int GALARY_PICK=1;

    private ProgressDialog mProgressdialog;

    //firebase storage reference
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage=(CircleImageView) findViewById(R.id.settings_image);
        mname=(TextView)findViewById(R.id.settings_display_name);
        mstatus=(TextView)findViewById(R.id.status);
        mstatusbtn=findViewById(R.id.settings_chansge_status_button);
        mImageButton=findViewById(R.id.settings_change_image_btn);

        mImageStorage= FirebaseStorage.getInstance().getReference();

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String currentuid=mCurrentUser.getUid();

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(currentuid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //what kind of data we receive
                //Toast.makeText(SettingsActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumbImage=dataSnapshot.child("thumb_image").getValue().toString();

                mname.setText(name);
                mstatus.setText(status);
                Picasso.with(SettingsActivity.this).load(image).into(mDisplayImage);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value=mstatus.getText().toString();

                Intent intent=new Intent(SettingsActivity.this,StatusActivity.class);
                intent.putExtra("status_value",status_value);
                startActivity(intent);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),GALARY_PICK);

              /*  CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALARY_PICK && resultCode==RESULT_OK){
            Uri imageuri=data.getData();

            CropImage.activity(imageuri)
                    .setAspectRatio(1,1)
                    .start(this);

          //  Toast.makeText(SettingsActivity.this,imageuri,Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //showing the progress dialog while loading
                mProgressdialog=new ProgressDialog(SettingsActivity.this);
                mProgressdialog.setTitle("uploading image");
                mProgressdialog.setMessage("please wait we upload and prosess the image");
                mProgressdialog.setCanceledOnTouchOutside(false);
                mProgressdialog.show();

                Uri resultUri = result.getUri();
                String current_user_id=mCurrentUser.getUid();


                StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                    //if successful set image url to database image field in fire base
                            String download_link=task.getResult().getDownloadUrl().toString();
                            mUserDatabase.child("image").setValue(download_link).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressdialog.dismiss();
                                        Toast.makeText(SettingsActivity.this,"success uploading",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }else{
                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                            mProgressdialog.dismiss();//if we do not get dissmiss the dialog
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
