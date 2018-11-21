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

package com.example.android.screencapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Provides UI for the screen capture.
 */
public class ScreenCaptureFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ScreenCaptureFragment";

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private int mScreenDensity;

    private int mResultCode;
    private Intent mResultData;

    private Surface mSurface;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private Button mButtonToggle;
    private Button mButtonShow;
    private Button mButtonWindow;
    private SurfaceView mSurfaceView;
    private ImageReader mImageReader;


    private int count;
    private int mClickId;
    private boolean mInReading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_capture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface);
        //mSurface = mSurfaceView.getHolder().getSurface();
        mButtonToggle = (Button) view.findViewById(R.id.toggle);
        mButtonShow = view.findViewById(R.id.btnShow);
        mButtonWindow = view.findViewById(R.id.window);

        mButtonShow.setOnClickListener(this);
        mButtonToggle.setOnClickListener(this);
        mButtonWindow.setOnClickListener(this);
        Activity activity = getActivity();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mImageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 1);
        mSurface = mImageReader.getSurface();
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (mInReading == true) return;
                mInReading = true;
//                if ( mVirtualDisplay == null) return;
//                stopScreenCapture(); // capture only one image.

                FileOutputStream fos = null;
                Bitmap bitmap = null;
                Image img = null;

                Activity activity = getActivity();
                DisplayMetrics metrics = new DisplayMetrics();

                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                try {
                    img = reader.acquireLatestImage();

                    if (img != null) {
                        Image.Plane[] planes = img.getPlanes();
                        if (planes[0].getBuffer() == null) {
                            return;
                        }
                        int width = img.getWidth();
                        int height = img.getHeight();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * width;
                        //byte[] newData = new byte[width * height * 4];

                        int offset = 0;

                        bitmap = Bitmap.createBitmap(metrics,width, height, Bitmap.Config.ARGB_8888);
                        int rowBytes = bitmap.getRowBytes();
                        int size = rowBytes * bitmap.getHeight();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                        byte[] bytes = byteBuffer.array();

                        ByteBuffer buffer = planes[0].getBuffer();
                        for (int i = 0; i < height; ++i) {
                            for (int j = 0; j < width; ++j) {
                                int pixel = 0;
//                                pixel |= (buffer.get(offset) & 0xff) << 16;     // R
//                                pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
//                                pixel |= (buffer.get(offset + 2) & 0xff);       // B
//                                pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
//                                bitmap.setPixel(j, i, pixel);
                                bytes[i * rowBytes + j*4+2] = buffer.get(offset);
                                bytes[i * rowBytes + j*4+1] = buffer.get(offset+1);
                                bytes[i * rowBytes + j*4] = buffer.get(offset+2);
                                bytes[i * rowBytes + j*4+3] = buffer.get(offset+3);
                                offset += pixelStride;
                            }
                            offset += rowPadding;
                        }
                        bitmap.copyPixelsFromBuffer(byteBuffer);

                        String name = "/myscreen" + count + ".png";
                        count++;
                        File file = new File(Environment.getExternalStorageDirectory(), name);
                        if ( count == 1) {
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        }
                        // Environment.getExternalStorageDirectory()
                        // Environment.getDataDirectory() + "/data/com.example.android.screencapture"
                        Log.i(TAG, "image saved in" + Environment.getExternalStorageDirectory()  + name);
                        img.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mInReading = false;
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != bitmap) {
                        bitmap.recycle();
                    }
                    if (null != img) {
                        img.close();
                    }

                }
            }
        }, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager)
                activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    @Override
    public void onClick(View v) {
        mClickId = v.getId();
        switch (v.getId()) {
            case R.id.toggle:
                if (mVirtualDisplay == null) {
                    startScreenCapture();
                } else {
                    stopScreenCapture();
                }
                break;
            case R.id.btnShow:
                fetchDepartments();
                break;
            case R.id.window:
                if ( mResultCode == 0) {
                    startActivityForResult(
                            mMediaProjectionManager.createScreenCaptureIntent(),
                            REQUEST_MEDIA_PROJECTION);
                } else {
                    startFloatingWindowService();
                }
                break;
        }
    }

    private void startFloatingWindowService() {
        Activity a = getActivity();
        Log.i(TAG, "startFloatingWindowService");
        // This initiates a prompt dialog for the user to confirm screen projection.

        if ( a != null && mResultCode != 0) {
            Intent service = new Intent(a, FloatingWindow.class);
            service.putExtra(STATE_RESULT_DATA, mResultData);
            service.putExtra(STATE_RESULT_CODE, mResultCode);
            tearDownMediaProjection();
            a.startService(service);
        }
    }

    private void fetchDepartments() {
        try {
            Connection mConnection = ERPConnectionFactory.GetConnection();
            Statement stmt = mConnection.createStatement();
            ResultSet rs = stmt.executeQuery("select depname from bdepartment");
            while (rs.next()) {
                Log.i(TAG, rs.getString("depname")); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled");
                Toast.makeText(getActivity(), R.string.user_cancelled, Toast.LENGTH_SHORT).show();
                return;
            }
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            Log.i(TAG, "Starting screen capture");
            mResultCode = resultCode;
            mResultData = data;
            if ( mClickId == R.id.toggle) {
                setUpMediaProjection();
                setUpVirtualDisplay();
            } else if ( mClickId == R.id.window) {
                startFloatingWindowService();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScreenCapture();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentActivity a = getActivity();
        if ( a != null ) {
            Intent service = new Intent(a, FloatingWindow.class);
            a.stopService(service);
        }
        tearDownMediaProjection();
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void startScreenCapture() {
        Activity activity = getActivity();
        if (mSurface == null || activity == null) {
            return;
        }
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            Log.i(TAG, "Requesting confirmation");
            // This initiates a prompt dialog for the user to confirm screen projection.
            startActivityForResult(
                    mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }
    }

    private void setUpVirtualDisplay() {
        Log.i(TAG, "Setting up a VirtualDisplay: " +
                mSurfaceView.getWidth() + "x" + mSurfaceView.getHeight() +
                " (" + mScreenDensity + ")");
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mSurfaceView.getWidth(), mSurfaceView.getHeight(), mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null, null);
        mButtonToggle.setText(R.string.stop);
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        mButtonToggle.setText(R.string.start);
    }

}
