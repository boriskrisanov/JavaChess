package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

public class Knight extends Piece {

    public Knight(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public long getAttackingSquares() {
        long squares = 0;

        var edgeDistance = new EdgeDistance(position);

        if (edgeDistance.left >= 2 && edgeDistance.top >= 1) {
            squares |= BitboardUtils.withSquare(position - 8 - 2);
        }
        if (edgeDistance.left >= 1 && edgeDistance.top >= 2) {
            squares |= BitboardUtils.withSquare(position - 8 * 2 - 1);
        }
        if (edgeDistance.right >= 1 && edgeDistance.top >= 2) {
            squares |= BitboardUtils.withSquare(position - 8 * 2 + 1);
        }
        if (edgeDistance.left >= 2 && edgeDistance.bottom >= 1) {
            squares |= BitboardUtils.withSquare(position - 2 + 8);
        }
        if (edgeDistance.right >= 2 && edgeDistance.bottom >= 1) {
            squares |= BitboardUtils.withSquare(position + 2 + 8);
        }
        if (edgeDistance.left >= 1 && edgeDistance.bottom >= 2) {
            squares |= BitboardUtils.withSquare(position + 8 * 2 - 1);
        }
        if (edgeDistance.right >= 1 && edgeDistance.bottom >= 2) {
            squares |= BitboardUtils.withSquare(position + 8 * 2 + 1);
        }
        if (edgeDistance.right >= 2 && edgeDistance.top >= 1) {
            squares |= BitboardUtils.withSquare(position - 8 + 2);
        }

        return squares;
    }

    @Override
    protected long getAttackingSquaresIncludingPins() {
        if (pinDirection != null) {
            // If the knight is pinned, it doesn't have any legal moves because it always moves in both axes and
            // therefore can't move to the pin diagonal
            return 0;
        }

        return getAttackingSquares();
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'N' : 'n';
    }

    @Override
    public int getValue() {
        return 300;
    }
}
