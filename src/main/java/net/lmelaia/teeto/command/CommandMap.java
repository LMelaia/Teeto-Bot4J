package net.lmelaia.teeto.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lmelaia.teeto.Constants;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.util.JsonUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Helper class to map command handler methods
 * to command information objects and command names
 * using unique command IDs.
 */
class CommandMap {

    /**
     * Map of command names to the matching command ID.
     */
    private HashMap<String, String> nameMap = new HashMap<>();

    /**
     * Map of command IDs to the matching command information object.
     */
    private HashMap<String, CommandInfo> map = new HashMap<>();

    /**
     * Retrieves command information from file and constructs a
     * new command map from it.
     *
     * @throws FileNotFoundException if the command config file
     * cannot be found.
     */
    CommandMap() throws FileNotFoundException {
        JsonObject commandsConfig = Teeto.GSON.fromJson(new FileReader(
                Constants.getCommandsConfigFile()), JsonObject.class);

        JsonArray commands = commandsConfig.getAsJsonArray("commands");

        for (JsonElement commandE : commands) {
            JsonObject command = commandE.getAsJsonObject();

            CommandInfo commandInfo = new CommandInfo(
                    command.get("commandID").getAsString(),
                    JsonUtil.jsonArrayToStringArray(command.get("names").getAsJsonArray()),
                    command.get("description").getAsString(),
                    (command.get("extraInfo").isJsonNull()) ? null : command.get("extraInfo").getAsString(),
                    command.get("visible").getAsBoolean()
            );

            for (String name: commandInfo.getNames()) {
                nameMap.put(name, commandInfo.getCommandID());
            }

            map.put(commandInfo.getCommandID(), commandInfo);
        }
    }

    /**
     * @param name one of the names of a command.
     * @return the command ID associated with the name.
     */
    String getCommandIDFromName(String name){
        return nameMap.get(name);
    }

    /**
     * @param ID the command ID.
     * @return the command information object from its ID.
     */
    CommandInfo getCommandInfoFromID(String ID){
        return map.get(ID);
    }

    /**
     * @return a list of all command information objects.
     */
    CommandInfo[] getAllCommands(){
        return map.values().toArray(new CommandInfo[0]);
    }
}
