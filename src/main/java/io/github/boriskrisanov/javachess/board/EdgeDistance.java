package io.github.boriskrisanov.javachess.board;

/**
 * Calculates the distance from a square to each edge of the board.
 * For example, the square A8 has the edge distance left=0, right=7, top=0, bottom=7.
 */
public class EdgeDistance {
    /** The number of squares between this square and the left edge of the board */
    public final int left;

    /** The number of squares between this square and the right edge of the board */
    public final int right;

    /** The number of squares between this square and the top edge of the board */
    public final int top;

    /** The number of squares between this square and the bottom edge of the board */
    public final int bottom;

    /**
     * Calculates the distance from a square to each edge of the board.
     * For example, the square A8 has the edge distance left=0, right=7, top=0, bottom=7.
     */
    public EdgeDistance(byte index) {
        int rank = index / 8 + 1;

        left = 8 - (rank * 8 - index);
        right = rank * 8 - index - 1;
        top = index / 8;
        bottom = (63 - index) / 8;
    }
}
