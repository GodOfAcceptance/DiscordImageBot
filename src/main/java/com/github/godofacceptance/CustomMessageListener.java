package com.github.godofacceptance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.text.html.Option;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class CustomMessageListener implements MessageCreateListener {

    private static long lastTime = 0; //seconds
    private static final long cooldown = 30; //seconds
    private static final Set<String> CommandSet = Set.of("!image", "!embedfail");

    //memory is a map from query to list of links. This saves number of api calls.
    private static Map<String,List<String>> memory = new HashMap<>();


    private boolean passedCooldown(){
        long elapsedTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - lastTime;
        return elapsedTime > cooldown;
    }


    private long remainingTime(){
        long elapsedTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - lastTime;
        return cooldown - elapsedTime;
    }


    /**
     * [getRandomEmbed] returns an embed associated with the [query]. It gets an image link from
     * the [memory] and then deletes it.
     * @param query
     * @return
     */
    private EmbedBuilder getRandomEmbed(String query){
        List<String> links;

        if(memory.containsKey(query)){
            links = memory.get(query);
        } else {
            links = RandomImager.search(query);
            memory.put(query,links);
        }

        EmbedBuilder embed = new EmbedBuilder();

        int n = (int) (Math.random() * (links.size() - 1));
        String link = links.get(n);
        System.out.println(link);
        embed.setImage(link);
        return embed;
    }

    /**
     * Returns: whether the message [m] is a command.
     * */
    private boolean isCommand(Message m){
        String strMessage = m.getContent();

        if(!strMessage.startsWith("!")){
            return false;
        }

        int space = strMessage.indexOf(" ");
        String comm = (space == -1) ? strMessage : strMessage.substring(0,space);
        return CommandSet.contains(comm);
    }


    /**
     * Returns: the command in the message [m]
     * Requires: [m] is a command
     * */
    private String getCommand(Message m){
        assert isCommand(m);
        String strMessage = m.getContent();
        int space = strMessage.indexOf(" ");
        String comm = (space == -1) ? strMessage : strMessage.substring(0,space);

        return comm;
    }

    /**
     * Returns: a command input.
     * Requires: [m] is a command that requires an input.
     * */
    private String getInput(Message m){
        assert isCommand(m);
        int commandLength = getCommand(m).length();
        System.out.println(m.getContent().substring(commandLength));
        return m.getContent().substring(commandLength);
    }


    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();

        if(isCommand(message)){
            switch(getCommand(message)){
                case ("!image"):
                    if (!passedCooldown()) {
                        event.getChannel().sendMessage("You can use this command after " + remainingTime() + " seconds");
                    } else {
                        lastTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
                        event.getChannel().sendMessage(getRandomEmbed(getInput(message)));
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
