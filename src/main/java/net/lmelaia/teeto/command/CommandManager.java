package net.lmelaia.teeto.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.util.AnnotatedTypeFinder;
import net.lmelaia.teeto.util.AnnotatedTypes;
import net.lmelaia.teeto.util.DiscordUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Handles command processing, execution and discovery.
 * <p>
 * Provides easy management over commands and command execution.
 * <p>
 * Finds any <b>static</b> method annotated with {@link CommandHandler}
 * in net.lmeleia.teeto.command.commands and calls it when an
 * appropriate command is received. These methods are known as
 * command handlers.
 * <p>
 * The command handler method can have any combination of four parameters (including none):
 * {@link net.dv8tion.jda.core.entities.MessageChannel} - the message
 * channel the command was sent to,
 * {@link net.dv8tion.jda.core.entities.User} - the user who sent the message,
 * {@link net.dv8tion.jda.core.entities.Guild} - the guild the message came from
 * and {@code String[]} - the whole message excluding the command
 * prefix {@code .split(" ")}.
 * <p>
 * The method can return {@code void} or {@code String}.
 * When a String is returned, a message is sent to the same
 * message channel as the command containing the String.
 * <p>
 * This class is also capable of executing commands through
 * their ID's and can provide a list of all available commands.
 */
public final class CommandManager {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * All methods annotated with {@link CommandHandler}.
     */
    private final AnnotatedTypes<CommandHandler> annotatedListeners = new AnnotatedTypeFinder<CommandHandler>(
            "net.lmelaia.teeto.command.commands", ElementType.METHOD).find(CommandHandler.class);

    /**
     * Singleton instance.
     */
    private static CommandManager instance;

    /**
     * Map of commands to command information objects as
     * well as a map of all commands to names and ID's.
     */
    private final CommandMap commandMap;

    /**
     * Constructs and initializes a new command manager.
     *
     * @param jda the JDA instance for the application.
     * @param commandPrefixes list of prefixes that denote a command.
     */
    private CommandManager(JDA jda, String[] commandPrefixes){
        CommandMap map = null;
        try {
            map = new CommandMap();
        } catch (FileNotFoundException e) {
            LOG.fatal("CommandMessageListener configuration file not found", e);
            Teeto.shutdown();
        }
        commandMap = map;

        jda.addEventListener(new CommandMessageListener(commandPrefixes) {
            @Override
            public void onPrefixedMessageReceived(String command, User author, MessageChannel channel,
                                                  Guild guild) {
                onPossibleCommandReceived(command, author, channel, guild);
            }
        });
    }

    /**
     * Called when a message is received beginning with one
     * of the command prefixes passed to this objects constructor.
     *
     * @param command the message from the user without the command prefix.
     * @param author the user who sent the command.
     * @param channel the message channel the command came from.
     * @param guild the guild, if any, the message channel is associated with.
     */
    private void onPossibleCommandReceived(String command, User author, MessageChannel channel, Guild guild){
        LOG.log(Level.INFO, "Possible command received: " + String.format(
                "CommandMessage[content: %s, User: %s, MessageChannel: %s, Guild: %s]",
                command, DiscordUtil.getUserAsUniqueString(author), channel.getName(),
                (guild == null) ? null : guild.getName()));

        String ID = commandMap.getCommandIDFromName(command.split(" ")[0]);
        Method mCommand = getCommandListener(ID);

        if(mCommand == null){
            LOG.info("Command listener: " + command + " not found.");
            channel.sendMessage(
                    Teeto.getTeeto().getResponses().getResponse("cmd.not_found")
                            .setPlaceholder("{@command}", command.split(" ")[0]).get()
            ).queue();
            return;
        }

        if(!Modifier.isStatic(mCommand.getModifiers())){
            LOG.error("Command listener method: " + mCommand.toString()
                    + " is not static and will NOT be executed.");
            channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("cmd.error").get()).queue();
            return;
        }

