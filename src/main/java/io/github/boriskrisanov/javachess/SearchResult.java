package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

// TODO: Add mate evaluation

/**
 * @param eval The evaluation from the perspective of the side to move in centipawns
 */
public record SearchResult(Piece.Color sideToMove, Move bestMove, int eval, long debugPositionsEvaluated) {
    /**
     * Positive if white has a better position, negative if black has a better position, zero if the position is equal
     * (in pawn equivalent value)
     */
    public double standardEval() {
        // Divide by 100 to convert centipawns to pawns
        return (double) (sideToMove == Piece.Color.BLACK ? eval * -1 : eval) / 100;
    }
}
