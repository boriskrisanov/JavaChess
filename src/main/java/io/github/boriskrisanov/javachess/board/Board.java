package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.*;
import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;
import static io.github.boriskrisanov.javachess.piece.Piece.Color.*;

/**
 * Holds the current state of the game (piece positions, side to move, check, checkmate and en passant target square)
 */
public class Board {
    private final Piece[] board = new Piece[64];
    private long squaresAttackedByWhite = 0;
    // Pawn attacking squares are only used for move ordering
    private long whitePawnAttackingSquares = 0;
    private long blackPawnAttackingSquares = 0;
    private final Deque<Move> moveHistory = new ArrayDeque<>();
    // TODO: Store this in moves
    public final Deque<BoardState> boardHistory = new ArrayDeque<>();
    public final Deque<Long> hashHistory = new ArrayDeque<>();
    private long squaresAttackedByBlack = 0;
    private final ArrayList<Integer> checkResolutions = new ArrayList<>();
    private int whiteKingPos = 0;
    private int blackKingPos = 0;
    private int enPassantTargetSquare;
    private Piece.Color sideToMove;
    private CastlingRights castlingRights = new CastlingRights(false, false, false, false);
    private int halfMoveClock = 0;
    private int moveNumber = 0;
    private BitboardManager bitboards = new BitboardManager();

    public static final String STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public Board(String fen) {
        Hash.init();
        loadFen(fen);
    }

    /**
     * Creates a board with the starting position
     */
    public Board() {
        Hash.init();
        loadStartingPosition();
    }

