package io.github.boriskrisanov.javachess.board;

import java.util.*;

public record BoardState(
        int enPassantTargetSquare,
        ArrayList<Integer> whiteAttackingSquares,
        ArrayList<Integer> blackAttackingSquares,
        ArrayList<Integer> checkResolutions,
        int whiteKingPos,
        int blackKingPos
) {

}
