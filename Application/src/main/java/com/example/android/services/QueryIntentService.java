package com.example.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.screencapture.Constants;
import com.example.android.screencapture.ERPConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QueryIntentService extends IntentService {
    private static final String TAG = "QueryIntentService";
    private static final String QUERY_RESULT_KEY = "QueryResult";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public QueryIntentService() {
        super("QueryIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        synchronized (this) {
            Intent localIntent =
                    new Intent(Constants.BROADCAST_ACTION)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTENDED_DATA_STATUS, 0);
            try {
                try {
                    Connection mConnection = ERPConnectionFactory.GetConnection();
                    Statement stmt = mConnection.createStatement();
                    ResultSet rs = stmt.executeQuery("select depname from bdepartment");

                    while (rs.next()) {
                        Log.i(TAG, rs.getString("depname"));
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(rs.getString("depname"));
                        localIntent.putStringArrayListExtra(QUERY_RESULT_KEY, strings);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }
}
