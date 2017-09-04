package com.darvin.cmritattendancemanager.network;
import android.content.Context;
import android.content.SharedPreferences;

import com.darvin.cmritattendancemanager.activities.MainActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.utils.Constants.PASSWORD;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;


public class FetchAttendanceData {
    public ArrayList<String> fetchAttendance(Context c){
        String date = MainActivity.date;
        String dateString = MainActivity.dateString;
        System.out.println(date);
        System.out.println(dateString);

        Connection.Response res;
        SharedPreferences prefs = c.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        //Saving Cookies

        //
        ArrayList<String> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect("http://203.201.63.40/educ8_git/inc/ajax/get_student_class.ajax.php")
                    .data("day1", dateString, "day6", dateString)
                    .cookies(cookies)
                    .post();
            Elements attendance = document.select("td:contains("+ date +") ~ td");
            if (attendance.size() == 0){
                list.add("No classes were conducted on "+ date);
            }
            else{
                for (Element attend : attendance) {
                    if(attend.text().equals("Leisure")){

                    }
                    else{
                        list.add(attend.text().replaceAll("\\(.*?\\) ?", ""));
                        list.add(attend.attr("title"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
