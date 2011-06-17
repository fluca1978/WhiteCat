package whitecat.core;

import java.lang.annotation.Annotation;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.WCAgent;
import whitecat.core.role.IRole;

public interface IRoleBooster {

	/**
	 * Provides the current role operation the role booster is executing.
	 * 
	 * @return the role operation abstraction
	 */
	public IRoleOperation getCurrentRoleOperation();

	/**
	 * Checks if the proxy has a public role thru an annotation.
	 * 
	 * @param proxy
	 *            the proxy to check
	 * @return true if the proxy has the annotation
	 */
	public boolean hasPublicRoleAnnotation(AgentProxy proxy);

	/**
	 * Determines if the proxy is currently playing a visible (i.e., public)
	 * role. A publi role must be marked thru the @PublicRole annotation.
	 * 
	 * @param proxy
	 *            the proxy to analyze.
	 * @return true if the proxy is playing a public role.
	 */
	public boolean hasPublicRoleInterface(AgentProxy proxy);

	/**
	 * Checks if the current proxy has the role annotation. This implies that
	 * the proxy has at least a visible role.
	 * 
	 * @param proxy
	 *            the proxy instance to analyze.
	 * @return true if the proxy has a role annotation or is annotated with an
	 *         annotation that has a role annotation
	 */
	public boolean hasRoleAnnotation(AgentProxy proxy);

	public AgentProxy injectPublicRole(WCAgent agent, AgentProxy proxy,
										IRole role) throws WCException;

	public AgentProxy injectVisibleRole(WCAgent agent, AgentProxy proxy,
										IRole role);

	/**
	 * Checks if a specific annotation is a role annotation, that is an
	 * annotation annotated with the @Role annotation.
	 * 
	 * @param annotation
	 *            the annotation to check
	 * @return true if the annotation is a role annotation, false if it is a
	 *         normal annotation or the specified annotation is null
	 */
	public boolean isRoleAnnotation(Annotation annotation);

	public AgentProxy removePublicRole(WCAgent agent, AgentProxy proxy,
										IRole role) throws WCException;

	/**
	 * Removes all the applied roles until the specified one (included). Since
	 * the roles are applied to a proxy by means of subclasses implementing the
	 * role interfaces, this method simply removes a role returnin a proxy that
	 * is the instance of the superclass of the class when the specified role
	 * has been applied. This means that the agent will loose all the role
	 * acquired since the specified one.
	 * 
	 * @param agent
	 *            the agent that wants to remove the role
	 * @param proxy
	 *            the proxy to change
	 * @param role
	 *            the role to remove
	 * @return the new instance of the agent proxy without all the roles until
	 *         the specified one
	 * @throws WCException
	 *             if something goes wrong
	 */
	public AgentProxy removeUntilRole(WCAgent agent, AgentProxy proxy,
										IRole role) throws WCException;

	public AgentProxy removeVisibleRole(AgentProxy proxy, IRole role);

	/**
	 * Set the current role operation. A role operation is an abstraction over
	 * the operation the role booster will (or is) doing. It contains
	 * configurable and pluggable parameters for the operation to succeed, such
	 * as the method forwarder generator and the proxy handler.
	 * 
	 * @param operation
	 *            the role operation and its parameters
	 */
	public void setCurrentRoleOperation(IRoleOperation operation);

}