import java.util.Scanner;

public class Main {
    private static Game game;
    public static void main(String[] args) {
        try(Scanner sc = new Scanner(System.in)){
            game = new Game(sc);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}