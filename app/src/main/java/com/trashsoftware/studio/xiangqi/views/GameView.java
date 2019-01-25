package com.trashsoftware.studio.xiangqi.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.trashsoftware.studio.xiangqi.GameActivity;
import com.trashsoftware.studio.xiangqi.LobbyActivity;
import com.trashsoftware.studio.xiangqi.R;
import com.trashsoftware.studio.xiangqi.connection.GameConnection;
import com.trashsoftware.studio.xiangqi.dock.NetGame;
import com.trashsoftware.studio.xiangqi.program.Chess;
import com.trashsoftware.studio.xiangqi.program.ChessGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GameView extends View implements NetGame {

    private final static long EXPIRE = 5000;

    private final static byte SELECT = 4;

    private final static byte DESELECT = 5;

    private final static byte MOVE = 6;

    private final static byte RECEIVED = 7;

    private final static byte TERMINATED = 8;

    private GameActivity parent;

    Paint boardPaint, redChessPaint, blackChessPaint,
            redTextPaint, blackTextPaint, chessSurfacePaint, selectionSurfacePaint,
            hintPaint;

    private final static int BLOCK_SIZE = 108;

    private ChessGame chessGame;

    private Chess selection;

    private DeathView blackDeaths, redDeaths;

    private InputStream inputStream;

    private OutputStream outputStream;

    private boolean isServer;

    private boolean isLocalGame;

    public GameView(Context context) {
        super(context);

        init();
    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        init();
    }

    public void setDeathViews(DeathView black, DeathView red) {
        blackDeaths = black;
        redDeaths = red;
    }

    public void startGame() {
        chessGame = new ChessGame();
        chessGame.initialize();
    }

    public void setParent(GameActivity parent) {
        this.parent = parent;
    }

    public void setConnection(InputStream is, OutputStream os, boolean server, boolean localGame) {
        inputStream = is;
        outputStream = os;
        isServer = server;
        isLocalGame = localGame;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    private void init() {
        setWillNotDraw(false);

        boardPaint = new Paint();
        boardPaint.setStrokeWidth(5);
        boardPaint.setColor(Color.BLACK);

        blackChessPaint = new Paint();
        blackChessPaint.setStyle(Paint.Style.STROKE);
        blackChessPaint.setStrokeWidth(8);
        blackChessPaint.setColor(Color.BLACK);

        redChessPaint = new Paint();
        redChessPaint.setStyle(Paint.Style.STROKE);
        redChessPaint.setStrokeWidth(8);
        redChessPaint.setColor(Color.RED);

        blackTextPaint = new Paint();
        blackTextPaint.setTextSize(60);
        blackTextPaint.setTextAlign(Paint.Align.CENTER);
        blackTextPaint.setColor(Color.BLACK);

        redTextPaint = new Paint();
        redTextPaint.setTextSize(60);
        redTextPaint.setTextAlign(Paint.Align.CENTER);
        redTextPaint.setColor(Color.RED);

        chessSurfacePaint = new Paint();
        chessSurfacePaint.setColor(Color.WHITE);

        selectionSurfacePaint = new Paint();
        selectionSurfacePaint.setColor(Color.CYAN);

        hintPaint = new Paint();
        hintPaint.setColor(Color.GRAY);
    }

    private void drawChessboard(Canvas canvas) {
        // draw frame
        int i;
        float x, y;
        for (i = 0; i < 10; i++) {
            y = getScreenY(i);
            canvas.drawLine(BLOCK_SIZE, y, 9 * BLOCK_SIZE, y, boardPaint);
        }
        for (i = 0; i < 9; i++) {
            x = getScreenX(i);
            canvas.drawLine(x, BLOCK_SIZE, x, 5 * BLOCK_SIZE, boardPaint);
            canvas.drawLine(x, 6 * BLOCK_SIZE, x, 10 * BLOCK_SIZE, boardPaint);
        }

        // draw connection
        canvas.drawLine(BLOCK_SIZE, 5 * BLOCK_SIZE,
                BLOCK_SIZE, 6 * BLOCK_SIZE, boardPaint);
        canvas.drawLine(9 * BLOCK_SIZE, 5 * BLOCK_SIZE,
                9 * BLOCK_SIZE, 6 * BLOCK_SIZE, boardPaint);

        // draw crosses
        canvas.drawLine(getScreenX(3), getScreenY(0),
                getScreenX(5), getScreenY(2), boardPaint);
        canvas.drawLine(getScreenX(3), getScreenY(7),
                getScreenX(5), getScreenY(9), boardPaint);
        canvas.drawLine(getScreenX(5), getScreenY(0),
                getScreenX(3), getScreenY(2), boardPaint);
        canvas.drawLine(getScreenX(5), getScreenY(7),
                getScreenX(3), getScreenY(9), boardPaint);

        drawCross(canvas, 2, 1);
        drawCross(canvas, 2, 7);
        drawCross(canvas, 7, 1);
        drawCross(canvas, 7, 7);
        for (i = 0; i < 9; i += 2) {
            drawCross(canvas, 3, i);
            drawCross(canvas, 6, i);
        }
    }

    private void drawCross(Canvas canvas, int r, int c) {
        float x = getScreenX(c);
        float y = getScreenY(r);
        float gap = BLOCK_SIZE / 12;
        float len = BLOCK_SIZE / 6;
        if (c != 8) {
            // draw right half cross
            canvas.drawLine(x + gap, y - gap, x + gap, y - gap - len, boardPaint);
            canvas.drawLine(x + gap, y - gap, x + gap + len, y - gap, boardPaint);
            canvas.drawLine(x + gap, y + gap, x + gap, y + gap + len, boardPaint);
            canvas.drawLine(x + gap, y + gap, x + gap + len, y + gap, boardPaint);
        }
        if (c != 0) {
            // draw left half cross
            canvas.drawLine(x - gap, y - gap, x - gap, y - gap - len, boardPaint);
            canvas.drawLine(x - gap, y - gap, x - gap - len, y - gap, boardPaint);
            canvas.drawLine(x - gap, y + gap, x - gap, y + gap + len, boardPaint);
            canvas.drawLine(x - gap, y + gap, x - gap - len, y + gap, boardPaint);
        }
    }

    private void drawChessOnBoard(Canvas canvas) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                Chess chess = chessGame.getChessAt(row, col);
                if (chess != null) {
                    Paint paint, textPaint, surfacePaint;
                    if (chess.isRed()) {
                        paint = redChessPaint;
                        textPaint = redTextPaint;
                    } else {
                        paint = blackChessPaint;
                        textPaint = blackTextPaint;
                    }
                    if (chess.isSelected()) {
                        surfacePaint = selectionSurfacePaint;
                    } else {
                        surfacePaint = chessSurfacePaint;
                    }
                    float x = getScreenX(col);
                    float y = getScreenY(row);
                    canvas.drawCircle(x, y, 48, paint);
                    canvas.drawCircle(x, y, 44, surfacePaint);
                    canvas.drawText(chess.toString(), x, y + 20, textPaint);
                }
                if (chessGame.getHintAt(row, col)) {
                    float x = getScreenX(col);
                    float y = getScreenY(row);
                    canvas.drawCircle(x, y, 20, hintPaint);
                }
            }
        }
    }

    private float getScreenX(int col) {
        return (col + 1) * BLOCK_SIZE;
    }

    private float getScreenY(int row) {
        return (row + 1) * BLOCK_SIZE;
    }

    private int[] getTouched(float x, float y) {
        int row = Math.round((y - BLOCK_SIZE) / BLOCK_SIZE);
        int col = Math.round((x - BLOCK_SIZE) / BLOCK_SIZE);

        return new int[]{row, col};
    }

    @Override
    public void listen() {
        if (isLocalGame) return;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[3];
                    byte action;
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        action = buffer[0];
                        if (read == 3) {
                            final int r = buffer[1] & 0xff;
                            final int c = buffer[2] & 0xff;
                            if (action == SELECT) {
//                            Platform.runLater(() -> {
//                                chessGame.selectPosition(r, c);
//                                selection = chessGame.getChessAt(r, c);
//                                invalidate();
//                            });
                                runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        chessGame.selectPosition(r, c);
                                        selection = chessGame.getChessAt(r, c);
                                        invalidate();
                                    }
                                });
                                replyConfirm();
                            } else if (action == DESELECT) {
//                            Platform.runLater(() -> {
//                                chessGame.deSelectPosition(r, c);
//                                selection = null;
//                                draw();
//                            });
                                runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        chessGame.deSelectPosition(r, c);
                                        selection = null;
                                        invalidate();
                                    }
                                });
                                replyConfirm();
                            } else if (action == MOVE) {
//                            Platform.runLater(() -> {
//                                chessGame.move(r, c);
//                                selection = null;
//                                draw();
//                                drawDead();
//                                checkTerminateAndShow();
//                            });
                                runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        chessGame.move(r, c);
                                        selection = null;
                                        invalidate();
                                        redDeaths.invalidate();
                                        blackDeaths.invalidate();
                                        checkTerminateAndShow();
                                    }
                                });
                                replyConfirm();
                            }
                        } else if (read == 1) {
                            if (buffer[0] == GameConnection.CONFIRM) {
//                            System.out.println("Confirmed!");
//                            stopTimer();
                            } else if (action == GameConnection.CLOSE) {
                                chessGame.terminate();
//                            Platform.runLater(() -> showAlert(resources.getString("error"), resources.getString("user_exit"), ""));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
//                Platform.runLater(() -> showAlert(resources.getString("error"), resources.getString("user_exit"), ""));
                }
            }
        });
        thread.start();

    }

    private void runLater(Runnable runnable) {
        parent.runOnUiThread(runnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawChessboard(canvas);
        drawChessOnBoard(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (chessGame.isTerminated()) return true;

        final float x = event.getX();
        final float y = event.getY();
        int[] pos = getTouched(x, y);

        if (clickable()) {
            if (checkPos(pos)) {
                if (selection == null) {
                    boolean click = chessGame.selectPosition(pos[0], pos[1]);
                    if (click) {
                        selection = chessGame.getChessAt(pos[0], pos[1]);
                        send(SELECT, pos[0], pos[1]);
                        invalidate();
                    }
                } else {
                    // move
                    Chess clicked = chessGame.getChessAt(pos[0], pos[1]);
                    if (clicked == selection) {
                        selection = null;
                        chessGame.deSelectPosition(pos[0], pos[1]);
                        send(DESELECT, pos[0], pos[1]);
                        invalidate();
                    } else {
                        if (chessGame.move(pos[0], pos[1])) {
                            // Successfully moved
                            selection = null;
                            send(MOVE, pos[0], pos[1]);
                            invalidate();
                            redDeaths.invalidate();
                            blackDeaths.invalidate();
                            checkTerminateAndShow();
                        }
                    }
                }
            }
        }

//        if (checkPos(pos)) {
//            if (selection == null) {
//                boolean click = chessGame.selectPosition(pos[0], pos[1]);
//                if (click) {
//                    selection = chessGame.getChessAt(pos[0], pos[1]);
//                    invalidate();
//                }
//            } else {
//                // move
//
//                Chess clicked = chessGame.getChessAt(pos[0], pos[1]);
//                if (clicked == selection) {
//                    selection = null;
//                    chessGame.deSelectPosition(pos[0], pos[1]);
//                    invalidate();
//                } else {
//                    if (chessGame.move(pos[0], pos[1])) {
//                        // Successfully moved
//                        selection = null;
//                        invalidate();
//                        if (chessGame.isTerminated()) {
//                            WinDialog dialog = new WinDialog();
//                            int p;
//                            if (chessGame.isRedWin()) {
//                                p = R.string.red_win;
//                            } else {
//                                p = R.string.black_win;
//                            }
//                            dialog.show(parent.getSupportFragmentManager(), parent.getString(p));
//                        }
//                        redDeaths.invalidate();
//                        blackDeaths.invalidate();
//                    }
//                }
//            }
//        }
        return super.onTouchEvent(event);
    }

    private static boolean checkPos(int[] pos) {
        return pos[0] >= 0 && pos[0] < 10 && pos[1] >= 0 && pos[1] < 9;
    }

    private boolean clickable() {
        if (isLocalGame) {
            return true;
        } else {
            return (isServer == chessGame.isRedTurn()) && !chessGame.isTerminated();
        }
    }

    private void checkTerminateAndShow() {
        if (chessGame.isTerminated()) {
            WinDialog dialog = new WinDialog();
            int p;
            if (chessGame.isRedWin()) {
                p = R.string.red_win;
            } else {
                p = R.string.black_win;
            }
            dialog.show(parent.getSupportFragmentManager(), parent.getString(p));
        }
    }

    private void send(final byte action, final int r, final int c) {
        if (isLocalGame) return;
        Thread sending = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] array = new byte[]{action, (byte) r, (byte) c};
                try {
                    outputStream.write(array);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sending.start();
    }

    private void replyConfirm() {
        try {
            outputStream.write(new byte[]{RECEIVED});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
