package com.hchauhan.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GameActivity extends AppCompatActivity {

    String[][] board = new String[3][3];
    String turn;
    String firstTurn;

    String code;
    String player;

    int score_x;
    int score_y;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myGameRef;

    TextView player_text;
    TextView turn_text;

    TextView score_x_text;
    TextView score_y_text;

    TextView one;
    TextView two;
    TextView three;
    TextView four;
    TextView five;
    TextView six;
    TextView seven;
    TextView eight;
    TextView nine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        code = intent.getStringExtra("session_code");
        player = intent.getStringExtra("my_player");

        myGameRef = database.getReference("games").child(code);

        player_text = (TextView) findViewById(R.id.my_player_text);
        player_text.setText(player);

        turn_text = (TextView) findViewById(R.id.turn_text);
        score_x_text = (TextView) findViewById(R.id.x_score_text);
        score_y_text = (TextView) findViewById(R.id.y_score_text);

        one = (TextView) findViewById(R.id.one);
        two = (TextView) findViewById(R.id.two);
        three = (TextView) findViewById(R.id.three);
        four = (TextView) findViewById(R.id.four);
        five = (TextView) findViewById(R.id.five);
        six = (TextView) findViewById(R.id.six);
        seven = (TextView) findViewById(R.id.seven);
        eight = (TextView) findViewById(R.id.eight);
        nine = (TextView) findViewById(R.id.nine);

        startLocal();
        startFB();
        updateUI();

        myGameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateLocal(dataSnapshot);
                if(checkWinning().equals("-")) {
                    if(check_draw()) {
                        turn_text.setText("DRAW!");
                    }
                } else {
                    turn_text.setText(checkWinning() + " WON!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Cancelled", "Failed to read value.", databaseError.toException());
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(1);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(2);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(3);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(4);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(5);
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(6);
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(7);
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(8);
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_local(9);
            }
        });
    }

    private void play_local(int i) {
        if(turn.equals(player)) {
            if(is_valid_move(i)) {
                board[(i-1)/3][(i-1)%3] = player;
            }
        }
        turn = player.equals("X") ? "O" : "X";
        updateUI();
        updateFB(i);
        if(checkWinning().equals("-")) {
            if(check_draw()) {
                turn_text.setText("DRAW!");
            }
        } else {
            turn_text.setText(checkWinning() + " WON!");
        }
    }

    private void updateFB(int i) {
        myGameRef.child("board").child(String.valueOf(i)).setValue(board[(i-1)/3][(i-1)%3]);
        myGameRef.child("turn").setValue(turn);
    }

    private void startFB() {
        for(int i = 1; i <= 9; i++) {
            myGameRef.child("board").child(String.valueOf(i)).setValue("-");
        }
        myGameRef.child("turn").setValue("X");
        myGameRef.child("scores").child("X").setValue(0);
        myGameRef.child("scores").child("O").setValue(0);
        myGameRef.child("first_turn").setValue("X");
    }

    private void startLocal() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                board[i][j] = "-";
            }
        }
        turn = "X";
        score_x = 0;
        score_y = 0;
        firstTurn = "X";
    }

    private void updateLocal(DataSnapshot dataSnapshot) {
        if(!turn.equals(player)) {
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    if (!board[i][j].equals(dataSnapshot.child("board").child(String.valueOf((i * 3) + j + 1))
                            .getValue(String.class))) {
                        turn = player;
                        board[i][j] = dataSnapshot.child("board").child(String.valueOf((i * 3) + j + 1))
                                .getValue(String.class);
                    }
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        score_x_text.setText("X - " + String.valueOf(score_x));
        score_y_text.setText("O - " + String.valueOf(score_y));
        turn_text.setText("Turn - " + turn);

        one.setText(  !board[0][0].equals("-") ? board[0][0] : " ");
        two.setText(  !board[0][1].equals("-") ? board[0][1] : " ");
        three.setText(!board[0][2].equals("-") ? board[0][2] : " ");
        four.setText( !board[1][0].equals("-") ? board[1][0] : " ");
        five.setText( !board[1][1].equals("-") ? board[1][1] : " ");
        six.setText(  !board[1][2].equals("-") ? board[1][2] : " ");
        seven.setText(!board[2][0].equals("-") ? board[2][0] : " ");
        eight.setText(!board[2][1].equals("-") ? board[2][1] : " ");
        nine.setText( !board[2][2].equals("-") ? board[2][2] : " ");
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }

    private void restartGame() {
        for(int i = 1; i <= 9; i++) {
            myGameRef.child("board").child(String.valueOf(i)).setValue("-");
        }
        myGameRef.child("turn").setValue("X");
        myGameRef.child("scores").child("X").setValue(0);
        myGameRef.child("scores").child("O").setValue(0);
        myGameRef.child("first_turn").setValue("X");

    }

    private boolean is_valid_move(int i) {
        if(i < 1 || i > 9) return false;

        return board[(i - 1) / 3][(i - 1) % 3].equals("-");

    }

    private String checkWinning() {
        for(int i = 0; i < 2; i++) {
            if(board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                return board[i][0];
            }
            if(board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                return board[0][i];
            }
        }
        if(board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            return board[0][0];
        }
        if(board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            return board[0][2];
        }
        return "-";
    }

    private boolean check_draw() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j].equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }
}
