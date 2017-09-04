package com.darvin.cmritattendancemanager.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.db.StudentContract;
import com.darvin.cmritattendancemanager.db.StudentDbHelper;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.db.StudentContract.StudentEntry.COLUMN_NAME;
import static com.darvin.cmritattendancemanager.db.StudentContract.StudentEntry.COLUMN_PERCENTAGE;
import static com.darvin.cmritattendancemanager.utils.Constants.CRITERIA;
import static com.darvin.cmritattendancemanager.utils.Constants.DEFAULT;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USER_NAME;


public class AttendanceCriteriaFragment extends Fragment {
    public AttendanceCriteriaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_criteria, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView percentageView =  view.findViewById(R.id.fac_percentage);
        final RangeBar rangeBar =  view.findViewById(R.id.fac_rangebar);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                percentageView.setText(new StringBuilder().append(rightPinValue).append("%").toString());
            }
        });
        FloatingActionButton fab =  view.findViewById(R.id.fac_save);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#FFFFFF")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putString(CRITERIA, rangeBar.getRightPinValue()).apply();
                StudentDbHelper mStudentDbHelper;
                mStudentDbHelper = new StudentDbHelper(getActivity());
                SQLiteDatabase db = mStudentDbHelper.getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_PERCENTAGE , rangeBar.getRightPinValue());
                long newRowId = db.update(StudentContract.StudentEntry.TABLE_NAME, contentValues,
                        COLUMN_NAME + "=" + "\"" +prefs.getString(USER_NAME, DEFAULT)  +"\"" , null);
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });
    }
}
