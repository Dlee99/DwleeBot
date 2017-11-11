package dlee99.DiscordBot;

public class Queen extends Piece {
    public Queen(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.QUEEN;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
