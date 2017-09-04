package com.darvin.cmritattendancemanager.network;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.darvin.cmritattendancemanager.db.StudentContract.StudentEntry;
import com.darvin.cmritattendancemanager.db.StudentDbHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.utils.Constants.EMAIL;
import static com.darvin.cmritattendancemanager.utils.Constants.GENDER;
import static com.darvin.cmritattendancemanager.utils.Constants.PASSWORD;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;
import static com.darvin.cmritattendancemanager.utils.Constants.USER_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USN;


public class LoginValidate {
    private Map<String, String> cookies;
   public int fetchData(Context c) throws NullPointerException{
        Connection.Response res = null;
        int response = 7;
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        try {
            res = Jsoup
                    .connect("http://203.201.63.40/educ8_git/inc/ajax/process_login.inc.php")
                    .data("username", prefs.getString(USERID, "default"), "password", prefs.getString(PASSWORD, "default"))
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Response Code "+ res.statusCode());
        System.out.println("Response Body "+ res.body());
        try {
            JSONObject jsonObject = new JSONObject(res.body());
            response = jsonObject.getInt("status");
            System.out.println("Response Code "+ response);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Saving Cookies
        cookies = res.cookies();
        if(response == 1){
            //Personal Details
            ArrayList<String> list = personalDetails();
            StudentDbHelper mStudentDbHelper;
            mStudentDbHelper = new StudentDbHelper(c);
            SQLiteDatabase db = mStudentDbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(StudentEntry.COLUMN_USERID , prefs.getString(USERID, "default"));
            contentValues.put(StudentEntry.COLUMN_PASSWORD , prefs.getString(PASSWORD, "default"));
            contentValues.put(StudentEntry.COLUMN_EMAIL , list.get(0));
            contentValues.put(StudentEntry.COLUMN_GENDER , list.get(1));
            contentValues.put(StudentEntry.COLUMN_USN , list.get(2));
            contentValues.put(StudentEntry.COLUMN_NAME , list.get(3));

            long newRowId = db.insert(StudentEntry.TABLE_NAME, null, contentValues);

            System.out.println("Db id:"+newRowId);
            prefs.edit().putString(EMAIL, list.get(0)).apply();
            prefs.edit().putString(GENDER, list.get(1)).apply();
            prefs.edit().putString(USN, list.get(2)).apply();
            prefs.edit().putString(USER_NAME, list.get(3)).apply();
            db.close();
        }
        return response;
    }

    private ArrayList<String> personalDetails(){
        ArrayList<String> list = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("http://203.201.63.40/educ8_git/dashboard_student.php").cookies(cookies).get();
            System.out.println("====================================");
            Elements elements = doc.select("label:contains(Email) > b");
            for(int i=0; i<elements.size(); i++) {
                System.out.println("Index = " + i + "\t" + "Value = " + elements.get(i).text());
                list.add(elements.get(i).text());
            }
            elements = doc.select("label:contains(Gender) > b");
            for(int i=0; i<elements.size(); i++) {
                System.out.println("Index = " + i + "\t" + "Value = " + elements.get(i).text());
                list.add(elements.get(i).text());
            }
            elements = doc.select("label:contains(USN) > b");
            for(int i=0; i<elements.size(); i++) {
                System.out.println("Index = " + i + "\t" + "Value = " + elements.get(i).text());
                list.add(elements.get(i).text());
            }
            elements = doc.select("span.username");
            for(int i=0; i<elements.size(); i++) {
                System.out.println("Index = " + i + "\t" + "Value = " + elements.get(i).text());
                list.add(elements.get(i).text());
            }
            } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
