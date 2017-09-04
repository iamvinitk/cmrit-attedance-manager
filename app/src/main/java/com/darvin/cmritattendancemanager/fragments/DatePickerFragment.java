package com.darvin.cmritattendancemanager.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;


import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.activities.MainActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String startDateString = "07/15/2017";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = new Date();
        try {
            startDate = df.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getDatePicker().setMinDate(startDate.getTime());

        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(MainActivity.isInternetconnected(getContext())){
        String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        System.out.println("Month"+month);
        MainActivity.date = day + " " + monthArray[month];
        MainActivity.dateString = year + "-" + (month+1) + "-" + day;
        DateFragment dateFragment = new DateFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, dateFragment).commit();}
        else
            Toast.makeText(getContext()," Network Error!! \n Check your Internet Connection",Toast.LENGTH_SHORT).show();

    }
}
