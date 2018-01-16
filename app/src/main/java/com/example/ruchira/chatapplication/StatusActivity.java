package com.example.ruchira.chatapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

    private EditText status;
    private Button saveChanges;

    private ProgressDialog mProgressDialog;

    //firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //get the current user for status
        mCurrentuser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentuser.getUid();

        //firebase
        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mtoolbar=(Toolbar) findViewById(R.id.status_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status=findViewById(R.id.status_input);
        saveChanges=findViewById(R.id.status_save_button);

        String status_value=getIntent().getStringExtra("status_value");
        status.setText(status_value);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog=new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Save changes");
                mProgressDialog.setMessage("please wait while we save the changes ");
                mProgressDialog.show();

                String newstatus=status.getText().toString();
                //new on complete listner for check task is complete
                mStatusDatabase.child("status").setValue(newstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgressDialog.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"there was some error",Toast.LENGTH_LONG).show();
                        }

                    }
                });


            }
        });


    }
}
