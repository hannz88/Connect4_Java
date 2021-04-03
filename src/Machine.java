import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.entry;

public class Machine extends Participant{

    private static final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerCase = upperCase.toLowerCase(Locale.ROOT);
    private static final String num = "0123456789";
    private static final String alphaNum = upperCase + lowerCase + num;
    private static Random rand = new Random();
    private static final String ansiYellow = "\u001B[33m";
    private static boolean firstTime=true;
    public Map<String, directionVector> dirWithVector;

    public Machine(String playerSymbol, int col, int row){
        super(generateSymbol(playerSymbol), col);
        this.setCurrentCol(rand.nextInt(col));
        this.createMapDirWithVector();
    }

    public static String generateSymbol(String avoidSymbol){
        // generate a random symbol that's different from avoidSymbol
        while(true){
            char tmp = alphaNum.charAt(rand.nextInt(alphaNum.length()));
            String symbol = String.valueOf(tmp);
            if(!symbol.equals(avoidSymbol)){
                return ansiYellow + symbol + ansiReset;
            }
        }
    }

    @Override
    public String getSymbol(){
        // get the symbol of the machine
        return this.symbol;
    }

    public int generateCol(String[][] array, String playerSymbol, int rowNum, int numOfConnect){
        int ans;
        if(firstTime==true){
            // if no col has been generated before
            ans=this.generateRandCol();
            this.firstTime = false;
        } else {
            ans = this.evaluateGenerateCol(array, playerSymbol, rowNum, numOfConnect);
        }
        return ans;
    }

    public int generateRandCol(){
        // generate a random column
        return rand.nextInt(this.getColLim());
    }

    public int evaluateGenerateCol(String[][] array, String playerSymbol, int rowNum, int numOfConnect){
        int potentialCol = 0, potentialI = 0, potentialJ=0;
        String bestDir = "";
        Map<String, Integer> previousBestScores = createHashMapPvC();  // create a bestscores = {Empty:0, Player:0, Computer:0}
        for(int i= rowNum-1; i >= 0; i--){
            for (int j=0; j<this.getColLim(); j++){
                if((i==0) && (!array[i][j].equals("[]"))){
                    continue;
                }
                scoresAndDirection scoreDir = this.checkScoresInAllDirections(array, numOfConnect ,i , j, playerSymbol);
                Map<String, Integer> newScores = scoreDir.scores;  // extract the best scores in all 4 directions
                String direction = scoreDir.direction;  // extract the best direction
                if(isNewScoresBetter(previousBestScores, newScores) == true){  // if the newScores are better than the existing scores
                    previousBestScores = newScores;
                    potentialI = i;  // store the row coord
                    potentialJ = j;  // store the col coord
                    bestDir = direction;  // store the direction from where it should move
                }
            }
        }
//        System.out.println(previousBestScores.toString() + " " + bestDir);
        if((bestDir.equals("")) || (isAllScoresEmpty(previousBestScores)==true)){  // ie if no best direction or all scores are empty
            return this.generateRandCol();  // randomly generate one
        } // edit here
//        System.out.format("Best i: %d, best j: %d\n", potentialI, potentialJ);  // debug
//        System.out.println(previousBestScores.toString() + " " + bestDir);  // debug
        int ans = findEmptySlot(array, potentialI, potentialJ, bestDir, numOfConnect);  // find the empty slot in the best direction.
        if(!array[0][ans].equals("[]")){  // if the suggested best column is already full
            return this.generateRandCol();
        }
        return ans;
    }

    public int findEmptySlot(String[][] array, int row, int col, String direction, int connect){
        int dRow = dirWithVector.get(direction).deltaRow;  // the direction of row moving
        int dCol = dirWithVector.get(direction).deltaCol;  // the direction col is moving to
        for(int roll = 1; roll<connect; roll++){
            if(array[row][col].equals("[]")){
                return col;
            }
            row += dRow;
            col += dCol;
        }
        return col;
    }

    public boolean isAllScoresEmpty(Map<String, Integer> scores){
        // check to see if all scores are 0
        for(Object key: scores.keySet()){
            if(scores.get(key) > 0){
                return false;
            }
        }
        return true;
    }

