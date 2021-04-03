public class Display {
    private static final String ansiReset = "\u001B[0m";
    private static final String ansiRed = "\u001B[31m";
    private String sep;

    public Display(int col){
        setSeparator(col);
    }

    public void displayBoard(String[][] array, int row, int col){
        // display the content of board
        this.displayHeader(col);
        for(int i=0; i<row; i++){
            String rowNum = ansiRed + i + ansiReset;
            String tmp = rowNum + "\t|";
            for(int j=0; j<col; j++){
                if(array[i][j].equals("[]")){
                    String emptyCells = String.format("%4s|"," ");
                    tmp += emptyCells;
                } else {
                    String symbol = String.format("%1s%2s%2s|"," ",array[i][j]," ");
                    tmp += symbol;
                }
            }
            System.out.format(tmp+"\n");
            System.out.println(this.sep);
        }
        this.displayColumn(col);
    }

    public void displayColumn(int col){
        // display the column
        String colNum = String.format("\t|");
        for(int j=0; j<col; j++){
            String num =  ansiRed+ j + ansiReset ;
            colNum += String.format("%1s%2s%2s|"," ",num," ");
        }
        System.out.println(colNum);
    }

    public void displayHeader(int col){
        // display header
        String tmp = String.format("%4s|", " ");
        for(int i=0; i<col; i++){
            tmp += String.format("%4s|", " ");
        }
        System.out.format(tmp +"\n");
        System.out.println(this.sep);
    }

    public void setSeparator(int col){
        // set the separator
        String sep = "----+";
        for(int i=0; i<col; i++){
            sep += "----+";
        }
        this.sep = sep;
    }
}
