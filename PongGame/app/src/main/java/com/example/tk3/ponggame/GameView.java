package com.example.tk3.ponggame;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.umundo.core.Message;

/**
 * TODO: document your custom view class.
 */
public class GameView extends SurfaceView  implements SurfaceHolder.Callback
{
    private GameThread _thread;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        if (!isInEditMode()) {
            _thread = new GameThread(holder, context, new Handler());
        }
    }

    float mLastTouchX;
    float mLastTouchY;
    int mActivePointerId;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = MotionEventCompat.getActionMasked(e);
        int pointerIndex;
        float x,y,dx,dy;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pointerIndex = MotionEventCompat.getActionIndex(e);
                x = MotionEventCompat.getX(e, pointerIndex);
                y = MotionEventCompat.getY(e, pointerIndex);
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = MotionEventCompat.getPointerId(e,0);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = MotionEventCompat.findPointerIndex(e, mActivePointerId);
                try {

                    x = MotionEventCompat.getX(e, pointerIndex);
                    y = MotionEventCompat.getY(e, pointerIndex);

                    // Calculate the distance moved
                    dx = x - mLastTouchX;
                    dy = y - mLastTouchY;

                    mLastTouchX = x;
                    mLastTouchY = y;
                    if (Mundo.getInstance().getId() < 2) {
                        return _thread.getGameState().moveBat(dx);
                    } else {
                        return _thread.getGameState().moveBat(dy);
                    }
                } catch (IllegalArgumentException ex) {}

        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Mundo m = Mundo.getInstance();
        Message ready = new Message();
        ready.putMeta("ready", "" + m.getId());
        m.getPub().send(ready);
        if (m.getParticipants().size() == 1) {
            _thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { /*Do nothing*/ }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //_thread.stop();
    }
}
