package com.example.ruchira.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Toolbar mtoolbar;

    private ProgressDialog mRegPrograss;

    private EditText name,email,password;
    private Button reg;

    private FirebaseAuth mAuth;

    //firebasedatabase instance
    DatabaseReference nDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        name=findViewById(R.id.display_name);
        email=findViewById(R.id.display_email);
        password=findViewById(R.id.display_password);

        mRegPrograss=new ProgressDialog(this);

        mtoolbar=(Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create acoount");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reg=findViewById(R.id.reg_btn);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayname=name.getText().toString();
                String displayemail=email.getText().toString();
                String displaypassword=password.getText().toString();

                if (!TextUtils.isEmpty(displayname)|| !TextUtils.isEmpty(displayemail)|| !TextUtils.isEmpty(displaypassword)){

                    mRegPrograss.setTitle("Registering new user");
                    mRegPrograss.setMessage("please wait while we create");
                    mRegPrograss.setCanceledOnTouchOutside(false);
                    mRegPrograss.show();


                    register_user(displayname,displayemail,displaypassword);

                }


            }
        });

    }

    //check the email already exit in firebase
   /* public void checkemail(String displayemail){

        mAuth.fetchProvidersForEmail(displayemail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {


                boolean result=!task.getResult().getProviders().isEmpty();

                if (result){
                    Toast.makeText(getApplicationContext(),"Email not found",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Email already presdent",Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/

    private void register_user(final String displayname, String displayemail, String displaypassword) {

        mAuth.createUserWithEmailAndPassword(displayemail,displaypassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if email and password valid
                if (task.isSuccessful()){

                    FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=currentuser.getUid();

                    nDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    //taking data from the object and asve to the database
                    HashMap<String,String> userMap=new HashMap<>();
                    userMap.put("name",displayname);
                    userMap.put("status","hi their im using this app");
                    userMap.put("image","this is hard corded value");
                    userMap.put("thumb_image","default");

                    nDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mRegPrograss.dismiss();
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }

                        }
                    });



                }else{

                    mRegPrograss.hide();
                    Toast.makeText(RegisterActivity.this,"you got some error",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
