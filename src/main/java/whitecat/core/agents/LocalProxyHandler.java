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
package whitecat.core.agents;

import whitecat.core.IProxyHandler;
import whitecat.core.exceptions.WCProxyException;

/**
 * A local proxy handler for handling source and destination proxies in a local
 * (i.e., not networked environment).
 * 
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
public class LocalProxyHandler implements IProxyHandler<LocalAgentProxy> {

	/**
	 * The source and destination proxy handled by this proxy handler.
	 */
	private LocalAgentProxy	sourceProxy			= null;
	private LocalAgentProxy	destinationProxy	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.ProxyHandler#setDestinationProxy(whitecat.core.agents.
	 * AgentProxy)
	 */
	public void setDestinationProxy(final LocalAgentProxy destination)
																		throws WCProxyException {

		// cannot set the source proxy if already set
		if (destinationProxy != null)
			throw new WCProxyException(
					"Cannot set the destination proxy multiple times on the same proxy handler. Hint: create a new proxy handler." );

		// cannot use a null proxy
		if (destination == null)
			throw new WCProxyException( "Cannot use a null destination proxy." );

		// set the proxy
		destinationProxy = destination;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.ProxyHandler#setSourceProxy(whitecat.core.agents.AgentProxy
	 * )
	 */
	public void setSourceProxy(final LocalAgentProxy source)
															throws WCProxyException {
		// cannot set the source proxy if already set
		if (sourceProxy != null)
			throw new WCProxyException(
					"Cannot set the source proxy multiple times on the same proxy handler. Hint: create a new proxy handler." );

		// cannot use a null proxy
		if (source == null)
			throw new WCProxyException( "Cannot use a null source proxy." );

		// set the proxy
		sourceProxy = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.ProxyHandler#updateProxy()
	 */
	public boolean updateProxy() throws WCProxyException {
		// check if both the proxy are in the right place
		if ((sourceProxy == null) || (destinationProxy == null))
			throw new WCProxyException(
					"Source or destination proxies are not set yet!" );

		// do the copy here
		destinationProxy.setMyAgent( sourceProxy.getMyAgent() );
		destinationProxy.initializeByCopy( sourceProxy );

		// if the old proxy instance is clonable, clone it now!
		// Please note that if the original agent proxy is clonable, also the
		// new one must be, but we check
		// for it in the case something in the manipulation process has going
		// bad!
		if ((sourceProxy instanceof IClonableAgentProxy)
				&& (destinationProxy instanceof IClonableAgentProxy))
			((IClonableAgentProxy) sourceProxy)
					.cloneAgentProxyState( (IClonableAgentProxy) destinationProxy );

		return true;
	}

}
