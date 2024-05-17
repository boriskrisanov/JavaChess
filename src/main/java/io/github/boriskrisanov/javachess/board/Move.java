package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;


public final class Move {
    private final int start;
    private final int destination;
    private final Piece capturedPiece;
    private final CastlingDirection castlingDirection;
    private final Promotion promotion;

    public Move(
            int start,
            int destination,
            Piece capturedPiece,
            CastlingDirection castlingDirection,
            Promotion promotion
    ) {
        this.start = start;
        this.destination = destination;
        this.capturedPiece = capturedPiece;
        this.castlingDirection = castlingDirection;
        this.promotion = promotion;
    }

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

        return new Square(start).toString() + new Square(destination) + promotion;
    }

    public int start() {
        return start;
    }

    public int destination() {
        return destination;
    }

    public Piece capturedPiece() {
        return capturedPiece;
    }

    public CastlingDirection castlingDirection() {
        return castlingDirection;
    }

    public Promotion promotion() {
        return promotion;
    }
}
