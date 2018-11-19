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

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.android.common.logger.Log;
import com.example.android.screencapture.MainActivity;
import com.example.android.screencapture.R;
import com.example.android.screencapture.ScreenCaptureFragment;
import com.example.android.services.HelloIntentService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Tests for ScreenCapture sample.
 */
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private ScreenCaptureFragment mTestFragment;
    private WindowManager mWindowManager;


    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTestActivity = getActivity();
        mTestFragment = (ScreenCaptureFragment)
                mTestActivity.getSupportFragmentManager().getFragments().get(1);

        mWindowManager = mTestActivity.getWindowManager();
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }

    public void testButtonToggle() {
        final View view = mTestFragment.getView();
        assertNotNull(view);
        final Button buttonToggle = view.findViewById(R.id.toggle);
        assertNotNull(buttonToggle);
    }

    public void testJDBC() throws  Throwable {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        DriverManager.setLoginTimeout(5);
        Connection mConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.18.77;instance=SQLEXPRESS;DatabaseName=CITEST", "lacty", "wu0g3tp6");
        DatabaseMetaData dbm = mConnection.getMetaData();
        ResultSet rs = null;
        Statement stmt = mConnection.createStatement();
        rs = stmt.executeQuery("select depname from bdepartment");
        while (rs.next()) {
            Log.i("TEST", rs.getString("depname")); }
    }

    public void testHelloIntentService() {
        Intent intent = new Intent(mTestActivity, HelloIntentService.class);
        mTestActivity.startService(intent);
        mTestActivity.stopService(intent);
    }

}
