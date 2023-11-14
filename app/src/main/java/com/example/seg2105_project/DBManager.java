package com.example.seg2105_project;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DBManager {
    private static DBHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
    }

    public DBManager open() throws SQLException { // opens connection with the DB
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { // closes connection with the DB
        db.close();
    }

    public void sendPatientRegistrationRequest(Patient patient) {
        String firstName = patient.getFirstName();
        String lastName = patient.getLastName();
        String email = patient.getEmail();
        String password = patient.getPassword();
        String telephone = patient.getTelephone();
        String address = patient.getAddress();
        String healthCardNumber = patient.getHealthCardNumber();
        String userType = patient.getUserType().type;

        Object[] valuesToInsert = new Object[]{firstName, lastName, email, password, telephone, address, healthCardNumber, userType};

        db.execSQL("INSERT INTO registration_requests (" +
                    "firstname, " +
                    "lastname, " +
                    "email, " +
                    "password, " +
                    "telephone, " +
                    "address, " +
                    "health_card_number, " +
                    "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert);
    }

    public void sendDoctorRegistrationRequest(Doctor doctor) {
        String firstName = doctor.getFirstName();
        String lastName = doctor.getLastName();
        String email = doctor.getEmail();
        String password = doctor.getPassword();
        String telephone = doctor.getTelephone();
        String address = doctor.getAddress();
        long employeeNumber = doctor.getEmployeeNumber();
        String specialties = doctor.getSpecialties();
        String userType = doctor.getUserType().type;

        Object[] valuesToInsert = new Object[]{firstName, lastName, email, password, telephone, address, employeeNumber, specialties, userType};

        db.execSQL("INSERT INTO registration_requests (" +
                    "firstname, " +
                    "lastname, " +
                    "email, " +
                    "password, " +
                    "telephone, " +
                    "address, " +
                    "employee_number, " +
                    "specialties, " +
                    "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert);
    }

    public void approveRegistration(int requestID) {
        String[] columnsToGet = new String[]{"firstname", "lastname", "email", "password", "telephone", "address", "user_type"};
        Cursor cursor = db.query("registration_requests", columnsToGet, "id = ?", new String[]{Integer.toString(requestID)}, null, null ,null);
        cursor.moveToFirst();

        // getting a hash-map of the user's info
        Map<String, Object> userInfo = new HashMap<>();
        int currentColumnIndex;
        for (String col : columnsToGet) {
            currentColumnIndex = cursor.getColumnIndex(col);
            userInfo.put(col, cursor.getString(currentColumnIndex));
        }

        UserType userType = UserType.fromString(userInfo.get("user_type").toString());
        String[] additionalColumnsToGet;
        if (userType.equals(UserType.DOCTOR)) { // DOCTOR
            additionalColumnsToGet = new String[]{"employee_number", "specialties"};
        } else { // PATIENT
            additionalColumnsToGet = new String[]{"health_card_number"};
        }

        cursor = db.query("registration_requests", additionalColumnsToGet, "id = ?", new String[]{Integer.toString(requestID)}, null, null, null);
        cursor.moveToFirst();
        for (String col : additionalColumnsToGet) {
            currentColumnIndex = cursor.getColumnIndex(col);
            userInfo.put(col, cursor.getString(currentColumnIndex));
        }
        cursor.close();

        // building the query to insert the user's info into the 'users' table
        ArrayList<Object> valuesToInsert = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(); // using StringBuilder instead of String since strings in Java are immutable (StringBuilder is faster)
        queryBuilder.append("INSERT INTO users (");

        for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
            queryBuilder.append(entry.getKey() + ", ");
            valuesToInsert.add(entry.getValue());
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // remove the ", " from the end of the query at this point

        queryBuilder.append(") VALUES (");
        for (int i = 0; i < valuesToInsert.size(); i++) {
            queryBuilder.append("?,");
        }
        queryBuilder.delete(queryBuilder.length() - 1, queryBuilder.length()); // remove the last comma
        queryBuilder.append(")");

        db.execSQL(queryBuilder.toString(), valuesToInsert.toArray()); // execute the built query

        db.execSQL("DELETE FROM registration_requests WHERE id=?", new Object[]{requestID});
    }

    public void rejectRegistrationRequest(int requestID) {
        db.execSQL("UPDATE registration_requests SET rejected = 1 WHERE id = ?;", new Object[]{requestID});
    }

    @SuppressLint("Range")
    public ArrayList<Map<String, Object>> getRejectedRegistrationRequests() {
        String[] columnsToGet = new String[]{"id","firstname", "lastname", "email", "telephone", "address", "user_type", "health_card_number", "employee_number", "specialties"}; // exclude 'password'
        Cursor cursor = db.query("registration_requests", columnsToGet, "rejected = 1", null, null, null, null);
        int currentColumnIndex;
        ArrayList<Map<String, Object>> rejectedUsers = new ArrayList<>();
        Map<String, Object> currentRejectedUser;
        String userType;

        while (cursor.moveToNext()) {
            currentRejectedUser = new HashMap<>();
            userType = cursor.getString(cursor.getColumnIndex("user_type"));

            for (String col : columnsToGet) {
                currentColumnIndex = cursor.getColumnIndex(col);
                currentRejectedUser.put(col, cursor.getString(currentColumnIndex));
            }

            if (userType.equals(UserType.DOCTOR.toString())) {
                currentRejectedUser.remove("health_card_number");
            } else {
                currentRejectedUser.remove("specialties");
                currentRejectedUser.remove("employee_number");
            }

            rejectedUsers.add(currentRejectedUser);
        }

        return rejectedUsers;
    }

    @SuppressLint("Range")
    public ArrayList<Map<String, Object>> getRegistrationRequests(){
        Cursor rows = db.rawQuery("SELECT * FROM registration_requests WHERE rejected=0", null);

        ArrayList<Map<String, Object>> users = new ArrayList<>();

        String[] columns = new String[]{"id", "firstname", "lastname", "email", "telephone", "address", "user_type", "health_card_number", "employee_number", "specialties"};
        Map<String, Object> request;
        String userType;
        while(rows.moveToNext()){
            request = new HashMap<>();
            userType = rows.getString(rows.getColumnIndex("user_type"));

            for(String col:columns){
                int columnIndex = rows.getColumnIndex(col);
                request.put(col, rows.getString(columnIndex));
            }

            if(userType.equals(UserType.DOCTOR.toString())){
                request.remove("health_card_number");
            }else{
                request.remove("specialties");
                request.remove("employee_number");
            }

            users.add(request);
        }
        rows.close();

        return users;
    }

    public Map<String, String> userExists(String email, String password) throws IllegalArgumentException {
        String[] columnsToRetrieve = new String[]{"user_type"};
        String[] valuesToSearch = new String[]{email, password}; // values to search for in the DB
        boolean userExists;
        Cursor cursor;
        HashMap<String, String> user= new HashMap<>();

        /********* Look in user's table **************/
        cursor = db.query("users", columnsToRetrieve, "email = ? AND password = ?", valuesToSearch, null, null ,null);
        userExists = cursor.moveToFirst();
        if (userExists) {
            int columnIndex = cursor.getColumnIndex("user_type");
            user.put("user_type", cursor.getString(columnIndex));
            user.put("approved", "true");
        } else {

            /********* Look in registration_requests' table **************/
            cursor = db.query("registration_requests", new String[]{"user_type", "rejected"}, "email = ? AND password = ?", valuesToSearch, null, null, null);
            userExists = cursor.moveToFirst();
            if (userExists) {
                int user_type_i = cursor.getColumnIndex("user_type");
                int rejected_i =  cursor.getColumnIndex("rejected");

                user.put("user_type", cursor.getString(user_type_i));
                user.put("approved", "false");

                int rejected = cursor.getInt(rejected_i);
                user.put("rejected", (rejected != 0)?"true":"false");


            }
        }
        cursor.close();
        return user;
    }

    /************ Deliverable 3 **************/
    ArrayList<HashMap<String, Object>> getRejectedAppointments(){
        return null;
    }
    ArrayList<HashMap<String, Object>> getApprovedAppointments(){
        return null;
    }
    ArrayList<HashMap<String, Object>> getPendingAppointments(){
        return null;
    }

    void cancelAppointment(int appointmentID){

    }
    void approveAppointment(int appointmentID){

    }

    ArrayList<HashMap<String, Object>> getShifts(int doctorID){
        return null;
    }

    void createShift(int doctorID, LocalDateTime start, LocalDateTime end){

    }
    void deleteShift(int id){

    }
}
