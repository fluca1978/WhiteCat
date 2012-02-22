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
package whitecat.core;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IMethodForwarderGenerator;
import whitecat.core.role.IRole;

/**
 * This is an abstraction to a role operation, that is a role manipulation by a
 * role booster instance. This abstraction contains information needed to
 * perform and complete the role operation, such as the method forwarder and
 * proxy handler, as well as the role injection type.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public interface IRoleOperation {

	/**
	 * Provides the agent proxy for the current manipulation.
	 * 
	 * @return the proxy of the agent that is going to be manipulated.
	 */
	public AgentProxy getAgentProxy();

	/**
	 * Provides the agent proxy handler to use to synchronize two proxy
	 * instances during this role operation.
	 * 
	 * @return the proxy handler to use in order to complete the role
	 *         manipulation
	 */
	public IProxyHandler<? extends AgentProxy> getAgentProxyHandler();

	/**
	 * Provides the method forwarder generator for this operation.
	 * 
	 * @return the method forwarder generation to use
	 */
	public IMethodForwarderGenerator getMethodForwarderGenerator();

	/**
	 * The operation exception of this operation, in the case it has been
	 * unsuccesful.
	 * 
	 * @return the operation exception
	 */
	public WCException getOperationException();

	/**
	 * Provides the role operation status (initiated, completed, etc.).
	 * 
	 * @return the operationStatus of the current operation
	 */
	public RoleOperationStatus getOperationStatus();

	/**
	 * Gets the public role class to inject into the proxy.
	 * 
	 * @return the class of the role to inject
	 */
	public Class getPublicRoleClass();

	/**
	 * Provides the public role interface to add/remove to/from the agent proxy.
	 * 
	 * @return the publicRoleInterface class
	 */
	public Class getPublicRoleInterface();

	/**
	 * Provides the role to use for this operation.
	 * 
	 * @return the role to use for the operation/manipulation
	 */
	public IRole getRole();

	/**
	 * Returns the annotation to use for a role operation.
	 * 
	 * @return the class representing the role annotation to inject into the
	 *         proxy
	 */
	public Class getRoleAnnotationClass();

	/**
	 * Constrcuts an unique role implementation access key, that is a string key
	 * that will be used when the forwarding methods need to access the role
	 * reference.
	 * 
	 * @return the string used internally to access the implementation methods
	 */
	public String getRoleImplementationAccessKey();

	/**
	 * This method provides the information about the role injection type, such
	 * as public, visible role addition/removal.
	 * 
	 * @return the role injection type for this operation
	 */
	public RoleInjectionType getRoleInjectionType();

	/**
	 * Sets the agent proxy for this operation, that is the initial agent proxy
	 * (the one that is going to be manipulated).
	 * 
	 * @param proxy
	 *            the agent proxy to use for the manipulation
	 */
	public void setAgentProxy(AgentProxy proxy);

	/**
	 * Sets the agent proxy handler for this role operation.
	 * 
	 * @param handler
	 *            the proxy handler to use for this role operation
	 */
	public void setAgentProxyHandler(IProxyHandler<? extends AgentProxy> handler);

	/**
	 * Sets the method forwarder generator for this role operation.
	 * 
	 * @param mfg
	 *            the forwarder generator to use
	 */
	public void setMethodForwarderGenerator(IMethodForwarderGenerator mfg);

	/**
	 * In the case the operation has failed this method should be used to set
	 * the exception and the motivation of the failure.
	 * 
	 * @param ex
	 *            the exception of the failure
	 */
	public void setOperationException(WCException ex);

	/**
	 * Sets the current operation status.
	 * 
	 * @param operationStatus
	 *            the operation Status to set
	 */
	public void setOperationStatus(RoleOperationStatus operationStatus);

	/**
	 * Sets the public role class to add/remove from the agent proxy
	 * 
	 * @param publicRoleClass
	 *            the class of the role to add/remove to/from the agent proxy
	 */
	public void setPublicRoleClass(Class publicRoleClass);

	/**
	 * Sets the public role interface class to add/remove to/from the agent
	 * proxy.
	 * 
	 * @param publicRoleInterface
	 *            the class to use for the role operation
	 */
	public void setPublicRoleInterface(Class publicRoleInterface);

	/**
	 * Sets the role to use for this role operation.
	 * 
	 * @param role
	 *            the role to use for the role operation
	 */
	public void setRole(IRole role);

	/**
	 * Sets the role annotation class to use for the operation.
	 * 
	 * @param annotationClass
	 *            the annotation to use for this operation
	 */
	public void setRoleAnnotationClass(Class annotationClass);

	/**
	 * Sets the role injection type for this kind of operation.
	 * 
	 * @param type
	 *            the role injection type at this time for this operation
	 */
	public void setRoleInjectionType(RoleInjectionType type);
}
