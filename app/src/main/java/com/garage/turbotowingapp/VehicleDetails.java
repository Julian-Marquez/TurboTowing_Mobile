package com.garage.turbotowingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import com.garage.turbotowingapp.databinding.ActivityMainBinding;
import com.garage.turbotowingapp.ui.gallery.AllVehiclesFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehicleDetails extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int SELECT_PICTURE = 1;
    private byte[] vehicleImage;
    private final String TAG = "VehicleDetails";
    private int width ;
    private int height;
    private Vehicle vehicle;
    private Database connect;
    private boolean insertImage = false;
    private int index;
    private boolean editMode = false;
    private boolean removeImage = false;
    // private Database connect = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // this will help the stay logged in as they tranverse through pages
        String email = sharedPref.getString("email",null);
        String password = sharedPref.getString("password",null);
        long vehicleId = sharedPref.getLong("vehicleId", 0);

        connect = new Database(this);
        User user = null;
        boolean isAdmin = false;

        for(User logged : connect.getAllUsers()){

            if(logged.getEmail().equals(email) && logged.getPassword().equals(password)){
                user = logged;
            }



            if(!logged.getVehiclelist().isEmpty()) {
                for (Vehicle vehicle1 : logged.getVehiclelist()) {
                    if(vehicle1.getUuid() == vehicleId){

                        vehicle = vehicle1;
                    }
                }
            }

        }

        try {
            if (user.getEmail().equals("turbotowing505@yahoo.com")) {
                isAdmin = true;
            }
        }catch(NullPointerException e){
            isAdmin = false;
        }

        setContentView(R.layout.vehicle_details);

        TextView brandText = findViewById(R.id.brand);
        TextView modelText = findViewById(R.id.model);
        TextView addressText = findViewById(R.id.address);
        TextView engineText = findViewById(R.id.engine);
        TextView YOVText = findViewById(R.id.age); // year of vehicle
        TextView damageText = findViewById(R.id.damage);
        TextView datePostedText = findViewById(R.id.datePosted);
        TextView DISText = findViewById(R.id.DIS); // days in storage
        TextView detailsText = findViewById(R.id.description);
        TextView ownerNameText = findViewById(R.id.owner_name);
        TextView phoneText = findViewById(R.id.phonenumber);
        TextView estimatedPrice = findViewById(R.id.estimatedPrice);

        TextView editView = findViewById(R.id.edit);
        TextView cancelEdit = findViewById(R.id.cancelEdit);
        TextView confirmEdit = findViewById(R.id.confirmEdit);

        EditText editBrand = findViewById(R.id.editBrand);
        EditText editModel = findViewById(R.id.editModel);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editEngine = findViewById(R.id.editEngine);
        EditText editYOV = findViewById(R.id.editAge); // year of vehicle
        EditText editDamage = findViewById(R.id.editDamage);
        EditText editDate = findViewById(R.id.editDatePosted);
        EditText editDIS = findViewById(R.id.editDIS); // days in storage
        EditText editDetails = findViewById(R.id.editDescription);

        ImageView vehicleImage = findViewById(R.id.vehicleImage);
        ImageView goBackArrow = findViewById(R.id.gobackview);
        ImageView saveVehicle = findViewById(R.id.save_vehicle);

        LinearLayout buildLayout = findViewById(R.id.build);
        ImageView addImage = new ImageView(this);

        if(!isAdmin){
            editView.setVisibility(View.GONE);
        }

        List<byte[]> imageData = vehicle.getImageBytesList();

        ViewGroup.LayoutParams params = buildLayout.getLayoutParams();

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (imageData.size() == 1) {
            layoutParams.gravity = Gravity.CENTER;
        } else {
            layoutParams.gravity = Gravity.NO_GRAVITY;
        }

        buildLayout.setLayoutParams(layoutParams);

    if(imageData.size() > 1){
        vehicleImage.setVisibility(View.GONE); //this is the first image so no duplicates
            for(int i = 0; i < imageData.size(); i++ ){

                byte[] bytes = imageData.get(i);
                    ImageView image = new ImageView(this); // Create a new ImageView for each image
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bitmap);
                    image.setAdjustViewBounds(true);
                    image.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, // Width
                            LinearLayout.LayoutParams.MATCH_PARENT,0  // Height
                    ));


                image.setScaleType(ImageView.ScaleType.FIT_XY);

                buildLayout.addView(image); // Add each new ImageView to the layout

                int finalI = i;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupWindow popupWindow;
                        index = finalI;
                        if(editMode){
                            index = finalI;
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.remove_image, null);

                            ImageView inflatedImage = popupView.findViewById(R.id.inflate_Image);
                            inflatedImage.setImageBitmap(bitmap);

                            // Create and configure popup window
                            popupWindow = new PopupWindow(popupView,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);

                            popupWindow.setFocusable(false); // Don't block interaction with other elements
                            popupWindow.setOutsideTouchable(true);

                            // Show popup at the top of the screen
                            popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 50);

                            Button image_options = popupView.findViewById(R.id.image_options);

                            image_options.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    builder.setIcon(R.mipmap.delete_image).setTitle("Image Options").setMessage("Choose and Image Option").
                                            setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //    connect.removeVehicleImage(vehicleId,);
                                                    dialogInterface.dismiss();
                                                    popupWindow.dismiss();
                                                    vehicle.getImageBytesList().remove(finalI);
                                                    connect.updateVehicle(vehicle);
                                                    startActivity(new Intent(VehicleDetails.this,VehicleDetails.class));
                                                }
                                            }).
                                            setNeutralButton("Replace Image", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Intent intent = new Intent(Intent.ACTION_PICK,
                                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    intent.setType("image/*");
                                                    startActivityForResult(intent, SELECT_PICTURE);
                                                }
                                            }).
                                            setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Log.d(TAG,"Cancel this option " + bytes.length);
                                                    dialogInterface.dismiss();
                                                    popupWindow.dismiss();
                                                }
                                            }).show();
                                }
                            });

                        }


                    }
                });

                }


            }else{
        byte[] bytes = vehicle.getImageBytesList().get(0);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        vehicleImage.setImageBitmap(bitmap);

        vehicleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertImage = false;
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });
    }

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.add_vehicle_ic);

        User finalUser = user;
        saveVehicle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(finalUser != null){

                builder2.setTitle("Save Vehicle For Later")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG,"Attempting to insert vehicle: " + vehicleId + " into database");

                               long result = connect.insertUserVehicle(finalUser.getUserId(),vehicleId);

                               if(result == -1){
                                   Toast.makeText(VehicleDetails.this, "Can not add a duplicate." , Toast.LENGTH_SHORT).show();
                               }else{
                                   Toast.makeText(VehicleDetails.this, vehicle.getBrand() + " " + vehicle.getModel() + " added to " + finalUser.getFirstName() + "'s Saved Vehicles successfully." , Toast.LENGTH_LONG).show();
                               }
                            }
                        }).setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }else{
                Toast.makeText(VehicleDetails.this,"Please Login to save vehicles.",Toast.LENGTH_SHORT).show();
            }
        }
    });

        addImage.setImageResource(R.mipmap.add_image); // outside the for loop because it's always added at the end

        addImage.setAdjustViewBounds(true);
        addImage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width
                ViewGroup.LayoutParams.MATCH_PARENT,0  // Height
        ));


        addImage.setScaleType(ImageView.ScaleType.FIT_XY);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertImage = true;
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        addImage.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                LinearLayout.LayoutParams.MATCH_PARENT  // Height
        ));

        LocalDate today = LocalDate.now();

        int Tow = 100;
        int storage = 15;


        Log.d(TAG,"Today is: " + today);

        String vehicleAge = String.valueOf(vehicle.getAge());
        String vehicleDIS = String.valueOf(vehicle.getDaysInStorage());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String date = formatter.format(vehicle.getDatePosted());

        User owner = vehicle.getOwner();

        int daysBetween = today.compareTo(vehicle.getDatePosted());

        int total_days_DIS = daysBetween + vehicle.getDaysInStorage();

        float price = Tow+(daysBetween*storage);

        double tax = 0.075 * price;

        DecimalFormat df = new DecimalFormat("#.##");

        String total = df.format(tax + price);

        Log.d(TAG,"Your subtotal is: " + total);

        brandText.setText(vehicle.getBrand());
        modelText.setText(vehicle.getModel());
        addressText.setText(vehicle.getLocation());
        engineText.setText(vehicle.getEngine());
        YOVText.setText(vehicleAge);
        damageText.setText("Vehicle Damage: " + vehicle.getDamage());
        datePostedText.setText(date);
        DISText.setText("Vehicle Days in Storage: " + total_days_DIS);
        detailsText.setText(vehicle.getDescription());
        ownerNameText.setText(owner.getFirstName() + " " + owner.getLastName());
        phoneText.setText(owner.getPhoneNumber());
        estimatedPrice.setText("Estimated Price $" + total);

        editBrand.setText(vehicle.getBrand());
        editModel.setText(vehicle.getModel());
        editAddress.setText(vehicle.getLocation());
        editEngine.setText(vehicle.getEngine());
        editYOV.setText(vehicleAge);
        editDamage.setText(vehicle.getDamage());
        editDate.setText(date);
        editDIS.setText(total_days_DIS + "");
        editDetails.setText(vehicle.getDescription());

        goBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocationName(addressText.getText().toString(),1);

                        Address address = addresses.get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + addressText.getText().toString();
                //open Google Maps
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                intent.setPackage("com.google.android.apps.maps"); // Optional: Open only in Google Maps app
                if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    view.getContext().startActivity(intent);
                } else {
                    // Fallback: Open in the browser if Google Maps app is unavailable
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                    view.getContext().startActivity(browserIntent);
                }
            }
        });
        
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editMode = true; //mainly just for the image Views

                if(imageData.size() > 1) {
                    layoutParams.gravity = Gravity.NO_GRAVITY;
                    buildLayout.setLayoutParams(layoutParams);
                }

                buildLayout.addView(addImage);
                editView.setVisibility(View.GONE);
                brandText.setVisibility(View.GONE);
                modelText.setVisibility(View.GONE);
                addressText.setVisibility(View.GONE);
                engineText.setVisibility(View.GONE);
                YOVText.setVisibility(View.GONE);
                damageText.setVisibility(View.GONE);
                datePostedText.setVisibility(View.GONE);
                DISText.setVisibility(View.GONE);
                detailsText.setVisibility(View.GONE);

                editBrand.setVisibility(View.VISIBLE);
                editModel.setVisibility(View.VISIBLE);
                editAddress.setVisibility(View.VISIBLE);
                editEngine.setVisibility(View.VISIBLE);
                editYOV.setVisibility(View.VISIBLE);
                editDamage.setVisibility(View.VISIBLE);
                editDate.setVisibility(View.VISIBLE);
                editDIS.setVisibility(View.VISIBLE);
                editDetails.setVisibility(View.VISIBLE);

                cancelEdit.setVisibility(View.VISIBLE);
                confirmEdit.setVisibility(View.VISIBLE);
            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editMode = false; //change back to false

                if(imageData.size() == 1) {
                    layoutParams.gravity = Gravity.CENTER;
                    buildLayout.setLayoutParams(layoutParams);
                }
                buildLayout.removeView(addImage);

                brandText.setVisibility(View.VISIBLE);
                modelText.setVisibility(View.VISIBLE);
                addressText.setVisibility(View.VISIBLE);
                engineText.setVisibility(View.VISIBLE);
                YOVText.setVisibility(View.VISIBLE);
                damageText.setVisibility(View.VISIBLE);
                datePostedText.setVisibility(View.VISIBLE);
                DISText.setVisibility(View.VISIBLE);
                detailsText.setVisibility(View.VISIBLE);

                editBrand.setVisibility(View.GONE);
                editModel.setVisibility(View.GONE);
                editAddress.setVisibility(View.GONE);
                editEngine.setVisibility(View.GONE);
                editYOV.setVisibility(View.GONE);
                editDamage.setVisibility(View.GONE);
                editDate.setVisibility(View.GONE);
                editDIS.setVisibility(View.GONE);
                editDetails.setVisibility(View.GONE);

                cancelEdit.setVisibility(View.GONE);
                confirmEdit.setVisibility(View.GONE);
                editView.setVisibility(View.VISIBLE);
            }
        });


        confirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int year = 0;
                int DIS = 0;
                try{
                    year = Integer.parseInt(editYOV.getText().toString());
                    DIS  = Integer.parseInt(editDIS.getText().toString());

                }catch (NumberFormatException e){

                }

                String brand = editBrand.getText().toString();
                String model = editModel.getText().toString();
                String address = editAddress.getText().toString();
                String engine = editEngine.getText().toString();
                String damage = editDamage.getText().toString();
                String details = editDetails.getText().toString();
                String date_Posted  = editDate.getText().toString();

                if(!brand.isEmpty()){
                    vehicle.setBrand(brand);
                }
                if(!model.isEmpty()) {
                    vehicle.setModel(model);
                }
                if(!address.isEmpty()){
                    vehicle.setLocation(address);
                }
                if(!engine.isEmpty()){
                    vehicle.setEngine(engine);
                }
                if(!damage.isEmpty()){
                    vehicle.setDamage(damage);
                }
                if(!details.isEmpty()){
                    vehicle.setDescription(details);
                }
                if(year > 1920){
                    vehicle.setAge(year);
                }
                if(DIS > 0){
                    vehicle.setDaysInStorage(DIS);
                }
                if(!date_Posted.isEmpty()){
                    LocalDate date = null;
                    try{
                        date = LocalDate.parse(date_Posted);
                        vehicle.setDatePosted(date);
                    } catch (DateTimeParseException e) {
                    }

                }
                connect.updateVehicle(vehicle);

                builder2.setIcon(R.drawable.confirm).setCancelable(false)
                        .setTitle("Confirm Edit")
                        .setMessage("Are you sure You want to confirm and save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivity(new Intent(VehicleDetails.this,VehicleDetails.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

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


                if(insertImage) {
                    vehicle.getImageBytesList().add(vehicleImage);
                }  else{
                     vehicle.getImageBytesList().set(index,vehicleImage);
                }

                connect.updateVehicle(vehicle);

                startActivity(new Intent(VehicleDetails.this,VehicleDetails.class));
                // Show a confirmation message for debugging purposes
                Toast.makeText(this, "Vehicle Image Processed", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
