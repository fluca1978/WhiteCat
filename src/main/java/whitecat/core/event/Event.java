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
package whitecat.core.event;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.AgentProxyID;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class Event {
    
    /**
     * The type of this event.
     */
    private EventType type = null;
    
    /**
     * The id of the agent proxy that is going manipulated.
     */
    private AgentProxyID agentProxyID = null;
    
    /**
     * The role descriptor this role event refers to.
     */
    private RoleDescriptor descriptor = null;
    
    /**
     * Builds an event with the specified proxy id and the type of event.
     * @param proxyID the id of the proxy
     * @param type the type of the event
     * @param descriptor the role descriptor this event refers to
     */
    private Event(AgentProxyID proxyID, EventType type, RoleDescriptor descriptor){
	super();
	this.agentProxyID = proxyID;
	this.type = type;
	this.descriptor = descriptor;
    }
    
    
    /**
     * Builds up the event for the specified proxy id and the type of the event.
     * @param proxyID the id of the proxy
     * @param type the type of the event
     * @param descriptor the role descriptor this event refers to
     * @return the event of the specified 
     */
    public static Event createEvent(AgentProxyID proxyID, EventType type, RoleDescriptor descriptor){
	return new Event(proxyID, type, descriptor);
	
    }
    
    
    /**
     * Provides the source agent proxy id.
     * @return the id of the proxy this event refers to
     */
    public AgentProxyID getAgentProxyID(){
	return this.agentProxyID;
    }
    
    /**
     * The event type for this event.
     * @return the event type of this event.
     */
    public EventType getEventType(){
	return this.type;
    }
    
    /**
     * Provides the role descriptor related to this role event.
     * @return the role descriptor
     */
    public RoleDescriptor getRoleDescriptor(){
	return this.descriptor;
    }
    
    
    public String toString(){
	return this.type + " - " +  this.agentProxyID;
    }
}
