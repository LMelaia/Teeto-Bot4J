package net.lmelaia.teeto.command.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.lmelaia.teeto.GuildSettings;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.command.CommandHandler;

/**
 * Commands for bot management.
 */
public class BotManageCommands {

    //Private constructor.
    private BotManageCommands(){}

    /**
     * Adds a command prefix to the guild settings.
     *
     * @param g the guild.
     * @param args the commands arguments.
     * @return the response to the user.
     */
    @CommandHandler(".bmanage.add_prefix")
    public static String addPrefix(Guild g, String[] args){
        try{g = getIfNotNull(g);} catch (NullPointerException e){return e.getMessage();}

        if(args.length != 2)
            return Teeto.getTeeto().getResponses().getResponse("cmd.arg_length_error")
                    .setPlaceholder("{@command}", "set-channel")
                    .setPlaceholder("{@argLength}", String.valueOf(args.length - 1))
                    .get();

        GuildSettings settings = GuildSettings.getGuildSettings(g);
        JsonArray botCmds = (settings.has(GuildSettings.Settings.BOT_COMMANDS))
                ? settings.getSetting(GuildSettings.Settings.BOT_COMMANDS).getAsJsonArray()
                : new JsonArray();
        botCmds.add(args[1]);

        settings.setSetting(GuildSettings.Settings.BOT_COMMANDS,
                botCmds
        );

        return (settings.save()) ? Teeto.getTeeto().getResponses().getResponse("settings.saved").get()
                : Teeto.getTeeto().getResponses().getResponse("settings.not_saved").get();
    }

    /**
     * Sets the message channel to send all bot messages to.
     *
     * @param g the guild.
     * @param args the command arguments.
     * @return the response to the user.
     */
    @CommandHandler(".bmanage.set_bot_channel")
    public static String setBotChannel(Guild g, String[] args){
        try{g = getIfNotNull(g);} catch (NullPointerException e){return e.getMessage();}

        if(args.length != 2)
            return Teeto.getTeeto().getResponses().getResponse("cmd.arg_length_error")
                    .setPlaceholder("{@command}", "set-channel")
                    .setPlaceholder("{@argLength}", String.valueOf(args.length - 1))
                    .get();

        MessageChannel channel = g.getTextChannelsByName(args[1], true).get(0);

        if(channel == null)
            return Teeto.getTeeto().getResponses().getResponse("audio.channel_not_found")
                    .setPlaceholder("{@channel}", args[1]).get();

        GuildSettings settings = GuildSettings.getGuildSettings(g);
        settings.setSetting(GuildSettings.Settings.BOT_CHANNEL, new JsonPrimitive(channel.getIdLong()));

        return (settings.save()) ? Teeto.getTeeto().getResponses().getResponse("settings.saved").get()
                : Teeto.getTeeto().getResponses().getResponse("settings.not_saved").get();
    }

    /**
     * Throws a null pointer if the passed guild parameter
     * is null.
     * @param g the guild.
     * @return the guild (g).
     */
    private static Guild getIfNotNull(Guild g){
        if(g == null)
            throw new NullPointerException(
                    Teeto.getTeeto().getResponses().getResponse("cmd.not_in_guild").get());
        return g;
    }
}
