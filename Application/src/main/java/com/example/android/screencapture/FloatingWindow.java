package com.example.android.screencapture;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.common.logger.Log;

public class FloatingWindow extends Service {
    private static final String TAG = "FloatingWindow";

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private int mScreenDensity;

    private int mResultCode;
    private Intent mResultData;

    private Surface mSurface;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private SurfaceView mSurfaceView;
    private ImageReader mImageReader;

    private WindowManager _wm;
    private LinearLayout _ll;
    private Button _stop;
    private String mStatus;
    //private ImageView _imageView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // main thread, store data only
        mResultData = intent.getParcelableExtra(STATE_RESULT_DATA);
        mResultCode = intent.getIntExtra(STATE_RESULT_CODE, 0);

        return super.onStartCommand(intent, flags, startId);
    }

    private void setUpVirtualDisplay() {
        mSurface = mSurfaceView.getHolder().getSurface();

        while (!mSurface.isValid());

        Log.i(TAG, "Setting up a VirtualDisplay: " +
                mSurfaceView.getWidth() + "x" + mSurfaceView.getHeight() +
                " (" + mScreenDensity + ")");

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mSurfaceView.getWidth(), mSurfaceView.getHeight(), mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null, null);
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        mMediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        _wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        _ll = new LinearLayout(this);
        _ll.setBackgroundColor(Color.argb(66,255,0,0));

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        _ll.setLayoutParams(llp);

        _stop = new Button(this);
        mSurfaceView = new SurfaceView(this);

//        _imageView = new ImageView(this);
//        _imageView.setBackgroundColor(Color.argb(255,128,63,255));

        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _stop.setText("Start");
        _stop.setLayoutParams(btnParameters);
        mStatus = "Start";

        ViewGroup.LayoutParams ivParameters =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSurfaceView.setLayoutParams(ivParameters);
//        _imageView.setLayoutParams(ivParameters);

        Log.i(TAG, "mSurfaceView Surfae isValid:" + mSurfaceView.getHolder().getSurface().isValid());

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(220, 450,WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER;

        _ll.setOrientation(LinearLayout.VERTICAL);
        _ll.addView(_stop);
        _ll.addView(mSurfaceView);
//        _ll.addView(_imageView);
        try {
            _wm.addView(_ll, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        _wm.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        Log.i(TAG, "mSurfaceView densityDpi:" + mScreenDensity);

        _ll.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updatedParameters = parameters;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case  MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        touchedX = motionEvent.getRawX();
                        touchedY = motionEvent.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        updatedParameters.x = (int) ( x + (motionEvent.getRawX() - touchedX));
                        updatedParameters.y = (int) ( y + (motionEvent.getRawY() - touchedY));

                        _wm.updateViewLayout(_ll, updatedParameters);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        _stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mStatus == "Start") {
                    setUpMediaProjection();
                    setUpVirtualDisplay();
                    mStatus = "Stop";
                    _stop.setText("Stop");
                }
                else {
                    stopScreenCapture();
                    tearDownMediaProjection();
                    mStatus = "Start";
                }
//                Bitmap b = ScreenShot.takescreenshotOfRootView(_imageView);
//                _imageView.setImageBitmap(b);
                //_wm.removeView(_ll);
                //stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "FloatingWindow Destroy.", Toast.LENGTH_SHORT).show();
        stopScreenCapture();
        tearDownMediaProjection();
        super.onDestroy();
    }

    private void stopScreenCapture() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        _stop.setText(R.string.start);
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
}