        if(invokeCommand(mCommand, channel, author, guild, command.split(" ")) == Boolean.FALSE)
            channel.sendMessage(
                    Teeto.getTeeto().getResponses().getResponse("cmd.error").get()
            ).queue();
    }

    /**
     * Attempts to invoke a command handler method.
     *
     * @param commandMethod the method to invoke. Must be static.
     * @param messageChannel the channel the command was requested from. Can be null.
     * @param author the user who requested the command. Can be null.
     * @param guild the guild, if any, associated with the message channel. Can be null.
     * @param args the command message used to called the command split by spaces ({@code .split(" ")}). Can be null.
     * @return the value returned from the command handler method. Can be null.
     */
    private @Nullable Object invokeCommand(Method commandMethod, @Nullable MessageChannel messageChannel,
                                           @Nullable User author, @Nullable Guild guild, @Nullable String[] args){
        Class<?>[] parameters = commandMethod.getParameterTypes();
        Object[] parametersToGive = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameter = parameters[i];

            if (parameter.equals(MessageChannel.class))
                parametersToGive[i] = messageChannel;
            else if(parameter.equals(User.class))
                parametersToGive[i] = author;
            else if(parameter.equals(Guild.class))
                parametersToGive[i] = guild;
            else if(parameter.equals(String[].class))
                parametersToGive[i] = args;
            else{
                LOG.error("Command listener method: " + commandMethod.toString()
                        + " contains an unsupported parameter type. Skipping execution...");
                return Boolean.FALSE;
            }
        }

        Object result;

        LOG.info("Invoking command: "
                + commandMethod.getDeclaringClass().getSimpleName()
                + "."
                + commandMethod.getName()
                + "() -> "
                + commandMethod.toString());
        try {
            result = commandMethod.invoke(null, parametersToGive);
        } catch (Exception e) {
            LOG.error("Failed to invoke command method: " + commandMethod.toString(), e);
            return false;
        }

        if(result instanceof String && messageChannel != null){
            messageChannel.sendMessage((String)result).queue();
        }

        return result;
    }

    /**
     * @param ID the ID of the command.
     * @return a command handler method from its ID.
     */
    private Method getCommandListener(String ID){
        if(ID == null)
            return null;

        for(Method methodX : getAllCommandHandler()){
            if(ID.split(" ")[0].toLowerCase()
                    .equals(annotatedListeners.getAnnotationFromMethod(methodX).value()))
                return methodX;
        }

        return null;
    }

    /**
     * @return a list of all command handler methods.
     */
    private Method[] getAllCommandHandler(){
        return annotatedListeners.getMethods();
    }

    /**
     * @return a list of all command information objects.
     */
    public CommandInfo[] getAllCommandInfoObjects(){
        return commandMap.getAllCommands();
    }

    /**
     * @param name any one of the names associated with the command.
     * @return a command handler method from one of its names.
     */
    public String getCommandIDFromName(String name){
        return commandMap.getCommandIDFromName(name);
    }

    /**
     * @param ID the commands ID.
     * @return a command information object from its ID.
     */
    public CommandInfo getCommandInfoFromID(String ID){
        return commandMap.getCommandInfoFromID(ID);
    }

    /**
     * Invokes a command handler method by its ID.
     * All other parameters are set to null.
     *
     * @param ID the command ID.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID){
        return invokeCommand(ID, null, null, null, null);
    }

    /**
     * Invokes a command handler method by its ID.
     * All parameters other than the message channel are set to null.
     *
     * @param ID the command ID.
     * @param channel the message channel the command was requested from.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID, MessageChannel channel){
        return invokeCommand(ID, channel, null, null, null);
    }

    /**
     * Invokes a command handler method by its ID.
     * All parameters other than the author are set to null.
     *
     * @param ID the command ID.
     * @param author the user who requested the command.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID, User author){
        return invokeCommand(ID, null, author, null, null);
    }

    /**
     * Invokes a command handler method by its ID.
     * All parameters other than the command arguments are set to null.
     *
     * @param ID the command ID.
     * @param args the command arguments.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID, String[] args){
        return invokeCommand(ID, null, null, args, null);
    }

    /**
     * Invokes a command handler method by its ID.
     * All parameters other than the guild are set to null.
     *
     * @param ID the command ID.
     * @param guild the guild the command was requested from, if any.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID, Guild guild){
        return invokeCommand(ID, null, null, null, guild);
    }

    /**
     * Invokes a command handler method by its ID.
     *
     * @param ID the command ID.
     * @param channel the message channel the command was requested from.
     * @param author the user who requested the command.
     * @param args the command arguments.
     * @param guild the guild the command was requested from, if any.
     * @return the value returned from the command handler method.
     */
    public Object invokeCommand(String ID, MessageChannel channel, User author, String[] args, Guild guild){
        return invokeCommand(getCommandListener(ID), channel, author, guild, args);
    }

    /**
     * Initializes the command manager.
     *
     * @param jda the programs Java Discord API instance.
     * @param commandPrefixes list of string prefixes used to denote a command.
     * @throws IllegalStateException if the command manager has already been
     * initialized.
     */
    public static void init(JDA jda, String[] commandPrefixes){
        if(instance != null)
            throw new IllegalStateException("Instance already initialized");

        instance = new CommandManager(jda, commandPrefixes);
    }

    /**
     * @return the singleton command manager instance.
     */
    public static CommandManager getInstance(){
        return instance;
    }

    /**
     * Message listener implementation designed to listen
     * for message beginning with one of the command prefixes.
     */
    private abstract class CommandMessageListener extends ListenerAdapter {

        /**
         * List of command prefixes.
         */
        private final String[] commandPrefixes;

        /**
         * Constructs a new command message listener.
         *
         * @param commandPrefixes list of string command prefixes
         *                        used to denote a command.
         */
        private CommandMessageListener(String[] commandPrefixes){
            this.commandPrefixes = commandPrefixes;
        }

        /**
         * Called when a message is received.
         *
         * This method determines if the message
         * is requesting a command or not. If it
         * determines the message is requesting a
         * command, it will further process the command
         * and eventually execute it if possible. Otherwise
         * the message is ignored.
         *
         * @param event MessageReceivedEvent
         */
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            String messageContent = event.getMessage().getContentRaw();
            String commandName;

            if((commandName = getCommandPrefix(messageContent)) == null)
                return;//Not a message we want to further process (i.e. a command)

            onPrefixedMessageReceived(commandName, event.getAuthor(), event.getMessage().getChannel(),
                    event.getMessage().getGuild()
            );
        }

        /**
         * Called when a message is received beginning with a command prefix.
         *
         * @param command the message text.
         * @param author the user who sent the message.
         * @param channel the channel the message came from.
         * @param guild the guild, if any, the message came from.
         */
        public abstract void onPrefixedMessageReceived(String command, User author, MessageChannel channel,
                                                       Guild guild);

        /**
         * @param messageContent a message from a user.
         * @return the command prefix, if any, the message begins with. Null
         * if no command prefix was found.
         */
        private String getCommandPrefix(String messageContent){
            for(String cmdPrefix : commandPrefixes){
                if(messageContent.toLowerCase().startsWith(cmdPrefix.toLowerCase() + " "))
                    return messageContent.substring(cmdPrefix.length() + 1, messageContent.length());

                if(messageContent.toLowerCase().startsWith(cmdPrefix.toLowerCase()))
                    return messageContent.substring(cmdPrefix.length(), messageContent.length());
            }

            return null;
        }
    }
}
