package com.example.android.screencapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.common.logger.Log;

public class QueryResultReceiver extends BroadcastReceiver {
    private static final String TAG = "QueryResultReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
    }
}
