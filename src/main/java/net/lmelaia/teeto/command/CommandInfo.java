package net.lmelaia.teeto.command;

/**
 * Metadata about a command.
 * <p>
 * This object holds the names, ID, description and extra
 * information about a single command.
 * This information is obtained from the config/commands.config.json
 * file. All objects created from this class are done so
 * by the {@link CommandManager} and cannot be created otherwise but
 * can be obtained from the CommandManager.
 */
public final class CommandInfo {

    /**
     * The unique ID of the command. This is
     * the value passed to {@link CommandHandler}
     * annotation.
     */
    private final String commandID;

    /**
     * The name(s) the command can be referenced by.
     */
    private final String[] names;

    /**
     * Basic description of the command.
     */
    private final String description;

    /**
     * Optional extra detailed information about the command
     * and how to use it.
     */
    private final String extraInfo;

    /**
     * Private constructor. Creates a new command info object.
     *
     * @param commandID The unique ID of the command. This is
     *                  the value passed to {@link CommandHandler}
     *                  annotation.
     * @param names The name(s) the command can be referenced by.
     * @param description Basic description of the command.
     * @param extraInfo Optional extra detailed information about the command
     *      * and how to use it.
     */
    CommandInfo(String commandID, String[] names, String description, String extraInfo){
        this.commandID = commandID;
        this.names = names;
        this.description = description;
        this.extraInfo = extraInfo;
    }

    /**
     * @return The unique ID of the command. This is
     *         the value passed to {@link CommandHandler}
     *         annotation.
     */
    public String getCommandID() {
        return commandID;
    }

    /**
     * @return The name(s) the command can be referenced by.
     */
    public String[] getNames() {
        return names;
    }

    /**
     * @return Basic description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Optional extra detailed information about the command
     *         and how to use it.
     */
    public String getExtraInfo() {
        return extraInfo;
    }
}
