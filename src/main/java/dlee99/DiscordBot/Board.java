package dlee99.DiscordBot;

public class Board {
    Coordinate[][] board;
    public Board(Player w, Player b){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Coordinate(i, j);
            }
        }
    }
}
