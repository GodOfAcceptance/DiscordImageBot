package com.github.godofacceptance;

import java.util.Optional;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

public class CustomReactionAddListener implements ReactionAddListener {

    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        System.out.println(event.getMessage().toString());
        MessageAuthor author = event.getMessageAuthor().orElseThrow();

        Emoji emoji = event.getEmoji();
        if (emoji.equalsEmoji("\uD83D\uDD95")){
            event.getChannel().sendMessage("middle finger");
        }
    }
}
