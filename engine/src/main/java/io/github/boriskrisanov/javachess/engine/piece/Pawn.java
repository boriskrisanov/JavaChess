package io.github.boriskrisanov.javachess.engine.piece;

import io.github.boriskrisanov.javachess.engine.*;
import io.github.boriskrisanov.javachess.engine.board.*;

import java.util.*;

public class Pawn extends Piece {

    public Pawn(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public long getAttackingSquares() {
        long bitboard = 0;

        var edgeDistance = new EdgeDistance(position);

        if (this.color == Color.WHITE) {
            if (edgeDistance.top > 0 && edgeDistance.right > 0) {
                bitboard |= BitboardUtils.withSquare(position - 8 + 1);
            }
            if (edgeDistance.top > 0 && edgeDistance.left > 0) {
                bitboard |= BitboardUtils.withSquare(position - 8 - 1);
            }
        } else {
            if (edgeDistance.bottom > 0 && edgeDistance.right > 0) {
                bitboard |= BitboardUtils.withSquare(position + 8 + 1);
            }
            if (edgeDistance.bottom > 0 && edgeDistance.left > 0) {
                bitboard |= BitboardUtils.withSquare(position + 8 - 1);
            }
        }

        return bitboard;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        var legalMoves = new ArrayList<Move>();

        var enPassantTargetSquare = board.getEnPassantTargetSquare();
        var attackingSquares = getAttackingSquares();
        var edgeDist = new EdgeDistance(position);

        // Ignore squares that are occupied by friendly pieces
        attackingSquares &= ~board.getPieces(this.color);

        // Captures
        for (int targetSquare : BitboardUtils.squaresOf(attackingSquares)) {
            if (board.getBoard()[targetSquare] == null || board.getBoard()[targetSquare].getColor() == this.color) {
                continue;
            }

            Piece capturedPiece = board.getPieceOn(targetSquare);
            Move move = new Move(this.position, targetSquare, capturedPiece);

            if (capturedPiece == null || capturedPiece.getColor() == this.color
                    || (targetSquare == position - 8 - 1 && (pinDirection != null && pinDirection != PinDirection.NEGATIVE_DIAGONAL))
                    || (targetSquare == position - 8 + 1 && (pinDirection != null && pinDirection != PinDirection.POSITIVE_DIAGONAL))
                    || (targetSquare == position + 8 - 1 && (pinDirection != null && pinDirection != PinDirection.POSITIVE_DIAGONAL))
                    || (targetSquare == position + 8 + 1 && (pinDirection != null && pinDirection != PinDirection.NEGATIVE_DIAGONAL))) {
                continue;
            }

            if ((color == Color.WHITE && Square.isLastRank(targetSquare)) || (color == Color.BLACK && Square.isFirstRank(targetSquare))) {
                legalMoves.add(new Move(position, targetSquare, capturedPiece, Promotion.KNIGHT));
                legalMoves.add(new Move(position, targetSquare, capturedPiece, Promotion.BISHOP));
                legalMoves.add(new Move(position, targetSquare, capturedPiece, Promotion.ROOK));
                legalMoves.add(new Move(position, targetSquare, capturedPiece, Promotion.QUEEN));
            } else {
                legalMoves.add(move);
            }
        }

        // En passant
        // The current pin detection method doesn't work for en passant, so isSideInCheckAfterMove is being used for this purpose.
        // This isn't very efficient, but en passant is rarely possible, so it's fine for now.
        if (enPassantTargetSquare != -1) {
            if (this.color == Color.WHITE) {
                if (enPassantTargetSquare == position - 8 - 1 && edgeDist.left > 0 && edgeDist.top > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position - 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        legalMoves.add(move);
                    }
                } else if (enPassantTargetSquare == position - 8 + 1 && edgeDist.right > 0 && edgeDist.top > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position + 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        legalMoves.add(move);
                    }
                }
            } else {
                if (enPassantTargetSquare == position + 8 - 1 && edgeDist.left > 0 && edgeDist.bottom > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position - 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        legalMoves.add(move);
                    }
                } else if (enPassantTargetSquare == position + 8 + 1 && edgeDist.right > 0 && edgeDist.bottom > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position + 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        legalMoves.add(move);
                    }
                }
            }
        }

        // Normal moves
        if (pinDirection == null || pinDirection == PinDirection.VERTICAL) {
            if (this.color == Color.WHITE) {
                if (board.isSquareEmpty(position - 8)) {
                    if (Square.isLastRank(position - 8)) {
                        legalMoves.add(new Move(position, position - 8, null, Promotion.KNIGHT));
                        legalMoves.add(new Move(position, position - 8, null, Promotion.BISHOP));
                        legalMoves.add(new Move(position, position - 8, null, Promotion.ROOK));
                        legalMoves.add(new Move(position, position - 8, null, Promotion.QUEEN));
                    } else {
                        legalMoves.add(new Move(position, position - 8, null));
                    }

                    if (Square.getRank(position) == 2 && board.isSquareEmpty(position - 8 * 2)) {
                        legalMoves.add(new Move(position, position - 8 * 2, null));
                    }
                }
            } else {
                if (board.isSquareEmpty(position + 8)) {
                    if (Square.isFirstRank(position + 8)) {
                        legalMoves.add(new Move(position, position + 8, null, Promotion.KNIGHT));
                        legalMoves.add(new Move(position, position + 8, null, Promotion.BISHOP));
                        legalMoves.add(new Move(position, position + 8, null, Promotion.ROOK));
                        legalMoves.add(new Move(position, position + 8, null, Promotion.QUEEN));
                    } else {
                        legalMoves.add(new Move(position, position + 8, null));
                    }

                    if (Square.getRank(position) == 7 && board.isSquareEmpty(position + 8 * 2)) {
                        legalMoves.add(new Move(position, position + 8 * 2, null));
                    }
                }
            }
        }

        if (board.isSideInCheck(this.color)) {
            var legalResolutionMoves = new ArrayList<Move>();

            for (Move move : legalMoves) {
                if (board.getCheckResolutions().contains(move.destination()) || (move.capturedPiece() != null && board.getCheckResolutions().contains(move.capturedPiece().getPosition()))) {
                    legalResolutionMoves.add(move);
                }
            }

            return legalResolutionMoves;
        }

        return legalMoves;
    }

    @Override
    protected long getAttackingSquaresIncludingPins() {
        return getAttackingSquares();
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'P' : 'p';
    }

    @Override
    public int getValue() {
        return 100;
    }
}
