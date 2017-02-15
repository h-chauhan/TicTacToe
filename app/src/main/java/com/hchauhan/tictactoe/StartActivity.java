package com.hchauhan.tictactoe;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    Calendar c = Calendar.getInstance();
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
                            if(dataSnapshot.child(String.valueOf(n)).child("start_time")
                                    .getValue(Integer.class) - c.get(Calendar.SECOND) >= 86400) {
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
                        startActivity(new Intent(getBaseContext(), GameActivity.class)
                                .putExtra("session_code", sessionCode));
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
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        isCodeGenerated = true;
        sessionCode = n;
        myRef.child(String.valueOf(n)).child("p1").setValue(android_id);
        myRef.child(String.valueOf(n)).child("start_time").setValue(c.get(Calendar.SECOND));
        TextView textView = (TextView) findViewById(R.id.code_text);
        textView.setText(String.valueOf(n));
        Log.e("CODE GENERATED", String.valueOf(n));
    }
}
