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

/**
 * The identificator of a proxy. It should be unique over the platform.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class AgentProxyID {

    /**
     * A sequence number that identificates a single proxy.
     */
    private long sequenceID = 0;
    
    
    /**
     * the generation sequence of this proxy id, increased each time a new
     * proxy is created.
     */
    private static long generationSequence = 0;
    
    /**
     * Constructs the proxy id with the specified sequence number.
     * @param sequence the sequence number for the proxy id.
     */
    private AgentProxyID(long sequence){
	super();
	this.sequenceID = sequence;
    }
    
    public String toString(){
	return "AgentProxyID-" + this.sequenceID;
    }
    
    public int hashCode(){
	return (int) this.sequenceID;
    }
    
    public boolean equals(Object toCompare){
	if( toCompare instanceof AgentProxyID )
	    return ( this.sequenceID == ((AgentProxyID) toCompare).sequenceID );
	else
	    return false;
    }
    
    /**
     * Creates a unique agent proxy id.
     * @return the agent proxy id generated.
     */
    public static final AgentProxyID getNextAgentProxyID(){
	return new AgentProxyID( ++generationSequence );
    }
    

    /**
     * Creates a agent proxy id copying the values from an original one. This is
     * useful to set the same agentproxyid after a role manipulation.
     * @param source the source agent proxy id to copy from
     * @return the agent proxy id with the same values.
     */
    public static final AgentProxyID createByCopy(AgentProxyID source){
	if( source == null )
	    return null;
	else
	    return new AgentProxyID( source.sequenceID );
    }

    /**
     * Provides the value of the sequenceID field.
     * @return the sequenceID
     */
    public synchronized final long getSequenceID() {
        return this.sequenceID;
    }
    
    
    
}
