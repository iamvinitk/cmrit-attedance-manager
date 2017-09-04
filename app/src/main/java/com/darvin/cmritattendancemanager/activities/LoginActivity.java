package com.darvin.cmritattendancemanager.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.darvin.cmritattendancemanager.R;
import com.darvin.cmritattendancemanager.network.LoginValidate;

import static com.darvin.cmritattendancemanager.utils.Constants.DEFAULT;
import static com.darvin.cmritattendancemanager.utils.Constants.PASSWORD;
import static com.darvin.cmritattendancemanager.utils.Constants.PREFS_NAME;
import static com.darvin.cmritattendancemanager.utils.Constants.USERID;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences prefs = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                EditText userName = (EditText)findViewById(R.id.login_userid);
                EditText password = (EditText)findViewById(R.id.login_password);
                prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putString(USERID, userName.getText().toString()).apply();
                prefs.edit().putString(PASSWORD, password.getText().toString()).apply();
                progressBar.setVisibility(View.VISIBLE);
                FetchData fetchData = new FetchData();
                fetchData.execute();
            }
        });
    }

    private class FetchData extends AsyncTask<Object,Object,Integer> {

        @Override
        protected Integer doInBackground(Object... objects) {
            int response;
            LoginValidate loginValidate = new LoginValidate();
            response = loginValidate.fetchData(LoginActivity.this);
            return response;
        }

        @Override
        protected void onPostExecute(Integer response) {
            if(response == 1){
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else{
                Toast.makeText(LoginActivity.this, "Username and Password do not match", Toast.LENGTH_LONG).show();
                prefs.edit().remove(USERID).apply();
            }
        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Now!! \n Maintain your attendance and internal marks with CAM");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=com.darvin.cmritattendancemanager");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                })
                .show();
    }
}
