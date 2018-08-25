package net.lmelaia.teeto.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.util.JsonUtil;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps audio files to their names and ID.
 */
class AudioMap {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Map of audio files to their IDs.
     */
    private final Map<String, AudioFile> idAudioFileMap = new HashMap<>();

    /**
     * Map of audio file IDs to their multiple names.
     */
    private final Map<String, String> nameIDMap = new HashMap<>();

    /**
     * The folder where audio files are kept.
     */
    private final File audioFolder;

    /**
     * Constructs a new audio map.
     *
     * @param audioConfig the audio config file.
     * @param audioFolder the audio folder where
     *                    audio files are kept.
     */
    AudioMap(File audioConfig, File audioFolder) {
        if(!audioFolder.exists()) {
            LOG.fatal("Audio folder not found: " + audioFolder);
            Teeto.shutdown();
        }
        this.audioFolder = audioFolder;

        loadMap(audioConfig);
    }

    /**
     * @param name one of the names of the audio file.
     * @return the audio files ID from its name.
     */
    public String getIDFromName(String name){
        return nameIDMap.get(name);
    }

    /**
     * @param ID the ID of the audio file.
     * @return an audio file from its ID.
     */
    public AudioFile getAudioFileFromID(String ID){
        return idAudioFileMap.get(ID);
    }

    /**
     * @param name one of the names of the audio file.
     * @return an audio file from one its names.
     */
    public AudioFile getAudioFileFromName(String name){
        return getAudioFileFromID(getIDFromName(name));
    }

    public AudioFile[] getAudioFiles(){
        return this.idAudioFileMap.values().toArray(new AudioFile[0]);
    }

    public boolean has(String name){
        return this.nameIDMap.containsKey(name);
    }

    /**
     * Loads the audio config settings from file
     * and gets all audio files.
     *
     * @param audioConfig the audio config file.
     */
    private void loadMap(File audioConfig) {
        JsonObject commandsConfig = null;
        try {
            commandsConfig = Teeto.GSON.fromJson(new FileReader(
                    audioConfig), JsonObject.class);
        } catch (FileNotFoundException e) {
            LOG.fatal("Failed to load audio config file", e);
            Teeto.shutdown();
        }

        assert commandsConfig != null;
        JsonArray audioFiles = commandsConfig.getAsJsonArray("audioFiles");

        for (JsonElement audioElement : audioFiles) {
            JsonObject element = audioElement.getAsJsonObject();

            File file = new File(audioFolder + "/" + element.get("fileName").getAsString());

            if(!file.exists()){
                LOG.error("Missing audio file: " + file + ". Skipping audio file...");
                continue;
            }

            AudioFile audioFile = new AudioFile(
                    element.get("id").getAsString(),
                    element.get("displayName").getAsString(),
                    file
            );

            for (String name: JsonUtil.jsonArrayToStringArray(element.get("names").getAsJsonArray())) {
                nameIDMap.put(name, audioFile.getId());
            }

            idAudioFileMap.put(audioFile.getId(), audioFile);

            LOG.info("Added audio file: " + audioFile.getId() + " -> " + audioFile.getAudioFile());
        }
    }
}
