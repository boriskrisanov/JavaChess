package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
import static io.github.boriskrisanov.javachess.board.PinDirection.*;

public class Queen extends Piece {

    public Queen(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        /*
        The same board is being passed to the rook and bishop constructor, so there will technically still be a queen on
        the square where the rook/bishop is supposed to be. This shouldn't cause any problems but it's worth mentioning.
        TODO: Benchmark to check whether making the Rook and Bishop calls static would be faster (I'm assuming that this
         will be optimised but it might still be faster to make Rook/Bishop.getAttackingSquares() static.)
        */
        long rookAttackingSquares = new Rook(color, position, board).getAttackingSquares();
        long bishopAttackingSquares = new Bishop(color, position, board).getAttackingSquares();
        return rookAttackingSquares | bishopAttackingSquares;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public int getValue() {
        return 900;
    }
}
