package com.garage.turbotowingapp;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.garage.turbotowingapp.ui.gallery.AllVehiclesFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    private List<Vehicle> vehicles;
    private Context context;
    private final String TAG = "VehicleAdapter";

    // Constructor
    public VehicleAdapter(List<Vehicle> vehicles, Context context) {
        this.vehicles = vehicles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Vehicle vehicle = vehicles.get(position);

        SharedPreferences sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean isAdmin = false;
        long vehicleUuid = vehicle.getUuid();

        String email = sharedPref.getString("email",null);
        String password = sharedPref.getString("password",null);

        Database connect = new Database(context);

        User user = null;
        try {
            if (!email.isEmpty()) {

                for (User user1 : connect.getAllUsers()) {
                    if (email.equals(user1.getEmail()) && password.equals(user1.getPassword())) {
                        user = user1;

                    }
                    if (email.equals("turbotowing505@yahoo.com")) { //admin
                        isAdmin = true;
                    }
                }
            }
        }catch(NullPointerException e){

        }

        Bitmap bits = BitmapFactory.decodeByteArray(vehicle.getImageBytesList().get(0), 0, vehicle.getImageBytesList().get(0).length);


        holder.title.setText(vehicle.getBrand() + " " + vehicle.getModel());
        holder.address.setText(vehicle.getLocation());
        holder.image.setImageBitmap(bits);


        if(isAdmin) {
            holder.deleteOption.setVisibility(View.VISIBLE);
        }else{
            holder.deleteOption.setVisibility(View.GONE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.deleteVehicle(vehicle.getUuid());
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });

        User finalUser = user;
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putLong("vehicleId",vehicleUuid);
                editor.apply();
                context.startActivity(new Intent(context,VehicleDetails.class));

            }
        });

        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = 0;
                double longitude = 0;

                Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocationName(holder.address.getText().toString(),1);

                    Address address = addresses.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String mapUrl = "https://www.google.com/maps/search/?api=1&query=" + holder.address.getText().toString();
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

    }


    @Override
    public int getItemViewType(int position) {

        return vehicles.size();
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    // ViewHolder class to hold references to item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView address;
        ImageView image;
        LinearLayout deleteOption;
        Button deleteButton;
        Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.Image);
            deleteOption = itemView.findViewById(R.id.deleteOption);
            deleteButton = itemView.findViewById(R.id.remove_button);
            viewButton = itemView.findViewById(R.id.view_button);
            title = itemView.findViewById(R.id.title);
            address = itemView.findViewById(R.id.address);


        }
    }
}
