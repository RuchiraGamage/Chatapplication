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

public class LoginActivity extends AppCompatActivity {
    //declare auth
    private FirebaseAuth mAuth;

    private Toolbar toolbar;
    private EditText log_email,log_password;
    private Button log;

    private ProgressDialog logindialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //declare auth
        mAuth = FirebaseAuth.getInstance();

        toolbar=(Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login ");

        logindialog=new ProgressDialog(this);

        log_email=findViewById(R.id.user_email);
        log_password=findViewById(R.id.user_password);
        log=findViewById(R.id.login);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emaill=log_email.getText().toString();
                String passs=log_password.getText().toString();

                if (!TextUtils.isEmpty(emaill)|| !TextUtils.isEmpty(passs)){

                    logindialog.setTitle("Logging in");
                    logindialog.setMessage("please wait while we check your ");
                    logindialog.setCanceledOnTouchOutside(false);
                    logindialog.show();

                    login(emaill,passs);
                }
            }
        });



    }

    private void login(String emaill, String passs) {

        mAuth.signInWithEmailAndPassword(emaill,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    logindialog.dismiss();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //it will not redirect to the previous page back from the app
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    logindialog.hide();
                    Toast.makeText(LoginActivity.this,"Login fail",Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}
