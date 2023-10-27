package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;

public record Move(
        int start,
        int destination,
        Piece capturedPiece,
        CastlingDirection castlingDirection,
        Promotion promotion
) {
    @Override
    public String toString() {
        return toUciString();
    }

    /**
     * Normal constructor
     */
    public Move(int start, int destination, Piece capturedPiece) {
        this(start, destination, capturedPiece, null, null);
    }

    /**
     * Castling constructor
     *
     * @apiNote The capturedPiece parameter is needed to differentiate between the default constructor, but it is never used
     * because a piece cannot be captured by castling. This parameter should just be set to null.
     */
    public Move(int start, int destination, Piece ignoredCapturedPiece, CastlingDirection castlingDirection) {
        this(start, destination, null, castlingDirection, null);
    }

    /**
     * Promotion constructor
     */
    public Move(int start, int destination, Piece capturedPiece, Promotion promotion) {
        this(start, destination, capturedPiece, null, promotion);
    }

    public String toUciString() {
        if (promotion == null) {
            return new Square(start).toString() + new Square(destination);
        }

        return new Square(start).toString() + new Square(destination) + switch (promotion) {
            case QUEEN -> "q";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KNIGHT -> "n";
        };
    }
}
