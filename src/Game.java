import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game {
    private Board newBoard;
    private int rowSize=6;
    private int colSize=7;
    private int connect=4;
    private String playerSymbol;
    private Player player;
    private Machine theMachine;
    private Display display;
    private int numMachines = 1;


    public Game(Scanner sc) throws Exception{
        // Welcome
        System.out.println("Welcome to Connect 4.");
        System.out.println("The objective: Make consecutive 4 tokens diagonally, vertically and horizontally!");
        // Ask player for a symbol
        this.playerChooseSymbol(sc);
        player = new Player(this.playerSymbol, this.colSize);
        // initiate board
        newBoard = new Board(this.rowSize, this.colSize);
        // initiate theMachine
        theMachine = new Machine(this.playerSymbol, this.colSize, this.rowSize);
        // initiate display
        display = new Display(this.colSize);
        // Recap
        this.recap();
        // playGame
        this.playGame(sc);

    }

    private void recap(){
        try {
            String s1, s2, s3, s4;
            s1 = String.format("The grid is is %dx%d", this.rowSize, this.colSize);
            s2 = String.format("The character that represents player: %s.", player.getSymbol());
            s3 = String.format("The character that represents the computer: %s.", theMachine.getSymbol());
            s4 = String.format("The matrix is loading...");
            printStringWithDelay(s1, 1500);
            printStringWithDelay(s2, 1500);
            printStringWithDelay(s3, 1500);
            printStringWithDelay(s4, 1500);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    private void playerChooseSymbol(Scanner sc){
        boolean isLenOne = false;
        String input;
        while(isLenOne == false){
            System.out.println("Please choose a single character to represent the player:");
            input = sc.nextLine();
            if(input.length() != 1){
                System.out.println("Input is not of a single character.");
                continue;
            } else {
                this.playerSymbol = input;
                isLenOne = true;
                break;
            }
        }

    }

    private void playGame(Scanner sc){
        boolean hasWon = false;
        boolean boardFull = newBoard.checkBoardFull();
        String[][] displayInput = newBoard.getBoard();
        System.out.println("The current board:");
        display.displayBoard(displayInput, this.rowSize, this.colSize);
        try {
            while ((hasWon == false) || (boardFull == false)) {  // while nobody won and the board is not full
                // player's move
                this.gameAskPlayerMove(sc);
                newBoard.placeCounter(player.getSymbol(), player.getCurrentCol());
                displayInput = newBoard.getBoard();
                display.displayBoard(displayInput, this.rowSize, this.colSize);
                if (newBoard.checkConsecutiveN(4, player.getSymbol()) == true) {
                    // check if player made a winning move
                    hasWon = true;
                    printStringWithDelay("Congratulations! You've won! Have a cookie!", 1000);
                    player.hasWon();
                    break;
                }
                // computer's move
                printStringWithDelay("It's the computer's turn now.", 2000);
                int computerMove = this.gameAskMachineMove();  // computer generate a response to player's move or random
                printStringWithDelay(String.format("The computer has chosen column %d.\n", computerMove), 1500);
                newBoard.placeCounter(theMachine.getSymbol(), theMachine.getCurrentCol());
                displayInput = newBoard.getBoard();
                display.displayBoard(displayInput, this.rowSize, this.colSize);
                if (newBoard.checkConsecutiveN(4, theMachine.getSymbol()) == true) {
                    hasWon = true;
                    printStringWithDelay("The Machine has won", 1500);
                    printStringWithDelay("The Matrix will now reload...", 1500);
                    theMachine.hasWon();
                    break;
                }
            }
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    private void gameAskPlayerMove(Scanner sc){
        boolean colFull = true;
        while(colFull == true) {
            System.out.println("It's the player's turn.");
            player.getUserInput(sc);  // get the user to chose the column
            int input = player.getCurrentCol();
            if(newBoard.checkColFull(input) == false){  // if the column is not full yet
                colFull = false;
                break;
            }
            System.out.println("Chosen column is already full.");
        }
    }

    private int gameAskMachineMove(){
        // get computer's next move
        boolean colFull = true;
        int generated=3;
        while(colFull == true) {
            generated = theMachine.generateCol(newBoard.getBoard(), player.getSymbol(), this.rowSize, this.connect);
            if (newBoard.checkColFull(generated) == false) {
                theMachine.setCurrentCol(generated);
                return generated;
            }
        }
        return generated;
    }

    private void printStringWithDelay(String str, long miliseconds) throws InterruptedException{
        // print a string follow by a delay
        System.out.println(str);
        TimeUnit.MILLISECONDS.sleep(miliseconds);
    }

}
