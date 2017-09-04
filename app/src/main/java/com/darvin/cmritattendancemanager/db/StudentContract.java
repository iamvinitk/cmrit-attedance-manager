package com.darvin.cmritattendancemanager.db;

import android.net.Uri;
import android.provider.BaseColumns;


public final class StudentContract {
    //Default Constructor to avoid misuse
    private StudentContract(){}

    public static final String CONTENT_AUTHORITY = "com.darvin.cmritattendancemanager";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ATTENDANCE = "attendance";
    public static final String PATH_INTERNALS = "internals";
    public static final class StudentEntry implements BaseColumns {
        //Table Name
        public final static String TABLE_NAME = "students";
        //Fields of the table
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_USERID = "userid";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_PERCENTAGE = "percentage";
        public static final String COLUMN_USN = "usn";
    }
    public static final class AttendanceEntry implements BaseColumns {
        public static final Uri ATTENDANCE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ATTENDANCE)
                .build();
        //Table Name
        public final static String ATTENDANCE_TABLE_NAME = "attendance";
        //Fields of the table
        public static final String COLUMN_USN = "usn";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_TOTAL_CLASSES = "totalclasses";
        public static final String COLUMN_ATTENDED_CLASSES = "attendedclasses";
    }
    public static final class InternalEntry implements BaseColumns {
        public static final Uri INTERNALS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INTERNALS)
                .build();
        //Table Name
        public final static String INTERNALS_TABLE_NAME = "internals";
        //Fields of the table
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_FIRST_INTERNAL = "firstinternal";
        public static final String COLUMN_SECOND_INTERNAL = "secondinternal";
        public static final String COLUMN_THIRD_INTERNAL = "thirdinternal";
        public static final String COLUMN_TOTAL_MARKS = "totalmarks";
        public static final String COLUMN_USN = "usn";
    }
}
