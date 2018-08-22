package net.lmelaia.teeto.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.Guild;
import net.lmelaia.teeto.Constants;
import net.lmelaia.teeto.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages audio players and audio files.
 * <p>
 * This class provides an audio player for a guild
 * as well as methods to retrieve audio files from
 * names and ID.
 */
public class AudioManager {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The singleton instance of this class.
     */
    private static AudioManager instance;

    /**
     * The list of initialized guild audio players.
     */
    private final Map<Long, AudioPlayer> audioPlayers = new HashMap<>();

    /**
     * Map of audio files to IDs and names.
     */
    private final AudioMap audioFileMap;

    /**
     * Audio player manager.
     */
    private final AudioPlayerManager playerManager;

    /**
     * Constructs a new audio manager.
     */
    AudioManager(){
        LOG.info("Initializing audio manager.");
        this.audioFileMap = new AudioMap(Constants.getAudioConfigFile(), Constants.getAudioFolder());
        playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    /**
     * @param name one of the names of the audio file.
     * @return the ID of an audio file from one of its names.
     */
    public String getIDFromName(String name){
        return audioFileMap.getIDFromName(name);
    }

    /**
     * @param ID the ID of the audio file.
     * @return an audio file from its ID.
     */
    public AudioFile getAudioFileFromID(String ID){
        return audioFileMap.getAudioFileFromID(ID);
    }

    /**
     * @param name one of the names of the audio file.
     * @return an audio file from one of its names.
     */
    public AudioFile getAudioFileFromName(String name){
        return audioFileMap.getAudioFileFromName(name);
    }

    /**
     * @param guild the guild to get an audio player for.
     * @return the audio player for the given guild.
     */
    public AudioPlayer getAudioPlayer(Guild guild){
        return getGuildAudioPlayer(guild);
    }

    /**
     * Initializes the singleton audio manager. Does
     * nothing if already initialized.
     */
    public static void init(){
        if(instance == null)
            instance = new AudioManager();
    }

    /**
     * @return the singleton audio manager instance, if
     * initialized, otherwise {@code null}
     */
    public static AudioManager getAudioManager(){
        return instance;
    }

    /**
     * Constructs or returns the audio player for the given guild.
     *
     * @param guild the guild to get an audio player for.
     * @return the guilds audio player.
     */
    private synchronized AudioPlayer getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        AudioPlayer audioPlayer = audioPlayers.get(guildId);

        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer(playerManager, guild);
            audioPlayers.put(guildId, audioPlayer);
        }

        guild.getAudioManager().setSendingHandler(audioPlayer.getSendHandler());

        return audioPlayer;
    }
}
