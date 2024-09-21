package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.piece.*;

import static io.github.boriskrisanov.javachess.piece.Piece.Color.BLACK;
import static io.github.boriskrisanov.javachess.piece.Piece.Color.WHITE;

public class BitboardManager {
    public long whitePawns = 0;
    public long whiteKnights = 0;
    public long whiteBishops = 0;
    public long whiteRooks = 0;
    public long whiteQueens = 0;
    public long whiteKing = 0;
    public long blackPawns = 0;
    public long blackKnights = 0;
    public long blackBishops = 0;
    public long blackRooks = 0;
    public long blackQueens = 0;
    public long blackKing = 0;

    public BitboardManager() {

    }

    public long getAllPieces() {
        return getPieces(WHITE) | getPieces(BLACK);
    }

    public long getPieces(Piece.Color side) {
        if (side == WHITE) {
            return whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        }
        return blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
    }
    
    public void addPiece(Promotion promotedPiece, Piece.Color side, int position) {
        if (side == WHITE) {
            switch (promotedPiece) {
                case QUEEN -> whiteQueens |= BitboardUtils.withSquare(position);
                case ROOK -> whiteRooks |= BitboardUtils.withSquare(position);
                case BISHOP -> whiteBishops |= BitboardUtils.withSquare(position);
                case KNIGHT -> whiteKnights |= BitboardUtils.withSquare(position);
            }
        } else {
            switch (promotedPiece) {
                case QUEEN -> blackQueens |= BitboardUtils.withSquare(position);
                case ROOK -> blackRooks |= BitboardUtils.withSquare(position);
                case BISHOP -> blackBishops |= BitboardUtils.withSquare(position);
                case KNIGHT -> blackKnights |= BitboardUtils.withSquare(position);
            }
        }
    }
    
    public void addPiece(Piece piece, int position) {
        if (piece.getColor() == WHITE) {
            switch (piece) {
                case Pawn pawn -> whitePawns |= BitboardUtils.withSquare(position);
                case Knight knight -> whiteKnights |= BitboardUtils.withSquare(position);
                case Bishop bishop -> whiteBishops |= BitboardUtils.withSquare(position);
                case Rook rook -> whiteRooks |= BitboardUtils.withSquare(position);
                case Queen queen -> whiteQueens |= BitboardUtils.withSquare(position);
                case King king -> whiteKing |= BitboardUtils.withSquare(position);
                default -> {
                }
            }
        } else {
            switch (piece) {
                case Pawn pawn -> blackPawns |= BitboardUtils.withSquare(position);
                case Knight knight -> blackKnights |= BitboardUtils.withSquare(position);
                case Bishop bishop -> blackBishops |= BitboardUtils.withSquare(position);
                case Rook rook -> blackRooks |= BitboardUtils.withSquare(position);
                case Queen queen -> blackQueens |= BitboardUtils.withSquare(position);
                case King king -> blackKing |= BitboardUtils.withSquare(position);
                default -> {
                }
            }
        }
    }

    public void removePiece(Piece piece, int position) {
        if (piece == null) {
            return;
        }

        if (piece.getColor() == WHITE) {
            switch (piece) {
                case Pawn pawn -> whitePawns &= ~BitboardUtils.withSquare(position);
                case Knight knight -> whiteKnights &= ~BitboardUtils.withSquare(position);
                case Bishop bishop -> whiteBishops &= ~BitboardUtils.withSquare(position);
                case Rook rook -> whiteRooks &= ~BitboardUtils.withSquare(position);
                case Queen queen -> whiteQueens &= ~BitboardUtils.withSquare(position);
                default -> {
                }
            }
        } else {
            switch (piece) {
                case Pawn pawn -> blackPawns &= ~BitboardUtils.withSquare(position);
                case Knight knight -> blackKnights &= ~BitboardUtils.withSquare(position);
                case Bishop bishop -> blackBishops &= ~BitboardUtils.withSquare(position);
                case Rook rook -> blackRooks &= ~BitboardUtils.withSquare(position);
                case Queen queen -> blackQueens &= ~BitboardUtils.withSquare(position);
                default -> {
                }
            }
        }
    }

