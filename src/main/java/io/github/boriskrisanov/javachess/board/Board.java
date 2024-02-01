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
    private Piece[] board = new Piece[64];
    private long squaresAttackedByWhite = 0;
    private final Deque<Move> moveHistory = new ArrayDeque<>();
    // TODO: Store this in moves
    private final Deque<BoardState> boardHistory = new ArrayDeque<>();
    private long squaresAttackedByBlack = 0;
    private ArrayList<Integer> checkResolutions = new ArrayList<>();
    private int whiteKingPos = 0;
    private int blackKingPos = 0;
    private int enPassantTargetSquare;
    private Piece.Color sideToMove;
    private CastlingRights castlingRights = new CastlingRights(false, false, false, false);
    private int halfMoveClock = 0;
    private int moveNumber = 0;
    private long whitePawns = 0;
    private long whiteKnights = 0;
    private long whiteBishops = 0;
    private long whiteRooks = 0;
    private long whiteQueens = 0;
    private long whiteKing = 0;
    private long blackPawns = 0;
    private long blackKnights = 0;
    private long blackBishops = 0;
    private long blackRooks = 0;
    private long blackQueens = 0;
    private long blackKing = 0;
    // Stores the number of times a position has occurred (the key is the position hash)

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
        for (int i = 0; i < 64; i++) {
            board[i] = null;
        }
        whitePawns = whiteKnights = whiteBishops = whiteRooks = whiteQueens = whiteKing
                = blackPawns = blackKnights = blackBishops = blackRooks = blackQueens = blackKing = 0;

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
                if (board[i].getColor() == WHITE) {
                    if (board[i] instanceof Pawn) {
                        whitePawns |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Knight) {
                        whiteKnights |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Bishop) {
                        whiteBishops |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Rook) {
                        whiteRooks |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Queen) {
                        whiteQueens |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof King) {
                        whiteKing |= BitboardUtils.withSquare(i);
                    }
                } else {
                    if (board[i] instanceof Pawn) {
                        blackPawns |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Knight) {
                        blackKnights |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Bishop) {
                        blackBishops |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Rook) {
                        blackRooks |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof Queen) {
                        blackQueens |= BitboardUtils.withSquare(i);
                    } else if (board[i] instanceof King) {
                        blackKing |= BitboardUtils.withSquare(i);
                    }
                }
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
        computePinLines();
        computeCheckResolutions();
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

    public boolean isSquareEmpty(Square square) {
        return board[square.getIndex()] == null;
    }

    public boolean isSquareEmpty(int index) {
        return board[index] == null;
    }

    public Piece getPieceOn(int index) {
        return board[index];
    }


    public Piece getPieceOn(Square square) {
        return board[square.getIndex()];
    }

    public void makeMove(String uciMove) {
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
        // TODO: Only compute hash in one method and reuse it for search
        long hash = Hash.hash(this);
        boardHistory.push(new BoardState(enPassantTargetSquare, squaresAttackedByWhite, squaresAttackedByBlack, checkResolutions, whiteKingPos, blackKingPos, new CastlingRights(castlingRights), halfMoveClock, hash));

        var movedPiece = board[move.start()];

        if (move.capturedPiece() == null && (!(movedPiece instanceof Pawn))) {
            halfMoveClock++;
        } else {
            halfMoveClock = 0;
        }

        boolean isPromotion = move.promotion() != null;
        boolean isEnPassant = move.destination() == enPassantTargetSquare;
        boolean isCapture = move.capturedPiece() != null;

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
                        movePiece(rook, null, rook.getPosition(), move.destination() - 1);
                        rook.setPosition(move.destination() - 1);
                    }
                    case LONG -> {
                        var rook = movedPiece.getColor() == WHITE ? board[56] : board[0];
                        board[rook.getPosition()] = null;
                        board[move.destination() + 1] = rook;
                        movePiece(rook, null, rook.getPosition(), move.destination() + 1);
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
        if (movedPiece instanceof Rook || rookWasCaptured) {
            if (!rookWasCaptured) {
                // Rook has moved
                if (movedPiece.getPosition() == 0) {
                    castlingRights.removeForSide(BLACK, CastlingDirection.LONG);
                } else if (movedPiece.getPosition() == 7) {
                    castlingRights.removeForSide(BLACK, CastlingDirection.SHORT);
                } else if (movedPiece.getPosition() == 56) {
                    castlingRights.removeForSide(WHITE, CastlingDirection.LONG);
                } else if (movedPiece.getPosition() == 63) {
                    castlingRights.removeForSide(WHITE, CastlingDirection.SHORT);
                }
            } else {
                // Rook was captured
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
        }

        if (isEnPassant && movedPiece instanceof Pawn) {
            if (sideToMove == WHITE) {
                blackPawns &= ~BitboardUtils.withSquare(move.destination() + 8);
                board[move.destination() + 8] = null;
            } else {
                whitePawns &= ~BitboardUtils.withSquare(move.destination() - 8);
                board[move.destination() - 8] = null;
            }
        }

        board[move.start()] = null;

        if (!isPromotion) {
            movePiece(movedPiece, board[move.destination()], move.start(), move.destination());
            board[move.destination()] = movedPiece;
            movedPiece.setPosition(move.destination());
        } else {
            if (sideToMove == WHITE) {
                whitePawns &= ~BitboardUtils.withSquare(move.start());
            } else {
                blackPawns &= ~BitboardUtils.withSquare(move.start());
            }
            board[move.destination()] = switch (move.promotion()) {
                case QUEEN -> new Queen(sideToMove, move.destination(), this);
                case ROOK -> new Rook(sideToMove, move.destination(), this);
                case BISHOP -> new Bishop(sideToMove, move.destination(), this);
                case KNIGHT -> new Knight(sideToMove, move.destination(), this);
            };
            long moveDestinationBitboard = BitboardUtils.withSquare(move.destination());
            if (sideToMove == WHITE) {
                switch (move.promotion()) {
                    case QUEEN -> whiteQueens |= moveDestinationBitboard;
                    case ROOK -> whiteRooks |= moveDestinationBitboard;
                    case BISHOP -> whiteBishops |= moveDestinationBitboard;
                    case KNIGHT -> whiteKnights |= moveDestinationBitboard;
                }
            } else {
                switch (move.promotion()) {
                    case QUEEN -> blackQueens |= moveDestinationBitboard;
                    case ROOK -> blackRooks |= moveDestinationBitboard;
                    case BISHOP -> blackBishops |= moveDestinationBitboard;
                    case KNIGHT -> blackKnights |= moveDestinationBitboard;
                }
            }
            // Remove captured piece from bitboards
            if (move.capturedPiece() != null) {
                if (move.capturedPiece().getColor() == WHITE) {
                    if (move.capturedPiece() instanceof Pawn) {
                        whitePawns &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Knight) {
                        whiteKnights &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Bishop) {
                        whiteBishops &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Rook) {
                        whiteRooks &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Queen) {
                        whiteQueens &= ~BitboardUtils.withSquare(move.destination());
                    }
                } else {
                    if (move.capturedPiece() instanceof Pawn) {
                        blackPawns &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Knight) {
                        blackKnights &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Bishop) {
                        blackBishops &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Rook) {
                        blackRooks &= ~BitboardUtils.withSquare(move.destination());
                    } else if (move.capturedPiece() instanceof Queen) {
                        blackQueens &= ~BitboardUtils.withSquare(move.destination());
                    }
                }
            }
        }

        sideToMove = sideToMove.getOpposite();

        computeAttackingSquares();
        computePinLines();
        computeCheckResolutions();
    }

    public void unmakeMove() {
        var move = moveHistory.pop();
        var boardState = boardHistory.pop();

        enPassantTargetSquare = boardState.enPassantTargetSquare();
        whiteKingPos = boardState.whiteKingPos();
        blackKingPos = boardState.blackKingPos();
        castlingRights = boardState.castlingRights();
        halfMoveClock = boardState.halfMoveClock();

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
                movePiece(rook, null, 61, 63);
                rook.setPosition(63);
            } else {
                var rook = board[5];
                board[5] = null;
                board[7] = rook;
                movePiece(rook, null, 5, 7);
                rook.setPosition(7);
            }
        } else if (move.castlingDirection() == CastlingDirection.LONG) {
            if (movedPiece.getColor() == WHITE) {
                var rook = board[59];
                board[59] = null;
                board[56] = rook;
                movePiece(rook, null, 59, 56);
                rook.setPosition(56);
            } else {
                var rook = board[3];
                board[3] = null;
                board[0] = rook;
                movePiece(rook, null, 3, 0);
                rook.setPosition(0);
            }
        }

        board[move.destination()] = null;
        board[move.start()] = movedPiece;

        movePiece(movedPiece, null, move.destination(), move.start());

        if (move.promotion() != null) {
            if (movedPiece.getColor() == WHITE) {
//            whitePawns |= move.destination() + 8;
                switch (move.promotion()) {
                    case QUEEN -> whiteQueens &= ~BitboardUtils.withSquare(move.destination());
                    case ROOK -> whiteRooks &= ~BitboardUtils.withSquare(move.destination());
                    case BISHOP -> whiteBishops &= ~BitboardUtils.withSquare(move.destination());
                    case KNIGHT -> whiteKnights &= ~BitboardUtils.withSquare(move.destination());
                }
            } else {
//            blackPawns |= move.destination() - 8;
                switch (move.promotion()) {
                    case QUEEN -> blackQueens &= ~BitboardUtils.withSquare(move.destination());
                    case ROOK -> blackRooks &= ~BitboardUtils.withSquare(move.destination());
                    case BISHOP -> blackBishops &= ~BitboardUtils.withSquare(move.destination());
                    case KNIGHT -> blackKnights &= ~BitboardUtils.withSquare(move.destination());
                }
            }
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
                    whitePawns |= BitboardUtils.withSquare(move.destination() - 8);
                } else {
                    board[move.destination() + 8] = move.capturedPiece();
                    move.capturedPiece().setPosition(move.destination() + 8);
                    // If the move is an en passant capture, the captured piece must be a pawn
                    blackPawns |= BitboardUtils.withSquare(move.destination() + 8);
                }
            } else {
                board[move.destination()] = move.capturedPiece();
                move.capturedPiece().setPosition(move.destination());
//                board[move.destination()].setPosition(move.destination());
                long capturedPiecePositionBitboard = BitboardUtils.withSquare(move.destination());
                if (move.capturedPiece().getColor() == WHITE) {
                    if (move.capturedPiece() instanceof Pawn) {
                        whitePawns |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Knight) {
                        whiteKnights |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Bishop) {
                        whiteBishops |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Rook) {
                        whiteRooks |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Queen) {
                        whiteQueens |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof King) {
                        // This probably isn't necessary, but I'll write this just in case the code breaks
                        whiteKing |= capturedPiecePositionBitboard;
                    }
                } else {
                    if (move.capturedPiece() instanceof Pawn) {
                        blackPawns |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Knight) {
                        blackKnights |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Bishop) {
                        blackBishops |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Rook) {
                        blackRooks |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof Queen) {
                        blackQueens |= capturedPiecePositionBitboard;
                    } else if (move.capturedPiece() instanceof King) {
                        // This probably isn't necessary, but I'll write this just in case the code breaks
                        blackKing |= capturedPiecePositionBitboard;
                    }
                }
            }
        }

        squaresAttackedByWhite = boardState.whiteAttackingSquares();
        squaresAttackedByBlack = boardState.blackAttackingSquares();

        sideToMove = sideToMove.getOpposite();

        computePinLines();
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

    private void computePinLines() {
        computePinLines(WHITE);
        computePinLines(BLACK);
    }

    private void computePinLines(Piece.Color side) {
        int kingPosition = getKingPosition(side);

        // Reset pin directions
        for (Piece piece : board) {
            if (piece != null && piece.getColor() == side) {
                piece.setPinDirection(null);
            }
        }

        for (Direction direction : Direction.values()) {
            Piece lastFriendlyPieceSeen = null;
            PinDirection pinDirection = null;

            if (direction == UP || direction == DOWN) {
                pinDirection = PinDirection.VERTICAL;
            } else if (direction == LEFT || direction == RIGHT) {
                pinDirection = PinDirection.HORIZONTAL;
            } else if (direction == BOTTOM_LEFT || direction == TOP_RIGHT) {
                pinDirection = PinDirection.POSITIVE_DIAGONAL;
            } else if (direction == BOTTOM_RIGHT || direction == TOP_LEFT) {
                pinDirection = PinDirection.NEGATIVE_DIAGONAL;
            }

            for (int i = 0; i < EdgeDistance.get(kingPosition, direction); i++) {
                int targetSquare = kingPosition + direction.offset * (i + 1);

                boolean kingCanBeAttackedByRook = direction == UP || direction == DOWN || direction == LEFT || direction == RIGHT;
                boolean kingCanBeAttackedByBishop = direction == TOP_LEFT || direction == TOP_RIGHT || direction == BOTTOM_LEFT || direction == BOTTOM_RIGHT;
                boolean targetPieceCanAttackKing = board[targetSquare] instanceof Queen || (board[targetSquare] instanceof Rook && kingCanBeAttackedByRook) || (board[targetSquare] instanceof Bishop && kingCanBeAttackedByBishop);


                if (board[targetSquare] == null) {
                    continue;
                }

                if (!targetPieceCanAttackKing && board[targetSquare].getColor() == side.getOpposite() && lastFriendlyPieceSeen == null) {
                    // Not in check from this direction
                    break;
                }

                if (board[targetSquare].isSlidingPiece() && board[targetSquare].getColor() != side
                        && (((direction == UP || direction == DOWN || direction == LEFT || direction == RIGHT) && (board[targetSquare] instanceof Rook || board[targetSquare] instanceof Queen))
                        || ((direction == TOP_LEFT || direction == TOP_RIGHT || direction == BOTTOM_LEFT || direction == BOTTOM_RIGHT) && (board[targetSquare] instanceof Bishop || board[targetSquare] instanceof Queen)))
                ) {
                    if (lastFriendlyPieceSeen == null) {
                        // King is in check from this direction
                        break;
                    }

                    lastFriendlyPieceSeen.setPinDirection(pinDirection);
                    break;
                }
                if (lastFriendlyPieceSeen != null && board[targetSquare] != null) {
                    // There are more than 2 pieces in front of the king, therefore none of them are pinned
                    break;
                }
                if (board[targetSquare].getColor() == side) {
                    lastFriendlyPieceSeen = board[targetSquare];
                }
            }
        }
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

    /**
     * Plays the move on a separate board and returns the check state of that board (the current board is not modified)
     */

    public Piece.Color getCheckStateAfterMove(Move move) {
        Piece.Color checkState;

        makeMove(move);
        checkState = getCheckState();
        unmakeMove();

        return checkState;
    }

    public boolean isSideInCheck(Piece.Color side) {
        long kingBitboard = side == WHITE ? whiteKing : blackKing;
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

    public boolean isDraw() {
        // TODO: Update material count on make and unmake move
        int whitePawnCount = Long.bitCount(whitePawns);
        int whiteKnightCount = Long.bitCount(whiteKnights);
        int whiteBishopCount = Long.bitCount(whiteBishops);
        int whiteRookCount = Long.bitCount(whiteRooks);
        int whiteQueenCount = Long.bitCount(whiteQueens);

        int blackPawnCount = Long.bitCount(blackPawns);
        int blackKnightCount = Long.bitCount(blackKnights);
        int blackBishopCount = Long.bitCount(blackBishops);
        int blackRookCount = Long.bitCount(blackRooks);
        int blackQueenCount = Long.bitCount(blackQueens);

        HashMap<Long, Integer> repetitions = new HashMap<>();

        boolean isThreefoldRepetition = false;
        for (BoardState boardState : boardHistory) {
            long hash = boardState.positionHash();
            if (repetitions.containsKey(hash)) {
                repetitions.put(hash, repetitions.get(hash) + 1);
            } else {
                repetitions.put(hash, 1);
            }
            if (repetitions.get(hash) >= 3) {
                isThreefoldRepetition = true;
            }
        }

        // TODO: Improve insufficient material detection
        boolean isInsufficientMaterial = whiteQueenCount + blackQueenCount + whiteRookCount + blackRookCount + whitePawnCount + blackPawnCount == 0;
        boolean isStalemate = !isCheck() && getLegalMovesForSideToMove().isEmpty();
        return halfMoveClock >= 50 || isStalemate || isInsufficientMaterial || isThreefoldRepetition;
    }

    public int getKingPosition(Piece.Color color) {
        return color == WHITE ? whiteKingPos : blackKingPos;
    }

    public long getSquaresAttackedBySide(Piece.Color side) {
        return side == WHITE ? squaresAttackedByWhite : squaresAttackedByBlack;
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

    public ArrayList<Move> getAllLegalMoves() {
        var legalMoves = new ArrayList<Move>();

        Arrays.stream(board)
                .filter(Objects::nonNull)
                .forEach(piece -> legalMoves.addAll(piece.getLegalMoves()));

        return legalMoves;
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

    public ArrayList<Move> getCapturesForSideToMove() {
        var moves = new ArrayList<Move>();

        for (Move move : getLegalMovesForSideToMove()) {
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
                is different, and add it to the move. If both the file and rank are the same (Such as in the position
                8/k7/8/8/7Q/8/8/4Q1KQ, where 3 queens can move to e4, we append the full square after the letter of the
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

// ********************************************************
// Bitboard methods
// ********************************************************

    /**
     * Updates the appropriate bitboard to reflect the new position of the piece
     *
     * @param piece       The piece being moved
     * @param start       The starting position index
     * @param destination The destination position index
     */
    private void movePiece(Piece piece, Piece capturedPiece, int start, int destination) {
        // Remove captured piece
        if (capturedPiece instanceof Pawn) {
            if (capturedPiece.getColor() == WHITE) {
                whitePawns &= ~BitboardUtils.withSquare(destination);
            } else {
                blackPawns &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Knight) {
            if (capturedPiece.getColor() == WHITE) {
                whiteKnights &= ~BitboardUtils.withSquare(destination);
            } else {
                blackKnights &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Bishop) {
            if (capturedPiece.getColor() == WHITE) {
                whiteBishops &= ~BitboardUtils.withSquare(destination);
            } else {
                blackBishops &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Rook) {
            if (capturedPiece.getColor() == WHITE) {
                whiteRooks &= ~BitboardUtils.withSquare(destination);
            } else {
                blackRooks &= ~BitboardUtils.withSquare(destination);
            }
        } else if (capturedPiece instanceof Queen) {
            if (capturedPiece.getColor() == WHITE) {
                whiteQueens &= ~BitboardUtils.withSquare(destination);
            } else {
                blackQueens &= ~BitboardUtils.withSquare(destination);
            }
        }

        // Move piece
        if (piece instanceof Pawn) {
            if (piece.getColor() == WHITE) {
                whitePawns &= ~(BitboardUtils.withSquare(start));
                whitePawns |= BitboardUtils.withSquare(destination);
            } else {
                blackPawns &= ~(BitboardUtils.withSquare(start));
                blackPawns |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Knight) {
            if (piece.getColor() == WHITE) {
                whiteKnights &= ~(BitboardUtils.withSquare(start));
                whiteKnights |= BitboardUtils.withSquare(destination);
            } else {
                blackKnights &= ~(BitboardUtils.withSquare(start));
                blackKnights |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Bishop) {
            if (piece.getColor() == WHITE) {
                whiteBishops &= ~(BitboardUtils.withSquare(start));
                whiteBishops |= BitboardUtils.withSquare(destination);
            } else {
                blackBishops &= ~(BitboardUtils.withSquare(start));
                blackBishops |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Rook) {
            if (piece.getColor() == WHITE) {
                whiteRooks &= ~(BitboardUtils.withSquare(start));
                whiteRooks |= BitboardUtils.withSquare(destination);
            } else {
                blackRooks &= ~(BitboardUtils.withSquare(start));
                blackRooks |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof Queen) {
            if (piece.getColor() == WHITE) {
                whiteQueens &= ~(BitboardUtils.withSquare(start));
                whiteQueens |= BitboardUtils.withSquare(destination);
            } else {
                blackQueens &= ~(BitboardUtils.withSquare(start));
                blackQueens |= BitboardUtils.withSquare(destination);
            }
        } else if (piece instanceof King) {
            if (piece.getColor() == WHITE) {
                whiteKing &= ~(BitboardUtils.withSquare(start));
                whiteKing |= BitboardUtils.withSquare(destination);
            } else {
                blackKing &= ~(BitboardUtils.withSquare(start));
                blackKing |= BitboardUtils.withSquare(destination);
            }
        }
    }

    public long getAllPieces() {
        return getPieces(WHITE) | getPieces(BLACK);
    }

    public long getPieces(Piece.Color side) {
        if (side == WHITE) {
            return whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        }
        return blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public long getWhiteKing() {
        return whiteKing;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getBlackQueens() {
        return blackQueens;
    }

    public long getBlackKing() {
        return blackKing;
    }
}
