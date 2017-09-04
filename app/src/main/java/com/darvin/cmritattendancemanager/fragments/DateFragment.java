package com.darvin.cmritattendancemanager.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.activities.MainActivity;
import com.darvin.cmritattendancemanager.adapters.DateAdapter;
import com.darvin.cmritattendancemanager.network.FetchAttendanceData;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DateFragment extends Fragment {

    private ArrayList<String> list = new ArrayList<>();
    private ProgressBar progressBar;
    private DateAdapter dateAdapter;
    TextView doe, sub, att;
    public DateFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Attendance");
        doe =  view.findViewById(R.id.fd_dateorerror);
        sub =  view.findViewById(R.id.rd_subject);
        att =  view.findViewById(R.id.rd_attendance);
        sub.setVisibility(View.GONE);
        att.setVisibility(View.GONE);
        progressBar =  view.findViewById(R.id.date_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView =  view.findViewById(R.id.date_recycler_view);
        recyclerView.setHasFixedSize(true);
        dateAdapter = new DateAdapter(list);
        recyclerView.setAdapter(dateAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        load();

    }
    private void load(){
        FetchData fetchDataAsyncTask = new FetchData();
        fetchDataAsyncTask.execute();
    }
    private class FetchData extends AsyncTask<Object,Object,ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            FetchAttendanceData fetchAttendanceData = new FetchAttendanceData();
            return fetchAttendanceData.fetchAttendance(getContext());
        }

        @Override
        protected void onPostExecute(ArrayList<String> attendanceArrayList) {
            list.addAll(attendanceArrayList);
            if (list.size() > 1 && list.size()%2==0){
                sub.setVisibility(View.VISIBLE);
                att.setVisibility(View.VISIBLE);
                dateAdapter.notifyDataSetChanged();
                doe.setText(MainActivity.date);
            }
            else{
                doe.setText(MessageFormat.format("No classes conducted on {0}", MainActivity.date));
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
