package whitecat.core.role.operation;

import whitecat.core.IProxyHandler;
import whitecat.core.IRoleOperation;
import whitecat.core.RoleInjectionType;
import whitecat.core.RoleOperationStatus;
import whitecat.core.WCException;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IMethodForwarderGenerator;
import whitecat.core.role.IRole;

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
 * Copyright (C) Luca Ferrari 2006-2012 - fluca1978 (at) gmail.com
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
 * The default implementation of a role operation. The role booster uses this
 * class as an abstraction over the configuration to complete a role operation.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class RoleOperationImpl implements IRoleOperation {

	/**
	 * The role injection type of this operation.
	 */
	private RoleInjectionType					injectionType				= null;

	/**
	 * The method forwarder generator.
	 */
	private IMethodForwarderGenerator			methodForwarderGenerator	= null;

	/**
	 * The proxy handler to use for this role operation.
	 */
	private IProxyHandler<? extends AgentProxy>	proxyHandler				= null;

	/**
	 * The role operation status. By default it is operation enqued, that is not
	 * started yet.
	 */
	private RoleOperationStatus					operationStatus				= RoleOperationStatus.ROLE_OPERATION_QUEUED;

	/**
	 * The agent proxy that is going to be manipulated.
	 */
	private AgentProxy							agentProxy					= null;

	/**
	 * The public role class to add to the agent proxy.
	 */
	private Class								publicRoleClass				= null;

	/**
	 * The public role interface to add to the agent proxy.
	 */
	private Class								publicRoleInterface			= null;

	/**
	 * The operation exception in the case the operation has been unsuccesful.
	 */
	private WCException							operationException			= null;

	/**
	 * A string used for method forwarders to access implementation details.
	 */
	private String								methodAccessKey				= null;

	/**
	 * The role this operation is tied to.
	 */
	private IRole								role						= null;

	/**
	 * The role annotation class to use.
	 */
	private Class								annotationClass				= null;

	/**
	 * Provides the value of the agentProxy field.
	 * 
	 * @return the agentProxy
	 */
	public synchronized final AgentProxy getAgentProxy() {
		return agentProxy;
	}

	public IProxyHandler<? extends AgentProxy> getAgentProxyHandler() {
		return proxyHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.IRoleOperation#getMethodForwarderGenerator()
	 */
	public IMethodForwarderGenerator getMethodForwarderGenerator() {
		return methodForwarderGenerator;
	}

	public synchronized WCException getOperationException() {
		return operationException;
	}

	/**
	 * Provides the value of the operationStatus field.
	 * 
	 * @return the operationStatus
	 */
	public synchronized final RoleOperationStatus getOperationStatus() {
		return operationStatus;
	}

	/**
	 * Provides the value of the publicRoleClass field.
	 * 
	 * @return the publicRoleClass
	 */
	public synchronized final Class getPublicRoleClass() {
		return publicRoleClass;
	}

	/**
	 * Provides the value of the publicRoleInterface field.
	 * 
	 * @return the publicRoleInterface
	 */
	public synchronized final Class getPublicRoleInterface() {
		return publicRoleInterface;
	}

	public final IRole getRole() {
		return role;
	}

	public synchronized final Class getRoleAnnotationClass() {
		return annotationClass;
	}

	public final synchronized String getRoleImplementationAccessKey() {
		// if the string is not yet implemented, construct it
		if (methodAccessKey == null)
			if (publicRoleInterface != null)
				methodAccessKey = publicRoleInterface.getClass().getName();
			else methodAccessKey = "";
		else methodAccessKey = "";

		// all done
		return methodAccessKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.IRoleOperation#getRoleInjectionType()
	 */
	public RoleInjectionType getRoleInjectionType() {
		return injectionType;
	}

	/**
	 * Sets the value of the agentProxy field as specified by the value of
	 * agentProxy.
	 * 
	 * @param agentProxy
	 *            the agentProxy to set
	 */
	public synchronized final void setAgentProxy(final AgentProxy agentProxy) {
		this.agentProxy = agentProxy;
	}

	public void setAgentProxyHandler(	final IProxyHandler<? extends AgentProxy> handler) {
		proxyHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleOperation#setMethodForwarderGenerator(whitecat.core
	 * .agents.IMethodForwarderGenerator)
	 */
	public void setMethodForwarderGenerator(final IMethodForwarderGenerator mfg) {
		methodForwarderGenerator = mfg;
	}

	public synchronized void setOperationException(final WCException ex) {
		operationException = ex;

		// consistency!
		if (operationException != null)
			operationStatus = RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE;
	}

	/**
	 * Sets the value of the operationStatus field as specified by the value of
	 * operationStatus.
	 * 
	 * @param operationStatus
	 *            the operationStatus to set
	 */
	public synchronized final void setOperationStatus(	final RoleOperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * Sets the value of the publicRoleClass field as specified by the value of
	 * publicRoleClass.
	 * 
	 * @param publicRoleClass
	 *            the publicRoleClass to set
	 */
	public synchronized final void setPublicRoleClass(	final Class publicRoleClass) {
		this.publicRoleClass = publicRoleClass;
	}

	/**
	 * Sets the value of the publicRoleInterface field as specified by the value
	 * of publicRoleInterface.
	 * 
	 * @param publicRoleInterface
	 *            the publicRoleInterface to set
	 */
	public synchronized final void setPublicRoleInterface(	final Class publicRoleInterface) {
		this.publicRoleInterface = publicRoleInterface;

		// consistency: each time the public role interface changes the access
		// key should be invalidated
		methodAccessKey = null;
	}

	public final void setRole(final IRole role) {
		this.role = role;
	}

	public synchronized final void setRoleAnnotationClass(	final Class annotationClass) {
		this.annotationClass = annotationClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.IRoleOperation#setRoleInjectionType(whitecat.core.
	 * RoleInjectionType)
	 */
	public void setRoleInjectionType(final RoleInjectionType type) {
		injectionType = type;
	}

}
