package com.garage.turbotowingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class VehicleActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int SELECT_PICTURE = 1;
    private byte[] vehicleImage;
    private final String TAG = "VehicleActivity";
   // private Database connect = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // this will help the stay logged in as they tranverse through pages
        String email = sharedPref.getString("email",null);
        String password = sharedPref.getString("password",null);

        Database connect = new Database(this);
        User user = null;

        for(User logged : connect.getAllUsers()){
            if (logged.getEmail().equalsIgnoreCase(email) && logged.getPassword().equalsIgnoreCase(password)) {
                user = logged;

            }
        }

        setContentView(R.layout.addvehicle);

        EditText brandText = findViewById(R.id.brand);
        EditText modelText = findViewById(R.id.model);
        EditText addressText = findViewById(R.id.address);
        EditText engineText = findViewById(R.id.engine);
        EditText YOVText = findViewById(R.id.year); // year of vehicle
        EditText damageText = findViewById(R.id.damage);
        EditText DISText = findViewById(R.id.daysinstorage); // days in storage
        EditText detailsText = findViewById(R.id.description);

        Button addImage = findViewById(R.id.addImageButton);
        Button addVehicle = findViewById(R.id.addVehicleButton);
        ImageView goBackArrow = findViewById(R.id.gobackview);

        goBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.add_vehicle_ic);


        User finalUser = user;
        User finalUser1 = user;
        addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean vehicleAdded = false;
                int year = 0;
                int DIS = 0;
                builder.setTitle("Incomplete")
                        .setMessage("All Text responses must not be empty ");
                try{
                    year = Integer.parseInt(YOVText.getText().toString());
                    DIS = Integer.parseInt(DISText.getText().toString());

                }catch(NumberFormatException e){
                }
                String brand = brandText.getText().toString();
                String model = modelText.getText().toString();
                String address = addressText.getText().toString();
                String engine = engineText.getText().toString();
                String damage = damageText.getText().toString();
                String details = detailsText.getText().toString();

                if(!brand.isEmpty() && !model.isEmpty() && !address.isEmpty() &&
                        !engine.isEmpty() && !damage.isEmpty() && !details.isEmpty()
                    && year > 1920 && DIS > 0 && vehicleImage.length != 0){

                    builder.setTitle("Vehicle Added")
                            .setMessage("Your " + brand + " " + model + " has been successfully added");

                    vehicleAdded = true;

                    Vehicle vehicle = new Vehicle(brand,model,year,damage,engine,DIS,details,address);
                    vehicle.getImageBytesList().add(vehicleImage);
                    finalUser.getSavedVehicles().add(vehicle);
                    connect.insertVehicle(vehicle, finalUser1);
                }else{
                    Toast.makeText(VehicleActivity.this,"Please fill out all feilds and insert an image.",Toast.LENGTH_SHORT);
                }
                if(vehicleAdded){
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(VehicleActivity.this,MainActivity.class));
                        }
                    }).show().setCancelable(false);
                }
                else{
                    builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setCancelable(true)
                            .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    startActivity(new Intent(VehicleActivity.this, MainActivity.class));
                                }
                            }).show();
                }

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }

        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {

                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Update the current user with the new profile picture
                vehicleImage = byteArray;

                // Show a confirmation message for debugging purposes
                Toast.makeText(this, "Vehicle Image Processed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}