    public class directionVector{
        // a class for vector, deltaRow=changes to row, deltaCol=changes to col
        // eg (-1,0)
        public int deltaRow;
        public int deltaCol;
        public directionVector(int row, int col){
            this.deltaRow = row;
            this.deltaCol = col;
        }
    }
    public void createMapDirWithVector(){
        // create a dictionary with direction as the key and the respective vector as the value
        // eg "up":(-1,0)
        directionVector dir1 = new directionVector(-1,0);  // up
        directionVector dir2 = new directionVector(0,1);  // right
        directionVector dir3 = new directionVector(-1,1);  // upright
        directionVector dir4 = new directionVector(-1,-1);  // upleft
        directionVector dir5 = new directionVector(0,-1);  // left
        this.dirWithVector = Map.ofEntries(
                entry("up", dir1),
                entry("right", dir2),
                entry("upRight", dir3),
                entry("upLeft", dir4),
                entry("left", dir5)
                );
    }
    public class scoresAndDirection{
        // a class to store scores together with the direction
        // eg {Empty:0, Player:2, Computer:0}, "up"
        Map<String, Integer> scores;
        String direction;
        public scoresAndDirection(Map<String, Integer> scores, String direction){
            this.scores = scores;
            this.direction = direction;
        }
    }

    public scoresAndDirection checkScoresInAllDirections(String[][] board, int connect, int row, int col, String playerSymbol) {
        Map<String, Integer> currentBestScores = createHashMapPvC(); // create a dictionary {Empty:0, Player:0, Comp:o} for PvC.
        Map<String, Integer>newScores;
        String bestDirection = "";
        for (Object dir : dirWithVector.keySet()) {
//            System.out.format("The direction is %s\n", (String)dir);  // debug
            int drow = dirWithVector.get(dir).deltaRow;
            int dcol = dirWithVector.get(dir).deltaCol;
            newScores = checkScoresInSingleDirection(board, drow, dcol, row, col, connect, playerSymbol);  // check the scores for that direction
            if(isNewScoresBetter(currentBestScores, newScores)==true){  // if the newScores are better than the old scores
                currentBestScores = newScores;
                bestDirection = (String)dir;
            }
        }
        scoresAndDirection scoreDir = new scoresAndDirection(currentBestScores, bestDirection);
//        System.out.println(currentBestScores.toString() + " " + bestDirection);
        return scoreDir;
    }

    public boolean isNewPlayerScoresBetter(Map<String, Integer> oldScores, Map<String, Integer> newScores){
        // find out if the new player scores are better than the old player score
        if ((newScores.get("Player") >= 2) && (newScores.get("Player") >= oldScores.get("Player"))){
           return true;
        }
        return false;
    }

    public boolean isNewComputerScoresBetter(Map<String, Integer> oldScores, Map<String, Integer> newScores){
        if(newScores.get("Computer")>=2){
            if((newScores.get("Computer") >= oldScores.get("Computer")) && (newScores.get("Computer") >= oldScores.get("Player"))){
                return true;
            }
        }
        return false;
    }

    public boolean isNewScoresBetter(Map<String, Integer> oldScores, Map<String, Integer> newScores){
        return (isNewPlayerScoresBetter(oldScores, newScores) || isNewComputerScoresBetter(oldScores, newScores));
    }

    public Map<String, Integer> checkScoresInSingleDirection(String[][] board, int drow, int dcol, int row, int col, int connect, String playerSymbol){
        // checks a single direction and return the scores eg {Empty:1, Player:2, Computer:1}
        Map<String, Integer> scores = createHashMapPvC();  // default scores = {Empty:0 Player:0, Computer:0}
        Map<String, Integer> tmpScores = createHashMapPvC();
//        System.out.format("row: %d, col:%d\n", row, col);  // debug
        for(int offset = 0; offset < connect; offset++){
            // need to have a temp here if it breaks then the scores should not change
            if(((row+(offset*drow))<0) || ((col+(offset*dcol)) >= this.getColLim()) || ((col+(offset*dcol)) <0)){  // if it gets out of bound
                break;
            }
            String value = board[row + (offset * drow)][col + (offset * dcol)];
            if (value.equals("[]")) {  // if the cell is empty
                tmpScores.merge("Empty", 1, (a,b) -> a + b);
            } else if (value.equals(playerSymbol)) {  // if the cell has player in
                tmpScores.merge("Player",1, (a,b) -> a + b);
            } else if(value.equals(this.getSymbol())){  // if the cell has computer in
                tmpScores.merge("Computer", 1, (a,b) -> a + b);
            }
            if(offset==3){  // if it breaks before completing, the bestscores will be {Empty:0, Player:0, Computer:0}
                scores = tmpScores;
            }
        }
//        System.out.println(scores.toString());
        return scores;
    }

