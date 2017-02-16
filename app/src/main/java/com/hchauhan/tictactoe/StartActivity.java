package com.hchauhan.tictactoe;

import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class StartActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("sessions");

    Calendar c;

    boolean isCodeGenerated = false;
    int sessionCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                while (!isCodeGenerated) {
                    Random random = new Random();
                    int n = random.nextInt(9000) + 1000;

                    if(dataSnapshot.child(String.valueOf(n)).exists()) {
                        if(dataSnapshot.child(String.valueOf(n)).child("p1").exists()) {
                            c = Calendar.getInstance();
                            if(dataSnapshot.child(String.valueOf(n)).child("start_time")
                                    .getValue(Long.class) - c.getTimeInMillis() >= 86400000) {
                                startSession(n);
                            }
                        } else {
                            startSession(n);
                        }
                    } else {
                        startSession(n);
                    }
                }

                if(isCodeGenerated) {
                    if(dataSnapshot.child(String.valueOf(sessionCode)).child("p2").exists()) {
                        Toast.makeText(getBaseContext(), "Game Started!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getBaseContext(), GameActivity.class)
                                .putExtra("session_code", String.valueOf(sessionCode))
                                .putExtra("my_player", "X"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
    }

    private void startSession(int n) {
        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        isCodeGenerated = true;
        sessionCode = n;

        myRef.child(String.valueOf(n)).child("p1").setValue(android_id);

        c = Calendar.getInstance();
        myRef.child(String.valueOf(n)).child("start_time").setValue(c.getTimeInMillis());

        TextView textView = (TextView) findViewById(R.id.code_text);
        textView.setText(String.valueOf(n));
    }

    @Override
    public void onBackPressed() {
//        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }
}
