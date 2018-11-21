package dlee99.DiscordBot;

import net.dv8tion.jda.bot.JDABot;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.Presence;

import java.io.*;

public class Bot {
    private static final String token = "";
    public static final String channelID = "186864743611367424";
    public static JDA api;
    public static void main(String[] args) throws Exception {
        api = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();

        api.addEventListener(new MessageListener());
        api.getPresence().setGame(Game.of(".help"));
        MessageListener.initialize();


    }

}
