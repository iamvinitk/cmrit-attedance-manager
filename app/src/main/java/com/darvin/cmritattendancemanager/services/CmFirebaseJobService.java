package com.darvin.cmritattendancemanager.services;
import android.content.Context;
import android.os.AsyncTask;

import com.darvin.cmritattendancemanager.network.CamSyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class CmFirebaseJobService extends JobService {


    private AsyncTask<Void, Void, Void> mFetchTask;


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                CamSyncTask camSyncTask = new CamSyncTask();
                camSyncTask.syncData(context);
                    jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                    jobFinished(jobParameters, false);
                }
        };

        mFetchTask.execute();
        return true;    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchTask != null) {
            mFetchTask.cancel(true);
        }
        return true;    }
}
