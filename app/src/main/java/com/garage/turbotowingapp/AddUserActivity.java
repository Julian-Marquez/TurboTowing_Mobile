package com.garage.turbotowingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddUserActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private final String TAG = "AddUserActivity";
    private static final int SELECT_PICTURE = 1;
    private int deafultwidth;
    private int deafultheight;
    private byte[] profilePic;
    // private Database connect = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Database connect =  new Database(this);

        setContentView(R.layout.adduser);

        ImageView backArrow = findViewById(R.id.gobackview);
        Button signUpButton = findViewById(R.id.SignUpButton);
        Button addPicture = findViewById(R.id.addImageButton);
        EditText newLastName = findViewById(R.id.lastname);
        EditText newEmail = findViewById(R.id.email);
        EditText newFirtsName = findViewById(R.id.firstname);
        EditText newpassword = findViewById(R.id.password);
        EditText confirmpassword = findViewById(R.id.confirmpassword);
        EditText phoneNumberText = findViewById(R.id.phonenumber);

        byte[] imageData = getByteDataFromMipmap(R.mipmap.default_pro);

        Log.d(TAG,"Testing to see if image data is empty: " + imageData.length);

        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deafultwidth = v.getWidth();
                 deafultheight = v.getHeight();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        signUpButton.setOnClickListener(sign ->{
            String email = newEmail.getText().toString();
            String firstname = newFirtsName.getText().toString();
            String lastname = newLastName.getText().toString();
            String password = newpassword.getText().toString();
            String confirmedpassword = confirmpassword.getText().toString();
            String phoneNumber = phoneNumberText.getText().toString();

            boolean usercraeted = false;
            boolean userExist = false;

            //check no feilds are blank
            if(!confirmedpassword.isEmpty() || !email.isEmpty() || !firstname.isEmpty() || !lastname.isEmpty()  || !password.isEmpty()){
                //check for approiate lenght
                for(User user : connect.getAllUsers()){ //check if the email is already in use
                    if(user.getEmail().equalsIgnoreCase(email)){
                        userExist = true;
                    }
                }



                if(password.length() > 7 && password.equals(confirmedpassword) && !userExist) {
                    //check for special symbols
                    if(password.contains("@") || password.contains("_") || password.contains("*") || password.contains("!") || password.contains("#")) {
                        //make a new user
                        if(password.contains("1") || password.contains("2") || password.contains("3") || password.contains("4") || password.contains("5") || password.contains("6") || password.contains("7") || password.contains("8") || password.contains("9") || password.contains("0")) {
                            //make a the changes
                            if (profilePic == null || profilePic.length == 0) {
                                profilePic = imageData;
                            }

                            User newuser = new User(firstname, lastname,password, email,phoneNumber,profilePic);

                            connect.insertUser(newuser);

                            SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();

                            // this will help the stay logged in as they tranverse through pages
                            editor.putString("email", newuser.getEmail());
                            editor.putString("password", newuser.getPassword());
                            editor.apply();



                            builder.setTitle("Welcome " + firstname) // welcome the user
                                    .setMessage("User succesfully created");
                            usercraeted = true;
                        } else{
                            builder.setTitle("Number Needed")
                                    .setMessage("Password must have at least one Number.");
                        }

                    }else {
                        builder.setTitle("Special Character ")
                                .setMessage("Password must have at least one special character.");
                    }
                }else {
                    builder.setTitle("Short Password")
                            .setMessage("Password Must be at least 8 character long and must match the confirmed password.");
                    if(userExist){
                        builder.setTitle("User email Taken")
                                .setMessage("The email " + email + " already exist within the database");
                    }
                }
            }else{
                builder.setTitle("Blank Feilds")
                        .setMessage("All feilds must be not be blank");

            }
            boolean finalUsercraeted = usercraeted;
            builder.setCancelable(false) // Prevent dialog from being dismissed by clicking outside
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss(); // dismiss when pressed
                            if(finalUsercraeted){
                                Intent intent = new Intent(AddUserActivity.this,AccountActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
            // Create and show the alert dialog
            AlertDialog alert = builder.create();
            alert.show();
        });

        backArrow.setOnClickListener(cancel ->{
            Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
            startActivity(intent);

        });


    }
    public byte[] getByteDataFromMipmap(int resId) {
        // Load the bitmap from resources
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        if (bitmap == null) {
            return null; // Return null if decoding fails
        }

        // Convert bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                // Get the original Bitmap
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);



                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, deafultwidth, deafultwidth, true);

                // Convert the scaled Bitmap to a byte array for the database
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Update the current user with the new profile picture
                profilePic = byteArray;

                // Show a confirmation message for debugging purposes
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}