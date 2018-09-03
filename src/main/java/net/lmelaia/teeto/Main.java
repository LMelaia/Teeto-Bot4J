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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;


/**
 * Entry class.
 */
public class Main {

    /**
     * Logger for this class.
     */
    private static final Logger LOG;

    /*
     * Initializes the logger.
     */
    static{
        LogManager.initialize("config/log4j.xml");
        LOG = LogManager.getLogger();
    }

    /**
     * Main method.
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        LOG.log(Level.INFO, "Starting new Teeto Bot instance.");
        LOG.log(Level.INFO, "Run directory: " + Teeto.getRunDirectory());
        try {
            Teeto.initTeeto();
        } catch (LoginException | InterruptedException e) {
            Teeto.shutdown();
        } catch (Exception e){
            LOG.fatal("Exception thrown during bot boot", e);
            Teeto.shutdown();
        }
    }
}
