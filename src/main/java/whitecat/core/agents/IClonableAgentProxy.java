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
package whitecat.core.agents;

/**
 * This interface is used to mark every agent proxy that must be clonable with a
 * custom user control. If your agent proxies have a specific state that must be
 * kept among different role manipulation by the role booster, you should add
 * this interface to your proxy implementation. Please consider that this method
 * does not override the initializeByCopy method of the agent proxy: the
 * initializeByCopy is used to guarantee that the minimun information about the
 * agent proxy are copied, while this interface gives the users more control
 * over which extended state to copy.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public interface IClonableAgentProxy {

	/**
	 * This method is used to clone the agent proxy status. Implement this
	 * method copying field by field the agent proxy state.
	 * 
	 * @param sourceAgentProxy
	 *            the agent from which copy the state (usually the old - not
	 *            manipulated - proxy instace)
	 */
	public void cloneAgentProxyState(IClonableAgentProxy sourceAgentProxy);

}
