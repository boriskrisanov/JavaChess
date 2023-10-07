package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;

class PinLines {
    private interface LoopCondition {
        boolean loopCondition(int i, EdgeDistance edgeDistance);
    }

    private static void computeForSide(Board board, Piece.Color side) {
        int kingPosition = board.getKing(side).getPosition();
        EdgeDistance edgeDistance = new EdgeDistance(kingPosition);
        Piece lastFriendlyPieceSeen = null;

        for (int i = kingPosition; Square.getRank(i) < edgeDistance.top; i += 8) {
            if (board.getBoard()[i].isSlidingPiece() && board.getBoard()[i].getColor() != side) {
                if (lastFriendlyPieceSeen == null) {
                    // King is in check from this direction
                    break;
                }
                lastFriendlyPieceSeen.setPinDirection(PinDirection.VERTICAL);
                break;
            }
            if (lastFriendlyPieceSeen != null && board.getBoard()[i] != null) {
                // There are more than 2 friendly pieces in front of the king, therefore none of them are pinned
                break;
            }
            if (board.getBoard()[i].getColor() == side) {
                lastFriendlyPieceSeen = board.getBoard()[i];
            }
        }
    }

    public static void compute(Board board) {
        computeForSide(board, Piece.Color.WHITE);
        computeForSide(board, Piece.Color.BLACK);
    }
}
