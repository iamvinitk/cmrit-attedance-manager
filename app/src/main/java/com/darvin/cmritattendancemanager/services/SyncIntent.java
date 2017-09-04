package com.darvin.cmritattendancemanager.services;
import android.app.IntentService;
import android.content.Intent;

import com.darvin.cmritattendancemanager.network.CamSyncTask;


public class SyncIntent extends IntentService {

    public SyncIntent() {
        super("SyncIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CamSyncTask camSyncTask = new CamSyncTask();
        camSyncTask.syncData(this);
    }
}