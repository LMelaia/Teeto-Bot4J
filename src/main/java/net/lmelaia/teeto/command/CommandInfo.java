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
     * True if the command should be displayed
     * in help messages and make public to anyone.
     * False if the command should not be made
     * known.
     */
    private final boolean visible;

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
    CommandInfo(String commandID, String[] names, String description, String extraInfo, boolean visible){
        this.commandID = commandID;
        this.names = names;
        this.description = description;
        this.extraInfo = extraInfo;
        this.visible = visible;
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

    /**
     * @return True if the command should be displayed
     * in help messages and make public to anyone.
     * False if the command should not be made
     * known.
     */
    public boolean isVisible() {
        return visible;
    }
}
