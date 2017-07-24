package com.example.tk3.ponggame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.umundo.core.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

public class GameSetup extends AppCompatActivity {

    private Button createGameButton;
    private ArrayAdapter<String> adapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        createGameButton = (Button) findViewById(R.id.buttonCreateGame);
        createGameButton.setVisibility(View.INVISIBLE);

        String creator = getIntent().getStringExtra("nickname");
        Mundo m = Mundo.getInstance(this, creator);


        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, m.getParticipants());

        // assign adapter to listVIew
        listView.setAdapter(adapter);

        createGameButton.setVisibility(View.VISIBLE);
        createGameButton.setEnabled(true);

    }

    public void updateList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Mundo m = Mundo.getInstance();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if(m.getId() == 0){
                    createGameButton.setEnabled(true);
                }
            }
        });

    }

    public void startGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
