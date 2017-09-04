package com.darvin.cmritattendancemanager.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.adapters.AttendanceAdapter;
import com.darvin.cmritattendancemanager.db.StudentContract;
import com.darvin.cmritattendancemanager.utils.CamSyncUtils;


public class AttendanceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private AttendanceAdapter attendanceAdapter;

    //Columns that needs to be selected from Attendance Table
    public static final String[] MAIN_FORECAST_PROJECTION = {
            StudentContract.AttendanceEntry.COLUMN_SUBJECT,
            StudentContract.AttendanceEntry.COLUMN_ATTENDED_CLASSES,
            StudentContract.AttendanceEntry.COLUMN_TOTAL_CLASSES,
            StudentContract.AttendanceEntry.COLUMN_USN,
    };
    public static final int INDEX_SUBJECT = 0;
    public static final int INDEX_ATTENDED_CLASSES = 1;
    public static final int INDEX_TOTAL_CLASSES = 2;

    //Loader ID
    private static final int ID_ATTENDANCE_LOADER = 44;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Attendance");

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        attendanceAdapter = new AttendanceAdapter(getContext());
        recyclerView.setAdapter(attendanceAdapter);
        load();
        getActivity().getSupportLoaderManager().initLoader(ID_ATTENDANCE_LOADER, null, this);
        System.out.println("Started Sync Operation");
        CamSyncUtils.initialize(getContext());
    }
    private void load(){
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void showAttendance(){
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case ID_ATTENDANCE_LOADER:
                Uri forecastQueryUri = StudentContract.AttendanceEntry.ATTENDANCE_CONTENT_URI;
                return new CursorLoader(getContext(),
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            attendanceAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0)
            showAttendance();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        attendanceAdapter.swapCursor(null);
    }
}
