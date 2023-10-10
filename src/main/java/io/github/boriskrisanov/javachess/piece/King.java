package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

public class King extends Piece {

    public King(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public ArrayList<Integer> getAttackingSquares() {
        var moves = new ArrayList<Integer>();
        var opponentAttackingSquares = board.getSquaresAttackedBySide(color.getOpposite());
        var edgeDist = new EdgeDistance(position);

        if (edgeDist.left >= 1 && !opponentAttackingSquares.contains(position - 1)) {
            moves.add(position - 1);
        }
        if (edgeDist.right >= 1 && !opponentAttackingSquares.contains(position + 1)) {
            moves.add(position + 1);
        }
        if (edgeDist.top >= 1 && !opponentAttackingSquares.contains(position - 8)) {
            moves.add(position - 8);
        }
        if (edgeDist.bottom >= 1 && !opponentAttackingSquares.contains(position + 8)) {
            moves.add(position + 8);
        }
        if (edgeDist.left >= 1 && edgeDist.top >= 1 && !opponentAttackingSquares.contains(position - 8 - 1)) {
            moves.add(position - 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.top >= 1 && !opponentAttackingSquares.contains(position - 8 + 1)) {
            moves.add(position - 8 + 1);
        }
        if (edgeDist.left >= 1 && edgeDist.bottom >= 1 && !opponentAttackingSquares.contains(position + 8 - 1)) {
            moves.add(position + 8 - 1);
        }
        if (edgeDist.right >= 1 && edgeDist.bottom >= 1 && !opponentAttackingSquares.contains(position + 8 + 1)) {
            moves.add(position + 8 + 1);
        }

        return moves;
    }

    @Override
    public ArrayList<Move> getLegalMoves() {
        ArrayList<Integer> opponentAttackingSquares = board.getSquaresAttackedBySide(color.getOpposite());
        var moves = new ArrayList<Move>();

        for (Integer attackingSquare : getAttackingSquares()) {
            if (opponentAttackingSquares.contains(attackingSquare)) {
                continue;
            }

            Piece capturedPiece = board.getPieceOn(attackingSquare);
            Move move = new Move(position, attackingSquare, capturedPiece);

            if (board.isSquareEmpty(attackingSquare) || board.getPieceOn(attackingSquare).getColor() == this.color.getOpposite()) {
                moves.add(move);
            }
        }

        return moves;
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'K' : 'k';
    }
}
