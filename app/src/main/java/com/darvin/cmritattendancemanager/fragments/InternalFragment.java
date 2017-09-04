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
import com.darvin.cmritattendancemanager.adapters.InternalAdapter;
import com.darvin.cmritattendancemanager.db.StudentContract;
import com.darvin.cmritattendancemanager.utils.CamSyncUtils;


public class InternalFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private InternalAdapter internalAdapter;


    //Columns that needs to be selected from Internals Table
    public static final String[] MAIN_FORECAST_PROJECTION = {
            StudentContract.InternalEntry.COLUMN_SUBJECT,
            StudentContract.InternalEntry.COLUMN_FIRST_INTERNAL,
            StudentContract.InternalEntry.COLUMN_SECOND_INTERNAL,
            StudentContract.InternalEntry.COLUMN_THIRD_INTERNAL,
            StudentContract.InternalEntry.COLUMN_TOTAL_MARKS,
            StudentContract.InternalEntry.COLUMN_USN,
    };

    public static final int INDEX_SUBJECT = 0;
    public static final int INDEX_IA1 = 1;
    public static final int INDEX_IA2 = 2;
    public static final int INDEX_IA3 = 3;
    public static final int INDEX_TOTAL = 4;

    private static final int ID_INTERNAl_LOADER = 46;

    public InternalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Attendance");

        recyclerView = view.findViewById(R.id.internal_recycler_view);
        progressBar = view.findViewById(R.id.internal_progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        internalAdapter = new InternalAdapter();
        recyclerView.setAdapter(internalAdapter);
        load();
        getActivity().getSupportLoaderManager().initLoader(ID_INTERNAl_LOADER, null, this);
        System.out.println("Started Sync Operation");
        CamSyncUtils.initialize(getContext());
    }

    private void load() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showAttendance() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case ID_INTERNAl_LOADER:
                Uri forecastQueryUri = StudentContract.InternalEntry.INTERNALS_CONTENT_URI;
                return new CursorLoader(getContext(),
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        internalAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        recyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0)
            showAttendance();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        internalAdapter.swapCursor(null);
    }
}