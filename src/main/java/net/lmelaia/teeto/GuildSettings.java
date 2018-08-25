package net.lmelaia.teeto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents settings for a guild stored on file.
 */
public class GuildSettings {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Settings object to guild id map.
     */
    private static final Map<Long, GuildSettings> GUILD_SETTINGS_HASH_MAP = new HashMap<>();

    /**
     * File IO for settings objects.
     */
    private static final SettingsIO IO;

    /**
     * The backing json object that stores the settings.
     */
    private final JsonObject backingObject;

    /*
     * Initializes IO and loads all files.
     */
    static{
        IO = new SettingsIO(Constants.getSettingsFolder(), GUILD_SETTINGS_HASH_MAP);
    }

    /**
     * Creates new guild settings object with unique ID.
     *
     * @param g the guild the settings are for.
     */
    private GuildSettings(Guild g){
        backingObject = new JsonObject();
        backingObject.add(Settings.ID.getProperty(), new JsonPrimitive(g.getIdLong()));
    }

    /**
     * Creates a new guild settings object from
     * json.
     *
     * @param jo the json object.
     */
    private GuildSettings(JsonObject jo){
        this.backingObject = jo;
    }

    /**
     * Returns a setting from guilds settings.
     *
     * @param s the settings "key".
     * @return the value of the setting.
     */
    public JsonElement getSetting(Setting s){
        return backingObject.get(s.getProperty());
    }

    /**
     * Sets the value of a setting in the guild settings.
     *
     * @param s the setting to set.
     * @param value the value to set.
     */
    public void setSetting(Setting s, JsonElement value){
        backingObject.add(s.getProperty(), value);
    }

    /**
     * @param s the setting to check for.
     * @return {@code true} if the guild settings
     * contains the given setting.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean has(Setting s){
        return backingObject.has(s.getProperty());
    }

    /**
     * Saves the settings to file.
     *
     * @return {@code true} if successful.
     */
    public boolean save(){
        try {
            IO.save(this);
        } catch (IOException e) {
            LOG.error("Failed to save guild settings: " + getSetting(Settings.ID).getAsString(), e);
            return false;
        }

        return true;
    }

    /**
     * Returns the guild settings object for the given guild.
     *
     * @param g the given guild.
     * @return the guild settings object.
     */
    public static GuildSettings getGuildSettings(Guild g){
        if(GUILD_SETTINGS_HASH_MAP.containsKey(g.getIdLong()))
            return GUILD_SETTINGS_HASH_MAP.get(g.getIdLong());

        GuildSettings newSettings = new GuildSettings(g);
        GUILD_SETTINGS_HASH_MAP.put(g.getIdLong(), newSettings);

        return newSettings;
    }

    /**
     * Handles file read and writes for the settings.
     */
    private static class SettingsIO {

        /**
         * Folder where guild settings files are kept.
         */
        private final File settingsFolder;

        /**
         * Creates new settings io instance.
         *
         * @param settingsFolder the folder where settings files are kept.
         * @param map the map of settings objects to guild IDs.
         */
        private SettingsIO(File settingsFolder, Map<Long, GuildSettings> map) {
            this.settingsFolder = settingsFolder;
            //noinspection ResultOfMethodCallIgnored
            settingsFolder.mkdirs();
            loadAll(map);
        }

        /**
         * Saves the guild settings object to file..
         *
         * @param g the guild settings object.
         * @throws IOException reason for failure if any.
         */
        public void save(GuildSettings g) throws IOException {
            File gf = getGuildFile(g.getSetting(Settings.ID).getAsLong());

            LOG.info("Saving guild settings: " + gf.getAbsoluteFile());
            FileWriter writer = new FileWriter(gf);
            writer.write(Teeto.GSON.toJson(g.backingObject, JsonObject.class));
            writer.flush();
            writer.close();
        }

        /**
         * Reads the guild settings from file.
         *
         * @param f the file to get the settings from.
         * @return the guild settings object.
         * @throws FileNotFoundException reason for failure if any.
         */
        private GuildSettings load(File f) throws FileNotFoundException {
            LOG.info("Loading guild settings: " + f.getAbsoluteFile());
            return new GuildSettings(Teeto.GSON.fromJson(new FileReader(f), JsonObject.class));
        }

        /**
         * Loads all settings files to the given map.
         *
         * @param map the given map.
         */
        private void loadAll(Map<Long, GuildSettings> map) {
            if(settingsFolder.listFiles() == null){
                LOG.warn("Settings folder not listing files. Skipping loading of guild settings...");
                return;
            }

            for(File f : Objects.requireNonNull(settingsFolder.listFiles())){
                if(!f.getName().endsWith(".json"))
                    continue;

                GuildSettings fileValue;
                try {
                    fileValue = load(f);
                    map.put(fileValue.getSetting(Settings.ID).getAsLong(), fileValue);
                } catch (FileNotFoundException e) {
                    LOG.error("Failed to load guild settings: " + f.getAbsoluteFile());
                }
            }
        }

        /**
         * @param id the guilds ID.
         * @return the guild settings file for the given guilds ID.
         * @throws IOException reason for failure if any.
         */
        @SuppressWarnings("ResultOfMethodCallIgnored")
        private File getGuildFile(long id) throws IOException {
            File guildFile = new File(settingsFolder.getAbsolutePath() + "/" + id + ".json");

            if(!guildFile.exists()){
                guildFile.createNewFile();
            }

            return new File(settingsFolder.getAbsolutePath() + "/" + id + ".json");
        }
    }

    /**
     * Enum list of all settings for the guilds.
     */
    public enum Settings implements Setting {
        /**
         * Channel to go to when a play request is received.
         */
        HELL_CHANNEL("channel"),

        /**
         * The song to play in the specified channel.
         */
        HELL_SONG("song"),

        /**
         * The channel bot message should be moved/sent to.
         */
        BOT_CHANNEL("bchannel"),

        /**
         * List of command prefixes for bots in the guild.
         */
        BOT_COMMANDS("bcommands");

        /**
         * String used as the property for the value in the backing json object.
         */
        private final String property;

        /**
         * Private ID setting. Don't want people overriding this.
         */
        private static final Setting ID = () -> "id";

        /**
         * @param property the String property for the value in the backing json object.
         */
        Settings(String property){
            this.property = property;
        }

        /**
         * @return the String property for the value in the backing json object.
         */
        @Nonnull
        @Override
        public String getProperty() {
            return this.property;
        }
    }

    /**
     * Used to create new settings without editing the settings enum.
     */
    public interface Setting {

        /**
         * The backing
         *
         * @return the String property for the value in the backing json object.
         */
        String getProperty();
    }
}
