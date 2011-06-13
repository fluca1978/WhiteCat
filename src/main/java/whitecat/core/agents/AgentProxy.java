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

import java.util.HashMap;

import whitecat.core.event.EventDispatcher;
import whitecat.core.event.EventListener;
import whitecat.core.role.IRole;

/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public abstract class AgentProxy {

    /**
     * This method is called every time that the owner of a proxy requires an update
     * on the proxy itself. What this method does is to return a new instance of
     * the proxy with the same status the proxy is at that moment. In other words, each time
     * this method is called, and the proxy has been manipulated by the role engine, this method
     * must return a new instance of the agent proxy with the manipulation done and the
     * status at the same point in time of when the method has been called.
     * 
     * The adoption of this clone-approach simplifies the instantiation of a new manipulated proxy
     * when required. 
     * 
     * <B>Please note that this method could provide a critical section, since the method could be
     * called while the proxy is changing and the owner of the proxy could still get the old (not manipulated)
     * instance of the proxy.</B>
     * 
     * It is strongly raccomended to provide an hidden clone mechanism of the proxy and to
     * store the prototype of the proxy within the proxy itself.
     * @return the update proxy
     */
    public abstract AgentProxy update();
  
    
    /**
     * The proxy id of this agent proxy. It must be unique
     * along the platform.
     */
    private AgentProxyID agentProxyID = AgentProxyID.getNextAgentProxyID();
    
    /**
     * Provides the identification of this proxy.
     * @return the agent proxy id.
     */
    public final AgentProxyID getAgentProxyID(){
	return this.agentProxyID;
    }
  
    /**
     * This method copies the agent proxy id. Even if this method could be overriden,
     * it should always be called from subclasses in order to ensure the copy of the proxy
     * id in the current (i.e., manipulated) proxy.
     * @param proxy the proxy from which the id must be copied from.
     */
    public void initializeByCopy(AgentProxy proxy) {
	// copy the proxy id
	this.agentProxyID = AgentProxyID.createByCopy( proxy.getAgentProxyID() );
    }
    
    /**
     * Attaches an event listener to changes related to this proxy.
     * @param listener the listener that will be notified about changes applied to this proxy
     * @return true if the listener has been added
     */
    public final boolean addEventListener(EventListener listener){
	return EventDispatcher.getInstance().addEventListener( this.getAgentProxyID(), listener);
    }

    /**
     * Removes the specified event listener from the list of listeners notified by changes applied
     * on this proxy.
     * @param listener the listener to be removed
     * @return true if the listener has been removed, false if not (e.g., the listener has not been
     * attached to this proxy before).
     */
    public final boolean removeEventListener( EventListener listener ){
	return EventDispatcher.getInstance().removeEventListener( this.getAgentProxyID(), listener);
    }
    
    
}
