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

import org.aspectj.lang.JoinPoint;

import whitecat.core.*;
import whitecat.core.agents.*;
import whitecat.core.annotation.Lock;
import whitecat.core.event.EventDispatcher;
import whitecat.core.event.EventType;
import whitecat.core.exceptions.WCProxyLockedException;
import whitecat.core.role.*;

/**
 * This aspect drives the proxy storage mechanism, ensuring that
 * each time a proxy is manipulated, the proxy and its agent is 
 * inserted in the proxy storage.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public aspect ProxyStorageAspect {

									
    
    /**
     * A pointcut to intercept the injection of a public role to an agent and its proxy.
     */
    private pointcut addingPublicRole() :  call( public final synchronized AgentProxy injectPublicRole(WCAgent, AgentProxy, IRole) throws WCException );
									

    /**
     * A pointcut that intercepts the removal of a public role over an agent and its proxy.
     */
    private pointcut removingPublicRole() : call( public AgentProxy RoleBooster.removePublicRole( WCAgent, AgentProxy, IRole) throws WCException );
    

    
    /**
     * A pointcut to define the locking of a public role manipulation.
     */
    private pointcut lockingPublicRoleForAddition( AgentProxy proxy ) : call( public final synchronized AgentProxy injectPublicRole(WCAgent, AgentProxy, IRole) throws WCException )
                                                             &&
                                                             args( proxy );
    
    
    /**
     * A pointcut for role removal.
     */
    private pointcut lockingPublicRoleForRemoval( AgentProxy proxy ) : call( public final synchronized AgentProxy removePublicRole(WCAgent, AgentProxy, IRole) throws WCException )
    										&&
    									args( proxy );

    /**
     * The pointcut to intercept the execution of a locking method.
     */
    private pointcut avoidLockedMethodInvocation( AgentProxy proxy, Lock lockingAnnotation ) : call( public @Lock * AgentProxy+.*(..) )
     									&&
     									@annotation(lockingAnnotation)
     									&&
     									target( proxy );
    
    
    /**
     * Avoid execution of a locked method.
     */
    Object around( AgentProxy proxy, Lock lockingAnnotation ) : avoidLockedMethodInvocation( proxy, lockingAnnotation ){
	// if the proxy is locked throw an exception
	ProxyStorage storage = ProxyStorage.getInstance();
	
	// check if the method must block until the proxy is unlocked
	if( lockingAnnotation.blocking().equals("true") ){
	    try{
		// the caller must wait until the proxy has unlocked.
		// I need to acquire a lock on the proxy id
		AgentProxyID proxyID = proxy.getAgentProxyID();
		while( storage.isAgentProxyLocked(proxyID) )
		    LockManager.getInstance().wait(proxyID);

		// now proceed with the method call
		return proceed( proxy, lockingAnnotation );
	    } catch (InterruptedException e) {
		throw new WCProxyLockedException( proxy.getAgentProxyID() );
	    }
	}
	else if( storage.isAgentProxyLocked( proxy.getAgentProxyID() ) )
	    // non-blocking behavior
	    throw new WCProxyLockedException( proxy.getAgentProxyID() );
	else
	    return proceed( proxy, lockingAnnotation );
    }
    
    
    /**
     * Before executing a role manipulation lock the proxy.
     */
    before( AgentProxy proxy ) : lockingPublicRoleForAddition( proxy ){
	// get the proxy storage
	ProxyStorage storage = ProxyStorage.getInstance();
	
	// lock the proxy
	storage.lockAgentProxy( proxy.getAgentProxyID() );
    }
    
    
    /**
     * Before removing a role lock the proxy.
     * @param proxy
     */
    before( AgentProxy proxy ) : lockingPublicRoleForRemoval( proxy ){
	// get the proxy storage
	ProxyStorage storage = ProxyStorage.getInstance();
	
	// lock the proxy
	storage.lockAgentProxy( proxy.getAgentProxyID() );
    }
    
    
    
    
     /**
      * Manage a public role addition.
      */
     after() returning( AgentProxy retProxy ) : addingPublicRole(){
	 // perform basic operation for adding a role: store the role in the map
	 this.storeRoleAddition( thisJoinPoint, retProxy );
	 
	 // unlock the proxy
	 ProxyStorage storage = ProxyStorage.getInstance();
	 storage.unlockAgentProxy( retProxy.getAgentProxyID() );

	 
	 // now perform the notification of events
	 EventDispatcher eventDispatcher = EventDispatcher.getInstance();
	 eventDispatcher.fireEvent( retProxy.getAgentProxyID(), EventType.PUBLIC_ROLE_ADDED );
     }
     
     
     /**
      * Manage the public role removal.
      */
     after() returning( AgentProxy retProxy ) : removingPublicRole(){
	 // perform basic operation for adding a role: store the role in the map
	 this.storeAgentProxyUpdate( retProxy.getAgentProxyID(), retProxy );
	 
	 // unlock the proxy
	 ProxyStorage storage = ProxyStorage.getInstance();
	 storage.unlockAgentProxy( retProxy.getAgentProxyID() );

	 
	 // now perform the notification of events
	 EventDispatcher eventDispatcher = EventDispatcher.getInstance();
	 eventDispatcher.fireEvent( retProxy.getAgentProxyID(), EventType.PUBLIC_ROLE_REMOVED );
     }
     
     
     /**
      * After a method call to add a role, this method stores the agent proxy in
      * the storage map extracting the parameters from the join point object.
      * @param jointPoint the join point of the method call (with the method arguments)
      * @param retProxy the proxy that is going to be returned by the join point method call
      */
     private void storeRoleAddition( JoinPoint jointPoint, AgentProxy retProxy ){
	 // get the arguments of the join point method call
	 Object arguments[] = jointPoint.getArgs();
	 // extract each argument
	 WCAgent agent = (WCAgent) arguments[0];
	 AgentProxy originalProxy = (AgentProxy) arguments[1];
	 AgentProxyID proxyID = originalProxy.getAgentProxyID();
	 
	 // now store the data about the agent proxy
	 this.storeAgentProxyUpdate(proxyID, retProxy);
     }
     
     
     /**
      * Stores the specified proxy in the storage map associating it to the specified proxy id.
      * @param proxyID the proxy id to use as key
      * @param proxy the proxy to store
      */
     private void storeAgentProxyUpdate( AgentProxyID proxyID, AgentProxy proxy ){
	 // remove the old proxy from the map
	 ProxyStorage storage = ProxyStorage.getInstance();
	 storage.remove( proxyID );
	 // now store the new agent proxy
	 storage.put( proxyID, proxy );
	 
     }
}
