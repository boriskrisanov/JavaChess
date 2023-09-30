package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(Color color, Square position, Board board) {
        super(color, position, board);
    }

    @Override
    public  ArrayList<Square> getAttackingSquares() {
        var rook = new Rook(color, position, board);
        var bishop = new Bishop(color, position, board);

        var moveIndexes = new ArrayList<Integer>();

        moveIndexes.addAll(rook.getAttackingSquares().stream()
                .map(Square::getIndex)
                .toList()
        );
        moveIndexes.addAll(bishop.getAttackingSquares().stream()
                .map(Square::getIndex)
                .toList()
        );

        return new ArrayList<>(
                moveIndexes.stream()
                        .map(Square::new)
                        .toList()
        );
    }

    @Override
    public  ArrayList<Move> getLegalMoves() {
        var rook = new Rook(color, position, board);
        var bishop = new Bishop(color, position, board);

        var moves = new ArrayList<Move>();

        moves.addAll(rook.getLegalMoves());
        moves.addAll(bishop.getLegalMoves());

        return moves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'Q' : 'q';
    }
}
