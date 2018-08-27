package net.lmelaia.teeto.aud;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import net.lmelaia.teeto.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Audio player for a guild.
 *
 * <p>
 * This class provides the ability to: connect/disconnect
 * from voice channels and play/pause/stop audio tracks
 * as well as attach a listener to listen for events
 * such as on track stop or on track end.
 */
public class AudioPlayer {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Number of created audio players.
     */
    private static int playerCount = 0;

    /**
     * Internal audio player API.
     */
    private final com.sedmelluq.discord.lavaplayer.player.AudioPlayer internalPlayer;

    /**
     * The audio manager.
     */
    private final AudioPlayerManager audioPlayerManager;

    /**
     * The guild this audio player is for.
     */
    private final Guild guild;

    /**
     * Constructs a new guild audio player.
     *
     * @param manager the audio manager.
     * @param guild the guild.
     */
    AudioPlayer(AudioPlayerManager manager, Guild guild){
        playerCount++;
        LOG.info("Creating new audio player for guild: " + guild.getName());
        LOG.info("Audio player count: " + playerCount);
        this.internalPlayer = manager.createPlayer();
        this.audioPlayerManager = manager;
        this.guild = guild;
    }

    /**
     * Adds a new AudioEventAdapter (implementation of
     * AudioEventListener).
     *
     * @param adapter the audio adapter.
     */
    public void addListener(AudioEventAdapter adapter){
        this.internalPlayer.addListener(adapter);
    }

    /**
     * Connects the bot to the given voice channel
     *
     * @param channel the voice channel in the guild.
     * @param force if the bot should disconnect and
     *              reconnect if the bot is already
     *              connect to a voice channel.
     * @return {@code true} if the bot successfully connected.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean connectToVoice(VoiceChannel channel, boolean force){
        if(getAudioManager().isAttemptingToConnect()){
            LOG.warn("Attempt to connect to voice channel while already attempting to connect. Connect ignored.");
            return false;
        }

        if(getAudioManager().isConnected()){
            LOG.warn("Attempting to connect to voice while already connected.");

            if(force){
                LOG.info("Force enabled. Forcing disconnect and reconnect...");
                disconnectFromVoice();
                internalConnect(channel);
            } else {
                LOG.info("Skipping connect");
                return false;
            }
        }

        internalConnect(channel);
        return getAudioManager().isConnected() || getAudioManager().isAttemptingToConnect();
    }

    /**
     * Disconnects the bot from the audio channel
     * it is in. If the bot is not connected,
     * nothing will happen.
     */
    public void disconnectFromVoice(){
        LOG.info("Disconnecting from voice in: " + guild.getName());
        internalPlayer.stopTrack();
        getAudioManager().closeAudioConnection();
    }

    /**
     * Pauses or un-pauses the current track.
     *
     * @param paused {@code true} if pausing,
     *               {@code false} if un-pausing.
     */
    public void setPaused(boolean paused){
        if(!getAudioManager().isConnected()){
            LOG.warn("Attempt to pause song when not connected to voice channel. Ignoring attempt.");
            return;
        }

        internalPlayer.setPaused(paused);
    }

    /**
     * Play the given audio track file.
     *
     * @param identifier the absolute path to the audio
     *                   file or audio resource.
     */
    public void play(String identifier){
        if(!getAudioManager().isConnected()){
            LOG.warn("Attempt to play song when not connected to voice channel. Going ahead anyway");
        }

        load(identifier);
    }

    /**
     * Stops the currently playing song.
     */
    public void stop(){
        LOG.info("Stopping track in guild: " + guild);
        internalPlayer.stopTrack();
    }

    /**
     * @return {@code true} if the audio player is
     * playing a track.
     */
    public boolean isPlaying(){
        return internalPlayer.getPlayingTrack() != null;
    }

    /**
     * @return the currently playing track
     * or {@code null} if no track is
     * playing.
     */
    public AudioTrack getPlayingTrack(){
        return internalPlayer.getPlayingTrack();
    }

    /**
     * @return the VoiceChannel the bot is connected
     * to in the guild or {@code null} if the bot
     * is not connected to voice.
     */
    public VoiceChannel getConnectedChannel(){
        return getAudioManager().getConnectedChannel();
    }

    /**
     * @return {@code true} if the bot is connected
     * to a voice channel within the current guild.
     */
    public boolean isConnected(){
        return getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTED;
    }

    /**
     * @return the audio send handler wrapper.
     */
    public JDAAudioSendHandler getSendHandler() {
        return new JDAAudioSendHandler(internalPlayer);
    }

    private int plays = 0;

    /**
     * Loads and plays the given audio file.
     *
     * @param identifier the absolute path to the audio
     *      *                   file or audio resource.
     */
    private void load(String identifier){
        audioPlayerManager.loadItemOrdered(this, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                LOG.debug("Playing audio track: " + track.getIdentifier());
                if(plays > 99){
                    LOG.info("100 Plays on audio track: " + track.getIdentifier());
                    plays = 0;
                }
                plays++;
                internalPlay(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                LOG.info("Adding to queue " + firstTrack.getIdentifier() + " (first track of playlist " + playlist.getName() + ")");
                internalPlay(firstTrack);
            }

            @Override
            public void noMatches() {
                LOG.warn("Audio resource not found: " + identifier);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                LOG.error("Failed to play audio resource: " + identifier, exception);
            }
        });
    }

    /**
     * Internal play method.
     *
     * @param track the audio track to play.
     */
    private void internalPlay(AudioTrack track) {
        this.internalPlayer.stopTrack();
        this.internalPlayer.playTrack(track);
    }

    /**
     * Internal connect method.
     *
     * @param channel the voice channel to connect to.
     */
    private void internalConnect(VoiceChannel channel){
        internalPlayer.stopTrack();
        getAudioManager().closeAudioConnection();
        getAudioManager().openAudioConnection(channel);
        LOG.info("Connected to voice channel: " + channel.getName() + " -> " + guild.getName());
    }

    /**
     * @return the guilds audio manager.
     */
    private AudioManager getAudioManager(){
        return guild.getAudioManager();
    }
}
