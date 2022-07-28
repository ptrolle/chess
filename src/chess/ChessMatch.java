package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check; //para verificar se player esta em check
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch(){
        board = new Board(8, 8);
        turn = 1; //primeiro turno do jogo
        currentPlayer = Color.WHITE; //comecando pelo usuario cor branca
        initialSetup();
    }

    public int getTurn(){
        return turn;
    }

    public Color getCurrentPlayer(){
       return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean getCheckMate(){
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted(){
        return promoted;
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];

        for(int i=0; i< board.getRows(); i++){
            for (int j=0; j< board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    //funcao para imprimir posicoes possiveis apartir da posicao de origem da peça
    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    //metodo para executar um movimento de uma peça
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source); //para validar pos de origem
        validateTargetPosition(source, target);
        Piece capturePiece = makeMove(source, target);

        //testo se movimento colocou o jogador em cheque
        if(testCheck(currentPlayer)){
            undoMove(source, target, capturePiece);
            throw new ChessException("You cant put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        //#special move promotion
        promoted = null;
        if(movedPiece instanceof Pawn){
            if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)){
                promoted = (ChessPiece) board.piece(target); //ai pego o peao que chegou no final do tabuleiro
                promoted = replacePromotedPiece("Q"); //aqui pode virar outro especial
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if(testCheckMate(opponent(currentPlayer))){ //aqui testamos se o jogo acabou
            checkMate = true; //checkmate aconteceu
        }
        else{
            nextTurn();
        }

        //#Special move en passant
        if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        }
        else{
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturePiece;
    }

    public ChessPiece replacePromotedPiece(String type){
        if(promoted == null){
            throw new IllegalStateException("There is no piece to be promoted");
        }
        if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){ //usando equals consigo comparar strings
            return promoted; // para caso digite uma letra errada entrando na funcao retorne a Q rainha que ja haviamos declaro anteriormente
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;
    }

    private ChessPiece newPiece(String type, Color color){
        if(type.equals("B")) return new Bishop(board, color);
        if(type.equals("N")) return new Knight(board, color);
        if(type.equals("Q")) return new Queen(board, color);
        return new Rook(board, color);
    }

    private void validateSourcePosition(Position position){
        if(!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position");
        }
        if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("The chosen piece is not yours");
        }
        if(!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private void validateTargetPosition(Position source, Position target){
        if(!board.piece(source).possibleMove(target)){
            throw new ChessException("The chosen piece cant move to target position");
        }
    }

    //metodo para trocar o turno de player
    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private  ChessPiece king(Color color){
        //filtro a list de peças no tabuleiro para encontrar o rei (king)
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p : list){
            if(p instanceof King){
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board"); //se acontecer de nao encontrar o king temos um problema serio no sistema
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());

        //testo se alguma peça oponente do king de tal cor pode colocar ele em check
        for(Piece p : opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color){
        if(!testCheck(color)){ //caso o king nao esta em CHECK claro que nao vai estar em checkMate e ja volta
            return false;
        }

        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());

        //aqui fazemos toda a logica para testar se o king tem alguma posicao para se movimentar se nao checkmate
        for(Piece p : list){
            boolean[][] mat = p.possibleMoves();
            for(int i=0; i<board.getRows(); i++){
                for(int j=0; j<board.getColumns(); j++){
                    if(mat[i][j]){ //movimento possivel
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    //funcao serve para remover a peça na posicao de origem e possivelmente posso remover a peça de target capturando-a
    //tambem no final retornando a peça capturada
    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target); //retiro do tabuleiro possivel peça capturada
        board.placePiece(p, target); //aqui coloco a peça na posicao final saiu da posicao de origem

        if(capturedPiece != null){ //caso tenha peça capturada
            piecesOnTheBoard.remove(capturedPiece); //remove a peça capturada do tabuleiro da list de peças ativas
            capturedPieces.add(capturedPiece); //assim add no list de peças capturadas
        }

        //#Special move castling kingside hook
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3 );
            Position targetT = new Position(source.getRow(), source.getColumn() + 1 );
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT); //retira o rook na posicao de origem
            board.placePiece(rook, targetT); //e coloca na posicao target
            rook.increaseMoveCount();
        }

        //#Special move castling queenside hook
        if(p instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4 );
            Position targetT = new Position(source.getRow(), source.getColumn() - 1 );
            ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        //#Special move en passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == null){
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(target.getRow()+1, target.getColumn());
                }
                else{
                    pawnPosition = new Position(target.getRow()-1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece) board.removePiece(target); //tiro a peça do posicao que foi final
        p.decreaseMoveCount();
        board.placePiece(p, source); //e recoloco no tabuleiro

        if(capturedPiece != null){ //para desfazer a jogada que colocou player em check, tirando uma possivel peça que poderia ter sido capturada
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        //#Special move castling kingside hook
        if(p instanceof King && target.getColumn() == source.getColumn() + 2){
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3 );
            Position targetT = new Position(source.getRow(), source.getColumn() + 1 );
            ChessPiece rook = (ChessPiece) board.removePiece(targetT); //retira o rook na posicao de origem
            board.placePiece(rook, sourceT); //e coloca na posicao target
            rook.decreaseMoveCount();
        }

        //#Special move castling queenside hook
        if(p instanceof King && target.getColumn() == source.getColumn() - 2){
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4 );
            Position targetT = new Position(source.getRow(), source.getColumn() - 1 );
            ChessPiece rook = (ChessPiece) board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        //#Special move en passant
        if(p instanceof Pawn){
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getColumn());
                }
                else{
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    //colocamos as peças no tabuleiro
    //aqui colocamos as peças mas com as coordenadas do xadrez e nao da matriz como anteriormente
    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);//tbm colocamos na list de peças no tabuleiro para controle das peças q estao ativas
    }

    //Esse metodo vai ser responsavel por iniciar a partida de xadrez colocando as peças no tabuleiro
    private void initialSetup(){
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE,this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK,this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK,this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));


        /*placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));*/
    }
}
