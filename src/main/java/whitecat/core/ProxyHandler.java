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
package whitecat.core;

import whitecat.core.agents.AgentProxy;
import whitecat.core.exceptions.WCProxyException;

/**
 * This interface is used to safely and consistently handling a couple of
 * proxies, that is the proxy before a role manipulation and a proxy after a
 * role manipulation. This interface is used by the role engine to update the
 * proxy after the manipulation to the proxy before the manipulation. Instances
 * of this interface can provide different update policies.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public interface ProxyHandler<PH extends AgentProxy> {

	/**
	 * Stores the proxy destination, that is a proxy that will be updated with
	 * the state of the proxy stored as a source.
	 * 
	 * @param destination
	 *            the proxy that will be later updated
	 * @throws WCProxyException
	 *             if the proxy is not valid (e.g., is null)
	 */
	public void setDestinationProxy(PH destination) throws WCProxyException;

	/**
	 * Stores the source proxy to the proxy handler.
	 * 
	 * @param source
	 *            the proxy that must be handled as source, that is the proxy
	 *            that must be used to update another proxy
	 * @throws WCProxyException
	 *             if the proxy is not valid (e.g., null) or the proxy has been
	 *             assigned several times as source (only one source assignement
	 *             should be granted).
	 */
	public void setSourceProxy(PH source) throws WCProxyException;

	/**
	 * Updates the proxy marked as destination with the state of the proxy
	 * marked as source. It is strongly recommended that the implementation of
	 * the proxy handler is in the same package (or in some ways has access) to
	 * the proxy private fields, so that it can easily access the fields and the
	 * state of the source proxy and can copy it to the destination proxy.
	 * Another way is to insert a method within the proxy class that allows a
	 * deeper copy of the proxy state (something like the clone method).
	 * <B>Please note that this method should take care of ICloneableAgentProxy
	 * interface and should invoke the clone method if the proxies are
	 * cloneable.</B>
	 * 
	 * @return true if the update has been performed, false otherwise
	 * @throws WCProxyException
	 *             if something goes wrong during the copy
	 */
	public boolean updateProxy() throws WCProxyException;
}
