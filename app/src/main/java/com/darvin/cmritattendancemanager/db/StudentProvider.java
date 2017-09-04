package com.darvin.cmritattendancemanager.db;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class StudentProvider extends ContentProvider {
    public static final int CODE_ATTENDANCE = 111;
    public static final int CODE_INTERNALS = 112;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private StudentDbHelper mStudentDbHelper;
    public StudentProvider() {
    }

    @Override
    public boolean onCreate() {
        mStudentDbHelper = new StudentDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mStudentDbHelper.getWritableDatabase();
        int rowsInserted;
        switch (sUriMatcher.match(uri)) {
            case CODE_ATTENDANCE:
                rowsInserted = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(StudentContract.AttendanceEntry.ATTENDANCE_TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                System.out.print("Rows Inserted:" + rowsInserted);
                return rowsInserted;
            case CODE_INTERNALS:
                db.beginTransaction();
                rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(StudentContract.InternalEntry.INTERNALS_TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                System.out.print("Rows Inserted:" + rowsInserted);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_ATTENDANCE: {

                cursor = mStudentDbHelper.getReadableDatabase().query(
                        StudentContract.AttendanceEntry.ATTENDANCE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_INTERNALS: {
                cursor = mStudentDbHelper.getReadableDatabase().query(
                        StudentContract.InternalEntry.INTERNALS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;
        if (null == selection) selection = "1";


        switch (sUriMatcher.match(uri)) {

            case CODE_ATTENDANCE:
                numRowsDeleted = mStudentDbHelper.getWritableDatabase().delete(
                        StudentContract.AttendanceEntry.ATTENDANCE_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_INTERNALS:
                numRowsDeleted = mStudentDbHelper.getWritableDatabase().delete(
                        StudentContract.InternalEntry.INTERNALS_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        System.out.print("Number of Rows Deleted: "+ numRowsDeleted);
        return numRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)){
            case CODE_ATTENDANCE:
                SQLiteDatabase db = mStudentDbHelper.getWritableDatabase();
                long newRowId = db.insert(StudentContract.AttendanceEntry.ATTENDANCE_TABLE_NAME, null, values);
                db.close();
                System.out.println("Number of rows inserted:" + newRowId);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StudentContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, StudentContract.PATH_ATTENDANCE, CODE_ATTENDANCE);
        matcher.addURI(authority, StudentContract.PATH_INTERNALS, CODE_INTERNALS);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mStudentDbHelper.close();
        super.shutdown();
    }
}
