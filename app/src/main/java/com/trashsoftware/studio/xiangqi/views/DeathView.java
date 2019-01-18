package com.trashsoftware.studio.xiangqi.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.trashsoftware.studio.xiangqi.program.Chess;

import java.util.ArrayList;

public class DeathView extends View {

    private GameView gameView;

    private boolean red;

    private final static int SCALE = 72;

    private Paint redTextPaint, blackTextPaint;

    public DeathView(Context context) {
        super(context);

        initialize();
    }

    public DeathView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initialize();
    }

    private void initialize() {
        setWillNotDraw(false);

        blackTextPaint = new Paint();
        blackTextPaint.setTextSize(40);
        blackTextPaint.setTextAlign(Paint.Align.CENTER);
        blackTextPaint.setColor(Color.BLACK);

        redTextPaint = new Paint();
        redTextPaint.setTextSize(40);
        redTextPaint.setTextAlign(Paint.Align.CENTER);
        redTextPaint.setColor(Color.RED);
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void setRed(boolean red) {
        this.red = red;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ArrayList<Chess> list;
        Paint textPaint;
        Paint paint;
        Paint fillPaint = gameView.chessSurfacePaint;
        if (red) {
            list = gameView.getChessGame().getRedDeaths();
            textPaint = redTextPaint;
            paint = gameView.redChessPaint;
        } else {
            list = gameView.getChessGame().getBlackDeaths();
            textPaint = blackTextPaint;
            paint = gameView.blackChessPaint;
        }
        for (int i = 0; i < list.size(); i++) {
            float x = (i + 1) * SCALE;
            float y = (float) SCALE / 2;
            canvas.drawCircle(x, y, 32, paint);
            canvas.drawCircle(x, y, 28, fillPaint);
            canvas.drawText(list.get(i).toString(), x, y + 14, textPaint);
        }
    }
}
