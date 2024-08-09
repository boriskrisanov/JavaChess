package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;


public class Bishop extends Piece {
    private static final long[] BISHOP_BLOCKER_MASKS = new long[64];

    @SuppressWarnings("unchecked")
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];
    private static final long[][] attackingSquaresArray = new long[64][];

    private static final long[] BISHOP_MAGICS = {0xb8d001f098f81e00L, 0x608526004064090L, 0x584f1948600c9c91L, 0xe2333ab7e2602083L, 0xbb8eb4dc10882089L, 0x9aa25ead2c633000L, 0x6e1bbba2880e8d21L, 0xe361039861b637dbL, 0x464ecb40f41fe041L, 0xe0c0f80f83c7830cL, 0x27e4caa0650d407fL, 0x421b379212440abeL, 0x23bcf95910410bceL, 0xca7e6ba3a5100445L, 0x869e968d7420139dL, 0xee4020cc9082543L, 0x9b790e4c8a02b092L, 0xd1eb1b0b0709a40bL, 0x399de3efefb62600L, 0x98ee6e703b6d575L, 0xe96c65008088e041L, 0x399928d647fdeffbL, 0xfe5924841912a45cL, 0xfe14f07af8e50e04L, 0x50349f0231d66c00L, 0xcf50a44c8eccf800L, 0x320f04daf0528793L, 0x16f2bedbffd3bddcL, 0xb0987fefca7fbfd2L, 0x5e244b495bad4658L, 0xcbfe038e2de72e2aL, 0x1a1887c884c4e03bL, 0xaf91c17679c0e63fL, 0x37aa2398eb380684L, 0x459c2357a2543de8L, 0x538185e1d430c2faL, 0x935571681f6fdbf7L, 0xc25f97052e844918L, 0x51a3a00c9757160bL, 0x1f84963ba6f603c4L, 0x7fcb81d3861900fL, 0x3e9c82fc08908805L, 0x39938dcad7a938e1L, 0xd0692149024012d8L, 0x81e0cf3e5f758447L, 0xf763b7f04f3b4f05L, 0xe96010e61600ce68L, 0x28d424ea68102500L, 0x3632c7bfbbff7760L, 0x18ef575d72945d9dL, 0x15994bdf7befd422L, 0xfcef1e05ee55acd1L, 0xd7a4e6066ac05c8eL, 0x81589a1a23410129L, 0x8640b904cc7c8083L, 0xe774703dd07f8f7aL, 0x6b02be82acde54f5L, 0xbdf88210810427d1L, 0x7de5c389dc68f251L, 0xb1d8820280d514d7L, 0xf348042a41ee4af1L, 0x19c01e38b70e474L, 0x47632631826a015fL, 0xe226625000cede4cL};
    private static final long[] BISHOP_SHIFTS = {57, 58, 58, 58, 58, 58, 58, 56, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 55, 55, 56, 55, 58, 58, 58, 58, 55, 52, 53, 55, 58, 58, 58, 58, 55, 53, 53, 55, 58, 58, 58, 58, 55, 56, 55, 55, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 56, 58, 58, 58, 58, 58, 58, 56};

    static {
        for (int i = 0; i < 64; i++) {
            ATTACKING_SQUARES[i] = new HashMap<>();
        }

        for (int bishopIndex = 0; bishopIndex < 64; bishopIndex++) {
            BISHOP_BLOCKER_MASKS[bishopIndex] = 0;

            Direction[] directions = {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};
            for (Direction direction : directions) {
                for (int i = 0; i < EdgeDistance.get(bishopIndex, direction); i++) {
                    int targetSquare = bishopIndex + direction.offset * (i + 1);
                    boolean isEdgeSquare = Square.isFirstRank(targetSquare) || Square.isLastRank(targetSquare) || Square.getFile(targetSquare) == 1 || Square.getFile(targetSquare) == 8;
                    if (!isEdgeSquare) {
                        BISHOP_BLOCKER_MASKS[bishopIndex] |= BitboardUtils.withSquare(targetSquare);
                    }
                }
            }

            for (long blockerPositions : BitboardUtils.computePossibleBlockerPositions(bishopIndex, BISHOP_BLOCKER_MASKS[bishopIndex])) {
                ATTACKING_SQUARES[bishopIndex].put(blockerPositions, SlidingPiece.getAttackingSquares2(blockerPositions, bishopIndex, new Direction[]{TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}));
            }
        }

        for (int i = 0; i < 64; i++) {
            int maxIndex = 0;
            for (long blockerBitboard : ATTACKING_SQUARES[i].keySet()) {
                maxIndex = Math.max(maxIndex, index(i, blockerBitboard));
            }
            attackingSquaresArray[i] = new long[maxIndex + 1];

            int finalI = i;
            ATTACKING_SQUARES[i].forEach((blockerBitboard, attackingSquaresBitboard) -> {
                attackingSquaresArray[finalI][index(finalI, blockerBitboard)] = attackingSquaresBitboard;
            });
        }
    }

    private static int index(int i, long blockerBitboard) {
        return (int) Math.abs((blockerBitboard * BISHOP_MAGICS[i]) >> BISHOP_SHIFTS[i]);
    }

    public static MagicBitboard getMagicBitboard() {
        List<List<Long>> possibleBlockerPositions = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            possibleBlockerPositions.add(BitboardUtils.computePossibleBlockerPositions(i, BISHOP_BLOCKER_MASKS[i]));
        }

        return new MagicBitboard(possibleBlockerPositions);
    }

    public long getAttackingSquares() {
        long blockers = board.getAllPieces() & BISHOP_BLOCKER_MASKS[position];
        return attackingSquaresArray[position][index(position, blockers)] & ~board.getPieces(color);
    }

    public Bishop(Color color, int position, Board board) {
        super(color, position, board);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'B' : 'b';
    }

    @Override
    public int getValue() {
        return 320;
    }
}
