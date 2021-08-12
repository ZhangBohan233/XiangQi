package com.trashsoftware.studio.xiangqi.program.chessAi;

import com.trashsoftware.studio.xiangqi.program.ChessGame;

import java.util.*;

public class ChessAI {

    private int limit;

    /**
     * Whether this AI controls the red side.
     */
    private boolean isRed;

    private Move bestMove;

    public ChessAI(int limit, boolean isRed) {
        this.limit = limit;
        this.isRed = isRed;
    }

    public boolean isRed() {
        return isRed;
    }

    public Move move(ChessGame game) {
        alphaBeta(new SimulatorGame(game, true), limit, -100000, 100000,
                true, new ArrayList());

        return bestMove;
    }

    private int alphaBeta(SimulatorGame simulator, int depth, int alpha, int beta,
                          boolean isAiMoving, List moveSequence) {
        simulator.setSide(!isAiMoving);
        if (depth == 0) {
            return simulator.evaluate();
        }
        Queue<Move> moves = simulator.getAllPossibleMoves();
        while (!moves.isEmpty()) {
            Move nextMove = moves.remove();
            simulator.move(nextMove);

            List<Move> newSeq = new ArrayList<>(moveSequence);
            newSeq.add(nextMove);
            int val = -alphaBeta(simulator, depth - 1, -beta, -alpha, !isAiMoving, newSeq);
            simulator.undoLastMove();

            if (val >= beta) {
                return beta;
            }
            if (val > alpha) {
                alpha = val;
                if (depth == limit) {
                    System.out.println(val);
                    bestMove = nextMove;
                }
            }
        }
        return alpha;
    }

}
