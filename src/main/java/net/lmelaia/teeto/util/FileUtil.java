package net.lmelaia.teeto.util;

import net.lmelaia.teeto.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public final class FileUtil {

    /**
     * Logger for this class.
     */
    @SuppressWarnings("unused")
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
        try (FileInputStream reader = new FileInputStream(file)){
            int val;
            while((val = reader.read()) != -1){
                result.append((char)val);
            }
        }

        return result.toString();
    }
}
