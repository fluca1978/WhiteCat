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
package whitecat.core.lock;

import whitecat.core.agents.AgentProxy;

/**
 * This class is a wrapper around the status of an agent proxy, such as its locking status,
 * its statistics about manipulations and so on. It is used by the storage to keep in sync
 * the status of an agent proxy.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class AgentProxyStatus {

    /**
     * The proxy (last updated) this status is referred to.
     */
    private AgentProxy proxy = null;
    
    /**
     * The number of times this proxy has been locked, that is how many locks it is handling at the moment.
     * (this is not a statistic information)
     */
    private int lockCount = 0;
    
    /**
     * The number of times this proxy has been manipulated, i.e., how many time this proxy has requested
     * an operation to the role booster.
     */
    private int manipulationCount = 0;
    
    
    /**
     * An object to lock on when a proxy method is invoked but cannot proceed.
     */
    private Object lockingObject = new Object();
    
    
    /**
     * Default constructor: it is possible to build a proxy status only having a proxy to wrap into.
     * @param proxy the proxy this status now refers to
     */
    private AgentProxyStatus( AgentProxy proxy ){
	super();
	this.proxy = proxy;
    }
    
    
    
    
    /**
     * Construct a new agent proxy status for the specified proxy.
     * @param proxy the proxy to wrap in the agent status
     * @return the agent proxy status wrapper
     */
    public static AgentProxyStatus newInstance( AgentProxy proxy ){
	    return new AgentProxyStatus( proxy );
    }
    

    /**
     * Provides the value of the lockCount field. If this value is greater than zero, it means that the proxy
     * is currently locked by something.
     * @return the lockCount
     */
    public synchronized final int getLockCount() {
        return this.lockCount;
    }


    /**
     * Provides the value of the manipulationCount field. It is a statistic field, so that it counts all
     * the manipulation this proxy has overtook.
     * @return the manipulationCount
     */
    public synchronized final int getManipulationCount() {
        return this.manipulationCount;
    }


    /**
     * Sets the value of the proxy field as specified
     * by the value of proxy.
     * @param proxy the proxy to set
     */
    public synchronized final void setProxy(AgentProxy proxy) {
        this.proxy = proxy;
        this.unlockAll();		// a new proxy should not be locked!
    }
    
    
    /**
     * Increments the manipulation count.
     */
    public synchronized final void incrementManipulationCount(){
	this.manipulationCount++;
    }
    
    /**
     * Increments the lock count, that is another thread/agent is locking this proxy.
     */
    public synchronized final void incrementLockCount(){
	this.lockCount++;
    }
    
    /**
     * Decrement the lock count, that is a thread is no more locking this proxy.
     */
    public synchronized final void decrementLockCount(){
	this.lockCount--;
	if( this.lockCount <= 0 )
	    this.notifyAll();
    }
    
    
    /**
     * This proxy is locked if the lock count is greater than zero.
     * @return true if the proxy has been locked and not yet unlocked
     */
    public synchronized final boolean isLocked(){
	return (this.lockCount > 0);
    }
    
    /**
     * Removes all the locks on this proxy.
     */
    public final void unlockAll(){
	synchronized ( this.lockingObject) {
	    this.lockCount = 0;
	    this.lockingObject.notifyAll();
	}
    }




    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer( 50 );
	buffer.append( this.proxy );
	buffer.append( " " );
	buffer.append( "locks = ");
	buffer.append( this.lockCount );
	buffer.append( " " );
	buffer.append( "manipulations = " );
	buffer.append( this.manipulationCount );
	return buffer.toString();
    }
    
    
    
    /**
     * Provides the value of the proxy field. If the current status is locked, than the calling thread
     * is suspended waiting for the status to become unlocked. This means that this method call is blocking!
     * @return the proxy
     */
    public final AgentProxy getProxy() {
	// if this status is locked, cannot return the agent proxy, so lock the calling thread
	try{
	    synchronized( this.lockingObject ){
		while( this.lockCount > 0 )
		    this.lockingObject.wait();
	    }
	}catch(InterruptedException e){
	    e.printStackTrace();
	}
	
        return this.proxy;
    }




    /**
     * Locks this agent proxy on its locking object.
     * The locking counter is incremented.
     */
    public void lock(){
	try{
	    synchronized( this.lockingObject ){
		this.incrementLockCount();
		this.lockingObject.wait();
	    }
	}catch(InterruptedException e){
	    e.printStackTrace();
	}
    }
    
    
    /**
     * Locks the current thread caller for the specified time.
     * @param thresold the max time to lock for
     */
    public void lock( long thresold ){
	try{
	    synchronized ( this.lockingObject) {
		this.incrementLockCount();
		this.lockingObject.wait( thresold );
	    }
	}catch(InterruptedException e){
	    e.printStackTrace();
	}
    }
    
    
 
    
}
