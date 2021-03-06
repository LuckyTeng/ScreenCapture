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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.services.QueryIntentService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.example.android.screencapture.Constants.QueryIntentService_FetchDep;

/**
 * Provides UI for the screen capture.
 */
public class ScreenCaptureFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "ScreenCaptureFragment";

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_OVERLAY_DRAW = 3;

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
    private Button mButtonShowList;
    private Button mButtonConst;
    private SurfaceView mSurfaceView;
    private ImageReader mImageReader;
    private RenderScriptTask mRenderScriptTask;
    private Bitmap mPhotoTaken;

    private int count;
    private int mClickId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
        InitInnerQueryResultReceiver();
    }

    private void InitInnerQueryResultReceiver() {
        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        class InnerQueryResultReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                mButtonShow.setEnabled(true);
            }
        }

        InnerQueryResultReceiver mDownloadStateReceiver =
                new InnerQueryResultReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mDownloadStateReceiver,
                statusIntentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_capture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSurfaceView = (SurfaceView) view.findViewById(R.id.surface);
//        mSurface = mSurfaceView.getHolder().getSurface();
        mButtonToggle = (Button) view.findViewById(R.id.toggle);
        mButtonShow = view.findViewById(R.id.btnShow);
        mButtonWindow = view.findViewById(R.id.window);
        mButtonShowList = view.findViewById(R.id.btnShowList);
        mButtonConst = view.findViewById(R.id.Constr);

        mButtonShow.setOnClickListener(this);
        mButtonToggle.setOnClickListener(this);
        mButtonWindow.setOnClickListener(this);
        mButtonShowList.setOnClickListener(this);
        mButtonConst.setOnClickListener(this);

        Activity activity = getActivity();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mImageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 1);
        mSurface = mImageReader.getSurface();
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                // every time when reader ok, you need to consume it
                SaveCaptureImage();
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

        mSurfaceView.getHolder().addCallback(this);
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
                startFetchDepartmentsService();
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
            case R.id.btnShowList:
                startList1View();
                break;
            case R.id.Constr:
                startConstructionActivity();
                break;
        }
    }

    private void startConstructionActivity() {
        Activity a = getActivity();
        if ( a== null) return;
        Intent intent = new Intent(getActivity(), ConstructionActivity.class);
        startActivity(intent);
    }

    private void startList1View() {
        Activity a = getActivity();
        if ( a== null) return;
        Intent intent = new Intent(getActivity(), List1View.class);
        startActivity(intent);
    }

    private void startFloatingWindowService() {
        Activity a = getActivity();
        Log.i(TAG, "startFloatingWindowService");
        // This initiates a prompt dialog for the user to confirm screen projection.
        checkDrawOverlayPermission();

        if ( a != null && mResultCode != 0) {
            Intent service = new Intent(a, FloatingWindow.class);
            service.putExtra(STATE_RESULT_DATA, mResultData);
            service.putExtra(STATE_RESULT_CODE, mResultCode);
            tearDownMediaProjection();
            a.startService(service);
        }
    }

    private void startFetchDepartmentsService() {
        Activity a = getActivity();
        Log.i(TAG, "startFetchDepartmentsService");
        // This initiates a prompt dialog for the user to confirm screen projection.

        if ( a != null ) {
            Intent service = new Intent(a, QueryIntentService.class);
            service.putExtra("job", QueryIntentService_FetchDep);
            a.startService(service);
            mButtonShow.setEnabled(false);
        }
    }

    @TargetApi(23)
    public void checkDrawOverlayPermission() {
        if ( Build.VERSION.SDK_INT > 21 ) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            /** check if we already  have permission to draw over other apps */
            if (!Settings.canDrawOverlays(activity)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity.getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_OVERLAY_DRAW);
            }
        }
    }

    @TargetApi(23)
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
        } else if (requestCode == REQUEST_OVERLAY_DRAW) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            if (Build.VERSION.SDK_INT > 21) {
                if (Settings.canDrawOverlays(activity)) {
                    // continue here - permission was granted
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            android.util.Log.d(TAG, "onActivityResult(REQUEST_IMAGE_CAPTURE): imageBitmap width:" + imageBitmap.getWidth() + " height:" + imageBitmap.getHeight());
            mPhotoTaken = imageBitmap;
            return;
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
        stopFloatingWindow();
        tearDownMediaProjection();
    }

    private void stopFloatingWindow() {
        FragmentActivity a = getActivity();
        if ( a != null ) {
            Intent service = new Intent(a, FloatingWindow.class);
            a.stopService(service);
        }
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

    private void SaveCaptureImage() {
        if ( mRenderScriptTask != null )
            mRenderScriptTask.cancel( false); // wait for cancel

        mRenderScriptTask = new RenderScriptTask();
        mRenderScriptTask.execute();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mPhotoTaken != null) {
            Canvas canvas = holder.lockCanvas();
            try {
                canvas.drawBitmap(mPhotoTaken, 0, 0, null);
            }
            finally {
                mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
            }
            mPhotoTaken = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private class RenderScriptTask extends AsyncTask<Float, Integer, Integer> {

        private boolean mIssued;

        protected Integer doInBackground(Float... values) {
            int index = -1;
            if (!isCancelled()) {
                //while ( !imageAvailable ) ; // wait for image

                mIssued = true;
                FileOutputStream fos = null;
                Bitmap bitmap = null;
                Image img = null;
                ImageReader reader = mImageReader;

                Activity activity = getActivity();
                DisplayMetrics metrics = new DisplayMetrics();

                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                try {
                    img = reader.acquireLatestImage();

                    if (img != null) {
                        Image.Plane[] planes = img.getPlanes();
                        if (planes[0].getBuffer() == null) {
                            return -1;
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

                        // TODO: 2018/11/21 use RenderScript to render it 
                        ByteBuffer buffer = planes[0].getBuffer();
                        for (int i = 0; i < height; ++i) {
                            for (int j = 0; j < width; ++j) {
//                                int pixel = 0;
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
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        Canvas canvas = mSurfaceView.getHolder().lockCanvas();
                        try {
                            canvas.drawBitmap(bitmap, 0, 0, null);
                        }
                        finally {
                            mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
                        }
                        // Environment.getExternalStorageDirectory()
                        // Environment.getDataDirectory() + "/data/com.example.android.screencapture"
                        Log.i(TAG, "image saved in" + Environment.getExternalStorageDirectory()  + name);
                        img.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
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
            return index;
        }

        void updateView(Integer result) {
            if (result != -1) {
                // Request UI update
//                mImageView.setImageBitmap(mBitmapsOut[result]);
//                mImageView.invalidate();
            }
        }

        protected void onPostExecute(Integer result) {
            updateView(result);
        }

        protected void onCancelled(Integer result) {
            if (mIssued) {
                updateView(result);
            }
        }
    }
}
