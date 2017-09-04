package com.darvin.cmritattendancemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.darvin.cmritattendancemanager.db.StudentContract.*;
import static com.darvin.cmritattendancemanager.db.StudentContract.AttendanceEntry.ATTENDANCE_TABLE_NAME;
import static com.darvin.cmritattendancemanager.db.StudentContract.AttendanceEntry.COLUMN_ATTENDED_CLASSES;
import static com.darvin.cmritattendancemanager.db.StudentContract.AttendanceEntry.COLUMN_SUBJECT;
import static com.darvin.cmritattendancemanager.db.StudentContract.AttendanceEntry.COLUMN_TOTAL_CLASSES;
import static com.darvin.cmritattendancemanager.db.StudentContract.InternalEntry.INTERNALS_TABLE_NAME;
import static com.darvin.cmritattendancemanager.db.StudentContract.StudentEntry.*;


public class StudentDbHelper extends SQLiteOpenHelper {


    /** Name of the database file */
    private static final String DATABASE_NAME = "student.db";
    private static int DATABASE_VERSION = 1;
    public StudentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STUDENT_TABLE =  "CREATE TABLE " + TABLE_NAME + " ("
                + StudentEntry._ID + " INTEGER AUTO INCREMENT, "
                + COLUMN_EMAIL + " TEXT NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_USERID + " TEXT NOT NULL, "
                + COLUMN_GENDER + " TEXT NOT NULL, "
                + COLUMN_PERCENTAGE + " TEXT, "
                + COLUMN_USN + " TEXT, "
                + COLUMN_PASSWORD + " TEXT NOT NULL );";
        // Execute the SQL statement
        String SQL_CREATE_ATTENDANCE_TABLE = "CREATE TABLE " + ATTENDANCE_TABLE_NAME + " ("
                + AttendanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SUBJECT + " TEXT NOT NULL, "
                + COLUMN_ATTENDED_CLASSES + " TEXT NOT NULL, "
                + COLUMN_TOTAL_CLASSES + " TEXT NOT NULL, "
                + AttendanceEntry.COLUMN_USN + " TEXT NOT NULL );"
                ;
        String SQL_CREATE_INTERNAL_TABLE = "CREATE TABLE " + INTERNALS_TABLE_NAME + " ("
                +InternalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InternalEntry.COLUMN_SUBJECT + " TEXT NOT NULL, "
                + InternalEntry.COLUMN_FIRST_INTERNAL + " TEXT NOT NULL, "
                + InternalEntry.COLUMN_SECOND_INTERNAL + " TEXT NOT NULL, "
                + InternalEntry.COLUMN_THIRD_INTERNAL + " TEXT NOT NULL, "
                + InternalEntry.COLUMN_TOTAL_MARKS + " TEXT NOT NULL, "
                + InternalEntry.COLUMN_USN + " TEXT NOT NULL );"
                ;
        System.out.println("Table creation table statement:-->>"+SQL_CREATE_STUDENT_TABLE);
        System.out.println("Table creation attendance statement:-->>"+SQL_CREATE_ATTENDANCE_TABLE);
        System.out.println("Table creation internal statement:-->>"+SQL_CREATE_INTERNAL_TABLE);

        db.execSQL(SQL_CREATE_ATTENDANCE_TABLE);
        db.execSQL(SQL_CREATE_INTERNAL_TABLE);
        db.execSQL(SQL_CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INTERNALS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ATTENDANCE_TABLE_NAME);
        onCreate(db);
    }
}
