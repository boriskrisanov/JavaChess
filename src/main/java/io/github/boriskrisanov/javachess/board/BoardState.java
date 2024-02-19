package io.github.boriskrisanov.javachess.board;

import java.util.*;

public record BoardState(
        int enPassantTargetSquare,
        long whiteAttackingSquares,
        long whitePawnAttackingSquares,
        long blackAttackingSquares,
        long blackPawnAttackingSquares,
        ArrayList<Integer> checkResolutions,
        int whiteKingPos,
        int blackKingPos,
        CastlingRights castlingRights,
        int halfMoveClock
) {

}
