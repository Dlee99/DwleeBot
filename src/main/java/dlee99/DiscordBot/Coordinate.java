package dlee99.DiscordBot;

public class Coordinate {
    public String file;
    public int rank;
    public Coordinate(int a, int b){
        switch (a) {
            case 1:
                file = "a";
                break;
            case 2:
                file = "b";
                break;
            case 3:
                file = "c";
                break;
            case 4:
                file = "d";
                break;
            case 5:
                file = "e";
                break;
            case 6:
                file = "f";
                break;
            case 7:
                file = "g";
                break;
            case 8:
                file = "h";
        }
        this.rank = b;
    }
}
