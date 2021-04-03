public class Board {
    private Cell[][] board;
    private int row;
    private int col;
    private int totalSpacesLeft;  // keep count of how many spaces left on the board

    public Board(int row, int col){
        // constructor for Board
        board = new Cell[row][col];
        this.row = row;
        this.col = col;
        this.totalSpacesLeft = row*col;
        for (int i=0; i < row; i++){
            for (int j=0; j<col; j++){
                board[i][j] = new Cell();
            }
        }
    }

    public String[][] getBoard(){
        // return the objects on the board as a 2D array
        String[][] array = new String[this.row][this.col];
        for (int i=0; i<this.row; i++){
            for(int j=0; j<this.col; j++){
                array[i][j] = board[i][j].getSymbol();
            }
        }
        return array;
    }

    public void placeCounter(String participantSymbol, int colNum){
        // sets the token on the board
        for(int i = this.row-1; i>=0; i--){  // go from the bottom up
            if(board[i][colNum].checkIfOccupy()==false){  // if the row is not occupy
                board[i][colNum].setSymbol(participantSymbol);
                this.totalSpacesLeft --;
                break;
            }
        }
    }

    public int getTotalSpacesLeft(){
        // return the total spaces left for game
        // not sure if it's useful yet
        return this.totalSpacesLeft;
    }

    public boolean checkColFull(int colNum){
        // check if a particular column chosen is already full
        return board[0][colNum].checkIfOccupy();  // if the first row is already occupied then the column is full
    }

    public boolean checkBoardFull(){
        // check if the board is already full
        return (this.getTotalSpacesLeft() <= 0);
    }

    public boolean checkConsecutiveN(int n, String participantSymbol){
        // checks for consecutive N participantSymbols horizontally, vertically, in forward slash and backward slash directions
        return (checkDiagonalForwardN(n,participantSymbol) || checkHorizontalN(n,participantSymbol) || checkVerticalN(n,participantSymbol) ||
                checkDiagonalBackwardN(n,participantSymbol) );
    }

    public boolean checkHorizontalN(int n, String participantSymbol){
        // check for N consecutive participantSymbol horizontally
        int k;
        boolean consecutive = false;
        for(int i=0; i<this.row; i++){
            int rowNum = i;
            for (int j=0; j<=this.col-n; j++){
                if (board[i][j].getSymbol().equals(participantSymbol)){
                    k = 1;
                    consecutive = true;
                    while((consecutive) && (k < n)){
                        if(board[rowNum][j+1].getSymbol().equals(participantSymbol)){
                            k++;
                            if(k==n){return true;}
                            else {
                                j++;
                            }
                        } else {
                            consecutive = false;
                            break;
                        }
                    }
                }
            }
        }
        return consecutive;
    }

    public boolean checkVerticalN(int n, String participantSymbol){
        // check for N consecutive participantSymbol vertically
        int k;
        boolean consecutive = false;
        for(int i =0; i<=this.row-n; i++){
            int rowNum = i;
            for (int j=0; j<this.col; j++){
                if (board[i][j].getSymbol().equals(participantSymbol)){
                    k = 1;
                    consecutive = true;
                    while((consecutive) && (k < n)){
                        if(board[rowNum+1][j].getSymbol().equals(participantSymbol)){
                            k++;
                            if(k==n){
                                return true;
                            } else {
                                rowNum++;
                            }
                        } else {
                            consecutive = false;
                            break;
                        }
                    }
                }
            }
        }
        return consecutive;
    }

    public boolean checkDiagonalBackwardN(int n, String participantSymbol){
        // check for N consecutive participantSymbol in backward diagonal method
        int k;
        boolean consecutive = false;
        for(int i=0; i<=this.row-n; i++){
            int rowNum = i;
            for (int j=0; j<=this.col-n; j++){
                if (board[i][j].getSymbol().equals(participantSymbol)){
                    k = 1;
                    consecutive = true;
                    while((consecutive) && (k < n)){
                        if(board[rowNum+1][j+1].getSymbol().equals(participantSymbol)){
                            k++;
                            if(k==n){return true;}
                            else {
                                rowNum++;
                                j++;
                            }
                        } else {
                            consecutive = false;
                            break;
                        }
                    }
                }
            }
        }
        return consecutive;
    }

    public boolean checkDiagonalForwardN(int n, String participantSymbol){
        // check for N consecutive participantSymbol in forward slash method
        int k;
        boolean consecutive = false;
        for(int i=0; i<=this.board.length - n; i++){
            int rowNum = i;
            for (int j=this.board[0].length-1; j>=n-1; j--){
                if (board[i][j].getSymbol().equals(participantSymbol)){
                    k = 1;
                    consecutive = true;
                    while((consecutive) && (k < n)){
                        if(board[rowNum+1][j-1].getSymbol().equals(participantSymbol)){
                            k++;
                            if(k==n){return true;}
                            else {
                                rowNum++;
                                j--;
                            }
                        } else {
                            consecutive = false;
                            break;
                        }
                    }
                }
            }
        }
        return consecutive;
    }
}
