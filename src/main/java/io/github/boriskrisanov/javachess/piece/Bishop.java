package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
import static io.github.boriskrisanov.javachess.board.PinDirection.*;


public class Bishop extends Piece {
    private static final long[] BISHOP_BLOCKER_MASKS = new long[64];

    @SuppressWarnings("unchecked")
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];
    private static final long[][] attackingSquaresArray = new long[64][];

    private static final long[] BISHOP_MAGICS = {0x2bca7a5fd5efd7b2L, 0xdca75c375c170404L, 0x575894811609203L, 0x54ac3f61be9fe5edL, 0x1ec847de4210440fL, 0xa1308bd3e4f4c167L, 0x8339dcb095901369L, 0x8660137b8f384001L, 0x95c9a3a3f631cc1aL, 0xd5c0b902021526bdL, 0x1c932409020a049aL, 0xb3dd6d582033071fL, 0x87727c6e442812fbL, 0xfaf290568290016fL, 0xc981a7159a0e0bdfL, 0x53a0532e8ba3e2dfL, 0x88f878908c111484L, 0x2ce827ffa7e756b4L, 0xb9e3777fcbc2ca1eL, 0xc65b4e4cfe306da6L, 0x596a5d4208042880L, 0x1492c283dbdd7778L, 0x2b5dca683ae08a16L, 0x54d041cff4a24bfdL, 0xef75c782e4ca7405L, 0xa9b000b0b781a5d3L, 0x8b7391ae72cc6bd0L, 0x4a1c8fcb356ff190L, 0x99c6f66c141b1971L, 0x13536c8cc0c8e4e3L, 0x2a3f468841602d03L, 0x12c4020226c83189L, 0xced566b8b058606aL, 0x501f5fd3a6abefe3L, 0xa7c2021d190eff96L, 0xe6d42f0c7dba46ccL, 0x772120fa7e4e2a12L, 0x6a40f60aa739d090L, 0xce12fccc5e900c89L, 0xfcfa4068e85d0698L, 0x504a004a402c440eL, 0x1733396928b001a2L, 0xa25b6f2121188231L, 0xada1ffd0eb37beebL, 0x3411c22e14c7a929L, 0x8bf5ffbafe66b9b0L, 0x1dc5cef00317b591L, 0x9798a08c6bdb3fa9L, 0x9a49f200ed9584b1L, 0xfda3df195e386ea1L, 0xf1fb0e058c2c73c4L, 0x14d8540422939e23L, 0x958820206145de2L, 0x729514114e860078L, 0x1b44e014167ba8d0L, 0x9de3401d289adae3L, 0x8cf946e2f9831ceL, 0x8bd251e88e535974L, 0x63c7bbeff9de3825L, 0x6ea8820251b40d8bL, 0xf972408681f7ac04L, 0xc088089625114c4cL, 0xba6910065102439bL, 0xfb496ed8ac20e825L};
    private static final long[] BISHOP_SHIFTS = {56, 58, 58, 57, 58, 57, 58, 56, 57, 58, 58, 58, 58, 58, 57, 57, 58, 57, 55, 55, 55, 55, 57, 57, 58, 58, 55, 52, 52, 55, 58, 58, 57, 57, 55, 52, 52, 55, 57, 58, 58, 57, 55, 55, 55, 55, 57, 57, 58, 57, 58, 58, 58, 58, 58, 57, 56, 57, 58, 58, 58, 58, 58, 56};

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
