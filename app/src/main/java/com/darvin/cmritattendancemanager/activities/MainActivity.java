package com.darvin.cmritattendancemanager.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.fragments.AttendanceCriteriaFragment;
import com.darvin.cmritattendancemanager.fragments.AttendanceFragment;
import com.darvin.cmritattendancemanager.fragments.DatePickerFragment;
import com.darvin.cmritattendancemanager.fragments.InternalFragment;

import java.util.Objects;

import static com.darvin.cmritattendancemanager.utils.Constants.CRITERIA;
import static com.darvin.cmritattendancemanager.utils.Constants.DEFAULT;
import static com.darvin.cmritattendancemanager.utils.Constants.EMAIL;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;
import static com.darvin.cmritattendancemanager.utils.Constants.USER_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    private Fragment fragment = null;
    public static String date;
    public static String dateString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(Objects.equals(prefs.getString(USERID, DEFAULT), DEFAULT)){
            if(isInternetconnected(this))
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            else
                Toast.makeText(this," Network Error!! \n Check your Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else {
            if (Objects.equals(prefs.getString(CRITERIA, DEFAULT), DEFAULT)) {
                displaySelectedScreen(R.id.action_settings);
            } else {
                displaySelectedScreen(R.id.attendance_fragment);
            }
        }
        TextView userTextView = header.findViewById(R.id.user_name_nav);
        TextView emailId = header.findViewById(R.id.user_email_nav);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userTextView.setText(prefs.getString(USER_NAME, "default"));
        emailId.setText(prefs.getString(EMAIL, "default"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                            startActivity(intent);
                            finish();
                            onDestroy();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("No", null)
                    .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareLink();
                        }
                    })
                    .show();        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            displaySelectedScreen(id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }
    private void displaySelectedScreen(int itemId) {
        switch (itemId) {
            case R.id.attendance_fragment:
                fragment = new AttendanceFragment();
                break;
            case R.id.internal_fragment:
                fragment = new InternalFragment();
                break;
            case R.id.go_to_date:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.action_settings:
                fragment = new AttendanceCriteriaFragment();
                break;
            case R.id.nav_rate:
                Uri uri = Uri.parse("market://details?id=" + "com.darvin.cmritattendancemanager");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + "com.darvin.cmritattendancemanager")));
                }                break;
            case R.id.nav_share:
                shareLink();
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer. closeDrawer(GravityCompat.START);
    }

    public void logout(){
        prefs.edit().clear().apply();
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    public static boolean isInternetconnected(Context context) {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {

            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            } else {
                connected = false;
            }
            return connected;
        } else {
            return false;
        }
    }
    public void shareLink(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Now!! \n Maintain your attendance and internal marks with CAM");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=com.darvin.cmritattendancemanager");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
