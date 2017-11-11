package dlee99.DiscordBot;

public class Bishop extends Piece {
    public Bishop(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.BISHOP;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
