package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.EdgeDistance;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(Color color, Square position, Board board) {
        super(color, position, board);
    }

    @Override
    public  ArrayList<Square> getAttackingSquares() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();

        var edgeDistance = new EdgeDistance(position);
        int index = position.getIndex();

        if (edgeDistance.left >= 2 && edgeDistance.top >= 1) {
            moveIndexes.add(index - 8 - 2);
        }
        if (edgeDistance.left >= 1 && edgeDistance.top >= 2) {
            moveIndexes.add(index - 8 * 2 - 1);
        }
        if (edgeDistance.right >= 1 && edgeDistance.top >= 2) {
            moveIndexes.add(index - 8 * 2 + 1);
        }
        if (edgeDistance.left >= 2 && edgeDistance.bottom >= 1) {
            moveIndexes.add(index - 2 + 8);
        }
        if (edgeDistance.right >= 2 && edgeDistance.bottom >= 1) {
            moveIndexes.add(index + 2 + 8);
        }
        if (edgeDistance.left >= 1 && edgeDistance.bottom >= 2) {
            moveIndexes.add(index + 8 * 2 - 1);
        }
        if (edgeDistance.right >= 1 && edgeDistance.bottom >= 2) {
            moveIndexes.add(index + 8 * 2 + 1);
        }
        if (edgeDistance.right >= 2 && edgeDistance.top >= 1) {
            moveIndexes.add(index - 8 + 2);
        }

        return new ArrayList<>(
                moveIndexes.stream()
                        .map(Square::new)
                        .toList()
        );
    }

    @Override
    public  ArrayList<Move> getLegalMoves() {
        return new ArrayList<>(
                getAttackingSquares().stream()
                        .map(square -> new Move(this.position, square, board.getPieceOn(square.getIndex())))
                        .filter(move -> board.isSquareEmpty(move.destination()) || board.getPieceOn(move.destination()).getColor() == this.color.getOpposite())
                        .filter(move -> !board.isSideInCheckAfterMove(this.color, move))
                        .toList()
        );
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'N' : 'n';
    }
}
