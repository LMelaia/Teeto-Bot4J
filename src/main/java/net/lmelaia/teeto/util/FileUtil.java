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
package net.lmelaia.teeto.util;

import net.lmelaia.teeto.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class FileUtil {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    //Private constructor.
    private FileUtil(){}

    /**
     * Reads an entire file.
     *
     * @param file the file to be read.
     * @return the content of the file as a string.
     * @throws IOException if the is a directory, does not exist
     * or cannot be read.
     */
    public static String readFile(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)){
            int val;
            while((val = reader.read()) != -1){
                result.append((char)val);
            }
        }

        return result.toString();
    }
}
