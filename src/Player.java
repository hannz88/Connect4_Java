import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Player extends  Participant{

    public static final String ansiBlue = "\u001B[34m";

    public Player(String symbol, int col){
        super(symbol, col);
    }

    public void getUserInput(Scanner sc){
        // method to ask player for the column input
        boolean isInt = false;
        while(isInt == false){
            System.out.println("Please enter a column for the token: ");
            if(sc.hasNextInt()){  // if input is int
                int input = sc.nextInt();
                if((input<0) || (input>this.getColLim())){  // if input is out bound
                    System.out.println("Input out of bound");
                    continue;
                }
                this.setCurrentCol(input);  //if input is int and not out of bound
                isInt = true;
            } else if (sc.hasNextLine()){  // if input is string instead
                sc.nextLine();
                int bufferLen = 0;
                try{
                    bufferLen = System.in.available(); // check if the system.in buffer is empty
                } catch(IOException ignored){}
                if (bufferLen > 0 ){  // if there's still something in the buffer, read the next line
                    sc.nextLine();
                }
                System.out.println("Invalid input for column.");
            }
        }
    }

    @Override
    public String getSymbol(){
        return this.ansiBlue + this.symbol + this.ansiReset;
    }

    @Override
    public void hasWon(){
        String cookieMons = "" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,,,..  .,,,,,,..   .,,,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,,.        ,,, MMM .   ,,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,..   ...  .,.7MMMM    .,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,..  .MMMM.,7,,MMM7.    ,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,,,..   MMMM.:D:,,,..... .,,,,,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,,:=7..... .,~=8I::,,,,,,,Z?++~,,,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,,,,=+?$$,:,::=?7D$$OD?~=~ZZIZ+=+++~,,,,,,,,,,,,,,\n" +
                ",,,,,,,,,,,+=I++$$7Z$???$I$$Z?Z7I$7$7O?+$7+I+=:,,,,,,,,,,,,,\n" +
                ",,,,,,,,,:=+7?777I?$$$IZ$ZZ7OOZZ$7O7I7?7Z$$?I+++~,,,,,,,,,,,\n" +
                ",,,,,,,,~+?7$$IZZO7$I$I7O7O$O7Z77$ZO7$77$I$?II+I?=,,,,,,,,,,\n" +
                ",,,,,,,,::IZO8ZOOO8Z7ZO$$Z8ZZZO$OO77Z$7OOZ$$$7O7$7=,,,,,,,,,\n" +
                ",,,,,,,,,,=IZ7Z7ZOZOO7Z$DO7$O7OOO88ZZ$8ZO8O8?I7IZ$+,,,,,,,,,\n" +
                ",,,,,,,,,,~7ZOO$DZOOOZ8ZOO8O$OZO$OO8O$O8O78O7$7$7$?~,,,,,,,,\n" +
                ",,,,,,,,:?88ZOMMMMMMMMDMMD8MM8OOZ8ONDMMNNMMM888OZ7+:,,,,,,,,\n" +
                ",,,,,,,:=I$ZZOMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM88OZI?:,,,,,,,,\n" +
                ",,,,,,,,++IZZZMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOOZZZ?,,,,,,,,,\n" +
                ",,,,,,,:,?7$8NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMDOOO$7+,,,,,,,,,\n" +
                ",,,,,,,,=$?7OOMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOOOZ7ZI=,,,,,,,,\n" +
                ",,,,,,,,,+7$$OMMMMMMMMMMMMMMMMMMMMMMMMMMMMM88Z$8Z$O$,,,,,,,,\n" +
                ",,,,,,,,+II7Z88MMMMMMMMMMMMMMMMMMMMMMMMMMMMOZZZ$777I,,,,,,,,\n" +
                ",,,,,,,,$+?IZZ88MMMMMMMMMMMMMMMMMMMMMMMMMMO$OO$$I$Z=~,,,,,,,\n" +
                ",,,,,,,,:?I+7$Z8O8MMMMMMMMMMMMMMMMMMMMMM8O$Z$$$ZI$$7+,,,,,,,\n" +
                ",,,,,,,:=8?I$?O$$88NMMMMMMMMMMMMMMMMMOO8Z$$7OZ$Z?$$O~,,,,,,,\n" +
                ",,,,,,,=$O8D87I$$88O88ZZDMMMMMMMMMOOZZ8$O7$77Z$$ZOZ8~,,,,,,,\n" +
                ",,,,,,,=+$ZO8Z?77ZO$8ZO8OD8OO8D8OZZO8$8ZOOO7$7ZO8O$ZZI,,,,,,\n" +
                ",,,,,,,,7$ZO8DD8Z$ZZOZOZ$$888OO88ZOO$Z7$Z8$$ZDDNO88O7,,,,,,,\n" +
                ",,,,,,,~?ZO8O8DDNDOO$$$$7$O8Z88Z8ZZOZZO$$Z7ODDDDDDO8O~,,,,,,\n" +
                ",,,,,,,=IOOODDDDDDDNDDNNO$O$7ZO$OZZ7$ZOZOODDDO88D888O+,,,,,,\n" +
                ",,,,,,:=7Z8DDDDD88DDDDDDD8$ZIZ$$$ND8DDDDD888DO8O88O8$+,,,,,,\n" +
                ",,,,,,,IOOODND8D8DDN88888DODODN8D8D8NO88OD88D8DD888O+~,,,,,,\n" +
                ",,,,,,,:$888DD88DNDDDD8NDD8D8DNNNNDDDDD8888D8D8D8O8OZ?:,,,,,\n" +
                ",,,:,::?Z8888DDD8DDD8D8ND88ODON8DD8DD8DOOOZ8O8OZOO$O$::,,,,,";
        String[] cookie = cookieMons.split("\\r?\\n");
        try{
            for(String line: cookie){
                System.out.println(line);
                TimeUnit.MILLISECONDS.sleep(51);
            }
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}
