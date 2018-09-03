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
