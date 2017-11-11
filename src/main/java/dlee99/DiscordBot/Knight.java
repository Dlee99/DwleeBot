package dlee99.DiscordBot;

public class Knight extends Piece {
    public Knight(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.KNIGHT;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
