package io.github.boriskrisanov.javachess.board;

public class CastlingRights {
    boolean whiteCanShortCastle;
    boolean whiteCanLongCastle;
    boolean blackCanShortCastle;
    boolean blackCanLongCastle;

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
}
