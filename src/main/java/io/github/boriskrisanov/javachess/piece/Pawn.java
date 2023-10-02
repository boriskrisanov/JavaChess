package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Pawn extends Piece {

    public Pawn(Color color, byte position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Byte> getAttackingSquares() {
        ArrayList<Byte> moveIndexes = new ArrayList<>();

        var edgeDistance = new EdgeDistance(position);
        var enPassantTargetSquare = board.getEnPassantTargetSquare();

        if (this.color == Color.WHITE) {
            if (!board.isSquareEmpty(position - 8 + 1) && edgeDistance.top > 0 && edgeDistance.right > 0) {
                moveIndexes.add((byte) (position - 8 + 1));
            }
            if (!board.isSquareEmpty(position - 8 - 1) && edgeDistance.top > 0 && edgeDistance.left > 0) {
                moveIndexes.add((byte) (position - 8 - 1));
            }
            if ((enPassantTargetSquare == position - 8 - 1 && edgeDistance.top > 0 && edgeDistance.left > 0)
                    || (enPassantTargetSquare == position - 8 + 1 && edgeDistance.top > 0 && edgeDistance.right > 0)) {
                moveIndexes.add(enPassantTargetSquare);
            }
        } else {
            if (!board.isSquareEmpty(position + 8 + 1) && edgeDistance.bottom > 0 && edgeDistance.right > 0) {
                moveIndexes.add((byte) (position + 8 + 1));
            }
            if (!board.isSquareEmpty(position + 8 - 1) && edgeDistance.bottom > 0 && edgeDistance.left > 0) {
                moveIndexes.add((byte) (position + 8 - 1));
            }
            if ((enPassantTargetSquare == position + 8 - 1 && edgeDistance.bottom > 0 && edgeDistance.left > 0) || (enPassantTargetSquare == position + 8 + 1 && edgeDistance.bottom > 0 && edgeDistance.right > 0)) {
                moveIndexes.add(enPassantTargetSquare);
            }
        }

        return moveIndexes;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        // TODO: Promotion
        var legalMoves = new ArrayList<Move>();
        var attackingSquares = getAttackingSquares();
        var enPassantTargetSquare = board.getEnPassantTargetSquare();

        // Captures
        for (byte destinationSquare : attackingSquares) {
            boolean isMoveEnPassantCapture = enPassantTargetSquare == destinationSquare;
            Piece capturedPiece = board.getPieceOn(destinationSquare);

            if (isMoveEnPassantCapture) {
                if (this.color == Color.WHITE) {
                    capturedPiece = board.getPieceOn(destinationSquare + 8);
                } else {
                    capturedPiece = board.getPieceOn(destinationSquare - 8);
                }
            }

            Move move = new Move(this.position, destinationSquare, capturedPiece);
//            var checkStateAfterMove = board.getCheckStateAfterMove(move);

            if (capturedPiece == null || capturedPiece.getColor() == this.color || board.isSideInCheckAfterMove(this.color, move)) {
                continue;
            }

            legalMoves.add(move);
        }

        // Normal moves
        if (this.color == Color.WHITE) {
            if (board.isSquareEmpty(position - 8)) {
                legalMoves.add(new Move(position, (byte) (position - 8), null));

                if (Square.getRank(position) == 2 && board.isSquareEmpty(position - 8 * 2)) {
                    legalMoves.add(new Move(position, (byte) (position - 8 * 2), null));
                }
            }
        } else {
            if (board.isSquareEmpty(position + 8)) {
                legalMoves.add(new Move(position, (byte) (position + 8), null));

                if (Square.getRank(position) == 7 && board.isSquareEmpty(position + 8 * 2)) {
                    legalMoves.add(new Move(position, (byte) (position + 8 * 2), null));
                }
            }
        }

        // En passant
        if (this.color == Color.WHITE) {

        } else {

        }

        ArrayList<Move> result = new ArrayList<>();

        for (Move move : legalMoves) {
            if (board.getCheckStateAfterMove(move) != this.color) {
                result.add(move);
            }
        }

        return result;

//        return (ArrayList<Move>) legalMoves.stream()
//                .filter(move -> board.getCheckStateAfterMove(move) != this.color)
//                .collect(Collectors.toList());
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'P' : 'p';
    }
}
