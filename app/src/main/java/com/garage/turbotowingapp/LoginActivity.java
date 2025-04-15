package com.garage.turbotowingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.garage.turbotowingapp.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private final String TAG = "LoginActivity";
    // private Database connect = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Message from: " + TAG);
        setContentView(R.layout.login);

        Database connect =  new Database(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView goBackArrow =  findViewById(R.id.gobackview);
        //Buttons
        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.SignUpButton);
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.password);
        TextView recover = findViewById(R.id.forgot_password);

        goBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,AddUserActivity.class);
                startActivity(intent);
            }
        });

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RecoverActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                for(User user : connect.getAllUsers()){

                    Log.d(TAG,"users: " + user.getEmail() + ' ' + user.getPassword());

                    if(email.equals(user.getEmail()) && password.equals(user.getPassword())){

                        SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        // this will help the stay logged in as they tranverse through pages
                        editor.putString("email", user.getEmail());
                        editor.putString("password", user.getPassword());
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this,AccountActivity.class));
                    }
                    else if(email.equals(user.getEmail()) && !password.equals(user.getPassword())){
                            builder.setTitle("Incorrect Password")
                                    .setMessage("The password provided does not match the account.");
                    }else{
                        builder.setTitle("No Account")
                                    .setMessage("No Account found with this email");
                   }

                }
                builder.setCancelable(false) // Prevent dialog from being dismissed by clicking outside
                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss(); // dismiss when pressed

                            }
                        })
                        .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }
                        }).show();

            }
        });

    }
}