package net.lmelaia.teeto.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

/**
 * Utilities for discord messages.
 */
public class MessageUtil {

    //Private constructor.
    private MessageUtil(){}

    /**
     * Creates a message embed quoting someone.
     *
     * @param author the user who we're quoting.
     * @param quote the text to quote.
     * @return the created message embed.
     */
    public static MessageEmbed quote(User author, String quote){
        return new EmbedBuilder()
                .setAuthor(author.getName() + "#" + author.getDiscriminator() + " said:")
                .setDescription(quote)
                .setFooter("This message came from a bot or a user issuing a command to a bot and was moved here" +
                                " by TeetoBot", author.getEffectiveAvatarUrl())
                .build();
    }
}
