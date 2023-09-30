package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.Board;
import io.github.boriskrisanov.javachess.board.EdgeDistance;
import io.github.boriskrisanov.javachess.board.Move;
import io.github.boriskrisanov.javachess.board.Square;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(Color color, Square position, Board board) {
        super(color, position, board);
    }

    @Override
    public  ArrayList<Square> getAttackingSquares() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();

        moveIndexes.addAll(getLegalMoveIndexesOnLine(Direction.NORTH));
        moveIndexes.addAll(getLegalMoveIndexesOnLine(Direction.SOUTH));
        moveIndexes.addAll(getLegalMoveIndexesOnLine(Direction.WEST));
        moveIndexes.addAll(getLegalMoveIndexesOnLine(Direction.EAST));

        return new ArrayList<>(
                moveIndexes.stream()
                        .map(Square::new)
                        .toList()
        );
    }

    @Override
    public  ArrayList<Move> getLegalMoves() {
        // TODO: Castling
        ArrayList<Square> attackingSquares = getAttackingSquares();
        var legalMoves = new ArrayList<Move>();

        attackingSquares.stream()
                .map(square -> new Move(this.position, square, board.getPieceOn(square.getIndex())))
                .filter(move -> !board.isSideInCheckAfterMove(this.color, move))
                .forEach(legalMoves::add);

        return legalMoves;
    }

    private ArrayList<Integer> getLegalMoveIndexesOnLine(Direction direction) {
        interface PossibleSquareCalculator {
            int calculate(int i);
        }

        var edgeDistance = new EdgeDistance(position);
        int index = position.getIndex();
        int maxI = 0;
        PossibleSquareCalculator possibleSquareCalculator = i -> 0;

        switch (direction) {
            case NORTH -> {
                maxI = edgeDistance.top + 1;
                possibleSquareCalculator = i -> index + (i * -8);
            }
            case SOUTH -> {
                maxI = edgeDistance.bottom + 1;
                possibleSquareCalculator = i -> index + (i * 8);
            }
            case WEST -> {
                maxI = edgeDistance.left + 1;
                possibleSquareCalculator = i -> index - i;
            }
            case EAST -> {
                maxI = edgeDistance.right + 1;
                possibleSquareCalculator = i -> index + i;
            }
        }

        ArrayList<Integer> possibleMoveIndexes = new ArrayList<>();

        for (int i = 1; i < maxI; i++) {
            int possibleSquare = possibleSquareCalculator.calculate(i);
            var targetPiece = board.getPieceOn(possibleSquare);

            if (targetPiece == null) {
                // Square is empty, we can keep moving in this direction
                possibleMoveIndexes.add(possibleSquare);
                continue;
            }

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
