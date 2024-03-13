package com.github.godofacceptance;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.event.connection.LostConnectionEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.connection.LostConnectionListener;

public class Main {

    public static void main(String[] args) {

        DiscordApi api = new DiscordApiBuilder()
                .setToken("")
                .addIntents(Intent.MESSAGE_CONTENT)
                .login().join();

        /*
         * Invitation
         */
        //System.out.println(api.createBotInvite());


        /*
         * Commands
         */
        SlashCommand searchCommand = SlashCommand.with("image",
                "Displays random image associated with the keyphrase. Has cooldown of 5 minute.",
                        Arrays.asList(
                                SlashCommandOption.createStringOption("query","search term",true)
                        ))
                .createGlobal(api)
                .join();


        SlashCommand embedfail = SlashCommand.with("embedfail", "use this command when an embed fails")
                .createGlobal(api).join();


        /*
         * Listeners
         */
        api.addMessageCreateListener(new CustomMessageListener());
        api.addSlashCommandCreateListener(new CustomSlashListener());
    }
}