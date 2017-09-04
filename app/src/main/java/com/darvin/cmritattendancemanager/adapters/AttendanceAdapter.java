package com.darvin.cmritattendancemanager.adapters;



import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darvin.cmritattendancemanager.fragments.AttendanceFragment;
import com.darvin.cmritattendancemanager.R;
import java.text.DecimalFormat;

import static com.darvin.cmritattendancemanager.utils.Constants.CRITERIA;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;


public class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Cursor mCursor;
    private Context context;
    private DecimalFormat twoDForm = new DecimalFormat("#");

    public AttendanceAdapter(Context context) {
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_attendance, parent, false);
        return new AttendanceHolder(menuItemLayoutView);    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        AttendanceHolder attendanceHolder = (AttendanceHolder) holder;
        String subjectName = mCursor.getString(AttendanceFragment.INDEX_SUBJECT);
        String totalClasses = mCursor.getString(AttendanceFragment.INDEX_TOTAL_CLASSES);
        String attendedClasses = mCursor.getString(AttendanceFragment.INDEX_ATTENDED_CLASSES);
        double x, y;
        x = Double.valueOf(attendedClasses);
        y = Double.valueOf(totalClasses);
        attendanceHolder.subjectName.setText(subjectName);
        attendanceHolder.attended.setText(String.format("Attended: %s", attendedClasses));
        attendanceHolder.total.setText(String.format("Total: %s", totalClasses));
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        double percentage;
        double required_percentage = Double.valueOf(prefs.getString(CRITERIA, "85"));
        percentage = ((x)/(y))*100;
        attendanceHolder.percentageAttendance.setText(String.valueOf(twoDForm.format(percentage)));

        double totalValue = ((required_percentage * y) - (100 * x))/(100 - required_percentage);
        int value = (int)(totalValue);
        if(required_percentage > percentage && value != 0 ){
            attendanceHolder.classSuggest.setText(String.format("Attend next %s classes", value));
            attendanceHolder.percentageAttendance.setTextColor(Color.parseColor("#D32F2F"));
        }
        else{
            totalValue = ((100 * x) - (required_percentage * y))/(required_percentage);
            attendanceHolder.percentageAttendance.setTextColor(Color.parseColor("#7CB342"));
            if(totalValue == 0)
                attendanceHolder.classSuggest.setText(R.string.required_percentage);
            else {
                attendanceHolder.classSuggest.setText(String.format("Bunk next %s classes", (int) totalValue));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
    private class AttendanceHolder extends RecyclerView.ViewHolder {
        TextView subjectName, attended, total, classSuggest, percentageAttendance;

        AttendanceHolder(View v) {
            super(v);
            context = v.getContext();
            subjectName =  v.findViewById(R.id.subjectName);
            attended =  v.findViewById(R.id.attended);
            total =  v.findViewById(R.id.total);
            classSuggest =  v.findViewById(R.id.classSuggest);
            percentageAttendance =  v.findViewById(R.id.percentageAttendance);
        }
    }
}
