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
     * List of names that identify the song.
     */
    private final String[] aliases;

    /**
     * Constructs a new audio file.
     *
     * @param id the unique ID.
     * @param displayName the display name (i.e. title)
     * @param audioFile the audio file itself.
     */
    AudioFile(String id, String displayName, File audioFile, String[] aliases){
        this.id = id;
        this.displayName = displayName;
        this.audioFile = audioFile;
        this.aliases = aliases;
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

    /**
     * @return The list of names that identify the song.
     */
    public String[] getAliases(){
        return this.aliases;
    }
}