    public Map createHashMapPvC(){
        Map<String, Integer> map = new HashMap<>();
        map = Stream.of(new Object[][]{
                {"Empty", 0},
                {"Player", 0},
                {"Computer", 0},
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Integer) data[1]));
        return map;
    }

    @Override
    public void hasWon(){
        String agentSmith = "" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDO$==7ODMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNZ,... ..,=8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8,..       .:8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8,...      ..~INMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8,..:,..,,,:,=OMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMD,.:........,?ONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM:D..?77Z888O8DO8D,MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8$,ZMMMMM?MMMMMNO7MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM~8.+NMMM7.ZMMMOI8$MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM:O8D8??..~D7DMMM+MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM+N88+DMMM8?8NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM$ZDI?,M$N,=8MMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNO8DD8O$NNMMMMMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOMMMN8MMDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMONNMOOMMMMMMMZMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM~MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM~=ZOMMMMMMMMMMMMMMMMMM7MO,MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM~~~~ZON=OOMMMMMMMMMMMMMMMZMM7OI~$88MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMM,=~~~~~~~~?ONM7MMMMMMMMMMMMMOMMDOO7::~~ONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMI~:~7~=~I~88M8NMMMMMMMMMMMMMMMN$MMMOOOOI~::~$ONDZMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMM:OO~ONOO+~OON8=MMMMZMMMMMMMMMMMMMMMMOOOZ=~++ZNNOZMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMD7MMN$IZM8?OOMMMMMM7OMMMMMMMMMMMMN8OO8ZZ8ODMMD8MMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMM.MMMMMMMOMO8OOIMNMMMMNMMMMMMMMMMMMNM88OZO$ONMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN8MMOZM=OO8MMMZ$IMMMMMMMO8MOOO?OMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMDMMMMMMMMMMMMMMOMMOOOOMMM8ZMMZMMZMZZMDZZOOMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMNMMMMMMMMMMMMMMOMMZZ$:$MNDMMZMNNNZMOMDO+NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDMMNM,OONMMMO$MM$ZN+MNMZMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMMMMMNOMMMMMOOMNZIMMMO$MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMZZMMDNMMMM7M$ZZMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMMNMMMM8MDDMMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM$DMMDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDZMM8DMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMZMMMMMMMMMMNMMMNNNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMM8MDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMDI.....=~.$MMMMMMMMMMMMMMNNNNNDMMMMMMMMMMMMMMMMMMMMMMM~.,+.....ONNMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMNDO,,..,,I:..==~: ..MMMMMMMMMMMMMMMNMMMMMMMMMMMMMMMMMMMMMMM.....~..:.?.....=8DNMMMMMMMMMMM\n" +
                "MMMMMMMMMN=+?7ZI7$Z?+==?.. ...?MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM+... ..++++?$I?I$I?+:NMMMMMMMMMM\n" +
                "MMMMMMMMMMD88NMMMM$7,.:$$$.:.,NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM=..~.~~=.,,$DMMMMNDDMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMN$=.,$Z$,++=+MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM+==~.$?O,.=ZNMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMN+....=I....+OMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM$:....I7...:?MMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMNO..=7.....=7?~MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.87..+...$+.,DMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMN8...$8...Z,.8888MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM88N7.,Z...Z?..,DMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMD,..788..I$::$NNMMMMMMMMMMMMMMM8ZDMMMMMMMMMMMMMMMMMMMMMMMMMMMMOII+I=.,8Z:..:NMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMD~?DD,..7DM88MMMMMMMMMMMMMMMMMMMMMMDD8MMMMMMMMMMMMMMMNMMMMMMMMMM88MO?..IDD??NMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMD=INMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM8?7MMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMMMMMMMMDNDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDNNDNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDNMD8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDMMMMD8DMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDMMMDNMMMMMMMMMMMMMMDDOMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDNDNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDNDDDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDNNDDDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNNMDNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNDNDDDNMM8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNNNMMMMM8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDMNDMMMMMM8MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMDMMMMMMM8NMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNNDMMMMMMOOMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMDMMMMMMMMDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNDMMDMMMMMMMMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMNMMMMNNMMNMMMNDDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\n" +
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM";
        String[] smith = agentSmith.split("\\r?\\n");
        try {
            for(String line:smith){
                System.out.println(line);
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}

