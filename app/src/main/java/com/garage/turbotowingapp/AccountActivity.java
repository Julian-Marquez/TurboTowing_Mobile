package com.garage.turbotowingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import com.garage.turbotowingapp.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
     private Database connect;
    private User currentUser;
    private int defaultwidth;
    private int defaultheight;
    private static final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);

        TextView editinfo = findViewById(R.id.edit);
        TextView confirmedit = findViewById(R.id.confirmedit);
        TextView displayname = findViewById(R.id.FullName);
        TextView displayemail = findViewById(R.id.email);
        TextView displaypassword = findViewById(R.id.password);
        TextView displayPhone = findViewById(R.id.phoneNumber);
        ImageView editPicture = findViewById(R.id.profilepic);
        ImageView goBackArrow = findViewById(R.id.gobackview);
        Button logOff = findViewById(R.id.logoffButton);
        Button delete = findViewById(R.id.deleteAccount);

        EditText newemail = findViewById(R.id.editemail);
        EditText newpassword = findViewById(R.id.editpassword);
        TextView canceledit = findViewById(R.id.canceledit);
        EditText editfirstname = findViewById(R.id.editFirstName);
        EditText editPhone = findViewById(R.id.editPhoneNumber);
        EditText editlastname = findViewById(R.id.editLastName);
        EditText confirmedpassword = findViewById(R.id.confirmpassword);
        LinearLayout layout = findViewById(R.id.confimlayout);
        ImageView confrimimage = findViewById(R.id.confirmedimage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SharedPreferences sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        goBackArrow.setOnClickListener(goBack ->{

            Intent intent = new Intent(AccountActivity.this,MainActivity.class);
            startActivity(intent);
        });

        logOff.setOnClickListener(off ->{
            editor.putString("email", null);
            editor.putString("password",null);
            editor.apply();

            Intent intent = new Intent(AccountActivity.this,MainActivity.class);
            startActivity(intent);

        });



        String email = sharedPref.getString("email", null);
        String password = sharedPref.getString("password", null);

        connect = new Database(this);

        List<User> allUsers = connect.getAllUsers();
        StringBuilder build = new StringBuilder();
        for(User user : allUsers){

            if (email.equalsIgnoreCase(user.getEmail()) && password.equalsIgnoreCase(user.getPassword())) {

                currentUser = user;

                displayemail.setText(currentUser.getEmail());
                displayname.setText(currentUser.getFirstName() + " " + user.getLastName());
                displayPhone.setText(currentUser.getPhoneNumber());
                for(int i = 0; i < currentUser.getPassword().length(); i++){
                    build.append("*");
                }
                displaypassword.setText(build); // just show the user the length of there password


                try {
                    if(user.getProfilePic() != null || user.getProfilePic().length != 0){
                        currentUser.setProfilePic(user.getProfilePic());
                        byte[] bytes = user.getProfilePic();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        editPicture.setImageBitmap(bitmap);
                    }
                } catch (NullPointerException e) {
                    editPicture.setImageResource(R.mipmap.default_pro); // set the default if the error is thrown

                }

                newemail.setText(currentUser.getEmail());
                editfirstname.setText(currentUser.getFirstName());
                editlastname.setText(currentUser.getLastName());
                editPhone.setText(currentUser.getPhoneNumber());
                newpassword.setText(currentUser.getPassword()); //this so the user can see there password

            }

        }

        delete.setOnClickListener(remove ->{

            builder.setTitle("Confirm Deletion")
                    .setMessage("This action is can not undone. Are you sure you want to proceed")
                    .setCancelable(false);

            builder.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    connect.deleteUser(currentUser.getUserId());
                    editor.putString("email", null);
                    editor.putString("password",null);
                    editor.apply();
                    Intent intent = new Intent(AccountActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setIcon(R.drawable.confirm);;


            AlertDialog alert = builder.create();
            alert.show();
        });

        editinfo.setOnClickListener(edit ->{
            editinfo.setVisibility(View.GONE);
            confirmedit.setVisibility(View.VISIBLE);


            //get rid of the old text
            displayname.setVisibility(View.GONE);
            displayemail.setVisibility(View.GONE);
            displaypassword.setVisibility(View.GONE);
            displayPhone.setVisibility(View.GONE);

            //display the edit feilds
            newemail.setVisibility(View.VISIBLE);
            newpassword.setVisibility(View.VISIBLE);
            canceledit.setVisibility(View.VISIBLE);
            editfirstname.setVisibility(View.VISIBLE);
            editlastname.setVisibility(View.VISIBLE);
            editPhone.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            confirmedpassword.setVisibility(View.VISIBLE);
            confrimimage.setVisibility(View.VISIBLE);

            canceledit.setOnClickListener(cancel -> {
                editinfo.setVisibility(View.VISIBLE);
                confirmedit.setVisibility(View.GONE);

                Intent intent = new Intent(AccountActivity.this,AccountActivity.class);
                startActivity(intent);

                //get rid of the old text
                displayname.setVisibility(View.VISIBLE);
                displayemail.setVisibility(View.VISIBLE);
                displaypassword.setVisibility(View.VISIBLE);
                displayPhone.setVisibility(View.VISIBLE);

                //display the edit feilds
                editPhone.setVisibility(View.GONE);
                newemail.setVisibility(View.GONE);
                newpassword.setVisibility(View.GONE);
                canceledit.setVisibility(View.GONE);
                editfirstname.setVisibility(View.GONE);
                editlastname.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                confirmedpassword.setVisibility(View.GONE);
                confrimimage.setVisibility(View.GONE);

            });

            confirmedit.setOnClickListener(confirmed ->{
                editinfo.setVisibility(View.VISIBLE);
                confirmedit.setVisibility(View.GONE);

                //get rid of the old text
                displayname.setVisibility(View.VISIBLE);
                displayemail.setVisibility(View.VISIBLE);
                displaypassword.setVisibility(View.VISIBLE);
                displayPhone.setVisibility(View.VISIBLE);


                //display the edit feilds
                newemail.setVisibility(View.GONE);
                editPhone.setVisibility(View.GONE);
                newpassword.setVisibility(View.GONE);
                canceledit.setVisibility(View.GONE);
                editfirstname.setVisibility(View.GONE);
                editlastname.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                confirmedpassword.setVisibility(View.GONE);
                confrimimage.setVisibility(View.GONE);

                String firstnameText = editfirstname.getText().toString();
                String lastnameText = editlastname.getText().toString();
                String cpasswordText = confirmedpassword.getText().toString();
                String passwordText = newpassword.getText().toString();
                String emailText = newemail.getText().toString();
                String phoneNumberText = editPhone.getText().toString();

                if(!emailText.isEmpty() || !emailText.isBlank()){
                    currentUser.setEmail(emailText);
                }
                if(!firstnameText.isEmpty() || !firstnameText.isBlank()){
                    currentUser.setFirstName(firstnameText);
                }
                if(!lastnameText.isEmpty() || !lastnameText.isBlank()){
                    currentUser.setLastName(lastnameText);
                }
                if(!phoneNumberText.isEmpty() || !phoneNumberText.isBlank()){
                    currentUser.setPhoneNumber(phoneNumberText);
                }
                boolean password_change = false;
                if(!passwordText.isEmpty()  && !passwordText.equals(currentUser.getPassword())){

                    password_change = true;

                    if(passwordText.length() > 7 && passwordText.equals(cpasswordText)) {
                        //check for special symbols
                        if(passwordText.contains("@") || passwordText.contains("_") || passwordText.contains("*") || passwordText.contains("!") || passwordText.contains("#")) {
                            if(passwordText.contains("1") || passwordText.contains("2") || passwordText.contains("3") || passwordText.contains("4") || passwordText.contains("5") || passwordText.contains("6") || passwordText.contains("7") || passwordText.contains("8") || passwordText.contains("9") || passwordText.contains("0") ) {
                                //make a the changes

                                password_change = false;
                                currentUser.setPassword(passwordText);
                            }else{
                                builder.setTitle("Number Needed")
                                        .setMessage("Password must have at least one Number.");
                            }

                        }else {
                            builder.setTitle("Special Character ")
                                    .setMessage("Password must have at least one special character.");
                            // alert.show();
                        }
                    }else {
                        builder.setTitle("Short Password")
                                .setMessage("Password Must be at least 8 character long and must match the confirmed password.");

                    }
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                if(!password_change) {
                    connect.updateUser(currentUser);
                    editor.putString("email", currentUser.getEmail());
                    editor.putString("password", currentUser.getPassword());
                    editor.apply();
                    Intent intent = new Intent(AccountActivity.this, AccountActivity.class);
                    startActivity(intent);
                }

            });


        });

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultwidth = v.getWidth();
                defaultheight = v.getHeight();
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
                // Get the original Bitmap
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);


                ImageView editPicture = findViewById(R.id.profilepic);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, defaultwidth, defaultwidth, true);

                // Set the scaled Bitmap to the ImageView
                editPicture.setImageBitmap(scaledBitmap);

                // Convert the scaled Bitmap to a byte array for the database
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Update the current user with the new profile picture
                currentUser.setProfilePic(byteArray);
               // operate.updateUser(currentUser);

                // Show a confirmation message for debugging purposes
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
