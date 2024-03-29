/* 
 * WhiteCat - A dynamic role injector for agents.
 *
 * This project represents a new implementation of the so called BlackCat,
 * a project I made during my thesis degree. For more information about such project please see:
 * 
 *   L. Ferrari et al.
 *   Injecting Roles in Java Agents Through Run-Time Bytecode Manipulation
 *   IBM Systems Journal, Vol. 44, No. 1, pp.185-208, 2005
 *
 * This new approach exploits a completely different implementation, keeping the
 * same idea of BlackCat.
 * 
 * See also the following paper for a better introduction to WhiteCat:
 *    L. Ferrari, and H., Zhu, 
 *    Autonomous Role Discovery for Collaborating Agents
 *    Software Practice and Experience
 *    2011
 *
 *
 * 
 *
 * Copyright (C) Luca Ferrari 2006-2013 - fluca1978 (at) gmail.com
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

import java.util.Random;

import whitecat.core.annotations.ROLE;
import whitecat.core.role.IRole;

/**
 * A private role, that is a role that must be used as it is and is not applied
 * to the agent proxy.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
@ROLE()
@DBRoleAnnotation()
public class DatabaseUser implements IRole {

	/**
	 * Returns the max price in the database.
	 * 
	 * @return the max price value
	 */
	public float getMaxPrice() {
		return new Random().nextFloat();
	}

	/**
	 * Count the tuples into a database.
	 * 
	 * @return the number of tuples
	 */
	public long selectCount() {
		return new Random().nextLong();
	}
}
