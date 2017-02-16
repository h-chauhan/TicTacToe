package com.hchauhan.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String code = intent.getStringExtra("session_code");
        String player = intent.getStringExtra("my_player");

        TextView player_text = (TextView) findViewById(R.id.my_player_text);
        player_text.setText(player);

        Log.e("CODE", code);

    }

    @Override
    public void onBackPressed() {
//        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }
}
