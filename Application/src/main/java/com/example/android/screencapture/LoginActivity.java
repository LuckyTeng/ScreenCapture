package com.example.android.screencapture;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends Activity {
    private static final String PASSWROD_KEY = "PASSWORD";
    private static final String EMAIL_KEY = "EMAIL";
    private TextView mPasswordView;
    private TextView mEmailView;
    private TextView mLoginView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mPasswordView = findViewById(R.id.password);
        mEmailView = findViewById(R.id.email);
        mLoginView = findViewById(R.id.email_sign_in_button);
        mProgressBar = findViewById(R.id.login_progress);

        if (savedInstanceState != null) {
            mPasswordView.setText(savedInstanceState.getString(PASSWROD_KEY));
            mEmailView.setText(savedInstanceState.getString(EMAIL_KEY));
        }

        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mProgressBar.getVisibility() == View.VISIBLE) return;
                mProgressBar.setVisibility(View.VISIBLE);

                new CheckLoginTask().execute(mEmailView.getText().toString(), mPasswordView.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class CheckLoginTask extends AsyncTask<String, Void, Boolean> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Boolean doInBackground(String... rss) {
            String email = rss[0];
            String password = rss[1];
            Connection connection = null;
            try {
                connection = ERPConnectionFactory.GetConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1 FROM Busers b WHERE b.USERID ='" + email + "' AND b.PWD = '" + password + "'");

                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.GONE);
            if ( result ) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else
                Toast.makeText(LoginActivity.this, "Login Fail!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PASSWROD_KEY, mPasswordView.getText().toString());
        outState.putString(EMAIL_KEY, mEmailView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPasswordView.setText(savedInstanceState.getString(PASSWROD_KEY));
        mEmailView.setText(savedInstanceState.getString(EMAIL_KEY));
    }
}
