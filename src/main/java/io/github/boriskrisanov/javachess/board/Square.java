package io.github.boriskrisanov.javachess.board;

public class Square {
    int index;
    int rank;

    public Square(int index) {
        this.index = index;

        // @formatter:off
        if      (index < 8)     rank = 8;
        else if (index < 8 * 2) rank = 7;
        else if (index < 8 * 3) rank = 6;
        else if (index < 8 * 4) rank = 5;
        else if (index < 8 * 5) rank = 4;
        else if (index < 8 * 6) rank = 3;
        else if (index < 8 * 7) rank = 2;
        else if (index < 8 * 8) rank = 1;
        // @formatter:on
    }

    public static byte fromString(String square) {
        byte rank;
        byte file;

        try {
            file = (byte) (square.toLowerCase().charAt(0) - 'a' + 1);
            rank = (byte) Integer.parseInt(String.valueOf(square.charAt(1)));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new InvalidSquareException("Square \"" + square + "\" is not a valid square");
        }

        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            throw new InvalidSquareException("Square \"" + square + "\" is not a valid square");
        }

        byte down = (byte) (8 * (8 - rank));
        byte left = (byte) (file - 1);

        return (byte) (down + left);
    }

    @Override
    
    public String toString() {
        int rank = 8;
        char file = 'a';
        for (int i = 0; i < index; i++) {
            if (file == 'h') {
                rank--;
                file = 'a';
                continue;
            }
            file++;
        }

        return file + String.valueOf(rank);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Square && ((Square) object).getIndex() == index;
    }

    public int getIndex() {
        return index;
    }

    public static byte getRank(byte index) {
        // @formatter:off
        if      (index < 8)     return 8;
        else if (index < 8 * 2) return 7;
        else if (index < 8 * 3) return 6;
        else if (index < 8 * 4) return 5;
        else if (index < 8 * 5) return 4;
        else if (index < 8 * 6) return 3;
        else if (index < 8 * 7) return 2;
        else if (index < 8 * 8) return 1;
        // @formatter:on
        else throw new IllegalStateException();
    }
}
