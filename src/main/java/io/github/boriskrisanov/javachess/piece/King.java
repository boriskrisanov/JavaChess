package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class King extends Piece {

    public King(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        long bitboard = 0;
        var edgeDist = new EdgeDistance(position);

        if (edgeDist.left >= 1) {
            bitboard |= BitboardUtils.withSquare(position - 1);
        }
        if (edgeDist.right >= 1) {
            bitboard |= BitboardUtils.withSquare(position + 1);
        }
        if (edgeDist.top >= 1) {
            bitboard |= BitboardUtils.withSquare(position - 8);
        }
        if (edgeDist.bottom >= 1) {
            bitboard |= BitboardUtils.withSquare(position + 8);
        }
        if (edgeDist.left >= 1 && edgeDist.top >= 1) {
            bitboard |= BitboardUtils.withSquare(position - 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.top >= 1) {
            bitboard |= BitboardUtils.withSquare(position - 8 + 1);
        }
        if (edgeDist.left >= 1 && edgeDist.bottom >= 1) {
            bitboard |= BitboardUtils.withSquare(position + 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.bottom >= 1) {
            bitboard |= BitboardUtils.withSquare(position + 8 + 1);
        }

        return bitboard;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Integer> opponentAttackingSquares = board.getSquaresAttackedBySide(color.getOpposite());
        var castlingRights = board.getCastlingRights();
        var moves = new ArrayList<Move>();

        for (int targetSquare : BitboardUtils.squaresOf(getAttackingSquares())) {
            if (opponentAttackingSquares.contains(targetSquare) || (!board.isSquareEmpty(targetSquare) && board.getPieceOn(targetSquare).getColor() == this.color)) {
                continue;
            }

            Piece capturedPiece = board.getPieceOn(targetSquare);
            Move move = new Move(position, targetSquare, capturedPiece);

            // TODO: Improve efficiency (possibly by storing which pieces are defended by another piece and thus cannot be captured by the king)
            if (board.isSideInCheck(this.color) && board.isSideInCheckAfterMove(this.color, move)) {
                continue;
            }

            moves.add(move);
        }

        // Castling
        if (!board.isSideInCheck(this.color)) {
            if (color == Color.WHITE) {
                if (castlingRights.canWhiteShortCastle()
                        && !opponentAttackingSquares.contains(position + 1)
                        && !opponentAttackingSquares.contains(position + 2)
                        && board.isSquareEmpty(position + 1)
                        && board.isSquareEmpty(position + 2)
                ) {
                    moves.add(new Move(position, position + 2, null, CastlingDirection.SHORT));
                }
                if (castlingRights.canWhiteLongCastle()
                        && !opponentAttackingSquares.contains(position - 1)
                        && !opponentAttackingSquares.contains(position - 2)
                        && board.isSquareEmpty(position - 1)
                        && board.isSquareEmpty(position - 2)
                        && board.isSquareEmpty(position - 3)
                ) {
                    moves.add(new Move(position, position - 2, null, CastlingDirection.LONG));
                }
            } else {
                if (castlingRights.canBlackShortCastle()
                        && !opponentAttackingSquares.contains(position + 1)
                        && !opponentAttackingSquares.contains(position + 2)
                        && board.isSquareEmpty(position + 1)
                        && board.isSquareEmpty(position + 2)
                ) {
                    moves.add(new Move(position, position + 2, null, CastlingDirection.SHORT));
                }
                if (castlingRights.canBlackLongCastle()
                        && !opponentAttackingSquares.contains(position - 1)
                        && !opponentAttackingSquares.contains(position - 2)
                        && board.isSquareEmpty(position - 1)
                        && board.isSquareEmpty(position - 2)
                        && board.isSquareEmpty(position - 3)
                ) {
                    moves.add(new Move(position, position - 2, null, CastlingDirection.LONG));
                }
            }
        }

        return moves;
    }

    protected long getAttackingSquaresIncludingPins() {
        return getAttackingSquares();
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'K' : 'k';
    }

    @Override
    public int getValue() {
        return 0;
    }
}
