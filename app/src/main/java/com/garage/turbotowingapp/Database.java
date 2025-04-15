package com.garage.turbotowingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TurboTowing.db";
    private static final int DATABASE_VERSION = 10;
    private static final String TAG = "Database";

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_FIRSTNAME = "firstname";
    public static final String COLUMN_USER_LASTNAME = "lastname";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_PIC = "profile_picture"; // optional
    public static final String COLUMN_USER_PASSWORD = "password"; // optional
    public static final String COLUMN_USER_ZIPCODE = "zipcode"; // optional
    public static final String COLUMN_USER_PHONENUMBER = "phone_number";


    // Appointment Table
    public static final String TABLE_VEHICLES = "vehicles";
    public static final String COLUMN_VEHICLE_ID = "vehicle_id";
    public static final String COLUMN_VEHICLE_OWNER_ID = "owner_id";
    public static final String COLUMN_VEHICLE_BRAND = "brand";
    public static final String COLUMN_VEHICLE_MODEL = "model";
    public static final String COLUMN_VEHICLE_AGE = "age";
    public static final String COLUMN_VEHICLE_ADDRESS = "address";
    public static final String COLUMN_VEHICLE_DAMAGE = "damage";
    public static final String COLUMN_VEHICLE_ENGINE = "engine";
    public static final String COLUMN_VEHICLE_DIS = "day_in_storage"; // days in storage
    public static final String COLUMN_VEHICLE_DESCRIPTION = "description";
    public static final String COLUMN_VEHICLE_DATE_POSTED = "date_posted";

    public static final String TABLE_VEHICLE_IMAGES = "vehicle_images";
    public static final String COLUMN_VEHICLE_PIC_ID = "vehicle_pic_id";
    public static final String COLUMN_IMAGE_DATA = "image_data";

    public static final String TABLE_USER_VEHICLES = "user_vehicles";
    public static final String COLUMN_USER_VEHICLE_USER_ID = "user_id";
    public static final String COLUMN_USER_VEHICLE_VEHICLE_ID = "vehicle_saved_id";

    // SQL for creating tables
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_FIRSTNAME + " TEXT, "
            + COLUMN_USER_LASTNAME + " TEXT, "
            + COLUMN_USER_PIC + " BLOB, "
            + COLUMN_USER_ZIPCODE + " TEXT, "
            + COLUMN_USER_PHONENUMBER + " TEXT, "
            + COLUMN_USER_EMAIL + " TEXT, "
            + COLUMN_USER_PASSWORD + " TEXT);";

    private static final String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_VEHICLES + " ("
            + COLUMN_VEHICLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_VEHICLE_AGE + " INTEGER, "
            + COLUMN_VEHICLE_OWNER_ID + " INTEGER, "
            + COLUMN_VEHICLE_BRAND + " TEXT, "
            + COLUMN_VEHICLE_MODEL + " TEXT, "
            + COLUMN_VEHICLE_DAMAGE + " TEXT, "
            + COLUMN_VEHICLE_ENGINE + " TEXT, "
            + COLUMN_VEHICLE_ADDRESS + " TEXT, "
            + COLUMN_VEHICLE_DIS + " INTEGER, "
            + COLUMN_VEHICLE_DESCRIPTION + " TEXT, "
            + COLUMN_VEHICLE_DATE_POSTED + " TEXT, "  // Changed DATE to TEXT (ISO-8601 format)
            + "FOREIGN KEY(" + COLUMN_VEHICLE_OWNER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE"
            + ");";  // Added missing closing parenthesis and semicolon


    private static final String CREATE_VEHICLE_IMAGES_TABLE = "CREATE TABLE " + TABLE_VEHICLE_IMAGES + " ("
            + COLUMN_VEHICLE_PIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_VEHICLE_ID + " INTEGER, "  // Add this line to define vehicle_id
            + COLUMN_IMAGE_DATA + " BLOB, "
            + "FOREIGN KEY(" + COLUMN_VEHICLE_ID + ") REFERENCES " + TABLE_VEHICLES + "(" + COLUMN_VEHICLE_ID + "))";

    public static final String CREATE_USER_VEHICLES_TABLE =
            "CREATE TABLE " + TABLE_USER_VEHICLES + " (" +
                    COLUMN_USER_VEHICLE_USER_ID + " INTEGER," +
                    COLUMN_USER_VEHICLE_VEHICLE_ID + " INTEGER," +
                    "PRIMARY KEY (" + COLUMN_USER_VEHICLE_USER_ID + ", " + COLUMN_USER_VEHICLE_VEHICLE_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_USER_VEHICLE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_USER_VEHICLE_VEHICLE_ID + ") REFERENCES " + TABLE_VEHICLES + "(" + COLUMN_VEHICLE_ID + ")" +
                    ");";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_VEHICLE_TABLE);
        db.execSQL(CREATE_VEHICLE_IMAGES_TABLE);
        db.execSQL(CREATE_USER_VEHICLES_TABLE);
        Log.d("DataBase", "Attempting to create database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_VEHICLES);
        onCreate(db);
    }


    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Begin a transaction for better performance and safety
        try {
            db.delete(TABLE_USERS, null, null);    // Clear all data from the patients table
            db.delete(TABLE_VEHICLES, null, null); // Clear all data from the appointments table
            db.delete(TABLE_VEHICLE_IMAGES, null, null);
            db.delete(TABLE_USER_VEHICLES, null, null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void clearVehicles(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Begin a transaction for better performance and safety
        try {   // Clear all data from the patients table
            db.delete(TABLE_VEHICLES, null, null); // Clear all data from the appointments table
            db.delete(TABLE_VEHICLE_IMAGES, null, null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_FIRSTNAME, user.getFirstName());
        values.put(COLUMN_USER_LASTNAME, user.getLastName());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONENUMBER, user.getPhoneNumber());
        values.put(COLUMN_USER_PIC, user.getProfilePic());
        values.put(COLUMN_USER_ZIPCODE, user.getZipcode());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FIRSTNAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_LASTNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONENUMBER));
                String zipcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ZIPCODE));

                byte[] profilePicBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_USER_PIC));

                User user = new User(firstName, lastName, password, email, phoneNumber, profilePicBytes);
                user.setZipcode(zipcode);
                user.setUserId(id);

                List<Vehicle> userVehicles = getAllVehicles(user.getUserId());

                List<Vehicle> savedVehicles = getUserSavedVehicles(user.getUserId());

                Log.d(TAG,"For now: " + user.getEmail() + " " + user.getPassword());

                if(!savedVehicles.isEmpty()){

                    for(Vehicle v : savedVehicles){
                        Log.d(TAG,"user's vehicles are " + v.getBrand() + " vehicleId: "+ v.getUuid() + " user: "+user.getUserId());
                    }

                   user.getSavedVehicles().addAll(savedVehicles);

                }

                if(!userVehicles.isEmpty()){
                    for(Vehicle vehicle: userVehicles){
                        vehicle.setOwner(user);
                       user.getVehiclelist().add(vehicle);
                    }
                }

                userList.add(user);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return userList;
    }

    public void deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_FIRSTNAME, user.getFirstName());
        values.put(COLUMN_USER_LASTNAME, user.getLastName());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONENUMBER, user.getPhoneNumber());
        values.put(COLUMN_USER_ZIPCODE, user.getZipcode());
        values.put(COLUMN_USER_PIC, user.getProfilePic());

        db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getUserId())});
        db.close();
    }

    public void insertVehicle(Vehicle vehicle, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vehicleValues = new ContentValues();

        vehicleValues.put(COLUMN_VEHICLE_OWNER_ID, user.getUserId());
        vehicleValues.put(COLUMN_VEHICLE_BRAND, vehicle.getBrand());
        vehicleValues.put(COLUMN_VEHICLE_MODEL, vehicle.getModel());
        vehicleValues.put(COLUMN_VEHICLE_AGE, vehicle.getAge());
        vehicleValues.put(COLUMN_VEHICLE_ADDRESS, vehicle.getLocation());
        vehicleValues.put(COLUMN_VEHICLE_DAMAGE, vehicle.getDamage());
        vehicleValues.put(COLUMN_VEHICLE_ENGINE, vehicle.getEngine());
        vehicleValues.put(COLUMN_VEHICLE_DIS, vehicle.getDaysInStorage());
        vehicleValues.put(COLUMN_VEHICLE_DESCRIPTION, vehicle.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

//        String datePosted = sdf.format(vehicle.getDatePosted());
        vehicleValues.put(COLUMN_VEHICLE_DATE_POSTED, vehicle.getDatePosted().toString());

        long vehicleId = db.insert(TABLE_VEHICLES, null, vehicleValues);

        if (vehicleId != -1 && vehicle.getImageBytesList() != null) {
            for (byte[] image : vehicle.getImageBytesList()) {
                insertVehicleImage(db, vehicleId, image);
            }
        }

        db.close();
    }

    private void insertVehicleImage(SQLiteDatabase db, long vehicleId, byte[] image) {


        ContentValues imageValues = new ContentValues();
        imageValues.put(COLUMN_VEHICLE_ID, vehicleId);
        byte[] compressed_image = compressImage(image,50);
        imageValues.put(COLUMN_IMAGE_DATA, compressed_image);

        db.insert(TABLE_VEHICLE_IMAGES, null, imageValues);
    }

    public void updateVehicle(Vehicle vehicle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vehicleValues = new ContentValues();

        vehicleValues.put(COLUMN_VEHICLE_BRAND, vehicle.getBrand());
        vehicleValues.put(COLUMN_VEHICLE_MODEL, vehicle.getModel());
        vehicleValues.put(COLUMN_VEHICLE_AGE, vehicle.getAge());
        vehicleValues.put(COLUMN_VEHICLE_ADDRESS, vehicle.getLocation());
        vehicleValues.put(COLUMN_VEHICLE_DAMAGE, vehicle.getDamage());
        vehicleValues.put(COLUMN_VEHICLE_ENGINE, vehicle.getEngine());
        vehicleValues.put(COLUMN_VEHICLE_DIS, vehicle.getDaysInStorage());
        vehicleValues.put(COLUMN_VEHICLE_DESCRIPTION, vehicle.getDescription());
        vehicleValues.put(COLUMN_VEHICLE_DATE_POSTED, vehicle.getDatePosted().toString());

        // Updating the vehicle details
        int rowsAffected = db.update(TABLE_VEHICLES, vehicleValues, COLUMN_VEHICLE_ID + " = ?",
                new String[]{String.valueOf(vehicle.getUuid())});

        if (rowsAffected > 0 && vehicle.getImageBytesList() != null) {
             //Delete old images associated with this vehicle
            db.delete(TABLE_VEHICLE_IMAGES, COLUMN_VEHICLE_ID + " = ?",
                    new String[]{String.valueOf(vehicle.getUuid())});

             //Insert new images
            for (byte[] image : vehicle.getImageBytesList()) {
                insertVehicleImage(db,  vehicle.getUuid(), image);
            }
        }

        db.close();
    }

    public List<Vehicle> getAllVehicles(long userId) {
        List<Vehicle> vehicleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String vehicleQuery = "SELECT * FROM " + TABLE_VEHICLES + " WHERE " + COLUMN_VEHICLE_OWNER_ID + " = ?";
        Cursor vehicleCursor = db.rawQuery(vehicleQuery, new String[]{String.valueOf(userId)});

        if (vehicleCursor.moveToFirst()) {
            do {
                long vehicleId = vehicleCursor.getLong(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ID));
                String brand = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_BRAND));
                String model = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_MODEL));
                int age = vehicleCursor.getInt(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_AGE));
                String address = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ADDRESS));
                String damage = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DAMAGE));
                String engine = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ENGINE));
                int daysInStorage = vehicleCursor.getInt(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DIS));
                String description = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DESCRIPTION));
                String datePostedString = vehicleCursor.getString(vehicleCursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DATE_POSTED));

                Date datePosted = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    datePosted = sdf.parse(datePostedString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Retrieve images for this vehicle
                List<byte[]> imageList = getVehicleImages(db, vehicleId);

                Vehicle vehicle = new Vehicle(brand, model, age, damage, engine, daysInStorage, description,address);
                vehicle.setUuid(vehicleId);
                LocalDate date = LocalDate.parse(datePostedString);
                vehicle.setDatePosted(date);
                vehicle.getImageBytesList().addAll(imageList);

                vehicleList.add(vehicle);
            } while (vehicleCursor.moveToNext());
        }

        vehicleCursor.close();
        db.close();

        return vehicleList;
    }

    public List<byte[]> getVehicleImages(SQLiteDatabase db, long vehicleId) {
        List<byte[]> imageList = new ArrayList<>();
        String imageQuery = "SELECT " + COLUMN_IMAGE_DATA + " FROM " + TABLE_VEHICLE_IMAGES +
                " WHERE " + COLUMN_VEHICLE_ID + " = ?";

        Cursor imageCursor = db.rawQuery(imageQuery, new String[]{String.valueOf(vehicleId)});

        if (imageCursor.moveToFirst()) {
            do {
                byte[] imageBytes = imageCursor.getBlob(imageCursor.getColumnIndexOrThrow(COLUMN_IMAGE_DATA));

                // Compress the image to reduce size
                byte[] compressedBytes = compressImage(imageBytes, 50); // Compress to 50% quality

                imageList.add(compressedBytes);
            } while (imageCursor.moveToNext());
        }

        imageCursor.close();
        return imageList;
    }
    private byte[] compressImage(byte[] imageData, int quality) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        if (bitmap == null) {
            return imageData; // Return original if decoding fails
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public void deleteVehicle(long vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VEHICLES, COLUMN_VEHICLE_ID + " = ?", new String[]{String.valueOf(vehicleId)});
        db.close();
    }

    public long insertUserVehicle(long userId, long vehicleId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the user already saved this vehicle
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_VEHICLES +
                        " WHERE " + COLUMN_USER_VEHICLE_USER_ID + " = ? AND " +
                        COLUMN_USER_VEHICLE_VEHICLE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(vehicleId)});

        if (cursor.getCount() > 0) {
            Log.d(TAG, "Vehicle already saved by this user, skipping insert.");
            cursor.close();
            db.close();
            return -1; // Indicate duplicate entry
        }
        cursor.close();

        // Insert the new saved vehicle
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_VEHICLE_USER_ID, userId);
        values.put(COLUMN_USER_VEHICLE_VEHICLE_ID, vehicleId);
        long newRowId = db.insert(TABLE_USER_VEHICLES, null, values);
        db.close();

        return newRowId;
    }

    public int removeUserVehicle(long userId, long vehicleId) {
        Log.d(TAG, "Attempting to remove vehicle: userId=" + userId + ", vehicleId=" + vehicleId);

        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_VEHICLE_USER_ID + " = ? AND " +
                COLUMN_USER_VEHICLE_VEHICLE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(vehicleId)};

        // Check if the record exists before deleting
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_VEHICLES +
                " WHERE " + COLUMN_USER_VEHICLE_USER_ID + " = ? AND " +
                COLUMN_USER_VEHICLE_VEHICLE_ID + " = ?", selectionArgs);

        if (cursor.getCount() == 0) {
            Log.d(TAG, "No matching record found, delete will not proceed.");
            cursor.close();
            db.close();
            return 0; // No deletion happened
        }

        cursor.close();

        // Now perform the deletion
        int deletedRows = db.delete(TABLE_USER_VEHICLES, selection, selectionArgs);
        Log.d(TAG, "Deleted rows count: " + deletedRows);

        db.close();
        return deletedRows;
    }


    public List<Vehicle> getUserSavedVehicles(long userId) {
        List<Vehicle> savedVehicles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch only vehicles saved by the given user
        String query = "SELECT v.* FROM " + TABLE_VEHICLES + " v " +
                "INNER JOIN " + TABLE_USER_VEHICLES + " sv " +
                "ON v." + COLUMN_VEHICLE_ID + " = sv." + COLUMN_USER_VEHICLE_VEHICLE_ID +
                " WHERE sv." + COLUMN_USER_VEHICLE_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int vehicleId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ID));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_BRAND));
                String model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_MODEL));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_AGE));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ADDRESS));
                String damage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DAMAGE));
                String engine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ENGINE));
                int daysInStorage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DIS));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DESCRIPTION));
                String datePosted = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_DATE_POSTED));

                Vehicle vehicle = new Vehicle(brand, model, age, damage, engine, daysInStorage, description, address);

                vehicle.setUuid(vehicleId);
                // Convert date from String to LocalDate
                LocalDate date = LocalDate.parse(datePosted);
                vehicle.setDatePosted(date);

                // Fetch and set images for this vehicle
                for (byte[] imgData : getVehicleImages(db, vehicleId)) {
                    vehicle.getImageBytesList().add(imgData);
                }

                savedVehicles.add(vehicle);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return savedVehicles;
    }


}
