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
package whitecat.core;

import java.io.OutputStream;

import java.io.PrintStream;
import java.util.*;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.AgentProxyID;
import whitecat.core.agents.WCAgent;
import whitecat.core.lock.AgentProxyStatus;

/**
 * This class represents a map for storing the last proxy
 * update for a specific agent.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class ProxyStorage {

    /**
     * A reference to myself, so that this class is used as singleton.
     */
    private static ProxyStorage mySelf = null;
    
    
    
    
    /**
     * A map with the status of all agents. The map is indexed by the agent proxy ID, that should not change
     * during each manipulation. The AgentProxyStatus object contains informations about the lock and the
     * last proxy update.
     */
    private Map<AgentProxyID, AgentProxyStatus> proxyMap = null;
    
    
    
   
    /**
     * Creates a new proxy storage.
     */
    private ProxyStorage(){
	super();
	
	// initialize the map of the agent proxies
	this.proxyMap = new HashMap<AgentProxyID, AgentProxyStatus>();
    }
    
    
    /**
     * Gets the shared instance of the proxy storage, or create a new one and share
     * it whitin the application.
     * @return the proxy storage.
     */
    public synchronized static ProxyStorage getInstance(){
	if( mySelf == null )
	    mySelf = new ProxyStorage();
	
	return mySelf;
    }
    
    
    /**
     * A method to lock the specified proxy. The method increases the locking counter
     * of the specified proxy.
     * @param proxyToLock the proxy to lock
     * @param lockCurrentThread true if the current thread must be locked now, false if only the status
     * of the agent proxy must be set to locked.
     * @param timeToLock the max amount of time to lock the thread if the lockCurrentThread flag is true. If a zero value
     * is passed the thread waits undefinitely.
     */
    public final  synchronized void  lockAgentProxy( AgentProxy proxyToLock, boolean lockCurrentThread, long timeToLock ){
	// check params
	if( proxyToLock == null )
	    return;
	
	// get the id of this proxy
	AgentProxyID id = proxyToLock.getAgentProxyID();
	
	// get the current status for the proxy id
	AgentProxyStatus status = null;
	if( this.proxyMap.containsKey( id ) )
	    status = this.proxyMap.get( id );
	else{
	    // WARNING: if here there is a map mismatch: an agent proxy is not presence
	    // in the storage map!!!!
	    status = AgentProxyStatus.newInstance( proxyToLock );
	    this.proxyMap.put( id , status);
	}
	
	assert( status == null );	// should never happen
	
	
	
	// now lock the thread
	if( lockCurrentThread )
	    if( timeToLock > 0 )
		status.lock( timeToLock );
	    else
		status.lock();
	else
	    status.incrementLockCount();
    }
    
    /**
     * A mehtod to unlock the specified proxy (i.e., to decrease the locking counter).
     * @param proxyToUnlock the proxy to lock
     * @param unlockThread true if the current thread must be unlocked
     */
    public final synchronized void unlockAgentProxy( AgentProxy proxyToUnlock, boolean unlockThread ){
	// check arguments
	if( proxyToUnlock == null || ! this.proxyMap.containsKey(proxyToUnlock.getAgentProxyID()) )
	    return;
	
	// get the id of this proxy
	AgentProxyID id = proxyToUnlock.getAgentProxyID();
	
	
	// get the current status
	AgentProxyStatus status = this.proxyMap.get( id );
	assert( status == null );		// should never happen
	
	
	// now unlock the agent proxy
	if( unlockThread )
	    status.unlockAll();
	else
	    status.decrementLockCount();
    }
    
    /**
     * A proxy is locked if the locking counter is greater than zero.
     * @param proxyToCheck
     * @return
     */
    public synchronized final  boolean isAgentProxyLocked( AgentProxy proxyToCheck ){
	// check arguments
	if( proxyToCheck == null || proxyToCheck.getAgentProxyID() == null )
	    return false;
	
	// get the agent proxy id
	AgentProxyID id = proxyToCheck.getAgentProxyID();
	
	
	if( ! this.proxyMap.containsKey(id) )
	    return false;
	else
	    return this.proxyMap.get(id).isLocked();
    }
    
    
    
    /**
     * Dumps the content of the map.
     * @param os the output stream to use.
     */
    public synchronized void dump(PrintStream os){
	for(AgentProxyID currentID : this.proxyMap.keySet() )
	    os.println("- " + currentID + " -> " + this.proxyMap.get( currentID ) );
    }
    
    
    /**
     * Adds a new agent proxy into the map creating a new status if needed.
     * @param proxy the agent proxy to store
     */
    public synchronized final void storeAgentProxy( AgentProxy proxy ){
	// check arguments
	if( proxy == null || proxy.getAgentProxyID() == null )
	    return;
	
	// get the agent proxy id
	AgentProxyID id = proxy.getAgentProxyID();
	
	// see if the agent proxy has been already stored in the map and get the status or
	// create a new status
	AgentProxyStatus status = null;
	if( this.proxyMap.containsKey(id) ){
	    status = this.proxyMap.get( id );
	    status.setProxy(proxy);
	}
	else{
	    status = AgentProxyStatus.newInstance(proxy);
	    this.proxyMap.put(id, status);
	}
    }
    
    
    /**
     * A method to increment the manipulation counter for a specified proxy.
     * @param proxy the proxy that has been manipulated
     * @return the actual number of manipulation this proxy has done, or -1 if the proxy is not yet stored in the map
     */
    public synchronized final int incrementManipulationCount( AgentProxy proxy ){
	// check arguments
	if( proxy == null || proxy.getAgentProxyID() == null || ! this.proxyMap.containsKey(proxy.getAgentProxyID()) )
	    return -1;
	
	// get the status
	AgentProxyStatus status = this.proxyMap.get( proxy.getAgentProxyID() );
	assert( status == null );
	
	// increment the manipulation count
	status.incrementLockCount();
	
	// return the current lock count
	return status.getLockCount();
    }
    
    
    /**
     * Removes a proxy from the storage map. This is useful if the proxy has been destroyed.
     * Before removing the proxy, the method unlocks the proxy so that waiters can be notified.
     * @param proxy the proxy to remove
     */
    public synchronized final void deleteAgentProxy( AgentProxy proxy ){
	// check arguments
	if( proxy == null || proxy.getAgentProxyID() == null )
	    return;
	
	// get the agent proxy id
	AgentProxyID id = proxy.getAgentProxyID();
	
	// remove the agent proxy from the map
	// but before that try to unlock waiters
	if( this.proxyMap.containsKey(id) )
	    this.proxyMap.get(id).unlock();
	
	
	this.proxyMap.remove(id);
	
    }
    
    
    /**
     * Provides the last updated proxy in the storage for the specified id.
     * @param id
     * @return
     */
    public synchronized AgentProxy getLastUpdatedAgentProxy( AgentProxyID id ){	
	// check arguments
	if( id == null || this.proxyMap.containsKey(id) )
	    throw new IllegalArgumentException("Cannot get a null-id agent proxy, or the agent proxy is not in the map!");
	
	// this is a blocking call on the status object!
	return this.proxyMap.get(id).getProxy();
	
	
	
    }
}
