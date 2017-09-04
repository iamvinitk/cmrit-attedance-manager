package com.darvin.cmritattendancemanager.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.darvin.cmritattendancemanager.db.StudentContract;
import com.darvin.cmritattendancemanager.services.CmFirebaseJobService;
import com.darvin.cmritattendancemanager.services.SyncIntent;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USER_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USN;

public class CamSyncUtils {

    /*
     * Interval at which to sync the attendance. Use TimeUnit for convenience, rather than
     * writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */
    private static final int SYNC_INTERVAL_HOURS = 8;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String CAM_SYNC_TAG = "attendance-sync";


    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        System.out.println("Automatic Sync Started");
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync Cam */
        Job syncCamJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Cam's data */
                .setService(CmFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(CAM_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the Cam data to be synced every 7 to 8 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncCamJob);
    }
    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {
        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri forecastQueryUri = StudentContract.AttendanceEntry.ATTENDANCE_CONTENT_URI;

                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                String[] projectionColumns = {StudentContract.AttendanceEntry.COLUMN_USN};
                String usn = prefs.getString(USN, "default");
                String[] selectionArgs = {usn};

                /* Here, we perform the query to check to see if we have any weather data */
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        " usn = ? ",
                        selectionArgs,
                        null);

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.start();

    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    private static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SyncIntent.class);
        context.startService(intentToSyncImmediately);
    }
}
