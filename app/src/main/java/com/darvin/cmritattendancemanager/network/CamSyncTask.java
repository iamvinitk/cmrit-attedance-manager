package com.darvin.cmritattendancemanager.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import com.darvin.cmritattendancemanager.db.StudentContract;
import com.darvin.cmritattendancemanager.utils.Attendance;
import com.darvin.cmritattendancemanager.utils.Internals;
import com.darvin.cmritattendancemanager.utils.NotificationUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.utils.Constants.PASSWORD;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;
import static com.darvin.cmritattendancemanager.utils.Constants.USN;


public class CamSyncTask {
    public synchronized void syncData(Context context){
        ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
        ArrayList<Internals> internalsArrayList = new ArrayList<>();
        Connection.Response res;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Map<String, String> cookies = null;
        try {
            res = Jsoup
                    .connect("http://203.201.63.40/educ8_git/inc/ajax/process_login.inc.php")
                    .data("username", prefs.getString(USERID, "default"), "password", prefs.getString(PASSWORD, "default"))
                    .method(Connection.Method.POST)
                    .execute();
            cookies = res.cookies();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Document doc = Jsoup.connect("http://203.201.63.40/educ8_git/dashboard_student.php").cookies(cookies).get();
            Elements attendance = doc.select("div.sub_att > input[value]");
            Elements internals = doc.select("div#tab_1_4 *");

            for(int i=0; i<attendance.size(); i+=3) {
                attendanceArrayList.add(new Attendance(attendance.get(i+2).val(), attendance.get(i).val(), attendance.get(i+1).val()));
            }
            Elements internal = internals.select("tr > td");

            for (int i=0; i<internal.size(); i+=5){
                internalsArrayList.add(new Internals(internal.get(i).text(), internal.get(i+1).text(), internal.get(i+2).text(), internal.get(i+3).text(), internal.get(i+4).text()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



            ContentValues[] attendanceContentValues = new ContentValues[attendanceArrayList.size()];
            for (int i = 0; i< attendanceArrayList.size(); i++){
                ContentValues contentValues = new ContentValues();
                contentValues.put(StudentContract.AttendanceEntry.COLUMN_SUBJECT, attendanceArrayList.get(i).getmSubject());
                contentValues.put(StudentContract.AttendanceEntry.COLUMN_ATTENDED_CLASSES, attendanceArrayList.get(i).getmAttended());
                contentValues.put(StudentContract.AttendanceEntry.COLUMN_TOTAL_CLASSES, attendanceArrayList.get(i).getmTotal());
                contentValues.put(StudentContract.AttendanceEntry.COLUMN_USN, prefs.getString(USN, "default"));
                attendanceContentValues[i] = contentValues;
            }




            if(attendanceContentValues.length != 0){
                String usn = prefs.getString(USN, "default");
                String[] args = {usn};
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(StudentContract.AttendanceEntry.ATTENDANCE_CONTENT_URI," usn = ? ", args);
                contentResolver.bulkInsert(StudentContract.AttendanceEntry.ATTENDANCE_CONTENT_URI, attendanceContentValues);
            }



        ContentValues[] internalContentValues = new ContentValues[internalsArrayList.size()];
        prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);



        for (int i = 0; i< internalsArrayList.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(StudentContract.InternalEntry.COLUMN_SUBJECT, internalsArrayList.get(i).getmSubject());
            contentValues.put(StudentContract.InternalEntry.COLUMN_FIRST_INTERNAL, internalsArrayList.get(i).getmFirstInternal());
            contentValues.put(StudentContract.InternalEntry.COLUMN_SECOND_INTERNAL, internalsArrayList.get(i).getmSecondInternal());
            contentValues.put(StudentContract.InternalEntry.COLUMN_THIRD_INTERNAL, internalsArrayList.get(i).getmThirdInternal());
            contentValues.put(StudentContract.InternalEntry.COLUMN_TOTAL_MARKS, internalsArrayList.get(i).getmTotalMarks());
            contentValues.put(StudentContract.InternalEntry.COLUMN_USN, prefs.getString(USN, "default"));
            internalContentValues[i] = contentValues;
        }

        if(internalContentValues.length != 0){
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.delete(StudentContract.InternalEntry.INTERNALS_CONTENT_URI, null, null);
            contentResolver.bulkInsert(StudentContract.InternalEntry.INTERNALS_CONTENT_URI, internalContentValues);
        }
        NotificationUtils.notifyUser(context);
    }


}
