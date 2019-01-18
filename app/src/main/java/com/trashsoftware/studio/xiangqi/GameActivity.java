package com.trashsoftware.studio.xiangqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.trashsoftware.studio.xiangqi.program.Chess;
import com.trashsoftware.studio.xiangqi.views.DeathView;
import com.trashsoftware.studio.xiangqi.views.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DeathView black = findViewById(R.id.black_deaths);
        black.setRed(false);
        DeathView red = findViewById(R.id.red_deaths);
        red.setRed(true);

        gameView = findViewById(R.id.game_view);
        gameView.setParent(this);
        gameView.setDeathViews(black, red);

        black.setGameView(gameView);
        red.setGameView(gameView);
        gameView.startNewGame();
    }
}
