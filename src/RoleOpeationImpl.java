import whitecat.core.IProxyHandler;

import whitecat.core.*;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IMethodForwarderGenerator;
import whitecat.core.role.IRole;

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

/**
 * The default implementation of a role operation.
 * The role booster uses this class as an abstraction over the configuration to
 * complete a role operation.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class RoleOpeationImpl implements IRoleOperation {

    /**
     * The role injection type of this operation.
     */
    private RoleInjectionType injectionType = null;
    
    
    /**
     * The method forwarder generator.
     */
    private IMethodForwarderGenerator methodForwarderGenerator = null;
    
    /**
     * The proxy handler to use for this role operation.
     */
    private IProxyHandler<? extends AgentProxy> proxyHandler = null;
    
    /**
     * The role operation status. By default it is operation enqued, that is not started yet.
     */
    private RoleOperationStatus operationStatus = RoleOperationStatus.ROLE_OPERATION_QUEUED;

    /**
     * The agent proxy that is going to be manipulated.
     */
    private AgentProxy agentProxy = null;
    
    /**
     * The public role class to add to the agent proxy.
     */
    private Class publicRoleClass = null;
    
    /**
     * The public role interface to add to the agent proxy.
     */
    private Class publicRoleInterface = null;
    
    /**
     * The operation exception in the case the operation has been unsuccesful.
     */
    private WCException operationException = null;
    
    
    /**
     * A string used for method forwarders to access implementation details.
     */
    private String methodAccessKey = null;
    
    
    /**
     * The role this operation is tied to.
     */
    private IRole role = null;
    
    
    /**
     * The role annotation class to use.
     */
    private Class annotationClass = null;
    
    
    /* (non-Javadoc)
     * @see whitecat.core.IRoleOperation#getMethodForwarderGenerator()
     */
    public IMethodForwarderGenerator getMethodForwarderGenerator() {
	return this.methodForwarderGenerator;
    }

    /* (non-Javadoc)
     * @see whitecat.core.IRoleOperation#getRoleInjectionType()
     */
    public RoleInjectionType getRoleInjectionType() {
	return this.injectionType;
    }

    /* (non-Javadoc)
     * @see whitecat.core.IRoleOperation#setMethodForwarderGenerator(whitecat.core.agents.IMethodForwarderGenerator)
     */
    public void setMethodForwarderGenerator(IMethodForwarderGenerator mfg) {
	this.methodForwarderGenerator = mfg;
    }

    /* (non-Javadoc)
     * @see whitecat.core.IRoleOperation#setRoleInjectionType(whitecat.core.RoleInjectionType)
     */
    public void setRoleInjectionType(RoleInjectionType type) {
	this.injectionType = type;
    }

    public IProxyHandler<? extends AgentProxy> getAgentProxyHandler() {
	return this.proxyHandler;
    }

    public void setAgentProxyHandler(IProxyHandler<? extends AgentProxy> handler) {
	this.proxyHandler = handler;
    }

    /**
     * Provides the value of the operationStatus field.
     * @return the operationStatus
     */
    public synchronized final RoleOperationStatus getOperationStatus() {
        return this.operationStatus;
    }

    /**
     * Sets the value of the operationStatus field as specified
     * by the value of operationStatus.
     * @param operationStatus the operationStatus to set
     */
    public synchronized final void setOperationStatus(
    	RoleOperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    /**
     * Provides the value of the agentProxy field.
     * @return the agentProxy
     */
    public synchronized final AgentProxy getAgentProxy() {
        return this.agentProxy;
    }

    /**
     * Sets the value of the agentProxy field as specified
     * by the value of agentProxy.
     * @param agentProxy the agentProxy to set
     */
    public synchronized final void setAgentProxy(AgentProxy agentProxy) {
        this.agentProxy = agentProxy;
    }

    /**
     * Provides the value of the publicRoleClass field.
     * @return the publicRoleClass
     */
    public synchronized final Class getPublicRoleClass() {
        return this.publicRoleClass;
    }

    /**
     * Sets the value of the publicRoleClass field as specified
     * by the value of publicRoleClass.
     * @param publicRoleClass the publicRoleClass to set
     */
    public synchronized final void setPublicRoleClass(Class publicRoleClass) {
        this.publicRoleClass = publicRoleClass;
    }

    /**
     * Provides the value of the publicRoleInterface field.
     * @return the publicRoleInterface
     */
    public synchronized final Class getPublicRoleInterface() {
        return this.publicRoleInterface;
    }

    /**
     * Sets the value of the publicRoleInterface field as specified
     * by the value of publicRoleInterface.
     * @param publicRoleInterface the publicRoleInterface to set
     */
    public synchronized final void setPublicRoleInterface(Class publicRoleInterface) {
        this.publicRoleInterface = publicRoleInterface;
        
        // consistency: each time the public role interface changes the access key should be invalidated
        this.methodAccessKey = null;
    }

    public synchronized WCException getOperationException() {
	return this.operationException;
    }

    public synchronized void setOperationException(WCException ex) {
	this.operationException = ex;
	
	// consistency!
	if( this.operationException != null )
	    this.operationStatus = RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE;
    }

    public final IRole getRole() {
	return this.role;
    }

    public final void setRole(IRole role) {
	this.role = role;
    }

    public final synchronized String getRoleImplementationAccessKey() {
	// if the string is not yet implemented, construct it
	if( this.methodAccessKey == null )
	    if( this.publicRoleInterface != null )
		this.methodAccessKey = this.publicRoleInterface.getClass().getName();
	    else	
		this.methodAccessKey = "";
	else
	    this.methodAccessKey = "";
	
	
	// all done
	return this.methodAccessKey;
    }

    public synchronized final Class getRoleAnnotationClass() {
	return this.annotationClass;
    }

    public synchronized final void setRoleAnnotationClass(Class annotationClass) {
	this.annotationClass = annotationClass;	
    }

   
    

}
