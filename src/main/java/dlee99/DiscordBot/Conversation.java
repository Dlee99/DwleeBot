package dlee99.DiscordBot;

import net.dv8tion.jda.core.entities.User;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Conversation implements Serializable{
    public static Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public String user1;
    public String user2;
    public Date date;
    public boolean fake;
    Conversation(User u1, User u2){
        user1 = u1.getId();
        user2 = u2.getId();
        date = Calendar.getInstance().getTime();
    }
    Conversation(boolean f){
        fake = f;
    }
    public Date getDate(){
        return date;
    }

    @Override
    public String toString() {
        return Bot.api.getUserById(user2).getName() + "\t" + formatter.format(date);
    }
}
