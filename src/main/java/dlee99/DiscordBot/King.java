package dlee99.DiscordBot;

public class King extends Piece {

    public King(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.KING;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
