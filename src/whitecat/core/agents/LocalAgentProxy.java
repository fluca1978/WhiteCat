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
package whitecat.core.agents;

import java.util.HashMap;

import whitecat.core.IProxyStorage;
import whitecat.core.ProxyStorageImpl;
import whitecat.core.WhiteCat;
import whitecat.core.role.IRole;

/**
 * A local proxy, that is a proxy that handles an agent locally.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class LocalAgentProxy extends AgentProxy {

    /**
     * The agent this proxy is handling.
     */
    private WCAgent myAgent = null;
    
    /**
     * Default constructor, used by the role engine for instantiating
     * a new proxy after a role manipulation.
     */
    public LocalAgentProxy(){
	super();
    }

    /**
     * Sets the value of the myAgent field as specified
     * by the value of myAgent.
     * @param myAgent the myAgent to set
     */
    protected synchronized final void setMyAgent(WCAgent myAgent) {
        this.myAgent = myAgent;
    }

    /**
     * Provides the value of the myAgent field.
     * @return the myAgent
     */
    protected synchronized final WCAgent getMyAgent() {
        return myAgent;
    }
    
    
    /**
     * A map that stores the references to the public role implementation.
     */
    protected HashMap<String, IRole> roleMap = new HashMap<String, IRole>();
    
    /**
     * Places a new role implementation into the map of public role implementations.
     * @param key the key for the role
     * @param role the role implementation reference
     * @return true if the role has been added
     */
    public final boolean addRoleImplementationReference(String key, IRole role){
	//check arguments
	if( key == null || role == null )
	    return false;
	
	this.roleMap.put(key, role);
	return true;
    }

    /* (non-Javadoc)
     * @see whitecat.core.agents.AgentProxy#initializeByCopy(whitecat.core.agents.AgentProxy)
     */
    @Override
    public void initializeByCopy(AgentProxy proxy) {
	super.initializeByCopy(proxy);
	
	if( proxy instanceof LocalAgentProxy )
	    this.roleMap.putAll( ((LocalAgentProxy)proxy).roleMap );
	    
    }

    @Override
    public AgentProxy update() {
	// return the proxy associated with the agent
	IProxyStorage storage = WhiteCat.getProxyStorage();
	return storage.getLastUpdatedAgentProxy( this.getAgentProxyID() );
    }
    
    

}