    /**
     * Loads the starting position (FEN rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1)
     */
    public void loadStartingPosition() {
        loadFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public void loadFen(String fen) {
        hashHistory.clear();
        for (int i = 0; i < 64; i++) {
            board[i] = null;
        }
        // Reset bitboards
        bitboards = new BitboardManager();

        // TODO: Allow FEN strings with only some information
        //  (Only placement info is needed, everything else can be set to default)
        String placement = fen.split(" ")[0];
        String sideToMove = fen.split(" ")[1];
        String castling = fen.split(" ")[2];
        String enPassantTargetSquare = fen.split(" ")[3];
        String halfMoveClock = fen.split(" ")[4];
        String fullMoveNumber = fen.split(" ")[5];

        placement = placement.replaceAll("/", "");
        int i = 0;

        for (char c : placement.toCharArray()) {
            if (Character.isDigit(c)) {
                int n = Integer.parseInt(String.valueOf(c));
                i += n - 1;
            } else {
                board[i] = Piece.fromChar(c, i, this);
                bitboards.addPiece(board[i], i);
                if (board[i] instanceof King) {
                    if (board[i].getColor() == WHITE) {
                        whiteKingPos = i;
                    } else {
                        blackKingPos = i;
                    }
                }
            }
            i++;
        }

        this.castlingRights = new CastlingRights(castling);

        if (enPassantTargetSquare.equals("-")) {
            this.enPassantTargetSquare = -1;
        } else {
            this.enPassantTargetSquare = Square.fromString(enPassantTargetSquare.toLowerCase());
        }

        this.sideToMove = sideToMove.equals("w") ? WHITE : BLACK;

        this.halfMoveClock = Integer.parseInt(halfMoveClock);
        this.moveNumber = Integer.parseInt(fullMoveNumber);

        computeAttackingSquares();
        computeCheckResolutions();
        hashHistory.push(Hash.hash(this));
    }

    /**
     * @return The FEN string of the current position
     */
    public String getFen() {
        var fen = new StringBuilder();
        int skippedSquaresCount = 0;

        for (int i = 0; i < 64; i++) {
            boolean goingToNextRank = i % 8 == 0 && i != 0;

            if (goingToNextRank) {
                if (skippedSquaresCount > 0) {
                    fen.append(skippedSquaresCount);
                    skippedSquaresCount = 0;
                }
                fen.append("/");
            }

            if (board[i] == null) {
                skippedSquaresCount++;
            } else {
                if (skippedSquaresCount > 0) {
                    fen.append(skippedSquaresCount);
                    skippedSquaresCount = 0;
                }

                fen.append(board[i].getChar());
            }
        }
        if (skippedSquaresCount != 0) {
            fen.append(skippedSquaresCount);
        }

        fen.append(sideToMove == WHITE ? " w " : " b ");
        fen.append(castlingRights).append(" ");
        fen.append(enPassantTargetSquare == -1 ? "-" : new Square(enPassantTargetSquare).toString());
        fen.append(" ").append(halfMoveClock);
        fen.append(" ").append(moveNumber);

        return fen.toString();
    }

    @Override
    public String toString() {
        var boardString = new StringBuilder();
        int j = 0;
        for (int i = 0; i < 64; i++) {
            // New line
            if (j == 8) {
                boardString
                        .append("  ")
                        .append(9 - i / 8)
                        .append("\n");
                j = 0;
            }
            if (board[i] == null) {
                boardString.append('.');
            } else {
                boardString.append(board[i].getChar());
            }
            boardString.append(" ");
            j++;
        }
        boardString.append("  1\n\n");
        boardString.append("A B C D E F G H\n");

        return boardString.toString();
    }

    public boolean isSquareEmpty(int index) {
        return board[index] == null;
    }

    public Piece getPieceOn(int index) {
        return board[index];
    }

    public void makeMove(String uciMove) {
        // TODO: This doesn't handle en passant (?)
        if (uciMove.length() != 4 && uciMove.length() != 5) {
            throw new IllegalArgumentException(uciMove);
        }

        int start = Square.fromString(String.valueOf(uciMove.charAt(0)) + uciMove.charAt(1));
        int destination = Square.fromString(String.valueOf(uciMove.charAt(2)) + uciMove.charAt(3));

        CastlingDirection castlingDirection = null;
        if (board[start] instanceof King) {
            if (board[start].getColor() == BLACK && destination == 6 && castlingRights.blackCanShortCastle) {
                castlingDirection = CastlingDirection.SHORT;
            } else if (board[start].getColor() == BLACK && destination == 1 && castlingRights.blackCanLongCastle) {
                castlingDirection = CastlingDirection.LONG;
            } else if (board[start].getColor() == WHITE && destination == 62 && castlingRights.whiteCanShortCastle) {
                castlingDirection = CastlingDirection.SHORT;
            } else if (board[start].getColor() == WHITE && destination == 58 && castlingRights.whiteCanLongCastle) {
                castlingDirection = CastlingDirection.LONG;
            }
        }

        Promotion promotion = null;
        if (uciMove.length() == 5) {
            promotion = switch (uciMove.charAt(4)) {
                case 'n' -> Promotion.KNIGHT;
                case 'b' -> Promotion.BISHOP;
                case 'r' -> Promotion.ROOK;
                case 'q' -> Promotion.QUEEN;
                default -> throw new IllegalArgumentException(uciMove);
            };
        }
        if (castlingDirection != null) {
            makeMove(new Move(start, destination, board[destination], castlingDirection));
            return;
        }
        makeMove(new Move(start, destination, board[destination], promotion));
    }

    /**
     * Makes a move on the board without checking if it is legal. Also updates en passant target square.
     */
    public void makeMove(Move move) {
        moveHistory.push(move);
        hashHistory.push(Hash.hash(this));
        // TODO: Only compute hash in one method and reuse it for search
        boardHistory.push(new BoardState(enPassantTargetSquare, squaresAttackedByWhite, whitePawnAttackingSquares, squaresAttackedByBlack, blackPawnAttackingSquares, checkResolutions, whiteKingPos, blackKingPos, new CastlingRights(castlingRights), halfMoveClock));

        var movedPiece = board[move.start()];

        if (move.capturedPiece() == null && (!(movedPiece instanceof Pawn))) {
            halfMoveClock++;
        } else {
            halfMoveClock = 0;
        }

        boolean isPromotion = move.promotion() != null;
        boolean isEnPassant = move.destination() == enPassantTargetSquare;

        // The right to capture en passant has been lost because another move has been made
        enPassantTargetSquare = -1;

        if (movedPiece instanceof Pawn) {
            // Set the en passant target square if a pawn moved 2 squares forward
            if (move.destination() == move.start() - 8 * 2) {
                enPassantTargetSquare = move.start() - 8;
            } else if (move.destination() == move.start() + 8 * 2) {
                enPassantTargetSquare = move.start() + 8;
            }
        }

        // Update king position and castling
        if (movedPiece instanceof King) {
            // Castling
            if (move.castlingDirection() != null) {
                // Move rook
                switch (move.castlingDirection()) {
                    case SHORT -> {
                        var rook = movedPiece.getColor() == WHITE ? board[63] : board[7];
                        board[rook.getPosition()] = null;
                        board[move.destination() - 1] = rook;
                        bitboards.movePiece(rook, null, rook.getPosition(), move.destination() - 1);
                        rook.setPosition(move.destination() - 1);
                    }
                    case LONG -> {
                        var rook = movedPiece.getColor() == WHITE ? board[56] : board[0];
                        board[rook.getPosition()] = null;
                        board[move.destination() + 1] = rook;
                        bitboards.movePiece(rook, null, rook.getPosition(), move.destination() + 1);
                        rook.setPosition(move.destination() + 1);
                    }
                }

                // This side has just castled so castling is no longer possible
                castlingRights.removeForSide(movedPiece.getColor());
            }

            if (movedPiece.getColor() == WHITE) {
                whiteKingPos = move.destination();
            } else {
                blackKingPos = move.destination();
            }
            // King has moved, so castling is no longer possible
            castlingRights.removeForSide(movedPiece.getColor());
        }

        // Update castling rights if rook has moved
        boolean rookWasCaptured = move.capturedPiece() instanceof Rook && (move.capturedPiece().getPosition() == 0 || move.capturedPiece().getPosition() == 7 || move.capturedPiece().getPosition() == 56 || move.capturedPiece().getPosition() == 63);
        if (movedPiece instanceof Rook) {
            if (movedPiece.getPosition() == 0) {
                castlingRights.removeForSide(BLACK, CastlingDirection.LONG);
            } else if (movedPiece.getPosition() == 7) {
                castlingRights.removeForSide(BLACK, CastlingDirection.SHORT);
            } else if (movedPiece.getPosition() == 56) {
                castlingRights.removeForSide(WHITE, CastlingDirection.LONG);
            } else if (movedPiece.getPosition() == 63) {
                castlingRights.removeForSide(WHITE, CastlingDirection.SHORT);
            }
        }
        if (rookWasCaptured) {
            if (move.destination() == 0) {
                castlingRights.removeForSide(BLACK, CastlingDirection.LONG);
            } else if (move.destination() == 7) {
                castlingRights.removeForSide(BLACK, CastlingDirection.SHORT);
            } else if (move.destination() == 56) {
                castlingRights.removeForSide(WHITE, CastlingDirection.LONG);
            } else if (move.destination() == 63) {
                castlingRights.removeForSide(WHITE, CastlingDirection.SHORT);
            }
        }

        if (isEnPassant && movedPiece instanceof Pawn) {
            if (sideToMove == WHITE) {
                bitboards.blackPawns &= ~BitboardUtils.withSquare(move.destination() + 8);
                board[move.destination() + 8] = null;
            } else {
                bitboards.whitePawns &= ~BitboardUtils.withSquare(move.destination() - 8);
                board[move.destination() - 8] = null;
            }
        }

        board[move.start()] = null;

        if (!isPromotion) {
            bitboards.movePiece(movedPiece, board[move.destination()], move.start(), move.destination());
            board[move.destination()] = movedPiece;
            movedPiece.setPosition(move.destination());
        } else {
            if (sideToMove == WHITE) {
                bitboards.whitePawns &= ~BitboardUtils.withSquare(move.start());
            } else {
                bitboards.blackPawns &= ~BitboardUtils.withSquare(move.start());
            }
            board[move.destination()] = switch (move.promotion()) {
                case QUEEN -> new Queen(sideToMove, move.destination(), this);
                case ROOK -> new Rook(sideToMove, move.destination(), this);
                case BISHOP -> new Bishop(sideToMove, move.destination(), this);
                case KNIGHT -> new Knight(sideToMove, move.destination(), this);
            };
            bitboards.addPiece(move.promotion(), sideToMove, move.destination());
            // Remove captured piece from bitboards
            if (move.capturedPiece() != null) {
                bitboards.removePiece(move.capturedPiece(), move.destination());
            }
        }

        sideToMove = sideToMove.getOpposite();

        computeAttackingSquares();
        updatePawnAttackingSquares();
        computeCheckResolutions();
    }

    public void unmakeMove() {
        hashHistory.pop();
        var move = moveHistory.pop();
        var boardState = boardHistory.pop();

        enPassantTargetSquare = boardState.enPassantTargetSquare();
        whiteKingPos = boardState.whiteKingPos();
        blackKingPos = boardState.blackKingPos();
        castlingRights = boardState.castlingRights();
        halfMoveClock = boardState.halfMoveClock();
        whitePawnAttackingSquares = boardState.whitePawnAttackingSquares();
        blackPawnAttackingSquares = boardState.blackPawnAttackingSquares();

        boolean isPromotion = move.promotion() != null;
        boolean isCapture = move.capturedPiece() != null;
        boolean isEnPassant = move.destination() == enPassantTargetSquare;
        // If this was a promotion, the piece at the destination square (movedPiece) would be the piece that the pawn
        // promoted to, rather than the pawn itself, which is why this special case is needed.
        Piece movedPiece = isPromotion ? new Pawn(sideToMove.getOpposite(), move.destination(), this) : board[move.destination()];

        // Undo castling
        if (move.castlingDirection() == CastlingDirection.SHORT) {
            if (movedPiece.getColor() == WHITE) {
                var rook = board[61];
                board[61] = null;
                board[63] = rook;
                bitboards.movePiece(rook, null, 61, 63);
                rook.setPosition(63);
            } else {
                var rook = board[5];
                board[5] = null;
                board[7] = rook;
                bitboards.movePiece(rook, null, 5, 7);
                rook.setPosition(7);
            }
        } else if (move.castlingDirection() == CastlingDirection.LONG) {
            if (movedPiece.getColor() == WHITE) {
                var rook = board[59];
                board[59] = null;
                board[56] = rook;
                bitboards.movePiece(rook, null, 59, 56);
                rook.setPosition(56);
            } else {
                var rook = board[3];
                board[3] = null;
                board[0] = rook;
                bitboards.movePiece(rook, null, 3, 0);
                rook.setPosition(0);
            }
        }

        board[move.destination()] = null;
        board[move.start()] = movedPiece;

        bitboards.movePiece(movedPiece, null, move.destination(), move.start());

        if (move.promotion() != null) {
            bitboards.removePiece(move.promotion(), movedPiece.getColor(), move.destination());
        }
        movedPiece.setPosition(move.start());

        if (isCapture) {
            // In the case of en passant, the position of the captured piece will not be the destination of the move,
            // so board[move.destination()] can't be used.
            if (isEnPassant) {
                if (sideToMove == WHITE) {
                    board[move.destination() - 8] = move.capturedPiece();
                    move.capturedPiece().setPosition(move.destination() - 8);
                    // If the move is an en passant capture, the captured piece must be a pawn
                    bitboards.whitePawns |= BitboardUtils.withSquare(move.destination() - 8);
                } else {
                    board[move.destination() + 8] = move.capturedPiece();
                    move.capturedPiece().setPosition(move.destination() + 8);
                    // If the move is an en passant capture, the captured piece must be a pawn
                    bitboards.blackPawns |= BitboardUtils.withSquare(move.destination() + 8);
                }
            } else {
                board[move.destination()] = move.capturedPiece();
                move.capturedPiece().setPosition(move.destination());
                bitboards.addPiece(move.capturedPiece(), move.destination());
            }
        }

        squaresAttackedByWhite = boardState.whiteAttackingSquares();
        squaresAttackedByBlack = boardState.blackAttackingSquares();

        sideToMove = sideToMove.getOpposite();

        computeCheckResolutions();
    }

    private void computeAttackingSquares() {
        squaresAttackedByWhite = computeAttackingSquaresForSide(WHITE);
        squaresAttackedByBlack = computeAttackingSquaresForSide(BLACK);
    }

    /**
     * Computes the squares to which a piece can move to block a check
     */
    private void computeCheckResolutions() {
        if (!isSideInCheck(sideToMove)) {
            return;
        }

        var sideInCheck = sideToMove;
        var checkResolutionBuffer = new ArrayList<Integer>();

        int kingPosition = getKingPosition(sideInCheck);
        int numSlidingCheckDirections = 0;

        // Sliding pieces
        for (Direction direction : Direction.values()) {
            ArrayList<Integer> squares = new ArrayList<>();
            for (int i = 0; i < EdgeDistance.get(kingPosition, direction); i++) {
                int targetSquare = kingPosition + direction.offset * (i + 1);

                if (board[targetSquare] == null) {
                    squares.add(targetSquare);
                } else if (((board[targetSquare] instanceof Rook && (direction == UP || direction == DOWN || direction == LEFT || direction == RIGHT)) || (board[targetSquare] instanceof Bishop && (direction == TOP_LEFT || direction == TOP_RIGHT || direction == BOTTOM_LEFT || direction == BOTTOM_RIGHT)) || board[targetSquare] instanceof Queen) && board[targetSquare].getColor() != sideInCheck) {
                    // In check from this direction
                    checkResolutionBuffer.addAll(squares);
                    checkResolutionBuffer.add(targetSquare);
                    numSlidingCheckDirections++;
                    break;
                } else if (board[targetSquare] != null) {
                    // Piece is in the way, not in check from this direction
                    break;
                }
            }
        }

        boolean checkFromNonSlidingPiece = false;

        // Pawns
        {
            int p1;
            int p2;
            boolean edgeDistanceRequirement1, edgeDistanceRequirement2;
            if (sideInCheck == WHITE) {
                p1 = kingPosition + TOP_LEFT.offset;
                p2 = kingPosition + TOP_RIGHT.offset;
                edgeDistanceRequirement1 = EdgeDistance.get(kingPosition, TOP_LEFT) > 0;
                edgeDistanceRequirement2 = EdgeDistance.get(kingPosition, TOP_RIGHT) > 0;
            } else {
                p1 = kingPosition + BOTTOM_LEFT.offset;
                p2 = kingPosition + BOTTOM_RIGHT.offset;
                edgeDistanceRequirement1 = EdgeDistance.get(kingPosition, BOTTOM_LEFT) > 0;
                edgeDistanceRequirement2 = EdgeDistance.get(kingPosition, BOTTOM_RIGHT) > 0;
            }
            if (edgeDistanceRequirement1 && board[p1] instanceof Pawn && board[p1].getColor() == sideInCheck.getOpposite()) {
                checkResolutionBuffer.add(p1);
                checkFromNonSlidingPiece = true;
            } else if (edgeDistanceRequirement2 && board[p2] instanceof Pawn && board[p2].getColor() == sideInCheck.getOpposite()) {
                checkResolutionBuffer.add(p2);
                checkFromNonSlidingPiece = true;
            }
        }

        // Knights
        {
            ArrayList<Integer> knightPositions = new ArrayList<>();
            var edgeDistance = new EdgeDistance(kingPosition);

            if (edgeDistance.left >= 1 && edgeDistance.top >= 2) {
                knightPositions.add(kingPosition + LEFT.offset + UP.offset * 2);
            }
            if (edgeDistance.right >= 1 && edgeDistance.top >= 2) {
                knightPositions.add(kingPosition + RIGHT.offset + UP.offset * 2);
            }
            if (edgeDistance.left >= 1 && edgeDistance.bottom >= 2) {
                knightPositions.add(kingPosition + LEFT.offset + DOWN.offset * 2);
            }
            if (edgeDistance.right >= 1 && edgeDistance.bottom >= 2) {
                knightPositions.add(kingPosition + RIGHT.offset + DOWN.offset * 2);
            }
            if (edgeDistance.left >= 2 && edgeDistance.top >= 1) {
                knightPositions.add(kingPosition + LEFT.offset * 2 + UP.offset);
            }
            if (edgeDistance.left >= 2 && edgeDistance.bottom >= 1) {
                knightPositions.add(kingPosition + LEFT.offset * 2 + DOWN.offset);
            }
            if (edgeDistance.right >= 2 && edgeDistance.top >= 1) {
                knightPositions.add(kingPosition + RIGHT.offset * 2 + UP.offset);
            }
            if (edgeDistance.right >= 2 && edgeDistance.bottom >= 1) {
                knightPositions.add(kingPosition + RIGHT.offset * 2 + DOWN.offset);
            }

            for (int position : knightPositions) {
                if (board[position] instanceof Knight && board[position].getColor() == sideInCheck.getOpposite()) {
                    checkResolutionBuffer.add(position);
                    checkFromNonSlidingPiece = true;
                    // There cannot be a check from 2 knights at once, so there is no need to check other possible knight positions
                    break;
                }
            }
        }

        if ((numSlidingCheckDirections > 0 && checkFromNonSlidingPiece) || numSlidingCheckDirections > 1) {
            // Discovered double check, there are no resolution squares because it cannot be blocked and both pieces
            // cannot be captured in one move.
            checkResolutionBuffer.clear();
        }

        // TODO: This might cause concurrency bugs
        checkResolutions.clear();
        checkResolutions.addAll(checkResolutionBuffer);
    }

    /**
     * @return The side currently in check, or null if no side is in check
     */
    public Piece.Color getCheckState() {
        if (isSideInCheck(WHITE)) {
            return WHITE;
        } else if (isSideInCheck(BLACK)) {
            return BLACK;
        }

        return null;
    }

    public boolean isSideInCheck(Piece.Color side) {
        long kingBitboard = side == WHITE ? bitboards.whiteKing : bitboards.blackKing;
        long squaresAttackedBySide = getSquaresAttackedBySide(side.getOpposite());
        return (squaresAttackedBySide & kingBitboard) != 0;
    }

    public boolean isSideInCheckAfterMove(Piece.Color side, Move move) {
        boolean isInCheck;

        makeMove(move);
        isInCheck = isSideInCheck(side);
        unmakeMove();

        return isInCheck;
    }

    public boolean isCheckmate(Piece.Color side) {
        return isSideInCheck(side) && getAllLegalMovesForSide(side).isEmpty();
    }

    public boolean isCheck() {
        return isSideInCheck(WHITE) || isSideInCheck(BLACK);
    }

    public boolean isThreefoldRepetition() {
        HashMap<Long, Integer> repetitions = new HashMap<>();

        for (long hash : hashHistory) {
            if (repetitions.containsKey(hash)) {
                repetitions.put(hash, repetitions.get(hash) + 1);
            } else {
                repetitions.put(hash, 1);
            }
            if (repetitions.get(hash) >= 3) {
                return true;
            }
        }

        return false;
    }

    public boolean isInsufficientMaterial() {
        // TODO: Improve insufficient material detection

        int whitePawnCount = Long.bitCount(bitboards.whitePawns);
        int whiteKnightCount = Long.bitCount(bitboards.whiteKnights);
        int whiteBishopCount = Long.bitCount(bitboards.whiteBishops);
        int whiteRookCount = Long.bitCount(bitboards.whiteRooks);
        int whiteQueenCount = Long.bitCount(bitboards.whiteQueens);

        int blackPawnCount = Long.bitCount(bitboards.blackPawns);
        int blackKnightCount = Long.bitCount(bitboards.blackKnights);
        int blackBishopCount = Long.bitCount(bitboards.blackBishops);
        int blackRookCount = Long.bitCount(bitboards.blackRooks);
        int blackQueenCount = Long.bitCount(bitboards.blackQueens);

        if (whiteQueenCount > 0 || blackQueenCount > 0 || whiteRookCount > 0 || blackRookCount > 0 || whitePawnCount > 0 || blackPawnCount > 0) {
            return false;
        }

        if (whiteKnightCount > 2 || blackKnightCount > 2) {
            return false;
        }

        if (whiteBishopCount > 2 || blackBishopCount > 2) {
            return false;
        }

        return true;
    }

    public boolean isStalemate() {
        return !isCheck() && getLegalMovesForSideToMove().isEmpty();
    }

    public boolean isDraw() {
        return halfMoveClock >= 50 || isStalemate() || isInsufficientMaterial() || isThreefoldRepetition();
    }

    public int getKingPosition(Piece.Color color) {
        return color == WHITE ? whiteKingPos : blackKingPos;
    }

    public long getSquaresAttackedBySide(Piece.Color side) {
        return side == WHITE ? squaresAttackedByWhite : squaresAttackedByBlack;
    }

    public void updatePawnAttackingSquares() {
        // TODO: Do this in computeAttackingSquares
        for (Piece piece : board) {
            if (piece instanceof Pawn) {
                if (piece.getColor() == WHITE) {
                    whitePawnAttackingSquares |= piece.getAttackingSquares();
                } else {
                    blackPawnAttackingSquares |= piece.getAttackingSquares();
                }
            }
        }
    }

    private long computeAttackingSquaresForSide(Piece.Color side) {
        long bitboard = 0;

        for (Piece piece : board) {
            if (piece != null && piece.getColor() == side) {
                bitboard |= piece.getAttackingSquares();
            }
        }

        return bitboard;
    }

    public boolean isPseudoLegalMoveLegal(Move pseudoLegalMove) {
        return !isSideInCheckAfterMove(board[pseudoLegalMove.start()].getColor(), pseudoLegalMove);
    }

    public ArrayList<Move> getPseudoLegalMoves() {
        var moves = new ArrayList<Move>();
        for (Piece piece : board) {
            if (piece == null || piece.getColor() != sideToMove) {
                continue;
            }
            moves.addAll(piece.getPseudoLegalMoves());
        }
        return moves;
    }

    public ArrayList<Move> getAllLegalMovesForSide(Piece.Color side) {
        var legalMoves = new ArrayList<Move>();

        for (Piece piece : board) {
            if (piece != null && piece.getColor() == side) {
                legalMoves.addAll(piece.getLegalMoves());
            }
        }

        return legalMoves;
    }

    public ArrayList<Move> getLegalMovesForSideToMove() {
        var moves = new ArrayList<Move>();

        for (Piece piece : board) {
            if (piece != null && piece.getColor() == sideToMove) {
                moves.addAll(piece.getLegalMoves());
            }
        }

        return moves;
    }

    public ArrayList<Move> getPseudoLegalCaptures() {
        var moves = new ArrayList<Move>();

        for (Move move : getPseudoLegalMoves()) {
            if (move.capturedPiece() != null) {
                moves.add(move);
            }
        }

        return moves;
    }

    public Piece[] getBoard() {
        return board;
    }

    public int getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

    public Piece.Color getSideToMove() {
        return sideToMove;
    }

    public CastlingRights getCastlingRights() {
        return castlingRights;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public ArrayList<Integer> getCheckResolutions() {
        return checkResolutions;
    }

    public String getMoveHistory() {
        StringBuilder s = new StringBuilder();
        Iterator<Move> it = moveHistory.descendingIterator();

        while (it.hasNext()) {
            s.append(it.next().toString());
            s.append(" ");
        }

        return s.toString();
    }

    public Deque<Move> getMoveHistoryStack() {
        return moveHistory;
    }

    public String getPgn() {
        /*
         This works by first creating a new board from the starting position and then playing each move in the
         reversed move history. This allows us to simply look at the piece that was on the board at that time
         to determine its type, rather than storing that information in the move. This method is rarely called, so
         performance isn't a concern.
        */

        StringBuilder s = new StringBuilder();
        Iterator<Move> it = moveHistory.descendingIterator();
        Board board2 = new Board();
        int moveCount = 1;

        while (it.hasNext()) {
            StringBuilder moveString = new StringBuilder();
            Move move = it.next();
            Piece movedPiece = board2.getPieceOn(move.start());
            if (movedPiece.getColor() == WHITE) {
                moveString.append(moveCount).append(". ");
                moveCount++;
            }

            if (movedPiece instanceof Pawn) {
                if (move.capturedPiece() != null) {
                    moveString.append(Square.getFileChar(move.start()));
                }
            } else if (move.castlingDirection() == CastlingDirection.SHORT) {
                moveString.append("O-O");
            } else if (move.castlingDirection() == CastlingDirection.LONG) {
                moveString.append("O-O-O");
            } else {
                moveString.append(Character.toUpperCase(movedPiece.getChar()));

                /*
                Resolve ambiguous moves where multiple pieces of the same type can move to the same square. This is done
                by first generating all the legal moves in that position and checking if there are any moves with the same
                destination square. If there are, we iterate over them to check which position component (file or rank)
                is different, and add it to the move. If both the file and rank are the same (such as in the position
                8/k7/8/8/7Q/8/8/4Q1KQ, where 3 queens can move to e4), we append the full square after the letter of the
                moving piece.
                 */
                var moves = board2.getLegalMovesForSideToMove();
                List<Move> movesToDestinationSquare = moves.stream()
                        .filter(m -> board2.getPieceOn(m.start()).getChar() == movedPiece.getChar())
                        .filter(m -> m.destination() == move.destination())
                        .toList();
                if (movesToDestinationSquare.size() > 1) {
                    List<Integer> otherStartPositions = movesToDestinationSquare.stream()
                            .map(Move::start)
                            .filter(start -> start != move.start())
                            .toList();
                    boolean hasDifferentStartingRank = true;
                    boolean hasDifferentStartingFile = true;
                    for (int start : otherStartPositions) {
                        if (Square.getRank(start) == Square.getRank(move.start())) {
                            hasDifferentStartingRank = false;
                        }
                        if (Square.getFile(start) == Square.getFile(move.start())) {
                            hasDifferentStartingFile = false;
                        }
                    }
                    if (hasDifferentStartingRank) {
                        moveString.append(Square.getRank(move.start()));
                    } else if (hasDifferentStartingFile) {
                        moveString.append(Square.getFileChar(move.start()));
                    } else {
                        moveString.append(new Square(move.start()));
                    }
                }
            }
            if (move.capturedPiece() != null) {
                moveString.append("x");
            }
            if (move.castlingDirection() == null) {
                moveString.append(new Square(move.destination()));
            }
            if (move.promotion() != null) {
                moveString.append("=").append(switch (move.promotion()) {
                    case QUEEN -> 'q';
                    case ROOK -> 'r';
                    case BISHOP -> 'b';
                    case KNIGHT -> 'n';
                });
            }
            if (board2.isCheckmate(WHITE) || board2.isCheckmate(BLACK)) {
                moveString.append("#");
            } else if (board2.isCheck()) {
                // TODO: Fix + incorrectly being appended at the end of the full move
                moveString.append("+");
            }
            s.append(moveString).append(" ");
            board2.makeMove(move);
        }

        return s.toString();
    }

    public long getWhitePawnAttackingSquares() {
        return whitePawnAttackingSquares;
    }

    public long getBlackPawnAttackingSquares() {
        return blackPawnAttackingSquares;
    }

    public long getAllPieces() {
        return bitboards.getAllPieces();
    }

    public long getPieces(Piece.Color side) {
        return bitboards.getPieces(side);
    }
}
