package io.github.boriskrisanov.javachess.board;

import io.github.boriskrisanov.javachess.piece.King;
import io.github.boriskrisanov.javachess.piece.Pawn;
import io.github.boriskrisanov.javachess.piece.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Holds the current state of the game (piece positions, side to move, check, checkmate and en passant target square)
 */
public class Board {
    private Piece[] board = new Piece[64];
    private Square enPassantTargetSquare;
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
            }
            board[i] = Piece.fromChar(c, new Square(i), this);
            i++;
        }

        this.castlingRights = new CastlingRights(castling);

        if (enPassantTargetSquare.equals("-")) {
            this.enPassantTargetSquare = null;
        } else {
            this.enPassantTargetSquare = new Square(enPassantTargetSquare.toLowerCase());
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
        byte skippedSquaresCount = 0;

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
        fen.append(enPassantTargetSquare == null ? "-" : enPassantTargetSquare);
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


    public Piece getPieceOn(String square) {
        return getPieceOn(new Square(square));
    }

    /**
     * Makes a move on the board without checking if it is legal. Also updates en passant target square.
     */
    public void makeMove(Move move) {
        int startIndex = move.start().getIndex();
        int destinationIndex = move.destination().getIndex();

        var piece = board[startIndex];

        // The right to capture en passant has been lost because another move has been made
        enPassantTargetSquare = null;

        // Set the en passant target square if a pawn moved 2 squares forward
        if (piece instanceof Pawn) {
            if (destinationIndex == startIndex - 8 * 2) {
                enPassantTargetSquare = new Square(startIndex - 8);
            } else if (destinationIndex == startIndex + 8 * 2) {
                enPassantTargetSquare = new Square(startIndex + 8);
            }
        }

        if (move.capturedPiece() != null) {
            board[move.capturedPiece().getPosition().getIndex()] = null;
        }

        board[startIndex] = null;
        board[destinationIndex] = piece;

        board[destinationIndex].setPosition(move.destination());
        board[destinationIndex].setBoard(this);
    }

    public void unmakeMove(Move move) {
        // makeMove(new Move(move.destination(), move.start(), null));

        Piece piece = board[move.destination().getIndex()];

        if (move.capturedPiece() != null) {
            board[move.destination().getIndex()] = null;
            board[move.start().getIndex()] = piece;
            board[move.capturedPiece().getPosition().getIndex()] = move.capturedPiece();
            board[move.capturedPiece().getPosition().getIndex()].setPosition(move.capturedPiece().getPosition());
            board[move.capturedPiece().getPosition().getIndex()].setBoard(this);
        } else {
            board[move.destination().getIndex()] = null;
            board[move.start().getIndex()] = piece;
        }

        piece.setPosition(move.start());
        piece.setBoard(this);
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

        ArrayList<Square> squaresAttackedBySide = getSquaresAttackedBySide(side.getOpposite());
        Square kingPos = king.getPosition();

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

    public ArrayList<Square> getSquaresAttackedBySide(Piece.Color side) {
        var squares = new ArrayList<Square>();

        Arrays.stream(board)
                .filter(piece -> piece != null && piece.getColor() == side)
                .forEach(piece ->
                        squares.addAll(piece.getAttackingSquares())
                );

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

    public Piece[] getBoard() {
        return board;
    }

    public Square getEnPassantTargetSquare() {
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
}
