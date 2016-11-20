package com.example.hiral.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
/**
 * Created by Hiral on 7/28/2016.
 */
public class LoginDataBaseAdapter {
        static final String DATABASE_NAME = "login.db";
        static final int DATABASE_VERSION = 1;
        public static final int NAME_COLUMN = 1;

        // SQL Statement to create a new database.
        static final String DATABASE_CREATE = "create table "+"LOGIN"+
                "( " +"ID"+" integer primary key autoincrement,"+ "USERNAME  text,PASSWORD text, LATITUDE text, LONGITUDE text); ";

        // Variable to hold the database instance
        public  SQLiteDatabase db;
        // Context of the application using the database.
        private final Context context;
        // Database open/upgrade helper
        private DataBaseHelper dbHelper;
        public  LoginDataBaseAdapter(Context _context)
        {
            context = _context;
            dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public  LoginDataBaseAdapter open() throws SQLException
        {
            db = dbHelper.getWritableDatabase();
            return this;
        }
        public void close()
        {
            db.close();
        }

        public  SQLiteDatabase getDatabaseInstance()
        {
            return db;
        }

        public void insertEntry(String userName,String password,String lat,String lng)
        {
            ContentValues newValues = new ContentValues();
            // Assigning values for each row.
            newValues.put("USERNAME",userName);
            newValues.put("PASSWORD",password);
            newValues.put("LATITUDE",lat);
            newValues.put("LONGITUDE",lng);

            // Insert the row into your table
            db.insert("LOGIN", null, newValues);
        }
        public int deleteEntry(String UserName)
        {
            String where="USERNAME=?";
            int numberOFEntriesDeleted= db.delete("LOGIN", where, new String[]{UserName}) ;
            return numberOFEntriesDeleted;
        }
        public String getSingleEntry(String userName)
        {
            Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
            if(cursor.getCount()<1) // UserName Not Exist
            {
                cursor.close();
                return "NOT EXIST";
            }
            cursor.moveToFirst();
            String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
            cursor.close();
            return password;
        }

        public String getLng(String userName){
            Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
            if(cursor.getCount()<1) // UserName Not Exist
            {
                cursor.close();
                return "NOT EXIST";
            }
            cursor.moveToFirst();
            String longitude= cursor.getString(cursor.getColumnIndex("LONGITUDE"));
            cursor.close();
            return longitude;
        }

    public String getLat(String userName){
        Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String latitude= cursor.getString(cursor.getColumnIndex("LATITUDE"));
        cursor.close();
        return latitude;
    }

    }

