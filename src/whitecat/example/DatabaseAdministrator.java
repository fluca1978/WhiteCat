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

import whitecat.core.annotation.PublicRole;
import whitecat.core.annotation.Role;

/**
 * An example of role. This role implements a database administrator, and thus
 * it has a public interface so that other agents can request operations and services
 * to the database administrator.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
@Role()		// to mark this as a role object

		// here I specify the part of the role that can be visible
		// to outsider and that will be applied to the proxy
@PublicRole(roleInterface = "whitecat.example.IDatabaseAdministrator",
            roleAnnotation = "whitecat.example.ExampleRoleAnnotation"
           )
public class DatabaseAdministrator 
implements IDatabaseAdministrator	
		// this is a role with a public interface
{

    /**
     * Public service: it can be called thru the proxy of an agent.
     */
    public StringBuffer backupDatabase(String databaseName) {
	System.out.println("DatabaseAdministrator backupping database " + databaseName);
	return new StringBuffer("SELECT * FROM MYTABLE;");
    }

    /**
     * Public service: it can be called thru the proxy of an agent.
     */
    public boolean createDatabase(String databaseName) {
	System.out.println("DatabaseAdministrator creating database " + databaseName);
	return true;
    }
    
    /**
     * Private service, that is a service that cannot be called thru the proxy of an agent.
     * @param databaseName the database to reindex.
     */
    public void reindexDatabase(String databaseName){
	System.out.println("DatabaseAdministrator is reindexing " + databaseName);
    }

}
