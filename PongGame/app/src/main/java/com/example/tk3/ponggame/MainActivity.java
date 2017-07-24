package com.example.tk3.ponggame;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.umundo.core.*;

public class MainActivity extends AppCompatActivity {

    private EditText nicknameText;
    private Button nicknameButton;
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.loadLibrary("umundoNativeJava_d");

        initWifi();

        self = getApplicationContext();
        nicknameText = (EditText) findViewById(R.id.nicknameText);

        nicknameButton = (Button) findViewById(R.id.nicknameButton);
        nicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go ahead only if nickname set
                String nickname = nicknameText.getText().toString();
                if(!nickname.isEmpty()){
                    Intent gameIntent = new Intent(self, GameSetup.class);
                    gameIntent.putExtra("nickname", nickname);
                    startActivity(gameIntent);
                }
            }
        });
    }

    private void initWifi(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock mcLock = wifi.createMulticastLock("mylock");
            mcLock.acquire();
            // mcLock.release();
        } else {
            Log.v("android-umundo", "Cannot get WifiManager");
        }
    }
}
