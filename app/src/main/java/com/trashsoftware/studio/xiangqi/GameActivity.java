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

        boolean isServer = false;
        boolean isLocal = true;
        boolean isPve = false;
        if (getIntent().getExtras() == null) {  // local pvp game
//            isServer = false;
//            isLocal = true;
        } else if (getIntent().getExtras().containsKey("isPve") &&
                getIntent().getExtras().getBoolean("isPve")) {  // local pve
            isPve = true;
        } else {
            isServer = getIntent().getExtras().getBoolean("isServer");
            isLocal = getIntent().getExtras().getBoolean("isLocal");
        }


        gameView.setConnection(LobbyActivity.getInputStream(), LobbyActivity.getOutputStream(),
                isServer, isLocal, isPve);

        black.setGameView(gameView);
        red.setGameView(gameView);

        gameView.listen();

        gameView.startGame();
    }
}
