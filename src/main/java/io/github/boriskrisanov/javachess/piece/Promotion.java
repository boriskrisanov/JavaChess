package io.github.boriskrisanov.javachess.piece;

public enum Promotion {
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT;

    @Override
    public String toString() {
        return switch (this) {
            case QUEEN -> "q";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KNIGHT -> "n";
        };
    }
}
