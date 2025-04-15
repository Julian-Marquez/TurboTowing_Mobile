package com.garage.turbotowingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.garage.turbotowingapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding addVehicle;
    private final String TAG = "MainActivity";
    private List<Vehicle> vehicles = new ArrayList<>();
    private int pictureWidth;
    private int pictureHeight;
   // private Database connect = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean loggedIn = false;

        SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // this will help the stay logged in as they tranverse through pages
        String email = sharedPref.getString("email",null);
        String password = sharedPref.getString("password",null);

        User user = null;
            if(email != null){

            Database connect = new Database(this);
           // connect.clearDatabase();

            List<User> allUsers = connect.getAllUsers();
            for(User user1 : allUsers){
                if(email.equals(user1.getEmail()) && password.equals(user1.getPassword())){
                    user = user1;
                    loggedIn = true;
                    Log.d(TAG,"User Found: " + user.getEmail() +  " " + user.getPassword());
                }
                if(!user1.getSavedVehicles().isEmpty()){
                    vehicles.addAll(user1.getSavedVehicles());
                    Log.d(TAG,"User: " + user1.getUserId() + " does have vehicles: " + user1.getSavedVehicles().get(0).getBrand()); //debugging
                }
            }
           }

            Log.d(TAG,"Code is being changed");

        addVehicle = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(addVehicle.getRoot());
      //  ImageView picture = addVehicle.getRoot().findViewById(R.id.profilePic);


        setSupportActionBar(addVehicle.appBarMain.toolbar);

        boolean finalLoggedIn = loggedIn;
        addVehicle.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();

                if(finalLoggedIn) {
                    Intent intent = new Intent(MainActivity.this, VehicleActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        DrawerLayout drawer = addVehicle.drawerLayout;
        NavigationView navigationView = addVehicle.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        Menu menu = navigationView.getMenu();

        View headerView = navigationView.getHeaderView(0); // Index 0 means first header

        ImageView profilePic = headerView.findViewById(R.id.profilePic);
        TextView fullName = headerView.findViewById(R.id.fullName);
        TextView emailText = headerView.findViewById(R.id.email);
        
        // Find the ImageView inside the header
        if(user != null) {

            fullName.setText(user.getFirstName() + " " + user.getLastName());
            emailText.setText(user.getEmail());

            Bitmap bits = BitmapFactory.decodeByteArray(user.getProfilePic(), 0, user.getProfilePic().length);

            profilePic.setImageBitmap(bits);

        }else{
            fullName.setVisibility(View.GONE);
            emailText.setVisibility(View.GONE);
        }

        boolean finalLoggedIn1 = loggedIn;
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalLoggedIn1){
                    startActivity(new Intent(MainActivity.this,AccountActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_all_vehicles, R.id.nav_saved_all_vehicles, R.id.nav_login, R.id.nav_signup, R.id.nav_profile,R.id.profilePic)
                .setOpenableLayout(drawer)
                .build();

        MenuItem profileItem = menu.findItem(R.id.nav_profile);
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem signUpItem = menu.findItem(R.id.nav_signup);

        if (loggedIn){
            profileItem.setVisible(true);
            loginItem.setVisible(false);
            signUpItem.setVisible(false);

        }else {
            profileItem.setVisible(false);
            loginItem.setVisible(true);
            signUpItem.setVisible(true);
        }





        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            // Get the NavigationView header view
            NavigationView navigationView = addVehicle.navView;
            View headerView = navigationView.getHeaderView(0);

            // Find the ImageView inside the header
            ImageView profilePic = headerView.findViewById(R.id.profilePic);

            // Ensure the ImageView has been rendered before getting dimensions
            profilePic.post(() -> {
                pictureWidth = profilePic.getWidth();
                pictureHeight = profilePic.getHeight();



            });
        }
    }

}