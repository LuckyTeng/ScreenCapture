package com.example.android.screencapture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.services.HelloIntentService;

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
                startService(new Intent(v.getContext(), HelloIntentService.class));

                try {
                    ResultSet rs = getLoginResultSet();

                    if ( rs.next()) {
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Fail!!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private ResultSet getLoginResultSet() throws SQLException {
        Connection connection = ERPConnectionFactory.GetConnection();
        CharSequence mail = mEmailView.getText();
        CharSequence password = mPasswordView.getText();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("SELECT 1 FROM Busers b WHERE b.USERID ='" + mail + "' AND b.PWD = '" + password + "'");
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
