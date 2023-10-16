package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.*;

import java.util.*;

import static io.github.boriskrisanov.javachess.board.Direction.*;

/**
 * Holds the current state of the game (piece positions, side to move, check, checkmate and en passant target square)
 */
public class Board {
    private Piece[] board = new Piece[64];
    private ArrayList<Integer> squaresAttackedByWhite = new ArrayList<>();
    private ArrayList<Integer> squaresAttackedByBlack = new ArrayList<>();
    private final ArrayList<Integer> checkResolutions = new ArrayList<>();
    private int enPassantTargetSquare;
    private Piece.Color sideToMove;
    private CastlingRights castlingRights = new CastlingRights(false, false, false, false);
    private int halfMoveClock;
    private int moveNumber;

    public Board(Piece[] board) {
        this.board = board;
    }

    public Board(String fen) {
        loadFen(fen);
    }

    /**
     * Creates a board with the starting position
     */
    public Board() {
        loadStartingPosition();
    }

    /**
     * Loads the starting position (FEN rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1)
     */
    public void loadStartingPosition() {
        loadFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public void loadFen(String fen) {
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
            }
            i++;
        }

        this.castlingRights = new CastlingRights(castling);

        if (enPassantTargetSquare.equals("-")) {
            this.enPassantTargetSquare = -1;
        } else {
            this.enPassantTargetSquare = Square.fromString(enPassantTargetSquare.toLowerCase());
        }

        this.sideToMove = sideToMove.equals("w") ? Piece.Color.WHITE : Piece.Color.BLACK;

        this.halfMoveClock = Integer.parseInt(halfMoveClock);
        this.moveNumber = Integer.parseInt(fullMoveNumber);
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

        fen.append(sideToMove == Piece.Color.WHITE ? " w " : " b ");
        fen.append(castlingRights).append(" ");
        fen.append(enPassantTargetSquare == -1 ? "-" : enPassantTargetSquare);
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

    /**
     * Makes a move on the board without checking if it is legal. Also updates en passant target square.
     */
    public void makeMove(Move move) {
        var piece = board[move.start()];

        // The right to capture en passant has been lost because another move has been made
        enPassantTargetSquare = -1;

        // Set the en passant target square if a pawn moved 2 squares forward
        if (piece instanceof Pawn) {
            if (move.destination() == move.start() - 8 * 2) {
                enPassantTargetSquare = move.start() - 8;
            } else if (move.destination() == move.start() + 8 * 2) {
                enPassantTargetSquare = move.start() + 8;
            }
        }

        if (move.capturedPiece() != null) {
            board[move.capturedPiece().getPosition()] = null;
        }

        board[move.start()] = null;
        board[move.destination()] = piece;

        board[move.destination()].setPosition(move.destination());
        board[move.destination()].setBoard(this);

        sideToMove = sideToMove.getOpposite();

        computeAttackingSquares();
        computePinLines();
        computeCheckResolutions();
    }

    public void unmakeMove(Move move) {
        Piece piece = board[move.destination()];

        if (move.capturedPiece() != null) {
            board[move.destination()] = null;
            board[move.start()] = piece;
            board[move.capturedPiece().getPosition()] = move.capturedPiece();
            board[move.capturedPiece().getPosition()].setPosition(move.capturedPiece().getPosition());
            board[move.capturedPiece().getPosition()].setBoard(this);
        } else {
            board[move.destination()] = null;
            board[move.start()] = piece;
        }

        piece.setPosition(move.start());
        piece.setBoard(this);

        sideToMove = sideToMove.getOpposite();

        computeAttackingSquares();
        computePinLines();
        computeCheckResolutions();
    }

