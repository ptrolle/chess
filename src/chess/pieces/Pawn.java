package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

//CLASSE PEÃO
public class Pawn extends ChessPiece {

    public Pawn (Board board, Color color){
        super(board, color);
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()]; //matriz auxiliar
        Position p = new Position(0,0); //auxiliar position

        if(getColor() == Color.WHITE) { //testo se a cor do peao é branco
            p.setValues(position.getRow() - 1, position.getColumn()); //posicao acima no tabuleiro
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;

            }

            p.setValues(position.getRow() - 2, position.getColumn()); //posicao 2 acima no tabuleiro
            Position p2 = new Position(position.getRow() - 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
                mat[p.getRow()][p.getColumn()] = true;
            }

            p.setValues(position.getRow() - 1, position.getColumn() - 1); //posicao diagonal left se tem inimigo
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;

            }

            p.setValues(position.getRow() - 1, position.getColumn() + 1); //posicao diagonal right se tem inimigo
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;

            }
        }
        else{ //PEAO PRETO
            p.setValues(position.getRow() + 1, position.getColumn()); //posicao acima no tabuleiro
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }

            p.setValues(position.getRow() + 2, position.getColumn()); //posicao 2 acima no tabuleiro
            Position p2 = new Position(position.getRow() + 1, position.getColumn());
            if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
                mat[p.getRow()][p.getColumn()] = true;
            }

            p.setValues(position.getRow() + 1, position.getColumn() - 1); //posicao diagonal left se tem inimigo
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }

            p.setValues(position.getRow() + 1, position.getColumn() + 1); //posicao diagonal right se tem inimigo
            if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
        }
        return mat;
    }

    @Override
    public String toString(){
        return "P";
    }
}
