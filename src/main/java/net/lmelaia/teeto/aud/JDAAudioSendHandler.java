package net.lmelaia.teeto.aud;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

/**
 * Wrapper class for the java discord API audio send
 * handler implementation.
 */
public class JDAAudioSendHandler implements AudioSendHandler {

    /**
     * The audio player who's send handler we're wrapping.
     */
    private final AudioPlayer audioPlayer;

    /**
     * The latest audio frame to be played.
     */
    private AudioFrame lastFrame;

    /**
     * Constructs a new audio send handler wrapper.
     *
     * @param audioPlayer the audio player who's send
     *                    handler we're wrapping.
     */
    public JDAAudioSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    /**
     * @return {@code true} if the send handler
     * can keep providing audio frames.
     */
    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        return lastFrame != null;
    }

    /**
     * @return the audio frame data as a byte array, or {@code null}
     * if no audio can be provided.
     */
    @Override
    public byte[] provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return data;
    }

    /**
     * @return {@code true}.
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}
