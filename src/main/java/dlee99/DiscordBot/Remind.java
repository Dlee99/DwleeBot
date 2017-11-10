package dlee99.DiscordBot;

import net.dv8tion.jda.core.entities.User;

import java.io.Serializable;
import java.util.Date;

public class Remind implements Serializable{
    long time;
    String message;
    String Id;
    public Remind(long t, String m, String id){
        time = t;
        message = m;
        Id = id;
    }

    public long getTime(){
        long now = new Date().getTime();
        return (time - now > 0) ? time - now : 0;
    }

    public String getMessage(){
        return message;
    }

    public User getUser(){
        return Bot.api.getUserById(Id);
    }
}
