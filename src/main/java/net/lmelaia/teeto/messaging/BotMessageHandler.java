/*
 *  This file is part of TeetoBot4J.
 *
 *  TeetoBot4J is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TeetoBot4J is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TeetoBot4J.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.lmelaia.teeto.messaging;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.lmelaia.teeto.GuildSettings;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.util.JsonUtil;
import org.apache.logging.log4j.Logger;

/**
 * Handles message sent by bots and people issuing
 * commands to bots and moves them to the appropriate
 * channel if set.
 */
public class BotMessageHandler {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Singleton instance.
     */
    private static BotMessageHandler instance;

    /**
     * Initializes the bot message manager.
     *
     * @param jda Java discord api instance.
     */
    public static void init(JDA jda){
        if(instance == null)
            instance = new BotMessageHandler(jda);
    }

    /**
     * @return the bot message manager is initialized, otherwise
     * null.
     */
    public static BotMessageHandler get(){
        return instance;
    }

    /**
     * Constructs the message manager.
     *
     * @param jda the java discord api.
     */
    private BotMessageHandler(JDA jda){
        jda.addEventListener(new BotMessageListener());
    }

    /**
     * Lists for messages related to bots and moves them
     * if appropriate.
     */
    private class BotMessageListener extends ListenerAdapter{

        /**
         * Handles the message received.
         *
         * @param event message arguments.
         */
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            if(event.getGuild() == null)
                return;//Don't do anything to messages in private channels.

            Guild guild = event.getGuild();
            MessageChannel channel = event.getChannel();
            User author = event.getAuthor();
            GuildSettings settings = GuildSettings.getGuildSettings(guild);

            if(!settings.has(GuildSettings.Settings.BOT_CHANNEL))
                return;//Do nothing if the guild doesn't have a bot channel.

            MessageChannel botMsgChannel = guild.getTextChannelById(settings.getSetting(GuildSettings.Settings.BOT_CHANNEL).getAsLong());

            if(botMsgChannel == null){
                LOG.warn("Bot message channel not found for guild: " + guild.getName());
                return;
            }

            if(botMsgChannel.getIdLong() == channel.getIdLong())
                return;//Do nothing. Message in bot channel


            if(author.isBot()){
                Message botMessageQuote = new MessageBuilder()
                        .append("")
                        .setEmbed(quoteBot(author, event.getMessage().getContentRaw())).build();
                event.getMessage().delete().submit();
                botMsgChannel.sendMessage(botMessageQuote).queue();//Move message to bot channel.
                return;//We're done
            }

            if(!settings.has(GuildSettings.Settings.BOT_COMMANDS))
                return;//DO nothing. We can't continue.

            for(String cmd : JsonUtil.jsonArrayToStringArray(
                    settings.getSetting(GuildSettings.Settings.BOT_COMMANDS).getAsJsonArray())){
                if(event.getMessage().getContentRaw().toLowerCase().startsWith(cmd.toLowerCase())){
                    Message botMessageQuote = new MessageBuilder()
                            .append("")
                            .setEmbed(quoteUser(author, event.getMessage().getContentRaw())).build();
                    event.getMessage().delete().submit();
                    botMsgChannel.sendMessage(botMessageQuote).queue();//Move message to bot channel.
                    return;//We're done
                }
            }
        }
    }

    /**
     * Creates a message embed quoting a user issuing a bot command.
     *
     * @param author the user who we're quoting.
     * @param quote the text to quote.
     * @return the created message embed.
     */
    private static MessageEmbed quoteUser(User author, String quote){
        return new EmbedBuilder()
                .setAuthor(author.getName())
                .setDescription(quote)
                .setFooter(
                        Teeto.getTeeto().getResponses().getResponse("msg.user_quote_footer")
                            .setPlaceholder("{@user}",
                                author.getName() + "#" + author.getDiscriminator())
                            .get(),
                        author.getEffectiveAvatarUrl()
                )
                .build();
    }

    /**
     * Creates a message embed quoting a message from a bot.
     *
     * @param author the bot that we're quoting.
     * @param quote the text to quote.
     * @return the created message embed.
     */
    private static MessageEmbed quoteBot(User author, String quote){
        return new EmbedBuilder()
                .setAuthor(author.getName())
                .setDescription(quote)
                .setFooter(
                        Teeto.getTeeto().getResponses().getResponse("msg.bot_quote_footer")
                                .setPlaceholder("{@bot}",
                                        author.getName() + "#" + author.getDiscriminator())
                                .get(),
                        author.getEffectiveAvatarUrl()
                )
                .build();
    }
}
