public class Cell {
    private String symbol = "[]";
    private boolean isOccupy = false;

    public String getSymbol(){
        // return the symbol that occupy the cell
        return this.symbol;
    }

    public void setSymbol(String symbol){
        // set the symbol and set the cell as occupied
        this.symbol = symbol;
        setOccupy();
    }

    public boolean checkIfOccupy(){
        // check of the cell is already occupied
        return this.isOccupy;
    }

    public void setOccupy(){
        // set the cell to be occupied
        this.isOccupy = true;
    }

}
