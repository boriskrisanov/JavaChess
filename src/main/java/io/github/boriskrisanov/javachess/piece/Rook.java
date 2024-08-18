package io.github.boriskrisanov.javachess.piece;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.board.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

public class Rook extends Piece {
    /**
     * The value at index n is bitboard of possible positions where pieces that block the movement of a rook on square n
     * could be. This is equivalent to the rook's legal moves from square n without squares on the edge, since it is
     * assumed that a blocking piece can always be captured, and thus has no effect on the rook's legal moves if it is on
     * the edge.
     */
    private static final long[] ROOK_BLOCKER_MASKS = new long[64];
    /**
     * The nth element stores, for a rook at square index n, a map of blocker bitboards to attacking squares (legal moves)
     */
    @SuppressWarnings("unchecked")
    private static final HashMap<Long, Long>[] ATTACKING_SQUARES = new HashMap[64];
    private static final long[][] attackingSquaresArray = new long[64][];

    private static final long[] ROOK_MAGICS = {0xb7c8ffffbdf8ed79L, 0x7cccb4acac99a09aL, 0x277f8a1f457fa352L, 0x7e7d01513baf5767L, 0xea6fff8a18fecce7L, 0x5d24e354a272711L, 0xcb734ff54bfdceabL, 0xc796020f8482c023L, 0xcd8c8f85cd8c7798L, 0xeaa063aac121fd78L, 0xdc1e46605b34c09cL, 0xcbacc491fc4f54bcL, 0x8036e0e6d8f8d7b8L, 0xd3b647d77960e7d8L, 0x9b20d4fa1bc46876L, 0x44c4264f0b18de1eL, 0x8855b001ac251d80L, 0x9625d5292d2e3c8eL, 0xdbda6f4a66e590a7L, 0x829058c99069906dL, 0xc9c0b0ea9c5521fbL, 0x4177cd4386a64fabL, 0x324a8dbe2ff95405L, 0x55cd15e172a8d76fL, 0xfb64a8f2415d7821L, 0xe7e48fdaafbff944L, 0xbbb74318d41d9980L, 0x11ab8facd32cad62L, 0x10fcc8bc23373750L, 0x528b8b07f650b407L, 0xe2ec3ddbe240271L, 0x658d05b962e98275L, 0xf70541a9e66a28a3L, 0x79336c523e22a894L, 0xe0543017e7f2ea61L, 0x626d5cde515429f3L, 0xda285c3eb049a381L, 0xb33e026abed080c8L, 0x4fd05955da71f2bdL, 0x6f5e84d217ad0bd7L, 0x96cd81400f2a7f68L, 0x815be01fdbcb6d01L, 0x66d6a657bfde74acL, 0xed07915ff915e160L, 0x4267b33c3ccf4512L, 0xb945f45e60bc88c0L, 0x6f25882bfdacac61L, 0xc16006db41b8fc7eL, 0x327dfffdbe7ae3aaL, 0x7cf6fa0a0d05f415L, 0xc480c82b51c4a8dfL, 0xf43028053a4e4b4L, 0x5475cff715cffbd0L, 0xdea9695deb61b438L, 0xac8aea22a7dbf996L, 0x73aecf15f4cd6390L, 0xd6f50be59bf640b1L, 0xa587df828f4368abL, 0x3581646cb6083d6bL, 0xe4ded3bf94deb829L, 0x1878781a0a5f7d3aL, 0x7a1ca6b38e4a76a1L, 0x3322c373d920ddc6L, 0x62ca191005858111L};
    private static final long[] ROOK_SHIFTS = {50, 50, 50, 50, 51, 50, 50, 49, 51, 52, 51, 51, 51, 52, 52, 51, 51, 52, 52, 52, 51, 51, 52, 50, 51, 52, 51, 51, 52, 51, 52, 50, 51, 52, 52, 51, 51, 51, 52, 51, 51, 52, 51, 52, 51, 51, 52, 51, 51, 52, 51, 51, 52, 52, 52, 50, 49, 50, 50, 50, 50, 50, 50, 49};

    static {
        for (int i = 0; i < 64; i++) {
            ATTACKING_SQUARES[i] = new HashMap<>();
        }

        for (int rookIndex = 0; rookIndex < 64; rookIndex++) {
            ROOK_BLOCKER_MASKS[rookIndex] = 0;

            for (int i = 0; i < 64; i++) {
                if (i != rookIndex && (Square.getRank(i) == Square.getRank(rookIndex) || Square.getFile(i) == Square.getFile(rookIndex))) {
                    // Don't add the square if it's on the edge
                    if ((Square.getFile(i) == 1 && Square.getFile(rookIndex) != 1) || (Square.getFile(i) == 8 && Square.getFile(rookIndex) != 8)) {
                        continue;
                    }
                    if ((Square.getRank(i) == 1 && Square.getRank(rookIndex) != 1) || (Square.getRank(i) == 8 && Square.getRank(rookIndex) != 8)) {
                        continue;
                    }

                    ROOK_BLOCKER_MASKS[rookIndex] |= BitboardUtils.withSquare(i);
                }
            }

            for (long blockerPositions : BitboardUtils.computePossibleBlockerPositions(rookIndex, ROOK_BLOCKER_MASKS[rookIndex])) {
                ATTACKING_SQUARES[rookIndex].put(blockerPositions, SlidingPiece.getAttackingSquares(blockerPositions, rookIndex, new Direction[]{UP, DOWN, LEFT, RIGHT}));
            }
        }

        for (int i = 0; i < 64; i++) {
            int maxIndex = 0;
            for (long blockerBitboard : ATTACKING_SQUARES[i].keySet()) {
                maxIndex = Math.max(maxIndex, index(i, blockerBitboard));
            }
            attackingSquaresArray[i] = new long[maxIndex + 1];

            int finalI = i;
            ATTACKING_SQUARES[i].forEach((blockerBitboard, attackingSquaresBitboard) -> attackingSquaresArray[finalI][index(finalI, blockerBitboard)] = attackingSquaresBitboard);
        }
    }

    private static int index(int i, long blockerBitboard) {
        return (int) Math.abs((blockerBitboard * ROOK_MAGICS[i]) >> ROOK_SHIFTS[i]);
    }

    public static MagicBitboard getMagicBitboard() {
        List<List<Long>> possibleBlockerPositions = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            possibleBlockerPositions.add(BitboardUtils.computePossibleBlockerPositions(i, ROOK_BLOCKER_MASKS[i]));
        }

        return new MagicBitboard(possibleBlockerPositions);
    }

    public Rook(Color color, int position, Board board) {
        super(color, position, board);
    }

    public long getAttackingSquares() {
        // Assume blocking pieces can be captured, then filter using friendly pieces bitboard later
        long blockers = board.getAllPieces() & ROOK_BLOCKER_MASKS[position];
        return attackingSquaresArray[position][index(position, blockers)] & ~board.getPieces(color);
    }

    @Override
    public char getChar() {
        return color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public int getValue() {
        return 500;
    }
}
