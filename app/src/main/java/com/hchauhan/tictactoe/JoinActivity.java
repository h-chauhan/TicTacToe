package com.hchauhan.tictactoe;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

import javax.xml.datatype.Duration;

public class JoinActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("sessions");
    DataSnapshot dataSnapshot;
    Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataSnapshot = snapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });

        Button submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText code_editText = (EditText) findViewById(R.id.code_edittext);
                if(code_editText.getText().length() == 4) {
                    String code = String.valueOf(code_editText.getText());
                    if(dataSnapshot.child(code).exists()) {
                        if(dataSnapshot.child(code).exists()) {
                            if(dataSnapshot.child(code).child("p2").exists()) {
                                Toast.makeText(getBaseContext(), "The Game has already started. Please generate a new code.", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                String android_id = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                                myRef.child(String.valueOf(code)).child("p2").setValue(android_id);
                                startActivity(new Intent(getBaseContext(), GameActivity.class)
                                        .putExtra("session_code", code));
                                Toast.makeText(getBaseContext(), "Game Started!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Invalid Code!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            }
        });
    }
}
