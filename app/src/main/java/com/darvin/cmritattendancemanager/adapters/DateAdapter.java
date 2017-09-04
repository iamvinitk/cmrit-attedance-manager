package com.darvin.cmritattendancemanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darvin.cmritattendancemanager.R;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<String> list;

    public DateAdapter( ArrayList<String> list) {
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_date, parent, false);
        return new DateHolder(menuItemLayoutView);    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DateHolder dateHolder = (DateHolder) holder;
            dateHolder.subjectName.setText(list.get(2*position));
            dateHolder.attendance.setText(list.get((2*position)+1));
    }

    @Override
    public int getItemCount() {
        return list.size()/2;
    }

    private class DateHolder extends RecyclerView.ViewHolder {
        TextView subjectName, attendance;

        DateHolder(View v) {
            super(v);
            subjectName = v.findViewById(R.id.rd_subject);
            attendance =  v.findViewById(R.id.rd_attendance);
        }
    }
}
