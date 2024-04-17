package io.github.boriskrisanov.javachess.engine.board;

public class EdgeDistance {
    public final int left;
    public final int right;
    public final int top;
    public final int bottom;
    public final int topLeft;
    public final int topRight;
    public final int bottomLeft;
    public final int bottomRight;

    public EdgeDistance(int index) {
        int rank = index / 8 + 1;

        left = 8 - (rank * 8 - index);
        right = rank * 8 - index - 1;
        top = index / 8;
        bottom = (63 - index) / 8;

        topLeft = Math.min(top, left);
        topRight = Math.min(top, right);
        bottomLeft = Math.min(bottom, left);
        bottomRight = Math.min(bottom, right);
    }

    public static int get(int index, Direction direction) {
        EdgeDistance edgeDistance = new EdgeDistance(index);

        return switch (direction) {
            case UP -> edgeDistance.top;
            case DOWN -> edgeDistance.bottom;
            case LEFT -> edgeDistance.left;
            case RIGHT -> edgeDistance.right;
            case TOP_LEFT -> edgeDistance.topLeft;
            case TOP_RIGHT -> edgeDistance.topRight;
            case BOTTOM_LEFT -> edgeDistance.bottomLeft;
            case BOTTOM_RIGHT -> edgeDistance.bottomRight;
        };
    }
}
