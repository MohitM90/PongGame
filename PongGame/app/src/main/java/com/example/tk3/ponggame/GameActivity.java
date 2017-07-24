package com.example.tk3.ponggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import org.umundo.core.Message;
import org.umundo.core.Receiver;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
}

class GameThread extends Thread {

    /** Handle to the surface manager object we interact with */
    private SurfaceHolder _surfaceHolder;
    private Paint _paint;
    private GameState _state;
    private int _numPlayers;
    private int _readyPlayers = 1;
    public GameThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        Mundo m = Mundo.getInstance();
        m.getSub().setReceiver(new StateReceiver());
        int i = m.getNode().getSubscribers().size();

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point resolution = new Point();
        d.getSize(resolution);
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        float scale = dm.density;

        _surfaceHolder = surfaceHolder;
        _paint = new Paint();

        _numPlayers = m.getParticipants().size();

        if (resolution.x < resolution.y) {
            _state = new GameState(resolution.x, resolution.x, scale, _numPlayers);
        } else {
            _state = new GameState(resolution.y, resolution.y, scale, _numPlayers);
        }
    }

    @Override
    public void run() {


        while(true)
        {
            Canvas canvas = _surfaceHolder.lockCanvas();
            _state.update();
            _state.draw(canvas,_paint);
            _surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    public GameState getGameState()
    {
        return _state;
    }

    public class StateReceiver extends Receiver {
        public void receive(Message msg) {
            Mundo m = Mundo.getInstance();
            if (msg.getMeta().containsKey("start")) {
                GameThread.this.start();
            }
            if (m.getId() == 0 && msg.getMeta().containsKey("ready")) {
                _readyPlayers++;
                if (_readyPlayers == _numPlayers) {
                    try {
                        Message start = new Message();
                        start.putMeta("start", "");
                        GameThread.sleep(1000);
                        m.getPub().send(start);
                        GameThread.this.start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (msg.getMeta().containsKey("pos")) {
                int id = Integer.parseInt(msg.getMeta("id"));
                float f = Float.parseFloat(msg.getMeta("pos"));
                _state.moveOpponentBat(id, f);
            }
            if (msg.getMeta().containsKey("ballX")) {
                float x = Float.parseFloat(msg.getMeta("ballX"));
                float y = Float.parseFloat(msg.getMeta("ballY"));
                _state.setBall(x, y);
            }
        }
    }
}