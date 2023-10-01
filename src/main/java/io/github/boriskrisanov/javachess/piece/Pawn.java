package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Pawn extends Piece {

    public Pawn(Color color, Square position, Board board) {
        super(color, position, board);
    }

    @Override
    public  ArrayList<Square> getAttackingSquares() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();

        var edgeDistance = new EdgeDistance(position);
        int index = position.getIndex();
        var enPassantTargetSquare = board.getEnPassantTargetSquare();

        if (this.color == Color.WHITE) {
            if (!board.isSquareEmpty(index - 8 + 1) && edgeDistance.top > 0 && edgeDistance.right > 0) {
                moveIndexes.add(index - 8 + 1);
            }
            if (!board.isSquareEmpty(index - 8 - 1) && edgeDistance.top > 0 && edgeDistance.left > 0) {
                moveIndexes.add(index - 8 - 1);
            }
            if (enPassantTargetSquare != null && ((enPassantTargetSquare.getIndex() == index - 8 - 1 && edgeDistance.top > 0 && edgeDistance.left > 0) || (enPassantTargetSquare.getIndex() == index - 8 + 1 && edgeDistance.top > 0 && edgeDistance.right > 0))) {
                moveIndexes.add(enPassantTargetSquare.getIndex());
            }
        } else {
            if (!board.isSquareEmpty(index + 8 + 1) && edgeDistance.bottom > 0 && edgeDistance.right > 0) {
                moveIndexes.add(index + 8 + 1);
            }
            if (!board.isSquareEmpty(index + 8 - 1) && edgeDistance.bottom > 0 && edgeDistance.left > 0) {
                moveIndexes.add(index + 8 - 1);
            }
            if (enPassantTargetSquare != null && ((enPassantTargetSquare.getIndex() == index + 8 - 1 && edgeDistance.bottom > 0 && edgeDistance.left > 0) || (enPassantTargetSquare.getIndex() == index + 8 + 1 && edgeDistance.bottom > 0 && edgeDistance.right > 0))) {
                moveIndexes.add(enPassantTargetSquare.getIndex());
            }
        }

        return new ArrayList<>(
                moveIndexes.stream()
                        .map(Square::new)
                        .toList()
        );
    }

    @Override
    public  ArrayList<Move> getLegalMoves() {
        // TODO: Promotion
        var legalMoves = new ArrayList<Move>();
        var attackingSquares = getAttackingSquares();
        var enPassantTargetSquare = board.getEnPassantTargetSquare();
        var index = position.getIndex();

        // Captures
        for (Square destinationSquare : attackingSquares) {
            boolean isMoveEnPassantCapture = enPassantTargetSquare != null && enPassantTargetSquare.getIndex() == destinationSquare.getIndex();
            Piece capturedPiece = board.getPieceOn(destinationSquare.getIndex());

            if (isMoveEnPassantCapture) {
                if (this.color == Color.WHITE) {
                    capturedPiece = board.getPieceOn(destinationSquare.getIndex() + 8);
                } else {
                    capturedPiece = board.getPieceOn(destinationSquare.getIndex() - 8);
                }
            }

            Move move = new Move(this.position, destinationSquare, capturedPiece);
//            var checkStateAfterMove = board.getCheckStateAfterMove(move);

            if (board.isSideInCheckAfterMove(this.color, move) || capturedPiece == null || capturedPiece.getColor() == this.color) {
                continue;
            }

            legalMoves.add(move);
        }

        // Normal moves
        if (this.color == Color.WHITE) {
            if (board.isSquareEmpty(index - 8)) {
                legalMoves.add(new Move(position, new Square(index - 8), null));

                if (position.getRank() == 2 && board.isSquareEmpty(index - 8 * 2)) {
                    legalMoves.add(new Move(position, new Square(index - 8 * 2), null));
                }
            }
        } else {
            if (board.isSquareEmpty(index + 8)) {
                legalMoves.add(new Move(position, new Square(index + 8), null));

                if (position.getRank() == 7 && board.isSquareEmpty(index + 8 * 2)) {
                    legalMoves.add(new Move(position, new Square(index + 8 * 2), null));
                }
            }
        }

        // En passant
        if (this.color == Color.WHITE) {

        } else {

        }

        return legalMoves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'P' : 'p';
    }
}
