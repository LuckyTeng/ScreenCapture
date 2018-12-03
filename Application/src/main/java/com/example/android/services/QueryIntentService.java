package com.example.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.android.common.logger.Log;
import com.example.android.screencapture.Constants;
import com.example.android.screencapture.ERPConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.example.android.screencapture.Constants.QueryIntentService_FetchConstructor;
import static com.example.android.screencapture.Constants.QueryIntentService_FetchDep;

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
        Log.i(TAG,"QueryIntentService starting");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        synchronized (this) {
            String jobString = intent.getStringExtra("job");
            switch (jobString)
            {
                case QueryIntentService_FetchDep:
                    fetchDepartments();
                    break;
                case QueryIntentService_FetchConstructor:
                    fetchConstructor();
                    break;
            }
        }
    }

    private void fetchConstructor() {
        String qry = "SELECT  DescriptionCN FROM    ConstructionApply";

        ExecuteQueryAndBroadcast(qry);
    }

    private void fetchDepartments() {
        String qry = "select depname from bdepartment";

        ExecuteQueryAndBroadcast(qry);
    }

    private void ExecuteQueryAndBroadcast(String qry) {
        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, 0);
        try {
            try {
                Connection mConnection = ERPConnectionFactory.GetConnection();
                Statement stmt = mConnection.createStatement();

                ResultSet rs = stmt.executeQuery(qry);

                ArrayList<String> strings = new ArrayList<>();
                while (rs.next()) {
                    strings.add(rs.getString("DescriptionCN"));
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
