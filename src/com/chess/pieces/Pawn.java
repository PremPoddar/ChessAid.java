//Created by Prem Poddar on the 19/08/2023

package com.chess.pieces;

import com.chess.Alliance;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.Tile;
import com.chess.moves.Move;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece{
    Piece promotionPiece;
    Pawn enPassantAttackedPawn;
    private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};
    public Pawn(final Alliance alliance, final int piecePosition){
        super(PieceType.PAWN, alliance, piecePosition);
    }

    @Override
    public String toString() {
        return alliance.isWhite() ? PieceType.PAWN.toString().toUpperCase() : PieceType.PAWN.toString().toLowerCase();
    }

    @Override
    public Collection<Move> calculateLegalMoves(final List<Tile> board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.getPiecePosition() + (currentCandidateOffset * this.getAlliance().getDirection());

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                //Checking if the square ahead of the pawn is not occupied then adding it as a legal move (one square)
                if (currentCandidateOffset == 8 && !board.get(candidateDestinationCoordinate).tileIsOccupied()) {
                    if(this.getAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)){
                        legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                        //TODO: handle the promotion properly from the Board class
                        promotionPiece = new Queen(this.getAlliance(), candidateDestinationCoordinate);
                    }else {
                        legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                    }

                    /*Checking if it is the first move of the pawn and if it is on its starting square and then checking
                     * if there is no piece ahead of it or two squares ahead of it then adding it as a legal move.
                     * (two squares)*/
                }else if (currentCandidateOffset == 16 && isFirstMove &&
                        ((BoardUtils.SECOND_RANK[this.getPiecePosition()] && this.getAlliance().isBlack()) ||
                                (BoardUtils.SEVENTH_RANK[this.getPiecePosition()] && this.getAlliance().isWhite()))) {
                    final int behindCandidateDestinationCoordinate = this.getPiecePosition() + (this.getAlliance().getDirection() * 8);
                    if (!board.get(behindCandidateDestinationCoordinate).tileIsOccupied() && !board.get(candidateDestinationCoordinate).tileIsOccupied()) {
                        legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                        Board.enPassantPawn = this;
                        //Pawn Jump
                    }

                    //Pawns attacking (diagonals) starts here

                /*If it is a white pawn and on the 8th column we cannot subtract 7 and if it is a black pawn and on the
                1st column we cannot add 7*/
                } else if (currentCandidateOffset == 7 &&
                        !((BoardUtils.EIGHT_COLUMN[this.getPiecePosition()] && this.getAlliance().isWhite()) ||
                                (BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getAlliance().isBlack()))) {
                    if (board.get(candidateDestinationCoordinate).tileIsOccupied()) {
                        System.out.println("Occupied");
                        final Piece pieceOnCandidate = board.get(candidateDestinationCoordinate).getPieceOnTile();
                        if (this.getAlliance() != pieceOnCandidate.getAlliance()) {
                            System.out.println("Alliance");
                            if(this.getAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)){
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                                //TODO: handle promotion properly from the Board class
                                promotionPiece = new Queen(this.getAlliance(), candidateDestinationCoordinate);
                            }else {
                                System.out.println("else");
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                            }
                        }
                    } else if (Board.getEnPassantPawn() != null){
                        if(Board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + this.getAlliance().getOpponentDirection())){
                            final Pawn pieceOnCandidateTile = Board.getEnPassantPawn();
                            if(this.getAlliance() != pieceOnCandidateTile.getAlliance()){
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                                enPassantAttackedPawn = pieceOnCandidateTile;
                            }
                        }
                    }

                /*If it is a white pawn and on the 1st column we cannot subtract 9 and if it is a black pawn and on the
                8th column we cannot add 9*/
                } else if (currentCandidateOffset == 9 &&
                        !((BoardUtils.FIRST_COLUMN[this.getPiecePosition()] && this.getAlliance().isWhite()) ||
                                (BoardUtils.EIGHT_COLUMN[this.getPiecePosition()] && this.getAlliance().isBlack()))) {
                    if (board.get(candidateDestinationCoordinate).tileIsOccupied()) {
                        final Piece pieceOnCandidate = board.get(candidateDestinationCoordinate).getPieceOnTile();
                        if (this.getAlliance() != pieceOnCandidate.getAlliance()) {
                            System.out.println("occupied2");
                            if(this.getAlliance().isPawnPromotionSquare(candidateDestinationCoordinate)){
                                System.out.println("!alliaance 2");
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                                promotionPiece = new Queen(this.getAlliance(), candidateDestinationCoordinate);
                                //TODO: handle promotion properly from the Board class
                            }else {
                                System.out.println("else2");
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                            }
                        }
                    }else if (Board.getEnPassantPawn() != null){
                        if(Board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - this.getAlliance().getOpponentDirection())){
                            final Pawn pieceOnCandidateTile = Board.getEnPassantPawn();
                            if(this.getAlliance() != pieceOnCandidateTile.getAlliance()){
                                legalMoves.add(new Move(piecePosition, candidateDestinationCoordinate));
                                enPassantAttackedPawn = pieceOnCandidateTile;
                            }
                        }
                    }
                }

                //Pawns attacking (diagonals) ends here
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public boolean isPawn(){return true;}

}
