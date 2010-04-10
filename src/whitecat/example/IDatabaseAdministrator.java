/* 
 * WhiteCat - A dynamic role injector for agents.
 *
 * This project represents a new implementation of the so called BlackCat,
 * a project I made during my thesis degree. For more information about such project please see:
 * 
 *   G., L. Ferrari, L. Leonardi,
 *   Injecting Roles in Java Agents Through Run-Time Bytecode Manipulation
 *   IBM Systems Journal, Vol. 44, No. 1, pp.185-208, 2005
 *
 * This new approach exploits a completely different implementation, keeping the
 * same idea of BlackCat.
 * 
 *
 * Copyright (C) Luca Ferrari 2008-2010 - cat4hire@users.sourceforge.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package whitecat.example;

import whitecat.core.role.IPublicRole;
import whitecat.core.role.IRole;
import whitecat.core.annotation.*;

/**
 * The public part of the database administrator.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
@Role()
public interface IDatabaseAdministrator 
extends IPublicRole	// this is a public role and so
		// extends the public role interface
{
    /**
     * An example of public service: create a database for the specified name.
     * @param databaseName the name of the database to create
     * @return true if the database has been created
     */
    public boolean createDatabase(String databaseName);
    
    /**
     * An example of a service: backup a database and return the database dump
     * as text.
     * @param databaseName the database to backup
     * @return the sql dump of the database
     */
    public StringBuffer backupDatabase(String databaseName);

}
