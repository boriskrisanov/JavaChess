package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;

public class CastlingRights {
    public boolean whiteCanShortCastle;
    public boolean whiteCanLongCastle;
    public boolean blackCanShortCastle;
    public boolean blackCanLongCastle;

    public CastlingRights(boolean whiteCanShortCastle, boolean whiteCanLongCastle, boolean blackCanShortCastle, boolean blackCanLongCastle) {
        this.whiteCanShortCastle = whiteCanShortCastle;
        this.whiteCanLongCastle = whiteCanLongCastle;
        this.blackCanShortCastle = blackCanShortCastle;
        this.blackCanLongCastle = blackCanLongCastle;
    }

    public CastlingRights(String fenCastlingRights) {
        whiteCanShortCastle = fenCastlingRights.contains("K");
        whiteCanLongCastle = fenCastlingRights.contains("Q");
        blackCanShortCastle = fenCastlingRights.contains("k");
        blackCanLongCastle = fenCastlingRights.contains("q");
    }

    public CastlingRights(CastlingRights other) {
        this.whiteCanShortCastle = other.whiteCanShortCastle;
        this.whiteCanLongCastle = other.whiteCanLongCastle;
        this.blackCanShortCastle = other.blackCanShortCastle;
        this.blackCanLongCastle = other.blackCanLongCastle;
    }

    @Override
    public String toString() {
        if (!whiteCanShortCastle && !whiteCanLongCastle && !blackCanShortCastle && !blackCanLongCastle) {
            return "-";
        }

        String string = "";

        if (whiteCanShortCastle) {
            string += "K";
        }
        if (whiteCanLongCastle) {
            string += "Q";
        }
        if (blackCanShortCastle) {
            string += "k";
        }
        if (blackCanLongCastle) {
            string += "q";
        }

        return string;
    }

    public boolean canWhiteShortCastle() {
        return whiteCanShortCastle;
    }

    public boolean canWhiteLongCastle() {
        return whiteCanLongCastle;
    }

    public boolean canBlackShortCastle() {
        return blackCanShortCastle;
    }

    public boolean canBlackLongCastle() {
        return blackCanLongCastle;
    }

    public void removeForSide(Piece.Color side, CastlingDirection direction) {
        if (side == Piece.Color.WHITE) {
            if (direction == CastlingDirection.SHORT) {
                whiteCanShortCastle = false;
            } else {
                whiteCanLongCastle = false;
            }
        } else {
            if (direction == CastlingDirection.SHORT) {
                blackCanShortCastle = false;
            } else {
                blackCanLongCastle = false;
            }
        }
    }

    public void removeForSide(Piece.Color side) {
        removeForSide(side, CastlingDirection.SHORT);
        removeForSide(side, CastlingDirection.LONG);
    }
}
