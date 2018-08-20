package net.lmelaia.teeto.command.commands;

import net.lmelaia.teeto.command.CommandHandler;

/**
 * A set of commands designed to aid the user
 * in using the bot.
 */
public class HelpCommands {

    /**
     * Help command.
     *
     * Sends a message back to the user in the message
     * channel the command came from, displaying help tips,
     * bot using instructions and a list of all commands or
     * detailed information of a command.
     *
     * @return the help message as a String.
     */
    @CommandHandler(".help")
    public static String help(){
        return "help()";
    }

    /**
     * List commands command.
     *
     * Sends a message back to the user in the message
     * channel the command came from, displaying a list
     * of all commands.
     *
     * @return a list of all commands as a String.
     */
    @CommandHandler(".list-commands")
    public static String listCommands(){
        return "listCommands()";
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
    @CommandHandler(".information")
    public static String information(){
        return "information()";
    }
}
