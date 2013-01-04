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
 * See also the following paper for a better introduction to WhiteCat:
 *    L. Ferrari, and H., Zhu,
 *    Autonomous Role Discovery for Collaborating Agents
 *    Software Practice and Experience
 *    2011
 *
 * Copyright (C) Luca Ferrari 2008-2013 - fluca1978 (at) gmail (dot) com
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
import whitecat.core.annotations.Lock;
import whitecat.core.event.EventDispatcher;
import whitecat.core.event.EventType;
import whitecat.core.exceptions.WCProxyLockedException;
import whitecat.core.role.*;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * This aspect drives the proxy storage mechanism, ensuring that
 * each time a proxy is manipulated, the proxy and its agent is
 * inserted in the proxy storage.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public aspect ProxyStorageAspect {

    /**
     * The role repository of the running system.
     */
    private IRoleRepository roleRepository = WhiteCat.getRoleRepository();


    /**
     * A pointcut to intercept the injection of a public role to an agent and its proxy.
     */
    private pointcut addingPublicRole() :  call( public  AgentProxy IRoleBooster.injectPublicRole(WCAgent, AgentProxy, IRole) throws WCException );


    /**
     * A pointcut that intercepts the removal of a public role over an agent and its proxy.
     */
    private pointcut removingPublicRole() : call( public AgentProxy IRoleBooster.removePublicRole( WCAgent, AgentProxy, IRole) throws WCException );



    /**
     * A pointcut to define the locking of a public role manipulation.
     */
    private pointcut lockingPublicRoleForAddition( AgentProxy proxy ) : call( public  AgentProxy IRoleBooster.injectPublicRole(WCAgent, AgentProxy, IRole) throws WCException )
                                                             &&
                                                             args( proxy );


    /**
     * A pointcut for role removal.
     */
    private pointcut lockingPublicRoleForRemoval( AgentProxy proxy ) : call( public AgentProxy IRoleBooster.removePublicRole(WCAgent, AgentProxy, IRole) throws WCException )
    										&&
    									args( proxy );

    /**
     * The pointcut to intercept the execution of a locking method.
     */
    private pointcut avoidLockedMethodInvocation( AgentProxy proxy, Lock lockingAnnotation ) : call(  @Lock public * AgentProxy+.*(..) )		// call to a public method with the @Lock annotation on a proxy (or a subclass of it)
     									&&
     									@annotation(lockingAnnotation)							// pass the annotation
     									&&
     									target( proxy )									// pass the proxy
     									&&
     									(! cflow( execution( * AgentProxy+.*(..) ) ) );									// the method is called from the outside of the proxy


    /**
     * Avoid execution of a locked method.
     * This advice checks if the method invoked aroung a proxy method has been annotated with the Lock annotation, if
     * so the execution must be suspended until the proxy storage notifies that the proxy is no more locked.
     */
    Object around( AgentProxy proxy, Lock lockingAnnotation ) : avoidLockedMethodInvocation( proxy, lockingAnnotation ){
	// if the proxy is locked throw an exception
	IProxyStorage storage = WhiteCat.getProxyStorage();

	// check if the method must block until the proxy is unlocked
	if( lockingAnnotation.blocking().equals("true") ){
	    try{
		// the caller must wait until the proxy has unlocked.
		// I need to acquire a lock on the proxy id
		while( storage.isAgentProxyLocked( proxy ) )
		    storage.lockAgentProxy(proxy,
			                   Boolean.parseBoolean( lockingAnnotation.blocking() ),
			                   lockingAnnotation.maxTimeToWait()
			                   );

		// now proceed with the method call
		return proceed( proxy, lockingAnnotation );
	    } catch (InterruptedException e) {
		throw new WCProxyLockedException( proxy.getAgentProxyID() );
	    }
	}
	else if( storage.isAgentProxyLocked( proxy ) )
	    // non-blocking behavior
	    throw new WCProxyLockedException( proxy.getAgentProxyID() );
	else
	    return proceed( proxy, lockingAnnotation );
    }


    /**
     * Before executing a role manipulation lock the proxy.
     */
    before( AgentProxy proxy ) : lockingPublicRoleForAddition( proxy )
    				||
    				lockingPublicRoleForRemoval( proxy )
    				{
	// get the proxy storage
	IProxyStorage storage = WhiteCat.getProxyStorage();

	// lock the proxy (without locking the current thread)
	storage.lockAgentProxy( proxy, false, -1 );
    }






     /**
      * Manage a public role addition.
      * This advice stores the proxy in the proxy storage, unlocks it (if it was already present and has been
      * locked) and notifies an event about a role manipulation.
      */
     after() returning( AgentProxy retProxy ) : addingPublicRole(){

	 // get the arguments of the join point method call
	 Object arguments[] = thisJoinPoint.getArgs();
	 // extract each argument
	 WCAgent agent = (WCAgent) arguments[0];
	 AgentProxy originalProxy = (AgentProxy) arguments[1];
	 AgentProxyID proxyID = originalProxy.getAgentProxyID();
	 IRole addedRole = (IRole) arguments[2];
	 RoleDescriptor roleDescriptor = roleRepository.getRoleDescriptor(addedRole);


	 // now store the agent proxy in the storage
	 IProxyStorage storage = WhiteCat.getProxyStorage();
	 storage.storeAgentProxy( retProxy );


	 // unlock the proxy (unlocking also the current thread)
	 storage.unlockAgentProxy( retProxy, true );


	 // now perform the notification of events
	 EventDispatcher eventDispatcher = EventDispatcher.getInstance();
	 eventDispatcher.fireEvent( retProxy.getAgentProxyID(), EventType.PUBLIC_ROLE_ADDED, roleDescriptor );
     }


     /**
      * Manage the public role removal.
      * The proxy is stored in the agent proxy storage, then unlocked and an event is notified to all
      * listeners.
      */
     after() returning( AgentProxy retProxy ) : removingPublicRole(){
	 // store the role (updated) in the proxy storage
	 IProxyStorage storage = WhiteCat.getProxyStorage();
	 storage.storeAgentProxy(retProxy);

	 // unlock the proxy (and unlock even the current thread)
	 storage.unlockAgentProxy( retProxy, true );

	// get the arguments of the join point method call
	 Object arguments[] = thisJoinPoint.getArgs();
	 // extract each argument
	 WCAgent agent = (WCAgent) arguments[0];
	 AgentProxy originalProxy = (AgentProxy) arguments[1];
	 AgentProxyID proxyID = originalProxy.getAgentProxyID();
	 IRole removedRole = (IRole) arguments[2];
	 RoleDescriptor roleDescriptor = roleRepository.getRoleDescriptor(removedRole);



	 // now perform the notification of events
	 EventDispatcher eventDispatcher = EventDispatcher.getInstance();
	 eventDispatcher.fireEvent( retProxy.getAgentProxyID(), EventType.PUBLIC_ROLE_REMOVED, roleDescriptor );
     }


     /**
      * Manage the public role removal just before it happens.
      * The proxy is stored in the agent proxy storage, then unlocked and an event is notified to all
      * listeners.
      */
     before()  : removingPublicRole(){

	// get the arguments of the join point method call
	 Object arguments[] = thisJoinPoint.getArgs();
	 // extract each argument
	 WCAgent agent = (WCAgent) arguments[0];
	 AgentProxy originalProxy = (AgentProxy) arguments[1];
	 AgentProxyID proxyID = originalProxy.getAgentProxyID();
	 IRole removedRole = (IRole) arguments[2];
	 RoleDescriptor roleDescriptor = roleRepository.getRoleDescriptor(removedRole);



	 // now perform the notification of events
	 EventDispatcher eventDispatcher = EventDispatcher.getInstance();
	 eventDispatcher.fireEvent( proxyID, EventType.PUBLIC_ROLE_REMOVING, roleDescriptor );
     }


}