    public void removePiece(Promotion promotedPiece, Piece.Color side, int position) {
        if (side == WHITE) {
            switch (promotedPiece) {
                case QUEEN -> whiteQueens &= ~BitboardUtils.withSquare(position);
                case ROOK -> whiteRooks &= ~BitboardUtils.withSquare(position);
                case BISHOP -> whiteBishops &= ~BitboardUtils.withSquare(position);
                case KNIGHT -> whiteKnights &= ~BitboardUtils.withSquare(position);
            }
        } else {
            switch (promotedPiece) {
                case QUEEN -> blackQueens &= ~BitboardUtils.withSquare(position);
                case ROOK -> blackRooks &= ~BitboardUtils.withSquare(position);
                case BISHOP -> blackBishops &= ~BitboardUtils.withSquare(position);
                case KNIGHT -> blackKnights &= ~BitboardUtils.withSquare(position);
            }
        }
    }

    public void movePiece(Piece piece, Piece capturedPiece, int start, int destination) {
        // This will still work if capturedPiece is null as no bitboards will be updated
        // Remove captured piece
        if (capturedPiece instanceof Pawn) {
            if (capturedPiece.getColor() == WHITE) {
                whitePawns &= ~BitboardUtils.withSquare(destination);
            } else {
                blackPawns &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Knight) {
            if (capturedPiece.getColor() == WHITE) {
                whiteKnights &= ~BitboardUtils.withSquare(destination);
            } else {
                blackKnights &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Bishop) {
            if (capturedPiece.getColor() == WHITE) {
                whiteBishops &= ~BitboardUtils.withSquare(destination);
            } else {
                blackBishops &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Rook) {
            if (capturedPiece.getColor() == WHITE) {
                whiteRooks &= ~BitboardUtils.withSquare(destination);
            } else {
                blackRooks &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Queen) {
            if (capturedPiece.getColor() == WHITE) {
                whiteQueens &= ~BitboardUtils.withSquare(destination);
            } else {
                blackQueens &= ~BitboardUtils.withSquare(destination);
            }
        }

        // Move piece
        if (piece instanceof Pawn) {
            if (piece.getColor() == WHITE) {
                whitePawns &= ~(BitboardUtils.withSquare(start));
                whitePawns |= BitboardUtils.withSquare(destination);
            } else {
                blackPawns &= ~(BitboardUtils.withSquare(start));
                blackPawns |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Knight) {
            if (piece.getColor() == WHITE) {
                whiteKnights &= ~(BitboardUtils.withSquare(start));
                whiteKnights |= BitboardUtils.withSquare(destination);
            } else {
                blackKnights &= ~(BitboardUtils.withSquare(start));
                blackKnights |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Bishop) {
            if (piece.getColor() == WHITE) {
                whiteBishops &= ~(BitboardUtils.withSquare(start));
                whiteBishops |= BitboardUtils.withSquare(destination);
            } else {
                blackBishops &= ~(BitboardUtils.withSquare(start));
                blackBishops |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Rook) {
            if (piece.getColor() == WHITE) {
                whiteRooks &= ~(BitboardUtils.withSquare(start));
                whiteRooks |= BitboardUtils.withSquare(destination);
            } else {
                blackRooks &= ~(BitboardUtils.withSquare(start));
                blackRooks |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Queen) {
            if (piece.getColor() == WHITE) {
                whiteQueens &= ~(BitboardUtils.withSquare(start));
                whiteQueens |= BitboardUtils.withSquare(destination);
            } else {
                blackQueens &= ~(BitboardUtils.withSquare(start));
                blackQueens |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof King) {
            if (piece.getColor() == WHITE) {
                whiteKing &= ~(BitboardUtils.withSquare(start));
                whiteKing |= BitboardUtils.withSquare(destination);
            } else {
                blackKing &= ~(BitboardUtils.withSquare(start));
                blackKing |= BitboardUtils.withSquare(destination);
            }
        }
    }
}
