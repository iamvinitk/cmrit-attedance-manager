package com.darvin.cmritattendancemanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.activities.MainActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import android.app.TaskStackBuilder;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.utils.Constants.PASSWORD;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;


public class NotificationUtils {
    private static String notificationText = "";
    private static final int NOTIFICATION_ID = 234;

    public static void notifyUser (Context context){
        notificationText = "";
        Connection.Response res = null;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        try {
            res = Jsoup
                    .connect("http://203.201.63.40/educ8_git/inc/ajax/process_login.inc.php")
                    .data("username", prefs.getString(USERID, "default"), "password", prefs.getString(PASSWORD, "default"))
                    .method(Connection.Method.POST)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> cookies = res.cookies();
        String monthDate = "";
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String modifiedDate = dateFormat.format(date);
            Date myDate = null;
            try {
                myDate = dateFormat.parse(modifiedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(myDate);
            cal1.add(Calendar.DAY_OF_YEAR, -1);
            Date previousDate = cal1.getTime();
            Document document = Jsoup.connect("http://203.201.63.40/educ8_git/inc/ajax/get_student_class.ajax.php")
                    .data("day1","2017-08-7", "day6", modifiedDate)
                    .cookies(cookies)
                    .post();
            monthDate = new SimpleDateFormat("dd MMM").format(previousDate);
            System.out.println("=="+monthDate);
            Elements status = document.select("td:contains("+ monthDate +") ~ td");
            System.out.println("============================== \n");
            if (status.size() == 0){
                System.out.println("No classes were conducted \n");
                notificationText += "No classes were conducted";
            }
            else {
                int counter = 1;
                for (Element stat : status) {
                    if(stat.text().equals("Leisure")){

                    }
                    else{
                        System.out.println(counter+") "+stat.text().replaceAll("\\(.*?\\) ?", "") + "- " + stat.attr("title"));
                        notificationText+=counter+") "+stat.text().replaceAll("\\(.*?\\) ?", "") + "- " + stat.attr("title")+"\n";
                        counter++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        createNotification(context, monthDate );
    }
    public static void createNotification(Context context, String date){

        Intent intent = new Intent(context, MainActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap Icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.web);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.cam)
                .setLargeIcon(Icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Attendance of " + date )
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
