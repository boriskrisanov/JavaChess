package io.github.boriskrisanov.javachess.engine.board;

public class InvalidSquareException extends RuntimeException {
    public InvalidSquareException(String message) {
        super(message);
    }
}
