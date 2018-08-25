package net.lmelaia.teeto.messaging;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.lmelaia.teeto.GuildSettings;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.util.JsonUtil;
import net.lmelaia.teeto.util.MessageUtil;
import org.apache.logging.log4j.Logger;

/**
 * Handles message sent by bots and people issuing
 * commands to bots and moves them to the appropriate
 * channel.
 */
public class BotMessageManager {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Singleton instance.
     */
    private static BotMessageManager instance;

    /**
     * Initializes the bot message manager.
     *
     * @param jda Java discord api instance.
     */
    public static void init(JDA jda){
        if(instance == null)
            instance = new BotMessageManager(jda);
    }

    /**
     * @return the bot message manager is initialized, otherwise
     * null.
     */
    public static BotMessageManager get(){
        return instance;
    }

    /**
     * Constructs the message manager.
     *
     * @param jda the java discord api.
     */
    private BotMessageManager(JDA jda){
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
                        .setEmbed(MessageUtil.quote(author, event.getMessage().getContentRaw())).build();
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
                            .setEmbed(MessageUtil.quote(author, event.getMessage().getContentRaw())).build();
                    event.getMessage().delete().submit();
                    botMsgChannel.sendMessage(botMessageQuote).queue();//Move message to bot channel.
                    return;//We're done
                }
            }
        }
    }
}
