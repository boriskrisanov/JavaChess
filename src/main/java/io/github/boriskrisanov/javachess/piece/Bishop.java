package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.EdgeDistance;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

public class Bishop extends Piece {
    @Override
    public  ArrayList<Square> getAttackingSquares() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();

        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.NORTHWEST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.NORTHEAST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.SOUTHEAST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.SOUTHWEST));

        return new ArrayList<>(
                moveIndexes.stream()
                        .map(Square::new)
                        .toList()
        );
    }

    @Override
    public  ArrayList<Move> getLegalMoves() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();
        ArrayList<Move> moves = new ArrayList<>();

        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.NORTHWEST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.NORTHEAST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.SOUTHEAST));
        moveIndexes.addAll(getLegalMoveIndexesOnDiagonal(Diagonal.SOUTHWEST));

        for (int moveIndex : moveIndexes) {
            Move move = new Move(this.position, new Square(moveIndex), board.getPieceOn(moveIndex));
            // var targetPiece = board.getPieceOn(moveIndex);

            // targetPiece != null && targetPiece.getColor() == this.color) ||
            if (board.isSideInCheckAfterMove(this.color, move)) {
                continue;
            }

            moves.add(move);
        }

        return moves;
    }

    public Bishop(Color color, Square position, Board board) {
        super(color, position, board);
    }

    private ArrayList<Integer> getLegalMoveIndexesOnDiagonal(Diagonal direction) {
        interface PossibleSquareCalculator {
            int calculate(int i);
        }

        var edgeDistance = new EdgeDistance(position);
        int index = position.getIndex();
        int maxI = 0;
        PossibleSquareCalculator possibleSquareCalculator = i -> 0;

        switch (direction) {
            case NORTHWEST -> {
                maxI = Math.min(edgeDistance.top, edgeDistance.left) + 1;
                possibleSquareCalculator = i -> index + (i * (-1 - 8));
            }
            case NORTHEAST -> {
                maxI = Math.min(edgeDistance.top, edgeDistance.right) + 1;
                possibleSquareCalculator = i -> index + (i * (-8 + 1));
            }
            case SOUTHEAST -> {
                maxI = Math.min(edgeDistance.bottom, edgeDistance.right) + 1;
                possibleSquareCalculator = i -> index + (i * (1 + 8));
            }
            case SOUTHWEST -> {
                maxI = Math.min(edgeDistance.bottom, edgeDistance.left) + 1;
                possibleSquareCalculator = i -> index + (i * (-1 + 8));
            }
        }

        ArrayList<Integer> possibleMoveIndexes = new ArrayList<>();

        for (int i = 1; i < maxI; i++) {
            int possibleSquare = possibleSquareCalculator.calculate(i);

            if (board.isSquareEmpty(possibleSquare)) {
                // Square is empty, we can keep moving in this direction
                possibleMoveIndexes.add(possibleSquare);
                continue;
            }

            Piece targetPiece = board.getPieceOn(possibleSquare);

            if (targetPiece.getColor() == this.color.getOpposite()) {
                // We can capture the piece but can't move any further in this direction
                possibleMoveIndexes.add(possibleSquare);
                break;
            } else if (targetPiece.getColor() == this.color) {
                // One of our own pieces is in the way, so we can't capture or move past it
                break;
            }
        }

        return possibleMoveIndexes;
    }

    private enum Diagonal {
        NORTHWEST,
        NORTHEAST,
        SOUTHEAST,
        SOUTHWEST
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'B' : 'b';
    }
}
