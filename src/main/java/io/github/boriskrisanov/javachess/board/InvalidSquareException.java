package io.github.boriskrisanov.javachess.board;

public class InvalidSquareException extends RuntimeException {
    public InvalidSquareException(String message) {
        super(message);
    }
}