    private void computeAttackingSquares() {
        squaresAttackedByWhite = computeAttackingSquaresForSide(Piece.Color.WHITE);
        squaresAttackedByBlack = computeAttackingSquaresForSide(Piece.Color.BLACK);
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

        int kingPosition = getKing(sideInCheck).getPosition();
        int numSlidingCheckDirections = 0;

        // Sliding pieces
        for (Direction direction : Direction.values()) {
            ArrayList<Integer> squares = new ArrayList<>();
            for (int i = 0; i < EdgeDistance.get(kingPosition, direction); i++) {
                int targetSquare = kingPosition + direction.offset * (i + 1);

                if (board[targetSquare] == null) {
                    squares.add(targetSquare);
                } else if (board[targetSquare].isSlidingPiece() && board[targetSquare].getColor() != sideInCheck) {
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
            if (sideInCheck == Piece.Color.WHITE) {
                p1 = kingPosition + Direction.TOP_LEFT.offset;
                p2 = kingPosition + Direction.TOP_RIGHT.offset;

            } else {
                p1 = kingPosition + Direction.BOTTOM_LEFT.offset;
                p2 = kingPosition + Direction.BOTTOM_RIGHT.offset;

            }
            if (board[p1] instanceof Pawn && board[p1].getColor() == sideInCheck.getOpposite()) {
                checkResolutions.add(p1);
                checkFromNonSlidingPiece = true;
            }
            if (board[p2] instanceof Pawn && board[p2].getColor() == sideInCheck.getOpposite()) {
                checkResolutions.add(p2);
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
        computePinLines(Piece.Color.WHITE);
        computePinLines(Piece.Color.BLACK);
    }

    private void computePinLines(Piece.Color side) {
        int kingPosition = getKing(side).getPosition();

        // Reset pin directions
        for (Piece piece : board) {
            if (piece != null && piece.getColor() == side) {
                piece.setPinDirection(null);
            }
        }

        for (Direction direction : Direction.values()) {
            Piece lastFriendlyPieceSeen = null;
            PinDirection pinDirection = null;

            if (direction == Direction.UP || direction == Direction.DOWN) {
                pinDirection = PinDirection.VERTICAL;
            } else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                pinDirection = PinDirection.HORIZONTAL;
            } else if (direction == Direction.BOTTOM_LEFT || direction == Direction.TOP_RIGHT) {
                pinDirection = PinDirection.POSITIVE_DIAGONAL;
            } else if (direction == Direction.BOTTOM_RIGHT || direction == Direction.TOP_LEFT) {
                pinDirection = PinDirection.NEGATIVE_DIAGONAL;
            }

            for (int i = 0; i < EdgeDistance.get(kingPosition, direction); i++) {
                int targetSquare = kingPosition + direction.offset * (i + 1);

                if (board[targetSquare] == null) {
                    continue;
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
                    // There are more than 2 friendly pieces in front of the king, therefore none of them are pinned
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
        if (isSideInCheck(Piece.Color.WHITE)) {
            return Piece.Color.WHITE;
        } else if (isSideInCheck(Piece.Color.BLACK)) {
            return Piece.Color.BLACK;
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
        unmakeMove(move);

        return checkState;
    }

    public boolean isSideInCheck(Piece.Color side) {
        King king = getKing(side);

        if (king == null) {
            // This position doesn't have a king, so the concept of check doesn't exist
            return false;
        }

        ArrayList<Integer> squaresAttackedBySide = getSquaresAttackedBySide(side.getOpposite());
        Integer kingPos = king.getPosition();

        return squaresAttackedBySide.contains(kingPos);
    }

    public boolean isSideInCheckAfterMove(Piece.Color side, Move move) {
        boolean isInCheck;

        makeMove(move);
        isInCheck = isSideInCheck(side);
        unmakeMove(move);

        return isInCheck;
    }


    public King getKing(Piece.Color color) {
        for (Piece piece : board) {
            if (piece instanceof King && piece.getColor() == color) {
                return (King) piece;
            }
        }

        return null;
        // throw new InvalidPositionException(color.name() + " king not found on board");
    }

    public ArrayList<Integer> getSquaresAttackedBySide(Piece.Color side) {
        return side == Piece.Color.WHITE ? squaresAttackedByWhite : squaresAttackedByBlack;
    }

    private ArrayList<Integer> computeAttackingSquaresForSide(Piece.Color side) {
        var squares = new ArrayList<Integer>();

        for (Piece piece : board) {
            if (piece != null && piece.getColor() == side) {
                squares.addAll(piece.getAttackingSquares());
            }
        }

        return squares;
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

        Arrays.stream(board)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == side)
                .forEach(piece -> legalMoves.addAll(piece.getLegalMoves()));

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
}
