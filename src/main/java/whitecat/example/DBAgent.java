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

import java.util.LinkedList;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.WCAgent;
import whitecat.core.role.IRole;

/**
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class DBAgent implements WCAgent {

	private boolean					administrator	= false;

	/**
	 * A list of my roles.
	 */
	private final LinkedList<IRole>	roles			= new LinkedList<IRole>();

	public DBAgent() {
		this( false );
	}

	public DBAgent(final boolean administrator) {
		super();
		this.administrator = administrator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.agents.WCAgent#getAgentProxy()
	 */
	public AgentProxy getAgentProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void run() {
		if (administrator){
			// I'm an administrator
		}else{
			// I'm a database user
			final IRole role = new DatabaseUser();
			roles.add( role );
			System.out
					.println( "Agent counting the tuples and getting the max "
							+ ((DatabaseUser) role).selectCount() + " "
							+ ((DatabaseUser) role).getMaxPrice() );
		}
	}

}
