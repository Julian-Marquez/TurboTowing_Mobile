package com.garage.turbotowingapp.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garage.turbotowingapp.Database;
import com.garage.turbotowingapp.R;
import com.garage.turbotowingapp.User;
import com.garage.turbotowingapp.Vehicle;
import com.garage.turbotowingapp.VehicleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView carousel;
    private int vehicleItemHeight = 0;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);

        String email = sharedPref.getString("email", null);
        String password = sharedPref.getString("password", null);

        Database connect = new Database(requireContext());
        User user = null;
        ArrayList<Vehicle> allVehicles = new ArrayList<>();

        Random rand = new Random();

        for (User logged : connect.getAllUsers()) {
            if (logged.getEmail().equalsIgnoreCase(email) && logged.getPassword().equalsIgnoreCase(password)) {
                user = logged;
            }

            if (!logged.getVehiclelist().isEmpty()) {
                allVehicles.addAll(logged.getVehiclelist());
            }
        }

        List<Vehicle> vehicleCarousel = new ArrayList<>();

        if (allVehicles.size() > 5) {
            while (vehicleCarousel.size() < 4) {
                try {
                    int num = rand.nextInt(allVehicles.size() - 1);
                    Vehicle vehicle = allVehicles.get(num);

                    if (!vehicleCarousel.contains(vehicle)) {
                        vehicleCarousel.add(vehicle);
                    }

                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "IndexOutOfBoundsException", e);
                }
            }
        } else {
            Log.d(TAG, "For now this will be to handle static vehicle place holders");
        }

        try {
            Context context = getContext();
            VehicleAdapter adapter = new VehicleAdapter(vehicleCarousel, context);

            carousel = view.findViewById(R.id.carousel);
            carousel.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            carousel.setAdapter(adapter);

            // Post to ensure the layout has been drawn before measuring
            carousel.post(() -> {
                if (carousel.getChildCount() > 0) {
                    View firstItem = carousel.getChildAt(0);
                    if (firstItem != null) {
                        vehicleItemHeight = firstItem.getHeight();
                        Log.d(TAG, "Vehicle item height: " + vehicleItemHeight);
                    }
                }
            });
            carousel.setMinimumHeight(vehicleItemHeight );

        } catch (NullPointerException e) {
            Log.e(TAG, "Error trying to access the adapter with null error", e);
        }
    }
}
