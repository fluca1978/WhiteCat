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

import java.util.HashMap;
import java.util.Map;

import whitecat.core.agents.AgentProxyID;

/**
 * A generic lock manager to handle locks (in the Java meaning)
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class LockManager {

    /**
     * A map with each object associate to an id.
     */
    private Map<Long, Object > locks = new HashMap<Long, Object>();
    
    
    /**
     * A singleton reference to myself.
     */
    private static LockManager mySelf = null;
    
    
    /**
     * Get the singleton instance of the lock manager.
     * @return
     */
    public synchronized static LockManager getInstance(){
	if( mySelf == null )
	    mySelf = new LockManager();
	
	return mySelf;	 
    }
    
    
    /**
     * Lock the thread for the specified proxy.
     * @param proxyid
     * @throws InterruptedException
     */
    public synchronized void wait( AgentProxyID proxyid ) throws InterruptedException{
	long sequenceNumber = proxyid.getSequenceID();
	
	if( ! this.locks.containsKey( sequenceNumber ) )
	    this.locks.put( sequenceNumber, new Object() );
	   
	// lock the thread
	this.locks.get( sequenceNumber ).wait();
	
    }
    
    /**
     * Unlock all the threads for the specified proxy id.
     * @param proxyid
     */
    public synchronized void notifyAll( AgentProxyID proxyid ){
	long sequenceNumber = proxyid.getSequenceID();
	
	if( ! this.locks.containsKey( sequenceNumber ) )
	    return;
	
	// unlock the thread
	this.locks.get( sequenceNumber ).notifyAll();
    }
}
