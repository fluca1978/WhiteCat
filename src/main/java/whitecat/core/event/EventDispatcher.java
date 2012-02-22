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
 * Copyright (C) Luca Ferrari 2006-2012 - fluca1978 (at) gmail.com
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
package whitecat.core.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import whitecat.core.agents.AgentProxyID;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * The event dispatcher, dispatches events to the components that have
 * registered for listening specific events.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class EventDispatcher {

	/**
	 * Singleton reference.
	 */
	private static EventDispatcher	mySelf	= null;

	/**
	 * Creates an instance of the event dispatcher.
	 * 
	 * @return the event dispatcher instance
	 */
	public synchronized static EventDispatcher getInstance() {
		if (mySelf == null)
			mySelf = new EventDispatcher();

		return mySelf;
	}

	/**
	 * A map with the registered listeners, the index is on the AgentProxyID.
	 */
	private final HashMap<AgentProxyID, List<EventListener>>	registeredListeners		= new HashMap<AgentProxyID, List<EventListener>>();

	/**
	 * A list of listener that will be notified for each event, no matter what
	 * agent proxy id is generating the event.
	 */
	private final List<EventListener>							globalEventListeners	= new LinkedList<EventListener>();

	/**
	 * Adds a new event listener to the global map of the listeners. Each
	 * listener is interested in a set of proxyID.
	 * 
	 * @param proxyID
	 *            the proxy id on which the event listener wants to wait for
	 *            events
	 * @param listener
	 *            the listener to add for the specified events
	 * @return true if the listener has been registered, false otherwise
	 */
	public synchronized boolean addEventListener(final AgentProxyID proxyID,
													final EventListener listener) {
		// check arguments
		if ((proxyID == null) || (listener == null))
			return false;

		// extract the list of the registerd users
		List<EventListener> currentListeners = null;
		if (registeredListeners.containsKey( proxyID ))
			currentListeners = registeredListeners.get( proxyID );
		else currentListeners = new LinkedList<EventListener>();

		// check if the current listener is already included in the list
		// and if not insert it
		if (currentListeners.contains( proxyID ))
			return false;
		else{
			currentListeners.add( listener );
			// place the new list in the map
			registeredListeners.put( proxyID, currentListeners );
			return true;
		}
	}

	/**
	 * Adds a global event listener, that is a listener that will be notified
	 * for each role event no matter what is the agent and the proxy the event
	 * is related to.
	 * 
	 * @param listener
	 *            the listener to add
	 * @return true if the listener is added
	 */
	public final synchronized boolean addGlobalEventListener(	final EventListener listener) {
		if (!globalEventListeners.contains( listener )){
			globalEventListeners.add( listener );
			return true;
		}else return false;
	}

	/**
	 * Notifies the listeners registered for a specific proxy of an event about
	 * a change in a proxy or an agent. If a set of global listeners is present,
	 * they are notified too.
	 * 
	 * @param proxyID
	 *            the id of the proxy
	 * @param type
	 *            the type of the event
	 * @param roleDescriptor
	 *            the role descriptor the firing event refers to
	 * @return the number of listeners notified (including the global ones).
	 */
	public synchronized int fireEvent(final AgentProxyID proxyID,
										final EventType type,
										final RoleDescriptor roleDescriptor) {
		// check arguments
		if ((proxyID == null)
				|| (type == null)
				|| (registeredListeners.isEmpty() && globalEventListeners
						.isEmpty()))
			return 0;

		// the counter of the notified events
		int notified = 0;

		if (!registeredListeners.isEmpty()){
			for (final EventListener currentListener : registeredListeners
					.get( proxyID )){
				// create a new event
				final Event event = Event.createEvent(
						proxyID,
						type,
						roleDescriptor );
				// notify the listener
				currentListener.handleEvent( event );
				// increment the counter of the notified listener
				notified++;
			}
		}

		// notify also the global event listener
		for (final EventListener currentListener : globalEventListeners){
			// create a new event
			final Event event = Event.createEvent(
					proxyID,
					type,
					roleDescriptor );
			// notify the listener
			currentListener.handleEvent( event );
			// increment the counter of the notified listener
			notified++;
		}

		// all done
		return notified;
	}

	/**
	 * Removes the specified listener for the specified proxy id.
	 * 
	 * @param proxyID
	 *            the proxy id to which the listener should be associated
	 * @param listener
	 *            the listener to remove
	 * @return true if the listener has been deregistered, false if the listener
	 *         is not associated yet to the proxy id
	 */
	public synchronized boolean removeEventListener(final AgentProxyID proxyID,
													final EventListener listener) {
		// check arguments
		if ((proxyID == null) || (listener == null)
				|| (registeredListeners == null)
				|| registeredListeners.containsKey( proxyID ))
			return false;

		// get the list from the map for the proxy id
		final List<EventListener> listeners = registeredListeners.get( proxyID );

		// check if the list is null and contains the proxy id
		if ((listeners == null) || (!listeners.contains( proxyID )))
			return false;

		// if here the list is valid and contains the listener, so remove it
		listeners.remove( listener );
		return true;

	}

	/**
	 * Removes a global event listener from the queue.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @return true if the listener is removed
	 */
	public final synchronized boolean removeGlobalEventListener(final EventListener listener) {
		if (!globalEventListeners.contains( listener ))
			return false;
		else{
			globalEventListeners.remove( listener );
			return true;
		}
	}

}
