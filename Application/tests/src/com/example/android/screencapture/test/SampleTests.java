/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.screencapture.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.android.common.logger.Log;
import com.example.android.screencapture.MainActivity;
import com.example.android.screencapture.R;
import com.example.android.screencapture.ScreenCaptureFragment;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tests for ScreenCapture sample.
 */
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private ScreenCaptureFragment mTestFragment;
    private WindowManager mWindowManager;
    private Connection mConnection;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTestActivity = getActivity();
        mTestFragment = (ScreenCaptureFragment)
                mTestActivity.getSupportFragmentManager().getFragments().get(1);
        String driver = "net.sourceforge.jtds.jdbc.Driver";

        mWindowManager = mTestActivity.getWindowManager();
        Class.forName(driver);
        mConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77:1433/CITest;instance=SQLEXPRESS", "lacty", "wu0g3tp6");
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);

        try {
            Date d = mConnection.prepareCall("select getdate()").getDate(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testButtonToggle() throws Throwable {
        final View view = mTestFragment.getView();
        assertNotNull(view);
        final Button buttonToggle = (Button) view.findViewById(R.id.toggle);
        assertNotNull(buttonToggle);
    }

    public void testJDBC() throws  Throwable {
        DatabaseMetaData dbm = mConnection.getMetaData();
        ResultSet rs = null;
        rs = dbm.getTables(null, null, "%", new String[] { "TABLE" });
        while (rs.next()) {
            Log.i("TEST", rs.getString("TABLE_NAME")); }
    }

}
