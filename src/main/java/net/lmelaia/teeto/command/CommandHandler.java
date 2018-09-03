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
package net.lmelaia.teeto.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a command handler method.
 * <p>
 * This annotation is used to mark any static method
 * within the net.lmelaia.teeto.command package as a command
 * handler.
 * <p>
 * When a registered command in the commands.config.json
 * file with the same ID passed as the one passed to this
 * annotation is requested by the user, the method will be invoked.
 * <p>
 * The method can have any combination of four parameters (including none):
 * {@link net.dv8tion.jda.core.entities.MessageChannel} - the message
 * channel the command was sent to,
 * {@link net.dv8tion.jda.core.entities.User} - the user who sent the message,
 * {@link net.dv8tion.jda.core.entities.Guild} - the guild the message came from
 * and {@code String[]}  - the whole message excluding the command
 * prefix {@code .split(" ")}.
 * <p>
 * The method can return {@code void} or {@code String}.
 * When a String is returned, a message is sent to the same
 * message channel as the command containing the String.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {

     /**
      * @return the unique ID for this command.
      * By convention, the id is the name of the
      * command beginning with a period. {@code E.g. .help}
     */
     String value();
}
