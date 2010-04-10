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
import java.util.HashMap;
import java.util.*;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.AgentProxyID;
import whitecat.core.agents.WCAgent;

/**
 * This class represents a map for storing the last proxy
 * update for a specific agent.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class ProxyStorage extends HashMap<AgentProxyID, AgentProxy>{

    /**
     * A reference to myself, so that this class is used as singleton.
     */
    private static ProxyStorage mySelf = null;
    
    
    /**
     * The lock map contains a counter for the locking of role assumption operations.
     * There is a counter instead of a boolean flag to allow nested role assumptions/releases.
     */
    private Map<AgentProxyID, Integer> lockMap = null;
    
    /**
     * Creates a new proxy storage.
     */
    private ProxyStorage(){
	super();
	
	// initialize the role lock counter map
	this.lockMap = new HashMap<AgentProxyID, Integer>();
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
     * @param proxyToLock
     */
    public final  synchronized void  lockAgentProxy( AgentProxyID proxyToLock ){
	// check params
	if( proxyToLock == null )
	    return;
	
	// get the current value of the counter (if it exists)
	int lockingCounter = 0;
	if( this.lockMap.containsKey(proxyToLock) )
	    lockingCounter = this.lockMap.get(proxyToLock);
	
	// increment the locking counter
	lockingCounter++;
	
	// place the locking counter
	this.lockMap.put(proxyToLock, lockingCounter );
	
    }
    
    /**
     * A mehtod to unlock the specified proxy (i.e., to decrease the locking counter).
     * @param proxyToUnlock
     */
    public final synchronized void unlockAgentProxy( AgentProxyID proxyToUnlock ){
	// if the proxy is in the map decrease the locking counter
	if( this.lockMap.containsKey(proxyToUnlock) )
	    this.lockMap.put( proxyToUnlock, this.lockMap.get(proxyToUnlock) - 1 );
	
	// unlock thread that have locked on the proxy id
	LockManager.getInstance().notifyAll(proxyToUnlock);
    }
    
    /**
     * A proxy is locked if the locking counter is greater than zero.
     * @param proxyToCheck
     * @return
     */
    public synchronized final  boolean isAgentProxyLocked( AgentProxyID proxyToCheck ){
	if( ! this.lockMap.containsKey(proxyToCheck) )
	    return false;
	else
	    return this.lockMap.get(proxyToCheck) > 0;
    }
    
    
    
    /**
     * Dumps the content of the map.
     * @param os the output stream to use.
     */
    public synchronized void dump(PrintStream os){
	for(AgentProxyID currentID : this.keySet() )
	    os.println("- " + currentID + " -> " + this.get( currentID ) );
    }
}
