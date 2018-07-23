package dlee99.DiscordBot;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    ArrayList<Conversation> convoArrayList = getConvoArrayList();
    public static ArrayList<Remind> reminds;
    public MessageListener() throws IOException, ClassNotFoundException {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        new Thread(() -> {
            String message = event.getMessage().getRawContent();
            MessageChannel channel = event.getChannel();
            User author = event.getAuthor();
            if (!author.isBot()) {
                if(event.getGuild() != null && event.getGuild().getId().equals(Bot.channelID)){
                    colorChannel(message, channel, author, event);
                }
                if (message.toLowerCase().startsWith(".dice")) {
                    dice(message, channel);
                } else if (message.toLowerCase().startsWith(".quote")) {
                    quote(channel);
                } else if (message.toLowerCase().startsWith(".bconv")) {
                    bconv(message, channel);
                } else if (message.toLowerCase().startsWith(".messageid")) {
                    try {
                        messageID(message, channel, author);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.toLowerCase().startsWith(".messagereply")) {
                    try {
                        messageReply(message, channel, author);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.toLowerCase().startsWith(".message")) {
                    try {
                        message(message, channel, author);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.toLowerCase().startsWith(".gpa")) {
                    gpa(message, channel);
                } else if (message.toLowerCase().startsWith(".reminddate") && ! author.isBot()) {
                    try {
                        remindDate(message, channel, author);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (message.toLowerCase().startsWith(".remindme") && ! author.isBot()) {
                    try {
                        remindMe(message, channel, author);
                    } catch (Exception e) {
                        channel.sendMessage("That is an invalid input.").queue();
                    }
                } else if (message.toLowerCase().startsWith(".terminate") && ! author.isBot()) {
                    terminateConvo(message, channel, author);
                } else if (message.toLowerCase().startsWith(".getconvos")) {
                    getConvos(message, channel, author);
                } else if (message.toLowerCase().startsWith(".chess")) {
                    chess(message, channel, author);
                } else if (message.toLowerCase().startsWith(".ping")) {
                    channel.sendMessage("The ping time is " + Long.toString(Bot.api.getPing()) + "ms").queue();
                } else if(message.toLowerCase().startsWith(".color")){
                    colorText(message, channel, author);
                } else if (message.toLowerCase().startsWith(".help")) {
                    send(
                            "```.dice n m``` to roll *n* many dice with *m* many sides." +
                                    "```.quote``` to get a random quote." +
                                    "```.bconv n s e``` to convert *n* from base *s* to base *e*. (Base 1 to 62)" +
                                    "```.message username {This is a message...}``` to send an anonymous message to this user." +
                                    "```.messageID userID {This is a message...}``` to send an anonymous message to the user with the specified ID." +
                                    "```.messageReply # {This is a message...}``` to send a reply to a user who sent you a message. The reply # is the one in parentheses." +
                                    "```.terminate convoID``` to end a conversation you created." +
                                    "```.getConvos``` to get a list of conversations you own." +
                                    "```.remindme h {This is a message...}``` to remind yourself in *h* hours of a message." +
                                    "```.remindDate yyyy:MM:dd:hh:mm {This is a message...}``` to remind yourself at a specified date." +
                                    "```.gpa LetterGrade1 Type(Prep/Honors/AP)1 LetterGrade2 Type2...``` to calculate your GPA." +
                                    "```.color {This is a message...}``` to get a colored image of the text."


                            , channel);

                }
            }
        }).start();
    }

    public int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (-- n > 0 && pos != - 1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public void send(String str, MessageChannel chan) {
        String s = str;
        while (true) {
            if (s.length() > 1999) {
                chan.sendMessage(s.substring(0, 2000)).queue();
                s = s.substring(2000);
            } else {
                chan.sendMessage(s).queue();
                return;
            }
        }
    }

    public String getNewBase(String number, int startb, int endb) {
        String num = number.trim();
        if (num.matches("(.*)[^0123456789A-Za-z](.*)")) {
            return "NAN";
        }
        if (startb > 62 || startb < 1 || endb > 62 || endb < 1) {
            return "NAN";

        } else {
            int total = 0;
            for (int i = 0; i < number.length(); i++) {
                if (startb < 37) {
                    total += Integer.parseInt(getCharacterValue(num.substring(i, i + 1).toUpperCase())) * Math.pow(startb, number.length() - i - 1);
                } else {
                    total += Integer.parseInt(getCharacterValue(num.substring(i, i + 1))) * Math.pow(startb, number.length() - i - 1);
                }

            }
            return baseTenToNew("" + total, endb);

        }
    }

    public String baseTenToNew(String number, int endb) {
        int num = Integer.parseInt(number.trim());
        String temp = "";
        if (endb != 1) {
            int pow = 0;
            for (int i = 0; true; i++) {
                if (Math.pow(endb, i) > num) {
                    break;
                } else {
                    pow = i;
                }
            }
            int start = pow;
            while (start >= 0) {
                if (Math.pow(endb, start) > num) {
                    temp = temp + "0";
                    start = start - 1;
                } else {

                    int t = num;

                    int exp = (int) Math.pow(endb, start);
                    int count = 0;
                    while (t >= exp) {
                        count++;
                        t = t - exp;

                    }
                    temp = temp + getCharacterValue("" + count);
                    num = num - (exp * count);
                    start = start - 1;
                }
            }
        } else {
            for (int i = 0; i < num; i++) {
                temp = temp + "1";
            }
        }
        return temp;
    }

    public String getCharacterValue(String num) {
        String t;
        switch (num) {
            case "0":
                t = "0";
                break;
            case "1":
                t = "1";
                break;
            case "2":
                t = "2";
                break;
            case "3":
                t = "3";
                break;
            case "4":
                t = "4";
                break;
            case "5":
                t = "5";
                break;
            case "6":
                t = "6";
                break;
            case "7":
                t = "7";
                break;
            case "8":
                t = "8";
                break;
            case "9":
                t = "9";
                break;
            case "10":
                t = "A";
                break;
            case "11":
                t = "B";
                break;
            case "12":
                t = "C";
                break;
            case "13":
                t = "D";
                break;
            case "14":
                t = "E";
                break;
            case "15":
                t = "F";
                break;
            case "16":
                t = "G";
                break;
            case "17":
                t = "H";
                break;
            case "18":
                t = "I";
                break;
            case "19":
                t = "J";
                break;
            case "20":
                t = "K";
                break;
            case "21":
                t = "L";
                break;
            case "22":
                t = "M";
                break;
            case "23":
                t = "N";
                break;
            case "24":
                t = "O";
                break;
            case "25":
                t = "P";
                break;
            case "26":
                t = "Q";
                break;
            case "27":
                t = "R";
                break;
            case "28":
                t = "S";
                break;
            case "29":
                t = "T";
                break;
            case "30":
                t = "U";
                break;
            case "31":
                t = "V";
                break;
            case "32":
                t = "W";
                break;
            case "33":
                t = "X";
                break;
            case "34":
                t = "Y";
                break;
            case "35":
                t = "Z";
                break;
            case "36":
                t = "a";
                break;
            case "37":
                t = "b";
                break;
            case "38":
                t = "c";
                break;
            case "39":
                t = "d";
                break;
            case "40":
                t = "e";
                break;
            case "41":
                t = "f";
                break;
            case "42":
                t = "g";
                break;
            case "43":
                t = "h";
                break;
            case "44":
                t = "i";
                break;
            case "45":
                t = "j";
                break;
            case "46":
                t = "k";
                break;
            case "47":
                t = "l";
                break;
            case "48":
                t = "m";
                break;
            case "49":
                t = "n";
                break;
            case "50":
                t = "o";
                break;
            case "51":
                t = "p";
                break;
            case "52":
                t = "q";
                break;
            case "53":
                t = "r";
                break;
            case "54":
                t = "s";
                break;
            case "55":
                t = "t";
                break;
            case "56":
                t = "u";
                break;
            case "57":
                t = "v";
                break;
            case "58":
                t = "w";
                break;
            case "59":
                t = "x";
                break;
            case "60":
                t = "y";
                break;
            case "61":
                t = "z";
                break;
            case "A":
                t = "10";
                break;
            case "B":
                t = "11";
                break;
            case "C":
                t = "12";
                break;
            case "D":
                t = "13";
                break;
            case "E":
                t = "14";
                break;
            case "F":
                t = "15";
                break;
            case "G":
                t = "16";
                break;
            case "H":
                t = "17";
                break;
            case "I":
                t = "18";
                break;
            case "J":
                t = "19";
                break;
            case "K":
                t = "20";
                break;
            case "L":
                t = "21";
                break;
            case "M":
                t = "22";
                break;
            case "N":
                t = "23";
                break;
            case "O":
                t = "24";
                break;
            case "P":
                t = "25";
                break;
            case "Q":
                t = "26";
                break;
            case "R":
                t = "27";
                break;
            case "S":
                t = "28";
                break;
            case "T":
                t = "29";
                break;
            case "U":
                t = "30";
                break;
            case "V":
                t = "31";
                break;
            case "W":
                t = "32";
                break;
            case "X":
                t = "33";
                break;
            case "Y":
                t = "34";
                break;
            case "Z":
                t = "35";
                break;
            case "a":
                t = "36";
                break;
            case "b":
                t = "37";
                break;
            case "c":
                t = "38";
                break;
            case "d":
                t = "39";
                break;
            case "e":
                t = "40";
                break;
            case "f":
                t = "41";
                break;
            case "g":
                t = "42";
                break;
            case "h":
                t = "43";
                break;
            case "i":
                t = "44";
                break;
            case "j":
                t = "45";
                break;
            case "k":
                t = "46";
                break;
            case "l":
                t = "47";
                break;
            case "m":
                t = "48";
                break;
            case "n":
                t = "49";
                break;
            case "o":
                t = "50";
                break;
            case "p":
                t = "51";
                break;
            case "q":
                t = "52";
                break;
            case "r":
                t = "53";
                break;
            case "s":
                t = "54";
                break;
            case "t":
                t = "55";
                break;
            case "u":
                t = "56";
                break;
            case "v":
                t = "57";
                break;
            case "w":
                t = "58";
                break;
            case "x":
                t = "59";
                break;
            case "y":
                t = "60";
                break;
            case "z":
                t = "61";
                break;
            default:
                t = "How did this happen???";
        }
        return t;
    }

    public void bconv(String message, MessageChannel channel) {
        String num, start, end;
        int s, e;
        String[] args = message.split(" ");
        try {
            num = args[1];
            start = args[2];
            end = args[3];
            s = Integer.parseInt(start.trim());
            e = Integer.parseInt(end.trim());
            if ("NAN".equals(getNewBase(num, s, e))) {
                channel.sendMessage("That isn't a valid input.").queue();
            } else {
                send("Converting... " + getNewBase(num, s, e) + " (Base " + s + " to base " + e + ")", channel);
            }
        } catch (Exception err) {
            channel.sendMessage("That isn't a valid input.").queue();
        }
    }

    public void quote(MessageChannel channel) {
        try {
            URL url = new URL("https://quotesondesign.com/wp-json/posts?filter[orderby]=rand&filter[posts_per_page]=1&callback=");
            String response = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
            String qauthor = response.substring(ordinalIndexOf(response, ":", 2) + 2, ordinalIndexOf(response, "\"", 6));
            String p1 = response.substring(response.indexOf(">") + 1);
            Pattern unicodeCharsPattern = Pattern.compile("\\\\u(\\p{XDigit}{4})");

            String quote = p1.substring(0, p1.indexOf("<")).replaceAll("\u2019", "");
            Matcher unicodeMatcher = unicodeCharsPattern.matcher(quote);
            if (unicodeMatcher.find()) {
                quote = unicodeMatcher.replaceAll("");
            }
            while (quote.contains("&")) {
                int pos = quote.indexOf("&");
                int pos2 = quote.substring(pos).indexOf(";") + pos + 1;
                quote = quote.substring(0, pos) + quote.substring(pos2);
            }
            if (! quote.equals("Modern typographic axioms:")) {
                channel.sendMessage("\"" + quote + "\" -" + qauthor).queue();
            } else if (quote.equals("Modern typographic axioms:")) {
                channel.sendMessage("\"Tell me and I forget. Teach me and I remember. Involve me and I learn.\" -Benjamin Franklin").queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dice(String message, MessageChannel channel) {
        int n, m;

        String[] args = message.split(" ");
        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (Exception e) {
                n = 1;
            }
            try {
                m = Integer.parseInt(args[2]);
            } catch (Exception e) {
                m = 6;
            }
            if (n > 1000) {
                channel.sendMessage("You can't roll more than 1000 dice.").queue();
            } else {
                int[] dice = new int[n];
                for (int i = 0; i < dice.length; i++) {
                    dice[i] = (int) (Math.random() * m + 1);
                }
                String print = "{";
                int total = 0;
                for (int i = 0; i < dice.length; i++) {
                    total = total + dice[i];
                    print = print + dice[i] + ", ";
                }
                String average = fractional(total, dice.length);
                print = print.substring(0, print.length() - 2) + "}";
                String text;
                if (n > 1) {
                    text = "Rolling " + n + " dice with " + m + " sides.\nTotal: " + total + "\nAverage: " + average + "\nDice:\n" + print;
                    send(text, channel);
                } else {
                    text = "Rolling.. " + total + " (" + n + " Die, " + m + " sides) ";
                    send(text, channel);
                }
            }
        } else {
            channel.sendMessage("Rolling... " + (int) (Math.random() * 6 + 1) + " (1 Die, 6 sides)").queue();
        }
    }

    public void message(String message, MessageChannel chan, User user) throws IOException {
        String[] args = message.split(" ");
        //userArrayList.add(new UserObject(user.getId()));
        try {
            String u = args[1];
            List<User> users = Bot.api.getUsersByName(u, true);
            String send = message.substring(8 + u.length() + 1);
            // openPrivateChannel provides a RestAction<PrivateChannel>
            // which means it supplies you with the resulting channel
            users.get(0).openPrivateChannel().queue((channel) ->
            {
                // value is a parameter for the `accept(T channel)` method of our callback.
                // here we implement the body of that method, which will be called later by JDA automatically.
                channel.sendMessage(send + " (" + (addConvo(new Conversation(user, users.get(0)))) + ")").queue();
                // here we access the enclosing scope variable -content-
                // which was provided to sendPrivateMessage(User, String) as a parameter
            });
            chan.sendMessage("Message was successfully sent to " + u + ".").queue();
        } catch (Exception e) {
            chan.sendMessage("That is not a valid input.").queue();
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("convos.ser"));
        objectOutputStream.writeObject(convoArrayList);
        objectOutputStream.flush();
        objectOutputStream.close();

    }

    public void messageID(String message, MessageChannel chan, User user2) throws IOException {
        String[] args = message.split(" ");
        //userArrayList.add(new UserObject(user2.getId()));
        try {

            String u = args[1];
            User user = Bot.api.getUserById(u.trim());
            String send = message.substring(10 + u.length() + 1);
            // openPrivateChannel provides a RestAction<PrivateChannel>
            // which means it supplies you with the resulting channel
            int convoID = (addConvo(new Conversation(user2, user)));
            user.openPrivateChannel().queue((channel) ->
            {
                // value is a parameter for the `acceptethod, which will be called later by JDA automatically.
                channel.sendMessage(send + " (" + convoID + ")").queue();
                // here we access the enclosing scope variable -content-
                // which was provided to sendPrivateMessage(User, String) as a parameter
            });
            chan.sendMessage("Message was successfully sent to " + user.getName() + " (" + convoID + ")").queue();
        } catch (Exception e) {
            chan.sendMessage("That is not a valid input.").queue();
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("convos.ser"));
        objectOutputStream.writeObject(convoArrayList);
        objectOutputStream.flush();
        objectOutputStream.close();

    }

    public void messageReply(String message, MessageChannel chan, User user) throws IOException {
        String[] args = message.split(" ");
        //userArrayList.add(new UserObject(user.getId()));
        try {
            String u = args[1];
            Conversation conversation = convoArrayList.get(Integer.parseInt(u));
            String send = message.substring(13 + u.length() + 1);
            // openPrivateChannel provides a RestAction<PrivateChannel>
            // which means it supplies you with the resulting channel
            User recipient;
            if (user.getId().equals(conversation.user1)) {
                recipient = Bot.api.getUserById(conversation.user2);
                recipient.openPrivateChannel().queue((channel) ->
                {
                    // value is a parameter for the `accept(T channel)` method of our callback.
                    // here we implement the body of that method, which will be called later by JDA automatically.
                    channel.sendMessage(send + " (" + Integer.parseInt(u) + ")").queue();
                    // here we access the enclosing scope variable -content-
                    // which was provided to sendPrivateMessage(User, String) as a parameter
                });
            } else {
                recipient = Bot.api.getUserById(conversation.user1);
                recipient.openPrivateChannel().queue((channel) ->
                {
                    // value is a parameter for the `accept(T channel)` method of our callback.
                    // here we implement the body of that method, which will be called later by JDA automatically.
                    channel.sendMessage(Bot.api.getUserById(conversation.user2).getName() + ": " + send + " (" + Integer.parseInt(u) + ")").queue();
                    // here we access the enclosing scope variable -content-
                    // which was provided to sendPrivateMessage(User, String) as a parameter
                });
            }
            chan.sendMessage("Message was successfully sent to " + recipient.getName() + ".").queue();
        } catch (Exception e) {
            chan.sendMessage("That is not a valid input.").queue();
        }
    }

    public ArrayList<Conversation> getConvoArrayList() throws IOException, ClassNotFoundException {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("convos.ser"));
            return (ArrayList<Conversation>) objectInputStream.readObject();
        } catch (java.io.FileNotFoundException e) {
            return new ArrayList();
        }
    }

    public void terminateConvo(String message, MessageChannel chan, User user) {
        String[] args = message.split(" ");
        int convoID = Integer.parseInt(args[1]);
        if (convoArrayList.get(convoID).user1.equals(user.getId())) {
            chan.sendMessage("Conversation successfully terminated.");
            convoArrayList.set(convoID, new Conversation(true));
        } else {
            chan.sendMessage("You do not own that conversation.");
        }
    }

    public int addConvo(Conversation conversation) {
        if (convoArrayList.size() > 1000) {
            int j = 0;
            for (int i = 0; i < 1000; i++) {
                if (convoArrayList.get(i).fake) {
                    j = i;
                    break;
                } else if (convoArrayList.get(i).getDate()
                        .before(convoArrayList.get(j).getDate())) {
                    j = i;
                }
            }
            convoArrayList.set(j, conversation);
            return j;
        } else {
            convoArrayList.add(conversation);
            return convoArrayList.size() - 1;
        }
    }

    public void getConvos(String message, MessageChannel chan, User user) {
        String s = "";
        for (int i = 0; i < convoArrayList.size(); i++) {
            if (convoArrayList.get(i).user1.equals(user.getId())) {
                s += i + ".\t" + convoArrayList.get(i) + "\n";

            }
        }
        String list[] = new String[(s.length() / 2000) + 1];
        int i = 0;
        while (s.length() > 2000) {
            list[i] = s.substring(0, 2000);
            i++;
            s = s.substring(2000);
        }
        list[i] = s;
        for (int j = 0; j < list.length; j++) {
            int a = j;
            user.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage(list[a]).queue();
            });
        }
    }

    public String fractional(int num, int denom) {
        return num / gcd(num, denom) + "/" + denom / gcd(num, denom);

    }

    public int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public void gpa(String message, MessageChannel channel) {
        String[] args = message.split(" ");
        double total = 0;
        for (int i = 1; i < args.length; i += 2) {
            total += grade(args[i].trim()) * level(args[i + 1].trim());
        }
        channel.sendMessage("The calculated GPA is: " + Math.round((total / ((args.length - 1) / 2)) * 1000.0) / 1000.0).queue();
    }

    public static double level(String s) {
        String s1 = s.toLowerCase();
        if (s1.equals("prep")) {
            return 1;

        } else if (s1.equals("honors")) {
            return 1.125;

        } else if (s1.equals("ap")) {
            return 1.25;

        } else {
            return 1;
        }
    }

    public static double grade(String s) {
        String s1 = s.toLowerCase();
        if (s1.equals("d-")) {
            return .7;
        } else if (s1.equals("d")) {
            return 1;
        } else if (s1.equals("d+")) {
            return 1.3;
        } else if (s1.equals("c-")) {
            return 1.7;
        } else if (s1.equals("c")) {
            return 2;
        } else if (s1.equals("c+")) {
            return 2.3;
        } else if (s1.equals("b-")) {
            return 2.7;
        } else if (s1.equals("b")) {
            return 3;
        } else if (s1.equals("b+")) {
            return 3.3;
        } else if (s1.equals("a-")) {
            return 3.7;
        } else if (s1.equals("a")) {
            return 4;
        } else if (s1.equals("a+")) {
            return 4.3;
        } else {
            return 3;
        }
    }

    public void remindMe(String message, MessageChannel chan, User user) throws Exception {
        String[] args = message.split(" ");

        double hours = (args[1].contains("/")) ? (Double.parseDouble(args[1].substring(0, args[1].indexOf("/")).trim())
                / Double.parseDouble(args[1].substring(args[1].indexOf("/") + 1).trim()))
                : Double.parseDouble(args[1]);
        String remind = message.substring(8 + 1 + args[1].length() + 2);
        Remind thisRemind = new Remind(new Date().getTime() + (long) (hours * 3600000), remind, user.getId());

        reminds.add(thisRemind);

        saveRemind();

        if (hours >= 0) {
            chan.sendMessage("You will be reminded in " + Math.round(hours * 10000.0) / 10000.0 + " hours.").queue();
        }
        try {
            Thread.sleep(Math.round(hours * 3600000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        reminds.remove(thisRemind);

        saveRemind();

        user.openPrivateChannel().queue((channel) -> channel.sendMessage("Remind: " + remind).queue());


    }

    public void remindDate(String message, MessageChannel chan, User user) throws IOException {
        String[] args = message.split(" ");
        String text = args[1];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm");
        Date date = null;
        try {
            date = dateFormat.parse(text);
        } catch (ParseException e) {
            chan.sendMessage("That is not a valid input.").queue();
        }
        String remind = message.substring(11 + 1 + args[1].length() + 1);
        Remind thisRemind = new Remind(date.getTime(), remind, user.getId());
        reminds.add(thisRemind);
        saveRemind();
        double wait = date.getTime() - new Date().getTime();
        double number = 3600000;

        if (wait >= 0.0) {
            chan.sendMessage("You will be reminded in " + Math.round(wait / number) + " hours.").queue();
            try {
                Thread.sleep((long) wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            chan.sendMessage("That is not a valid input, you must input a date in the future.").queue();
        }

        reminds.remove(thisRemind);
        saveRemind();

        if (wait > 0) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Remind: " + remind).queue());
        }

    }


    public static ArrayList<Remind> getReminds() throws Exception {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("remind.ser"));
            return (ArrayList<Remind>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList();
        }

    }

    public static void saveRemind() throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("remind.ser"));
        objectOutputStream.writeObject(reminds);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static void initialize() {
        try {
            reminds = getReminds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (! reminds.isEmpty()) {
            Remind[] list = new Remind[reminds.size()];
            for (int i = 0; i < reminds.size(); i++) {
                list[i] = reminds.get(i);
            }
            for (int i = 0; i < list.length; i++) {
                Remind reminder = list[i];
                if (reminder.getTime() > 0) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(reminder.getTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        reminder.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("Remind: " + reminder.getMessage()).queue());
                        reminds.remove(reminder);
                        try {
                            saveRemind();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }).start();
                } else {
                    reminder.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage("Remind: " + reminder.getMessage()).queue());
                    reminds.remove(reminder);
                    try {
                        saveRemind();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void chess(String message, MessageChannel channel, User author) {

    }

    public static void colorText(String message, MessageChannel channel, User author) {
        String text = message.substring(6);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        int maxLine = 0;
        ArrayList<String> lines = new ArrayList();
        /*for (int i = 0; i < text.length(); i += 20) {
            int endSubstring = (i + 20 > text.length()) ? text.length() : i + 20;
            if(maxLine < fm.stringWidth(text.substring(i, endSubstring))){
                maxLine = fm.stringWidth(text.substring(i, endSubstring));
            }

        }*/
        int position = 0;
        int j2 = 0;
        boolean notDone1 = true;
        int cpl = (20 >= (text.length() / Math.ceil((Math.sqrt(text.length() * 0.05))))) ? 20 : (int) (text.length() / Math.ceil((Math.sqrt(text.length() * 0.05)))); // Character per line
        while(notDone1) {
            while(text.substring(position, position + 1).equals(" ")){
                position++;
                if(position == text.length()){
                    notDone1 = false;
                    break;
                }
            }
            boolean notDone2 = true;
            for (int j = 0; notDone2 && notDone1 ; j++) {
                if (text.length() <= position + cpl + 1 + j) {
                    lines.add(text.substring(position));
                    notDone1 = false;
                }
                else if (text.substring(position + cpl + j, position + cpl + 1 + j).equals(" ")) {
                    lines.add(text.substring(position, position + cpl + 1 + j));
                    notDone2 = false;
                }
                j2 = j;
            }
            position = position + cpl + 1 + j2;
        }
        int numberOfLines = lines.size();
        Font font = new Font("Comic Sans MS", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i < lines.size(); i++) {
            if(maxLine <= fm.stringWidth(lines.get(i))){
                maxLine = fm.stringWidth(lines.get(i));
            }
        }
        int width = maxLine;

        int height = fm.getHeight() * numberOfLines;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        int xSum = 0;
        int ySum = fm.getAscent();
        for (int j = 0; j < lines.size(); j++){
            for (int i = 0; i < lines.get(j).length(); i++) {
                String line = lines.get(j);

                if (equalsAny(line.substring(i, i + 1), new String[]{"a", "g", "m", "s", "y"})) {
                    g2d.setColor(Color.RED);
                } else if (equalsAny(line.substring(i, i + 1), new String[]{"b", "h", "n", "t", "z"})) {
                    g2d.setColor(new Color(255, 140, 0));
                } else if (equalsAny(line.substring(i, i + 1), new String[]{"c", "i", "o", "u"})) {
                    g2d.setColor(Color.YELLOW);
                } else if (equalsAny(line.substring(i, i + 1), new String[]{"d", "j", "p", "v"})) {
                    g2d.setColor(Color.GREEN);
                } else if (equalsAny(line.substring(i, i + 1), new String[]{"e", "k", "q", "w"})) {
                    g2d.setColor(Color.BLUE);
                } else if (equalsAny(line.substring(i, i + 1), new String[]{"f", "l", "r", "x"})) {
                    g2d.setColor(new Color(75, 0, 130));
                    //g2d.setColor(Color.magenta);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawString(lines.get(j).substring(i, i + 1), xSum, ySum);
                xSum = xSum + fm.stringWidth(lines.get(j).substring(i, i + 1));
            }
            xSum = 0;
            ySum += fm.getAscent();
        }
            /*if(!(xSum == 0 && text.substring(i, i + 1).equals(" "))) {
                g2d.drawString(text.substring(i, i + 1), xSum, ySum);
                xSum = xSum + fm.stringWidth(text.substring(i, i + 1));
                if((i + 1) % 20 == 0){
                    xSum = 0;
                    ySum = ySum + fm.getHeight();
                }
            }
            else{
                text = text.substring(0, i) + text.substring(i+1);
                i = i - 1;
            }*/



        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Message msg = new MessageBuilder().append(author.getName() + " said:").build();
        channel.sendFile(new File("Text.png"), msg).queue();
    }
    public static void colorChannel(String message, MessageChannel channel, User author, MessageReceivedEvent event) {
        String text = message;
        text = text.replaceAll("]","");
        if(!(message.toLowerCase().startsWith("http") || message.toLowerCase().contains(".com") || message.toLowerCase().contains("www."))) {

            System.out.println(text);
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            Font font = new Font("Arial", Font.PLAIN, 48);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int maxLine = 0;
            ArrayList<String> lines = new ArrayList();
        /*for (int i = 0; i < text.length(); i += 20) {
            int endSubstring = (i + 20 > text.length()) ? text.length() : i + 20;
            if(maxLine < fm.stringWidth(text.substring(i, endSubstring))){
                maxLine = fm.stringWidth(text.substring(i, endSubstring));
            }

        }*/
            int position = 0;
            int j2 = 0;
            boolean notDone1 = true;
            while (notDone1) {
                while (text.substring(position, position + 1).equals(" ")) {
                    position++;
                    if (position == text.length()) {
                        notDone1 = false;
                        break;
                    }
                }
                boolean notDone2 = true;
                for (int j = 0; notDone2 && notDone1; j++) {
                    if (text.length() <= position + 21 + j) {
                        lines.add(text.substring(position));
                        notDone1 = false;
                    } else if (text.substring(position + 20 + j, position + 21 + j).equals(" ")) {
                        lines.add(text.substring(position, position + 21 + j));
                        notDone2 = false;
                    }
                    j2 = j;
                }
                position = position + 21 + j2;
            }
            for (int i = 0; i < lines.size(); i++) {
                if (maxLine <= fm.stringWidth(lines.get(i))) {
                    maxLine = fm.stringWidth(lines.get(i));
                }
            }
            int width = maxLine;
            int numberOfLines = lines.size();
            int height = fm.getHeight() * numberOfLines;
            g2d.dispose();

            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2d.setFont(font);
            fm = g2d.getFontMetrics();
            int xSum = 0;
            int ySum = fm.getAscent();
            for (int j = 0; j < lines.size(); j++) {


                for (int i = 0; i < lines.get(j).length(); i++) {
                    String line = lines.get(j);
                    if (equalsAny(line.substring(i, i + 1), new String[]{"a", "g", "m", "s", "y"})) {
                        g2d.setColor(Color.RED);
                    } else if (equalsAny(line.substring(i, i + 1), new String[]{"b", "h", "n", "t", "z"})) {
                        g2d.setColor(new Color(255, 140, 0));
                    } else if (equalsAny(line.substring(i, i + 1), new String[]{"c", "i", "o", "u"})) {
                        g2d.setColor(Color.YELLOW);
                    } else if (equalsAny(line.substring(i, i + 1), new String[]{"d", "j", "p", "v"})) {
                        g2d.setColor(Color.GREEN);
                    } else if (equalsAny(line.substring(i, i + 1), new String[]{"e", "k", "q", "w"})) {
                        g2d.setColor(Color.BLUE);
                    } else if (equalsAny(line.substring(i, i + 1), new String[]{"f", "l", "r", "x"})) {
                        g2d.setColor(new Color(75, 0, 130));
                        //g2d.setColor(Color.magenta);
                    } else {
                        g2d.setColor(Color.WHITE);
                    }
                    g2d.drawString(lines.get(j).substring(i, i + 1), xSum, ySum);
                    xSum = xSum + fm.stringWidth(lines.get(j).substring(i, i + 1));
                }
                xSum = 0;
                ySum += fm.getAscent();
            }
            /*if(!(xSum == 0 && text.substring(i, i + 1).equals(" "))) {
                g2d.drawString(text.substring(i, i + 1), xSum, ySum);
                xSum = xSum + fm.stringWidth(text.substring(i, i + 1));
                if((i + 1) % 20 == 0){
                    xSum = 0;
                    ySum = ySum + fm.getHeight();
                }
            }
            else{
                text = text.substring(0, i) + text.substring(i+1);
                i = i - 1;
            }*/


            g2d.dispose();
            try {
                ImageIO.write(img, "png", new File("Text.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Message msg = new MessageBuilder().append(author.getName() + " said:").build();
            channel.sendFile(new File("Text.png"), msg).queue();
            event.getMessage().delete().queue();
        }

    }

    public static boolean equalsAny(String a, String[] letters) {
        for (int i = 0; i < letters.length; i++) {
            if (a.equalsIgnoreCase(letters[i])) {
                return true;
            }
        }
        return false;
    }
}