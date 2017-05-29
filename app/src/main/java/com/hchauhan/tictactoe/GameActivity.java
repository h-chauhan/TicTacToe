package com.hchauhan.tictactoe;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {

    String[][] board = new String[3][3];
    String turn, firstTurn, code, player;

    int score_x, score_y;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myGameRef;

    TextView player_text, turn_text, score_x_text, score_y_text;
    ImageView one, two, three, four, five, six, seven, eight, nine;

    Boolean gameEnd;
    private Button restartBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        code = intent.getStringExtra("session_code");
        player = intent.getStringExtra("my_player");

        myGameRef = database.getReference("games").child(code);

        player_text = (TextView) findViewById(R.id.my_player_text);
        player_text.setText("You are: " + player);

        turn_text = (TextView) findViewById(R.id.turn_text);

        score_x_text = (TextView) findViewById(R.id.x_score_text);
        score_y_text = (TextView) findViewById(R.id.y_score_text);

        one = (ImageView) findViewById(R.id.one);
        two = (ImageView) findViewById(R.id.two);
        three = (ImageView) findViewById(R.id.three);
        four = (ImageView) findViewById(R.id.four);
        five = (ImageView) findViewById(R.id.five);
        six = (ImageView) findViewById(R.id.six);
        seven = (ImageView) findViewById(R.id.seven);
        eight = (ImageView) findViewById(R.id.eight);
        nine = (ImageView) findViewById(R.id.nine);

        restartBtn = (Button) findViewById(R.id.restart_btn);

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
                        gameEnd = true;
                    }
                } else {
                    score_x = dataSnapshot.child("scores").child("X").getValue(Integer.class);
                    score_y = dataSnapshot.child("scores").child("O").getValue(Integer.class);
                    score_x_text.setText("X - " + String.valueOf(score_x));
                    score_y_text.setText("O - " + String.valueOf(score_y));
                    turn_text.setText(checkWinning() + " WON!");
                    gameEnd = true;
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

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myGameRef.child("restart").child(player).setValue(true);
            }
        });
    }

    private void play_local(int i) {
        if(!gameEnd) {
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
                    gameEnd = true;
                }
            } else {
                turn_text.setText(checkWinning() + " WON!");
                gameEnd = true;
                if(checkWinning().equals("X")) {
                    score_x += 1;
                    score_x_text.setText("X - " + String.valueOf(score_x));
                    myGameRef.child("scores").child(checkWinning()).setValue(score_x);
                } else {
                    score_y += 1;
                    score_y_text.setText("O - " + String.valueOf(score_y));
                    myGameRef.child("scores").child(checkWinning()).setValue(score_y);
                }
            }
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
        myGameRef.child("restart").child("X").setValue(false);
        myGameRef.child("restart").child("O").setValue(false);
        myGameRef.child("first_turn").setValue("X");
    }

    private void startLocal() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                board[i][j] = "-";
            }
        }
        turn = firstTurn = "X";
        score_x = score_y = 0;
        gameEnd = false;
    }

    private void updateLocal(DataSnapshot dataSnapshot) {
        if(dataSnapshot.child("restart").child("X").getValue(Boolean.class) &&
                dataSnapshot.child("restart").child("O").getValue(Boolean.class)) {
            // Clear Board
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    board[i][j] = "-";
                }
            }
            for(int i = 1; i <= 9; i++) {
                myGameRef.child("board").child(String.valueOf(i)).setValue("-");
            }
            // First Turn
            firstTurn = firstTurn.equals("X") ? "O" : "X";
            myGameRef.child("first_turn").setValue(firstTurn);
            // Turn
            turn = firstTurn;
            myGameRef.child("turn").setValue(turn);
            // Restart
            gameEnd = false;
            myGameRef.child("restart").child("X").setValue(false);
            myGameRef.child("restart").child("O").setValue(false);
            // Scores
            score_x = dataSnapshot.child("scores").child("X").getValue(Integer.class);
            score_y = dataSnapshot.child("scores").child("O").getValue(Integer.class);
            // Restart Button
            updateUI();
        }

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
        turn_text.setText(turn);

        switch (board[0][0]) {
            case "X":
                one.setImageResource(R.drawable.cross);
                break;
            case "O":
                one.setImageResource(R.drawable.knot);
                break;
            default:
                one.setImageDrawable(null);
                break;
        }

        switch (board[0][1]) {
            case "X":
                two.setImageResource(R.drawable.cross);
                break;
            case "O":
                two.setImageResource(R.drawable.knot);
                break;
            default:
                two.setImageDrawable(null);
                break;
        }

        switch (board[0][2]) {
            case "X":
                three.setImageResource(R.drawable.cross);
                break;
            case "O":
                three.setImageResource(R.drawable.knot);
                break;
            default:
                three.setImageDrawable(null);
                break;
        }

        switch (board[1][0]) {
            case "X":
                four.setImageResource(R.drawable.cross);
                break;
            case "O":
                four.setImageResource(R.drawable.knot);
                break;
            default:
                four.setImageDrawable(null);
                break;
        }

        switch (board[1][1]) {
            case "X":
                five.setImageResource(R.drawable.cross);
                break;
            case "O":
                five.setImageResource(R.drawable.knot);
                break;
            default:
                five.setImageDrawable(null);
                break;
        }

        switch (board[1][2]) {
            case "X":
                six.setImageResource(R.drawable.cross);
                break;
            case "O":
                six.setImageResource(R.drawable.knot);
                break;
            default:
                six.setImageDrawable(null);
                break;
        }

        switch (board[2][0]) {
            case "X":
                seven.setImageResource(R.drawable.cross);
                break;
            case "O":
                seven.setImageResource(R.drawable.knot);
                break;
            default:
                seven.setImageDrawable(null);
                break;
        }

        switch (board[2][1]) {
            case "X":
                eight.setImageResource(R.drawable.cross);
                break;
            case "O":
                eight.setImageResource(R.drawable.knot);
                break;
            default:
                eight.setImageDrawable(null);
                break;
        }

        switch (board[2][2]) {
            case "X":
                nine.setImageResource(R.drawable.cross);
                break;
            case "O":
                nine.setImageResource(R.drawable.knot);
                break;
            default:
                nine.setImageDrawable(null);
                break;
        }



    }

    private boolean is_valid_move(int i) {
        if(i < 1 || i > 9) return false;

        return board[(i - 1) / 3][(i - 1) % 3].equals("-");

    }

    private String checkWinning() {
        for(int i = 0; i < 3; i++) {
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

    @Override
    public void onBackPressed() {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }
}
