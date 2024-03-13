package com.github.godofacceptance;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class CustomSlashListener implements SlashCommandCreateListener {

    private static String memsrc = "test.ser";
    private static int callCount = 0;
    private static long lastTime = 0; //seconds
    private static final long cooldown = 3; //seconds
    private static Map<String,List<String>> imageMemory = initImageMem(memsrc);


    private static Map<String,List<String>> initImageMem(String src){
        Map<String,List<String>> map = new HashMap<>();
        try{
            map = (Map<String,List<String>>)ReadWriteWizard.readObjectFromFile(src);
            System.out.println("@SlashCommandCreateListener: initImageMem():: Successfully initialized imageMemory from storage.");
        } catch (IOException e){
            System.out.println("@SlashCommandCreateListener: initImageMem():: Could not find serialized data. Initialized a fresh map.");
        }
        return map;
    }


    public static Map<String,List<String>> getMemory(){
        return imageMemory;
    }


    private boolean passedCooldown(){
        long elapsedTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - lastTime;
        return elapsedTime > cooldown;
    }


    private long remainingTime(){
        long elapsedTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - lastTime;
        return cooldown - elapsedTime;
    }


    /**
     * Effects: calculates the time elapsed since last call. Then, sends an embed that contains
     * a random image.
     * */
    private void imageCommand(SlashCommandInteraction interaction){

        if(!passedCooldown()){
            interaction.createImmediateResponder()
                    .setContent("You can use this command after " + remainingTime() + " seconds").respond();
        } else {
            lastTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
            String query = interaction.getArgumentStringRepresentationValueByName("query").orElse("");
            List<String> links;
            if(imageMemory.containsKey(query)){
                links = imageMemory.get(query);
            } else {
                links = RandomImager.search(query);
                imageMemory.put(query,links);
            }

            EmbedBuilder embed = new EmbedBuilder();
            int randint = (int) (Math.random() * (links.size() - 1));
            embed.setImage(links.get(randint));
            interaction.createImmediateResponder().addEmbeds(embed).respond();
        }
    }


    private void embedFailCommand(SlashCommandInteraction interaction){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setImage("https://media1.tenor.com/m/7NL8QR6JdZMAAAAC/epic-embed-fail-ryan-gosling.gif");
        interaction.createImmediateResponder().addEmbeds(eb).respond();
    }


    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        String command = event.getSlashCommandInteraction().getCommandName();

        if(command.equalsIgnoreCase("image")){
            imageCommand(interaction);
            callCount++;
            if(callCount > 3){
                System.out.println("@SlashCommandCreateListener: onSlashCommandCreate():: Call count of /image exceeded 3. Saving hashmap...");
                ReadWriteWizard.saveObject(imageMemory, "test");
                callCount = 0;
            }
        }
        else if(command.equalsIgnoreCase("embedfail")){
            embedFailCommand(interaction);
        }
    }
}
