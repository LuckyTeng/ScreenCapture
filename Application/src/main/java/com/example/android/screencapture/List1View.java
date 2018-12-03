package com.example.android.screencapture;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.services.QueryIntentService;

import java.util.ArrayList;

import static com.example.android.screencapture.Constants.QueryIntentService_FetchConstructor;

public class List1View extends ListActivity {
    public static final String TAG = "List1View";
    private static final String QUERY_RESULT_KEY = "QueryResult";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
//        getListView().setTextFilterEnabled(true);
        InitInnerQueryResultReceiver();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Toast.makeText(this, "id:" + id, Toast.LENGTH_SHORT).show();
    }

    private void InitInnerQueryResultReceiver() {
        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        final Context contextSelf = this;
        class InnerQueryResultReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Use an existing ListAdapter that will map an array of strings to TextViews
                ArrayList<String> mLists = intent.getStringArrayListExtra(QUERY_RESULT_KEY);
                setListAdapter(new ArrayAdapter<String>(contextSelf, android.R.layout.simple_list_item_1, mLists));
                getListView().setTextFilterEnabled(true);
            }
        }

        InnerQueryResultReceiver mDownloadStateReceiver =
                new InnerQueryResultReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDownloadStateReceiver,
                statusIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG, "Resume");
        startFetchConstructorService();
    }

    private void startFetchConstructorService() {
        Log.i(TAG, "startFetchConstructorService");
        // This initiates a prompt dialog for the user to confirm screen projection.

        Intent service = new Intent(this, QueryIntentService.class);
        service.putExtra("job", QueryIntentService_FetchConstructor);
        this.startService(service);
    }

    private String[] mStrings = Cheeses.sCheeseStrings;
}
