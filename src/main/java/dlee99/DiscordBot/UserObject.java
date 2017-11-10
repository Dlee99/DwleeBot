package dlee99.DiscordBot;

import net.dv8tion.jda.core.entities.User;

import java.io.Serializable;
import java.util.Date;

public class UserObject implements Serializable {
    String ID;
    public UserObject(String id){
        ID = id;
        date = new Date();
    }
    Date date;
    public User getUser(){
        return Bot.api.getUserById(ID);
    }
    public Date getDate(){
        return date;
    }
}
