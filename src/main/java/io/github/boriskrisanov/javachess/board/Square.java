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

    public Square(String square) {
        int file;

        try {
            file = square.toLowerCase().charAt(0) - 'a' + 1;
            rank = Integer.parseInt(String.valueOf(square.charAt(1)));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new InvalidSquareException("Square \"" + square + "\" is not a valid square");
        }

        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            throw new InvalidSquareException("Square \"" + square + "\" is not a valid square");
        }

        int down = 8 * (8 - rank);
        int left = file - 1;

        this.index = down + left;
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

    
    EdgeDistance getEdgeDistance() {
        return new EdgeDistance(this);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Square && ((Square) object).getIndex() == index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
