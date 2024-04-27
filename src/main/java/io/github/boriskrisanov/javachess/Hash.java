package io.github.boriskrisanov.javachess;

import io.github.boriskrisanov.javachess.board.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;

public class Hash {
    private static final long[] randomValues = new long[(12 * 64) + 1 + 4 + 8];

    public static void init() {
        Random random = new Random(612042340);

        for (int i = 0; i < randomValues.length; i++) {
            randomValues[i] = random.nextLong();
        }
    }

    public static long hash(Board board) {
        // TODO: Use bitboards
        long result = 0;

        for (int i = 0; i < 64; i++) {
            if (board.isSquareEmpty(i)) {
                continue;
            }

            int pieceIndex = 0;

            // 0 is the pawn index, so no need to check for it
            if (board.getPieceOn(i) instanceof Knight) {
                pieceIndex = 1;
            } else if (board.getPieceOn(i) instanceof Bishop) {
                pieceIndex = 2;
            } else if (board.getPieceOn(i) instanceof Rook) {
                pieceIndex = 3;
            } else if (board.getPieceOn(i) instanceof Queen) {
                pieceIndex = 4;
            } else if (board.getPieceOn(i) instanceof King) {
                pieceIndex = 5;
            }

            if (board.getPieceOn(i).getColor() == Piece.Color.BLACK) {
                pieceIndex += 6;
            }

            result ^= randomValues[(pieceIndex * 64) + i];
        }

        if (board.getSideToMove() == Piece.Color.BLACK) {
            result ^= randomValues[(11 * 64) + 63 + 1];
        }

        if (board.getCastlingRights().whiteCanShortCastle) {
            result ^= randomValues[(11 * 64) + 63 + 2];
        }
        if (board.getCastlingRights().whiteCanLongCastle) {
            result ^= randomValues[(11 * 64) + 63 + 3];
        }
        if (board.getCastlingRights().blackCanShortCastle) {
            result ^= randomValues[(11 * 64) + 63 + 4];
        }
        if (board.getCastlingRights().blackCanLongCastle) {
            result ^= randomValues[(11 * 64) + 63 + 5];
        }

        if (board.getEnPassantTargetSquare() != -1) {
            result ^= randomValues[(11 * 64) + 63 + 5 + Square.getFile(board.getEnPassantTargetSquare())];
        }

        return result;
    }
}
