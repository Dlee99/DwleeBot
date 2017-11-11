package dlee99.DiscordBot;

public class Pawn extends Piece {
    public Pawn(Player player, Coordinate starting) {
        super(player, starting);
        this.type = Type.PAWN;
    }

    public boolean validMove(Coordinate endPos) {
        return false;
    }

    public Coordinate[] path(Coordinate endPos) {
        return new Coordinate[0];
    }
}
