package com.example.ruchira.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mtoolbar;

    //declare view pager
    private ViewPager nviewpager;
    private SectionPageAdapter nsectionPageAdapter;

    private TabLayout mtablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define the tabs on main tab layout


        //tab
        nviewpager=findViewById(R.id.main_tabpager);
        nsectionPageAdapter=new SectionPageAdapter(getSupportFragmentManager());
        nviewpager.setAdapter(nsectionPageAdapter);

        mtablayout=(TabLayout) findViewById(R.id.main_tabs);
        mtablayout.setupWithViewPager(nviewpager);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat now");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        if (currentUser==null){

          sendtostartActivity();
        }
    }

    private void sendtostartActivity() {

        Intent startintent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);


         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        //acording to the menu redirect to the logout
         if (item.getItemId()==R.id.main_logout){
             FirebaseAuth.getInstance().signOut();
             sendtostartActivity();
         }

        //acording to the menu redirect to the setting change page
         if (item.getItemId()==R.id.main_settings_btn){

             Intent settingintent=new Intent(MainActivity.this,SettingsActivity.class);
             startActivity(settingintent);

         }

        return true;
    }
}
