package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.Piece;

public record Move(
         Square start,
         Square destination,
         Piece capturedPiece
) {
    @Override
    public String toString() {
        return String.format("Move(%s -> %s)", start, destination);
    }

    public String toUciString() {
        return start.toString() + destination.toString();
    }
}
