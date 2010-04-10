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
package whitecat.core.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import whitecat.core.agents.AgentProxyID;

/**
 * The event dispatcher, dispatches events to the components that have registered
 * for listening specific events.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class EventDispatcher {

    
    /**
     * Singleton reference.
     */
    private static EventDispatcher mySelf = null;
    
    
    
    /**
     * A map with the registered listeners, the index is 
     * on the AgentProxyID.
     */
    private HashMap< AgentProxyID, List<EventListener> > registeredListeners = new HashMap< AgentProxyID, List<EventListener> >();
    
    
    
    /**
     * Creates an instance of the event dispatcher.
     * @return the event dispatcher instance
     */
    public synchronized static EventDispatcher getInstance(){
	if( mySelf == null )
	    mySelf = new EventDispatcher();
	
	return mySelf;
    }
    
    
    
    
    
    
    
    /**
     * Adds a new event listener to the global map of the listeners. Each listener is
     * interested in a set of proxyID.
     * @param proxyID the proxy id on which the event listener wants to wait for events
     * @param listener the listener to add for the specified events
     * @return true if the listener has been registered, false otherwise
     */
    public synchronized boolean addEventListener(AgentProxyID proxyID, EventListener listener){
	// check arguments
	if( proxyID == null || listener == null )
	    return false;
	
	// extract the list of the registerd users
	List<EventListener> currentListeners = null;
	if( this.registeredListeners.containsKey( proxyID ) )
	    currentListeners = this.registeredListeners.get( proxyID );
	else
	    currentListeners = new LinkedList<EventListener>();
	
	// check if the current listener is already included in the list
	// and if not insert it
	if( currentListeners.contains(proxyID) )
	    return false;
	else{
	    currentListeners.add( listener );
	    // place the new list in the map
	    this.registeredListeners.put(proxyID, currentListeners);
	    return true;
	}
    }
    
    
    /**
     * Removes the specified listener for the specified proxy id.
     * @param proxyID the proxy id to which the listener should be associated
     * @param listener the listener to remove
     * @return true if the listener has been deregistered, false if the listener is
     * not associated yet to the proxy id
     */
    public synchronized  boolean removeEventListener( AgentProxyID proxyID, EventListener listener ){
	// check arguments
	if( proxyID == null || listener == null
	 || this.registeredListeners == null
	 || this.registeredListeners.containsKey( proxyID ) )
	    return false;
	
	// get the list from the map for the proxy id
	List<EventListener> listeners = this.registeredListeners.get( proxyID );
	
	// check if the list is null and contains the proxy id
	if( listeners == null || (! listeners.contains(proxyID)) )
	    return false;
	
	// if here the list is valid and contains the listener, so remove it
	listeners.remove( listener );
	return true;
	
    }
    
    
    /**
     * Notifies the listeners registered for a specific proxy of
     * an event about a change in a proxy or an agent.
     * @param proxyID the id of the proxy
     * @param type the type of the event
     * @return the number of listeners notified.
     */
    public int fireEvent( AgentProxyID proxyID, EventType type ){
	// check arguments
	if( proxyID == null || type == null
	   || this.registeredListeners == null
	   || this.registeredListeners.containsKey( proxyID ) == false)
	    return 0;
	
	// the counter of the notified events
	int notified = 0;
	
	for( EventListener currentListener : this.registeredListeners.get( proxyID ) ){
	    // create a new event
	    Event event = Event.createEvent(proxyID, type);
	    // notify the listener
	    currentListener.handleEvent(event);
	    // increment the counter of the notified listener
	    notified ++;
	}
	
	
	// all done
	return notified;
    }
    
    
}