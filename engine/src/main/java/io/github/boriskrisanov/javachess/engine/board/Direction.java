package io.github.boriskrisanov.javachess.engine.board;

public enum Direction {
    UP(-8),
    DOWN(8),
    LEFT(-1),
    RIGHT(1),
    TOP_LEFT(UP.offset + LEFT.offset),
    TOP_RIGHT(UP.offset + RIGHT.offset),
    BOTTOM_LEFT(DOWN.offset + LEFT.offset),
    BOTTOM_RIGHT(DOWN.offset + RIGHT.offset);


    public final int offset;

    Direction(int offset) {
        this.offset = offset;
    }
}
