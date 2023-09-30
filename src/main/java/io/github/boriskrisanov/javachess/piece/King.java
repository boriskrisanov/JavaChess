package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.EdgeDistance;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

public class King extends Piece {

    public King(Color color, Square position, Board board) {
        super(color, position, board);
    }

    @Override
    public  ArrayList<Square> getAttackingSquares() {
        var moveIndexes = new ArrayList<Integer>();
        var edgeDist = new EdgeDistance(position);
        int index = position.getIndex();

        if (edgeDist.left >= 1) {
            moveIndexes.add(index - 1);
        }
        if (edgeDist.right >= 1) {
            moveIndexes.add(index + 1);
        }
        if (edgeDist.top >= 1) {
            moveIndexes.add(index - 8);
        }
        if (edgeDist.bottom >= 1) {
            moveIndexes.add(index + 8);
        }
        if (edgeDist.left >= 1 && edgeDist.top >= 1) {
            moveIndexes.add(index - 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.top >= 1) {
            moveIndexes.add(index - 8 + 1);
        }
        if (edgeDist.left >= 1 && edgeDist.bottom >= 1) {
            moveIndexes.add(index + 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.bottom >= 1) {
            moveIndexes.add(index + 8 + 1);
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
                        .filter(square -> board.getPieceOn(square) == null || board.getPieceOn(square).getColor() == this.color.getOpposite())
                        .map(square -> new Move(this.position, square, board.getPieceOn(square)))
                        .filter(move -> !board.isSideInCheckAfterMove(this.color, move))
                        .toList()
        );
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'K' : 'k';
    }
}
