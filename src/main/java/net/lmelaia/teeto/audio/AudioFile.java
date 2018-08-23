package net.lmelaia.teeto.audio;

import java.io.File;

/**
 * Represents an audio file on disk with metadata. The
 * metadata includes the id and display name.
 */
public final class AudioFile {

    /**
     * The unique ID of the audio file.
     */
    private final String id;

    /**
     * The display name (i.e. title) of the audio file.
     */
    private final String displayName;

    /**
     * The audio file itself.
     */
    private final File audioFile;

    /**
     * Constructs a new audio file.
     *
     * @param id the unique ID.
     * @param displayName the display name (i.e. title)
     * @param audioFile the audio file itself.
     */
    AudioFile(String id, String displayName, File audioFile){
        this.id = id;
        this.displayName = displayName;
        this.audioFile = audioFile;
    }

    /**
     * @return The unique ID of the audio file.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The display name (i.e. title) of the audio file.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return The audio file itself.
     */
    public File getAudioFile() {
        return audioFile;
    }
}
