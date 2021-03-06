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
package net.lmelaia.teeto;

import java.io.File;

/**
 * Constant variables for the application as whole.
 *
 * For now this class holds paths to files,
 * both external and internal.
 */
@SuppressWarnings("WeakerAccess")
public final class Constants {

    //Private constructor.
    private Constants(){}

    /**
     * @return the path to the log4j2 config file within
     * the resources folder.
     */
    @SuppressWarnings("WeakerAccess")
    public static String getRelativeLog4jConfigFile(){
        return "config/log4j.xml";
    }

    /**
     * @return the bot config file (config/bot.config.json).
     */
    @SuppressWarnings("WeakerAccess")
    public static File getBotConfigFile(){
        return new File(Teeto.getRunDirectory() + "/config/bot.config.json");
    }

    /**
     * @return the .TOKEN file.
     */
    @SuppressWarnings("WeakerAccess")
    public static File getTokenFile(){
        return new File(Teeto.getRunDirectory() + "/.TOKEN");
    }

    /**
     * @return the commands.config.json file.
     */
    public static File getCommandsConfigFile(){
        return new File(Teeto.getRunDirectory() + "/config/commands.config.json");
    }

    /**
     * @return the templates folder.
     */
    public static File getTemplateFolder(){
        return new File(Teeto.getRunDirectory() + "/config/templates/");
    }

    /**
     * @return the responses.properties file.
     */
    public static File getResponsesFile(){
        return new File(Teeto.getRunDirectory() + "/config/responses.properties");
    }

    /**
     * @return the audio.config.json file.
     */
    public static File getAudioConfigFile(){
        return new File(Teeto.getRunDirectory() + "/config/audio.config.json");
    }

    /**
     * @return the audio folder where audio files are kept.
     */
    public static File getAudioFolder(){
        return new File(Teeto.getRunDirectory() + "/audio/");
    }

    /**
     * @return the folder where guild settings are kept.
     */
    public static File getSettingsFolder(){
        return new File(Teeto.getAbsoluteRunDirectory() + "/guild_settings/");
    }
}
