package com.trashsoftware.studio.xiangqi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doubleGameAction(View view) {
        doubleGame();
    }

    private void doubleGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void lanGameAction(View view) {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
