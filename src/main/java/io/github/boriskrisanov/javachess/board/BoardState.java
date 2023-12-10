package io.github.boriskrisanov.javachess.board;

public record BoardState(
        int enPassantTargetSquare,
        long whiteAttackingSquares,
        long blackAttackingSquares,
        int whiteKingPos,
        int blackKingPos,
        CastlingRights castlingRights
) {

}
