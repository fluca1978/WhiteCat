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
 * Copyright (C) Luca Ferrari 2008-2011 - cat4hire@users.sourceforge.net
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
package whitecat.core.exceptions;

import whitecat.core.agents.AgentProxyID;

/**
 * This exception is thrown when an agent is undergoing a role manipulation
 * operation by the role engine. This exception is thrown by mutator methods
 * within the proxy (i.e., methods that can change the internal state of a
 * proxy) since the newest state could possibly be not copied to the manipulated
 * proxy.
 * 
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
public class WCProxyLockedException extends RuntimeException {

	public WCProxyLockedException(final AgentProxyID lockedProxyID) {
		super( "The proxy " + lockedProxyID
				+ " points to an agent that is undergoing role manipulation!" );
	}
}
