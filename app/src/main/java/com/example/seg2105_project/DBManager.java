package com.example.seg2105_project;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

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

    @SuppressLint("Range")
    public ArrayList<HashMap<String, Object>> getRejectedAppointments(){
        Cursor rows = db.rawQuery("SELECT * FROM patient_appointments WHERE rejected = 1;", null);
        ArrayList<HashMap<String, Object>> rejectedAppointments = new ArrayList<>();
        HashMap<String, Object> currentAppointment;
        while(rows.moveToNext()){
            currentAppointment = new HashMap<>();
            currentAppointment.put("patient_id", rows.getInt(rows.getColumnIndex("patient_id")));
            currentAppointment.put("doctor_id", rows.getInt(rows.getColumnIndex("doctor_id")));
            currentAppointment.put("start_time", rows.getInt(rows.getColumnIndex("start_time")));
            currentAppointment.put("end_time", rows.getInt(rows.getColumnIndex("end_time")));
            rejectedAppointments.add(currentAppointment);
        }
        rows.close();
        return rejectedAppointments;
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, Object>> getApprovedAppointments(){
        Cursor rows = db.rawQuery("SELECT * FROM patient_appointments WHERE rejected = 0;", null);
        ArrayList<HashMap<String, Object>> approvedAppointments = new ArrayList<>();
        HashMap<String, Object> currentAppointment;
        while(rows.moveToNext()){
            currentAppointment = new HashMap<>();
            currentAppointment.put("patient_id", rows.getInt(rows.getColumnIndex("patient_id")));
            currentAppointment.put("doctor_id", rows.getInt(rows.getColumnIndex("doctor_id")));
            currentAppointment.put("start_time", rows.getInt(rows.getColumnIndex("start_time")));
            currentAppointment.put("end_time", rows.getInt(rows.getColumnIndex("end_time")));
            approvedAppointments.add(currentAppointment);
        }
        rows.close();
        return approvedAppointments;
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, Object>> getPendingAppointments(){
        Cursor rows = db.rawQuery("SELECT * FROM patient_appointments WHERE rejected = NULL;", null);
        ArrayList<HashMap<String, Object>> pendingAppointments = new ArrayList<>();
        HashMap<String, Object> currentAppointment;
        while(rows.moveToNext()){
            currentAppointment = new HashMap<>();
            currentAppointment.put("patient_id", rows.getInt(rows.getColumnIndex("patient_id")));
            currentAppointment.put("doctor_id", rows.getInt(rows.getColumnIndex("doctor_id")));
            currentAppointment.put("start_time", rows.getInt(rows.getColumnIndex("start_time")));
            currentAppointment.put("end_time", rows.getInt(rows.getColumnIndex("end_time")));
            pendingAppointments.add(currentAppointment);
        }
        rows.close();
        return pendingAppointments;
    }

    public void cancelAppointment(int appointmentID){
        db.execSQL("UPDATE patient_appointments SET rejected = 1 WHERE id = ?;", new Object[]{appointmentID});
    }
    public void approveAppointment(int appointmentID){
        db.execSQL("UPDATE patient_appointments SET rejected = 0 WHERE id = ?;", new Object[]{appointmentID});
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, Object>> getShifts(int doctorID){
        Cursor rows = db.rawQuery("SELECT * FROM shifts WHERE doctor_id = ?;", new String[]{Integer.toString(doctorID)});
        ArrayList<HashMap<String, Object>> shifts = new ArrayList<>();
        HashMap<String, Object> currentShift;
        while(rows.moveToNext()){
            currentShift = new HashMap<>();
            currentShift.put("start_time", rows.getInt(rows.getColumnIndex("start_time")));
            currentShift.put("end_time", rows.getInt(rows.getColumnIndex("end_time")));
            shifts.add(currentShift);
        }
        rows.close();
        return shifts;
    }

    @SuppressLint("Range")
    public boolean createShift(int doctorID, int patientID, String start, String end) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startUnixTime = dateFormat.parse(start).getTime();
        long endUnixTime = dateFormat.parse(end).getTime();
        long currentAppointmentStart, currentAppointmentEnd;
        boolean overlaping = false;

        Cursor rows = db.rawQuery("SELECT start_time, end_time FROM patient_appointments WHERE rejected = 0;", null);
        while(rows.moveToNext()){
            currentAppointmentStart = rows.getInt(rows.getColumnIndex("start_time"));
            currentAppointmentEnd =  rows.getInt(rows.getColumnIndex("end_time"));
            if ((endUnixTime >= currentAppointmentStart && endUnixTime <= currentAppointmentEnd) || (startUnixTime >= currentAppointmentStart && startUnixTime <= currentAppointmentEnd)) {
                overlaping = true;
                break;
            }
        }
        rows.close();

        if (!overlaping) {
            db.execSQL("INSERT INTO patient_appointments (patient_id, doctor_id, start_time, end_time) VALUES (?,?,?,?)", new Object[]{patientID, doctorID, startUnixTime, endUnixTime});
        }

        return overlaping;
    }
    public void deleteShift(int id){
        db.execSQL("DELETE FROM shifts WHERE id = ?;", new Object[]{id});
    }

    /**
     * return info about a user
     * */
    @SuppressLint("Range")
    public HashMap<String, Object> getUser(int id){
        Cursor rows = db.rawQuery("SELECT * FROM users WHERE id = ?;", new String[]{Integer.toString(id)});
        rows.moveToNext();
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstname", rows.getString(rows.getColumnIndex("firstname")));
        userInfo.put("lastname", rows.getString(rows.getColumnIndex("lastname")));
        userInfo.put("email", rows.getString(rows.getColumnIndex("email")));
        userInfo.put("telephone", rows.getString(rows.getColumnIndex("telephone")));
        userInfo.put("address", rows.getString(rows.getColumnIndex("address")));
        userInfo.put("health_card_number", rows.getString(rows.getColumnIndex("health_card_number")));
        userInfo.put("employee_number", rows.getInt(rows.getColumnIndex("employee_number")));
        userInfo.put("specialties", rows.getString(rows.getColumnIndex("specialties")));
        userInfo.put("user_type", UserType.fromString(rows.getString(rows.getColumnIndex("user_type"))));
        return userInfo;
    }
}
