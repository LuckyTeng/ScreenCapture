package com.example.android.screencapture;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FloatingWindow extends Service {
    private WindowManager _wm;
    private LinearLayout _ll;
    private Button _stop;
    private ImageView _imageView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        _ll = new LinearLayout(this);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        _ll.setBackgroundColor(Color.argb(66,255,0,0));
        _ll.setLayoutParams(llp);

        _stop = new Button(this);
        _imageView = new ImageView(this);
        _imageView.setBackgroundColor(Color.argb(255,128,63,255));

        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _stop.setText("Stop");
        _stop.setLayoutParams(btnParameters);

        ViewGroup.LayoutParams ivParameters =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        _imageView.setLayoutParams(ivParameters);


        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(600, 450,WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER;

        _ll.setOrientation(LinearLayout.VERTICAL);
        _ll.addView(_stop);
        _ll.addView(_imageView);
        _wm.addView(_ll, parameters);

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
                Bitmap b = ScreenShot.takescreenshotOfRootView(_imageView);
                _imageView.setImageBitmap(b);
                //_wm.removeView(_ll);

                //stopSelf();
            }
        });
    }
}
