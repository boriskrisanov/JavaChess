package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Bishop extends Piece {
    @Override
    public ArrayList<Byte> getAttackingSquares() {
        ArrayList<Byte> moves = new ArrayList<>();

        moves.addAll(getAttackingSquaresOnDiagonal(Diagonal.NORTHWEST));
        moves.addAll(getAttackingSquaresOnDiagonal(Diagonal.NORTHEAST));
        moves.addAll(getAttackingSquaresOnDiagonal(Diagonal.SOUTHEAST));
        moves.addAll(getAttackingSquaresOnDiagonal(Diagonal.SOUTHWEST));

        return moves;
    }

    public Bishop(Color color, byte position, Board board) {
        super(color, position, board);
    }

    private ArrayList<Byte> getAttackingSquaresOnDiagonal(Diagonal direction) {
        interface PossibleSquareCalculator {
            byte calculate(byte i);
        }

        var edgeDistance = new EdgeDistance(position);
        int maxI = 0;
        PossibleSquareCalculator possibleSquareCalculator = i -> (byte) 0;

        switch (direction) {
            case NORTHWEST -> {
                maxI = Math.min(edgeDistance.top, edgeDistance.left) + 1;
                possibleSquareCalculator = i -> (byte) (position + (i * (-1 - 8)));
            }
            case NORTHEAST -> {
                maxI = Math.min(edgeDistance.top, edgeDistance.right) + 1;
                possibleSquareCalculator = i -> (byte) (position + (i * (-8 + 1)));
            }
            case SOUTHEAST -> {
                maxI = Math.min(edgeDistance.bottom, edgeDistance.right) + 1;
                possibleSquareCalculator = i -> (byte) (position + (i * (1 + 8)));
            }
            case SOUTHWEST -> {
                maxI = Math.min(edgeDistance.bottom, edgeDistance.left) + 1;
                possibleSquareCalculator = i -> (byte) (position + (i * (-1 + 8)));
            }
        }

        ArrayList<Byte> possibleMoveIndexes = new ArrayList<>();

        for (byte i = 1; i < maxI; i++) {
            int possibleSquare = possibleSquareCalculator.calculate(i);

            if (board.isSquareEmpty(possibleSquare)) {
                // Square is empty, we can keep moving in this direction
                possibleMoveIndexes.add((byte) possibleSquare);
                continue;
            }

            Piece targetPiece = board.getPieceOn(possibleSquare);

            if (targetPiece.getColor() == this.color.getOpposite()) {
                // We can capture the piece but can't move any further in this direction
                possibleMoveIndexes.add((byte) possibleSquare);
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
