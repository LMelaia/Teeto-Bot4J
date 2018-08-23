package net.lmelaia.teeto.command.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.command.CommandHandler;
import net.lmelaia.teeto.command.CommandInfo;
import net.lmelaia.teeto.command.CommandManager;
import net.lmelaia.teeto.util.TemplateBuilder;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of commands designed to aid the user
 * in using the bot.
 */
public final class HelpCommands {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Text displayed to the user when they request the help command.
     */
    private static final String HELP_TEXT;

    /**
     * Teeto instance.
     */
    private static final Teeto TEETO;

    /*
     * Initializes the help text.
     */
    static{
        TEETO = Teeto.getTeeto();

        String bt = null;
        try {
            bt = new TemplateBuilder("help")
                    .setPlaceholder("{@name}", TEETO.getTeetoConfig().getName())
                    .setPlaceholder("{@commandPrefixes}", getCommandPrefixes())
                    .setPlaceholder("{@nameCap}", TEETO.getTeetoConfig().getName().toUpperCase())
                    .build();
        } catch (IOException e) {
            LOG.fatal("Failed to get help template", e);
            Teeto.shutdown();
        } finally {
            HELP_TEXT = bt;
        }

    }

    //Private constructor
    private HelpCommands(){}

    /**
     * Help command.
     *
     * Sends a message back to the user in the message
     * channel the command came from, displaying help tips,
     * bot usage instructions and a list of all commands or
     * detailed information of a command.
     */
    @CommandHandler(".help.help")
    public static void help(String[] args, MessageChannel channel){
        if(args.length == 1){
            channel.sendMessage(HELP_TEXT).embed(new EmbedBuilder()
                    .setAuthor(TEETO.getResponses().getResponse("help.author").get())
                    .setFooter(TEETO.getResponses().getResponse("help.footer").get(), null)
                    .build()
            ).queue();

            for(String command : getCommandList(true, false))
                channel.sendMessage(command).queue();
        } else if(args.length > 2) {
            channel.sendMessage(
                    Teeto.getTeeto().getResponses().getResponse("cmd.arg_length_error")
                    .setPlaceholder("{@command}", "help")
                    .setPlaceholder("{@argLength}", String.valueOf(args.length - 1))
                    .get()
            ).queue();
        } else {
            if(CommandManager.getInstance().getCommandIDFromName(args[1]) == null) {
                channel.sendMessage(
                        Teeto.getTeeto().getResponses().getResponse("cmd.not_found")
                        .setPlaceholder("{@command}", args[1]).get()
                ).queue();
                return;
            }

            channel.sendMessage(getCommandInfo(true,
                    CommandManager.getInstance().getCommandInfoFromID(CommandManager.getInstance()
                            .getCommandIDFromName(args[1])))).queue();

        }
    }

    /**
     * List commands command.
     *
     * Sends a message back to the user in the message
     * channel the command came from, displaying a list
     * of all commands.
     */
    @CommandHandler(".help.list-commands")
    public static void listCommands(String[] args, MessageChannel channel){
        if(args.length == 2 && args[1].equals("-unlisted")){
            for(String command : getCommandList(true, true))
                channel.sendMessage(command).queue();
        }

        for(String command : getCommandList(true, false))
            channel.sendMessage(command).queue();
    }

    /**
     * Bot information and statistics command.
     *
     * Sends a message back to the user in the message
     * channel the command came from, displaying detailed
     * statistics and information about the bot.
     *
     * @return bot statistics and information as a String.
     */
    @CommandHandler(".help.information")
    public static String information(){
        return Teeto.getTeeto().getResponses().getResponse("cmd.not_implemented").get();
    }

    /**
     * @return a styled discord string displaying all the command prefixes.
     */
    private static String getCommandPrefixes(){
        StringBuilder result = new StringBuilder();

        for(String prefix : TEETO.getTeetoConfig().getCommandPrefixes()){
            result.append("`").append(prefix).append("`, ");
        }

        return result.toString().substring(0, result.length() - 2);
    }


    /**
     * @param detailed if true, the commands extra information will be included.
     * @param showUnlisted if true, shows invisible commands.
     * @return a styled discord string displaying all the commands.
     */
    private static String[] getCommandList(boolean detailed, boolean showUnlisted){
        List<String> commands = new ArrayList<>();

        for(CommandInfo commandInfo : CommandManager.getInstance().getAllCommandInfoObjects()){
            if(!commandInfo.isVisible() && !showUnlisted)
                continue;
            commands.add(getCommandInfo(detailed, commandInfo) + "\n.\n");
        }

        return commands.toArray(new String[0]);
    }

    /**
     * @param detailed if true, the commands extra information will be included.
     * @param commandInfo the commands command information object.
     * @return a styled discord string displaying the information of a command.
     */
    private static String getCommandInfo(boolean detailed, CommandInfo commandInfo){
        StringBuilder result = new StringBuilder();

        result.append("Command: `").append(commandInfo.getNames()[0]).append("`\n");
        result.append("Aliases: ").append(getCommandAliases(commandInfo)).append("\n");
        result.append("**").append("Description: ").append(commandInfo.getDescription()).append("**");
        if(detailed)
            result.append("__").append("\nExtra Information: ").append(
                    (commandInfo.getExtraInfo() == null)
                            ? "`No Extra Information`" : commandInfo.getExtraInfo()
            ).append("__");

        return result.toString();
    }

    /**
     * @param commandInfo the commands command information object.
     * @return a styled discord string displaying all the names of a command.
     */
    private static String getCommandAliases(CommandInfo commandInfo){
        StringBuilder result = new StringBuilder();

        for(String alias : commandInfo.getNames()){
            result.append("`").append(alias).append("`, ");
        }

        return result.toString().substring(0, result.length() - 2);
    }
}
