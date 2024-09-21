package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

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
    public void getPseudoLegalMoves(ArrayList<Move> moves) {
        var enPassantTargetSquare = board.getEnPassantTargetSquare();
        var attackingSquares = getAttackingSquares();
        var edgeDist = new EdgeDistance(position);

        // Ignore squares that are occupied by friendly pieces
        attackingSquares &= ~board.getPieces(this.color);

        // Captures
        for (int targetSquare : BitboardUtils.squaresOf(attackingSquares)) {
            // TODO: Optimise with bitboards
            if (board.getBoard()[targetSquare] == null || board.getBoard()[targetSquare].getColor() == this.color) {
                continue;
            }

            Piece capturedPiece = board.getPieceOn(targetSquare);
            Move move = new Move(this.position, targetSquare, capturedPiece);

            if ((color == Color.WHITE && Square.isLastRank(targetSquare)) || (color == Color.BLACK && Square.isFirstRank(targetSquare))) {
                moves.add(new Move(position, targetSquare, capturedPiece, Promotion.KNIGHT));
                moves.add(new Move(position, targetSquare, capturedPiece, Promotion.BISHOP));
                moves.add(new Move(position, targetSquare, capturedPiece, Promotion.ROOK));
                moves.add(new Move(position, targetSquare, capturedPiece, Promotion.QUEEN));
            } else {
                moves.add(move);
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
                        moves.add(move);
                    }
                } else if (enPassantTargetSquare == position - 8 + 1 && edgeDist.right > 0 && edgeDist.top > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position + 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        moves.add(move);
                    }
                }
            } else {
                if (enPassantTargetSquare == position + 8 - 1 && edgeDist.left > 0 && edgeDist.bottom > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position - 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        moves.add(move);
                    }
                } else if (enPassantTargetSquare == position + 8 + 1 && edgeDist.right > 0 && edgeDist.bottom > 0) {
                    Move move = new Move(position, enPassantTargetSquare, board.getPieceOn(position + 1));
                    if (!board.isSideInCheckAfterMove(this.color, move)) {
                        moves.add(move);
                    }
                }
            }
        }

        // Normal moves
        if (this.color == Color.WHITE) {
            if (board.isSquareEmpty(position - 8)) {
                if (Square.isLastRank(position - 8)) {
                    moves.add(new Move(position, position - 8, null, Promotion.KNIGHT));
                    moves.add(new Move(position, position - 8, null, Promotion.BISHOP));
                    moves.add(new Move(position, position - 8, null, Promotion.ROOK));
                    moves.add(new Move(position, position - 8, null, Promotion.QUEEN));
                } else {
                    moves.add(new Move(position, position - 8, null));
                }

                if (Square.getRank(position) == 2 && board.isSquareEmpty(position - 8 * 2)) {
                    moves.add(new Move(position, position - 8 * 2, null));
                }
            }
        } else {
            if (board.isSquareEmpty(position + 8)) {
                if (Square.isFirstRank(position + 8)) {
                    moves.add(new Move(position, position + 8, null, Promotion.KNIGHT));
                    moves.add(new Move(position, position + 8, null, Promotion.BISHOP));
                    moves.add(new Move(position, position + 8, null, Promotion.ROOK));
                    moves.add(new Move(position, position + 8, null, Promotion.QUEEN));
                } else {
                    moves.add(new Move(position, position + 8, null));
                }

                if (Square.getRank(position) == 7 && board.isSquareEmpty(position + 8 * 2)) {
                    moves.add(new Move(position, position + 8 * 2, null));
                }
            }
        }
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
