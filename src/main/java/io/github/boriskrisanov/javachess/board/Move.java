package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;

public record Move(
        byte start,
        byte destination,
         Piece capturedPiece
) {
    @Override
    public String toString() {
        return String.format("Move(%s -> %s)", new Square(start), new Square(destination));
    }

    public String toUciString() {
        return new Square(start).toString() + new Square(destination);
    }
}
