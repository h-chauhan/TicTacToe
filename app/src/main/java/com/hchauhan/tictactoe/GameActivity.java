package com.hchauhan.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    @Override
    public void onBackPressed() {
//        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }
}
