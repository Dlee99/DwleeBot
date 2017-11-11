package dlee99.DiscordBot;

import net.dv8tion.jda.core.entities.User;

public abstract class Piece {
    public Player player;
    public Coordinate location;
    public Type type;
    //sets the player the piece belongs to and its initial position on the board

    public Piece(Player player, Coordinate starting){
        this.player = player;
        this.location = starting;
    }

    //tests if the move is a legal chess move

    public abstract boolean validMove(Coordinate endPos);

    //returns all of the coordinates the piece must move through

    public abstract Coordinate[] path(Coordinate endPos);

    public Type getType(){
        return type;
    }

}
