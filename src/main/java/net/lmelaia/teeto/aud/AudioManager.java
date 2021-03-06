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
package net.lmelaia.teeto.aud;

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
    private AudioPlayerManager playerManager;

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
     * @return an array of the registered audio files.
     */
    public AudioFile[] getAudioFiles(){
        return audioFileMap.getAudioFiles();
    }

    /**
     * @param name any one of the audio files names.
     * @return {@code true} if the audio file with
     * the given name exists.
     */
    public boolean hasAudioFile(String name){
        return audioFileMap.has(name);
    }

    /**
     * Initializes the singleton audio manager. Does
     * nothing if already initialized.
     */
    public static AudioManager init(){
        if(instance == null)
            instance = new AudioManager();
        else
            throw new IllegalStateException("Audio manager already initialized");

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
            audioPlayer = new AudioPlayer(this, playerManager, guild);
            audioPlayers.put(guildId, audioPlayer);
        }

        guild.getAudioManager().setSendingHandler(audioPlayer.getSendHandler());

        return audioPlayer;
    }
}
