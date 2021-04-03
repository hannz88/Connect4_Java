public abstract class Participant {
    protected String symbol;
    private int colLim;  // the limit of the column
    private int currentCol=0;
    protected static final String ansiReset = "\u001B[0m";

    public Participant(String symbol, int col){
        this.symbol = symbol;
        this.colLim = col;
    }

    public abstract String getSymbol();  // abstract method

    public abstract void hasWon();

    public int getColLim(){
        // return colLim
        return this.colLim;
    }

    public void setCurrentCol(int col){
        this.currentCol = col;
    }

    public int getCurrentCol(){
        return this.currentCol;
    }

}
