package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class Pawn extends Piece {

    public Pawn(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        ArrayList<Integer> moveIndexes = new ArrayList<>();

        var edgeDistance = new EdgeDistance(position);
        var enPassantTargetSquare = board.getEnPassantTargetSquare();

        if (this.color == Color.WHITE) {
            if (edgeDistance.top > 0 && edgeDistance.right > 0) {
                moveIndexes.add(position - 8 + 1);
            }
            if (edgeDistance.top > 0 && edgeDistance.left > 0) {
                moveIndexes.add(position - 8 - 1);
            }
            if ((enPassantTargetSquare == position - 8 - 1 && edgeDistance.top > 0 && edgeDistance.left > 0)
                    || (enPassantTargetSquare == position - 8 + 1 && edgeDistance.top > 0 && edgeDistance.right > 0)) {
                moveIndexes.add(enPassantTargetSquare);
            }
        } else {
            if (edgeDistance.bottom > 0 && edgeDistance.right > 0) {
                moveIndexes.add(position + 8 + 1);
            }
            if (edgeDistance.bottom > 0 && edgeDistance.left > 0) {
                moveIndexes.add(position + 8 - 1);
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
        var enPassantTargetSquare = board.getEnPassantTargetSquare();
        var attackingSquares = getAttackingSquares();

        // Captures
        for (int targetSquare : attackingSquares) {
            if (board.getBoard()[targetSquare] == null || board.getBoard()[targetSquare].getColor() == this.color) {
                continue;
            }

            boolean isMoveEnPassantCapture = enPassantTargetSquare == targetSquare;
            Piece capturedPiece = board.getPieceOn(targetSquare);

            if (isMoveEnPassantCapture && pinDirection != null) {
                if (this.color == Color.WHITE) {
                    capturedPiece = board.getPieceOn(targetSquare + 8);
                } else {
                    capturedPiece = board.getPieceOn(targetSquare - 8);
                }
            }

            Move move = new Move(this.position, targetSquare, capturedPiece);

            if (capturedPiece == null || capturedPiece.getColor() == this.color
                    || (targetSquare == position - 8 - 1 && (pinDirection != null && pinDirection != PinDirection.NEGATIVE_DIAGONAL))
                    || (targetSquare == position - 8 + 1 && (pinDirection != null && pinDirection != PinDirection.POSITIVE_DIAGONAL))
                    || (targetSquare == position + 8 - 1 && (pinDirection != null && pinDirection != PinDirection.POSITIVE_DIAGONAL))
                    || (targetSquare == position + 8 - 1 && (pinDirection != null && pinDirection != PinDirection.NEGATIVE_DIAGONAL))) {
                continue;
            }

            legalMoves.add(move);
        }

        // Normal moves
        if (pinDirection == null || pinDirection == PinDirection.VERTICAL) {
            if (this.color == Color.WHITE) {
                if (board.isSquareEmpty(position - 8)) {
                    legalMoves.add(new Move(position, position - 8, null));

                    if (Square.getRank(position) == 2 && board.isSquareEmpty(position - 8 * 2)) {
                        legalMoves.add(new Move(position, position - 8 * 2, null));
                    }
                }
            } else {
                if (board.isSquareEmpty(position + 8)) {
                    legalMoves.add(new Move(position, position + 8, null));

                    if (Square.getRank(position) == 7 && board.isSquareEmpty(position + 8 * 2)) {
                        legalMoves.add(new Move(position, position + 8 * 2, null));
                    }
                }
            }
        }

        if (board.isSideInCheck(this.color)) {
            var legalResolutionMoves = new ArrayList<Move>();

            for (Move move : legalMoves) {
                if (board.getCheckResolutions().contains(move.destination())) {
                    legalResolutionMoves.add(move);
                }
            }

            return legalResolutionMoves;
        }

        return legalMoves;
    }

    @Override
    protected ArrayList<Integer> getAttackingSquaresIncludingPins() {
        return getAttackingSquares();
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'P' : 'p';
    }
}
