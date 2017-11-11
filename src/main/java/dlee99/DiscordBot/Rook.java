package dlee99.DiscordBot;

public class Rook extends Piece {
    public Rook(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.ROOK;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
