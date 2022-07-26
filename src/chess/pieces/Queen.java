package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

    public Queen(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString(){
        return "Q";
    }

    //aqui realmente testa os possiveis movimentos da torre no tabuleiro
    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0,0);

        //positions ABOVE, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow() - 1, position.getColumn());
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setRow(p.getRow() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions LEFT, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow(), position.getColumn() - 1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setColumn(p.getColumn() - 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){ //se existe peça adversaria no final
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions RIGHT, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow(), position.getColumn() + 1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setColumn(p.getColumn() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){ //se existe peça adversaria no final
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions BELOW, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow() + 1, position.getColumn());
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setRow(p.getRow() + 1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions NW, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow() - 1, position.getColumn() - 1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setValues(p.getRow() - 1, p.getColumn() - 1 );
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions NE, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setValues(p.getRow()-1, p.getColumn()+1 );
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){ //se existe peça adversaria no final
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions SE, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow()+1, position.getColumn() + 1);
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setValues(p.getRow()+1, p.getColumn()+1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){ //se existe peça adversaria no final
            mat[p.getRow()][p.getColumn()] = true;
        }

        //positions SW, percorrendo para marcar de positivo posicoes disponiveis
        p.setValues(position.getRow() + 1, position.getColumn() -1 );
        while(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getColumn()] = true; //assim retornando positivo que pode ir para essas posicoes
            p.setValues(p.getRow()+1, p.getColumn()-1);
        }
        if(getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getColumn()] = true;
        }

        return mat;
    }
}
