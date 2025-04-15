package com.garage.turbotowingapp.ui.slideshow;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.garage.turbotowingapp.Database;
import com.garage.turbotowingapp.R;
import com.garage.turbotowingapp.User;
import com.garage.turbotowingapp.Vehicle;
import com.garage.turbotowingapp.VehicleSavedAdapter;

import java.util.ArrayList;

public class SavedVehiclesFragment extends Fragment {

    private static final String TAG = "SavedVehiclesFragment";
    private RecyclerView gridLayout;

    public SavedVehiclesFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_vehicles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);

        // Stay logged in while navigating
        String email = sharedPref.getString("email", null);
        String password = sharedPref.getString("password", null);

        Database connect = new Database(requireContext());
        User user = null;
        ArrayList<Vehicle> allVehicles = new ArrayList<>();

        for (User logged : connect.getAllUsers()) {
            if (logged.getEmail().equalsIgnoreCase(email) && logged.getPassword().equalsIgnoreCase(password)) {
                user = logged;
            }

        }

        if (!user.getSavedVehicles().isEmpty()) {
            for(Vehicle v : user.getSavedVehicles()) {
                Log.d(TAG, "Vehicle that is saved is: " + v.getBrand());
            }
            allVehicles.addAll(user.getSavedVehicles());
        }

        try {
            Context context = getContext();
            VehicleSavedAdapter adapter = new VehicleSavedAdapter(allVehicles, context);

            gridLayout = view.findViewById(R.id.grid_layout2);
            gridLayout.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            gridLayout.setAdapter(adapter);
        } catch (NullPointerException e) {
            Log.d(TAG, "Error trying to access the adapter with null error");
        }
    }
}
