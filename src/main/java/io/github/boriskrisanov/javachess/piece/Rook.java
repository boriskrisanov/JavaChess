package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Rook extends Piece {

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        ArrayList<Integer> moves = new ArrayList<>();

        moves.addAll(getLegalMoveIndexesOnLine(Direction.NORTH));
        moves.addAll(getLegalMoveIndexesOnLine(Direction.SOUTH));
        moves.addAll(getLegalMoveIndexesOnLine(Direction.WEST));
        moves.addAll(getLegalMoveIndexesOnLine(Direction.EAST));

        return moves;
    }

    private ArrayList<Integer> getLegalMoveIndexesOnLine(Direction direction) {
        interface PossibleSquareCalculator {
            int calculate(int i);
        }

        var edgeDistance = new EdgeDistance(position);
        int maxI = 0;
        PossibleSquareCalculator possibleSquareCalculator = i -> (int) 0;

        switch (direction) {
            case NORTH -> {
                maxI = edgeDistance.top + 1;
                possibleSquareCalculator = i -> (int) (position + (i * -8));
            }
            case SOUTH -> {
                maxI = edgeDistance.bottom + 1;
                possibleSquareCalculator = i -> (int) (position + (i * 8));
            }
            case WEST -> {
                maxI = edgeDistance.left + 1;
                possibleSquareCalculator = i -> (int) (position - i);
            }
            case EAST -> {
                maxI = edgeDistance.right + 1;
                possibleSquareCalculator = i -> (int) (position + i);
            }
        }

        ArrayList<Integer> possibleMoves = new ArrayList<>();

        for (int i = 1; i < maxI; i++) {
            int possibleSquare = possibleSquareCalculator.calculate(i);
            var targetPiece = board.getPieceOn(possibleSquare);

            if (targetPiece == null) {
                // Square is empty, we can keep moving in this direction
                possibleMoves.add(possibleSquare);
                continue;
            }

            if (targetPiece.getColor() == this.color.getOpposite()) {
                // We can capture the piece but can't move any further in this direction
                possibleMoves.add(possibleSquare);
                break;
            } else if (targetPiece.getColor() == this.color) {
                // One of our own pieces is in the way, so we can't capture or move past it
                break;
            }
        }

        return possibleMoves;
    }

    private enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'R' : 'r';
    }
}
