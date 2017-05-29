package com.hchauhan.tictactoe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
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

public class JoinActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("sessions");
    DataSnapshot dataSnapshot;
    Calendar c;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final String[] code = {"0000"};

        pd = new ProgressDialog(JoinActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait");
        pd.show();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                pd.dismiss();
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
                    code[0] = String.valueOf(code_editText.getText());
                    if(dataSnapshot.child(code[0]).exists()) {
                        if(dataSnapshot.child(code[0]).child("p2").exists()) {
                            Toast.makeText(getBaseContext(),
                                    "The Game has already started. Please generate a new code.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            c = Calendar.getInstance();
                            if(dataSnapshot.child(code[0]).child("start_time")
                                    .getValue(Long.class) - c.getTimeInMillis() >= 86400000) {
                                Toast.makeText(getBaseContext(),
                                        "The code has expired. Please generate a new code.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                String android_id = Secure.getString(getContentResolver(),
                                        Secure.ANDROID_ID);
                                if(android_id.equals(dataSnapshot.child(code[0]).child("p1")
                                                .getValue(String.class))) {
                                    Toast.makeText(getBaseContext(),
                                            "You can't join a game started by you.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    myRef.child(String.valueOf(code[0])).child("p2")
                                            .setValue(android_id);
                                    Toast.makeText(getBaseContext(), "Game Started!",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getBaseContext(), GameActivity.class)
                                            .putExtra("session_code", code[0]).putExtra("my_player", "O"));
                                    finish();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Invalid Code!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
