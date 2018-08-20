package net.lmelaia.teeto.util;

import net.dv8tion.jda.core.entities.User;

/**
 * A set of DiscordUtil API utilities.
 */
public class DiscordUtil {

    /**
     * Returns a string containing a users name and id
     * in the format {@code name#id}.
     *
     * @param u the user.
     * @return the users unique string.
     */
    public static String getUserAsUniqueString(User u){
        return u.getName() + "#" + u.getDiscriminator();
    }
}
