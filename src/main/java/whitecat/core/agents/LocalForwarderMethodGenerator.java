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

import javassist.CtClass;
import javassist.CtMethod;
import whitecat.core.IRoleOperation;
import whitecat.core.exceptions.WCForwarderMethodException;
import whitecat.core.role.IRole;

/**
 * A method forwarder generator that works assuming that the proxy and the agent
 * using the role are local each other. The method generated exploits a
 * capability of the LocalAgentProxy that is an hashmap that stores references
 * to the role instance.
 * 
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
public class LocalForwarderMethodGenerator implements IMethodForwarderGenerator {

	/**
	 * The key used for the method forwarding.
	 */
	private String	hashMapKey		= null;

	/**
	 * The role class name.
	 */
	private String	roleClassName	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.agents.MethodForwarderGenerator#bindReferences()
	 */
	public boolean bindReferences(final AgentProxy proxy,
									final IRole roleInstance)
																throws WCForwarderMethodException {
		// bind the role instance to the hashmap key
		if (proxy instanceof LocalAgentProxy){
			((LocalAgentProxy) proxy).addRoleImplementationReference(
					hashMapKey,
					roleInstance );
			return true;
		}else return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.agents.MethodForwarderGenerator#getMethodForwarderCode(
	 * javassist.CtMethod)
	 */
	public String getMethodForwarderCode(final CtMethod interfaceMethod)
																		throws WCForwarderMethodException {
		try{
			// construct the Java source code for the new method
			final StringBuffer methodCode = new StringBuffer( 200 );
			methodCode.append( " public " );
			methodCode.append( interfaceMethod.getReturnType().getName() );
			methodCode.append( " " );
			methodCode.append( interfaceMethod.getName() );
			methodCode.append( "( " );
			final CtClass params[] = interfaceMethod.getParameterTypes();
			for (int parNo = 0; (params != null) && (parNo < params.length); parNo++){
				if (parNo > 0)
					methodCode.append( "," ); // more than one parameter,
												// separate them with comma

				methodCode.append( params[parNo].getName() ); // parameter type
				methodCode.append( " param" + parNo ); // parameter identifier
			}

			methodCode.append( " ) " );

			// body definition: I need to cast the reference extracted from the
			// proxy
			// to an object of the type of the role, and than I need to invoke
			// the method
			// passing all the argument of the forwarder one. If the method has
			// a return type
			// different from void, that I also need to insert a return
			// statement.
			methodCode.append( "{ " );

			if (!"void".equals( interfaceMethod.getReturnType().getName() ))
				methodCode.append( " return " );

			methodCode.append( " ((" );
			methodCode.append( roleClassName );
			methodCode.append( " ) " );
			methodCode.append( " this.roleMap.get(\"" );
			methodCode.append( hashMapKey );
			methodCode.append( "\"))." );
			methodCode.append( interfaceMethod.getName() );
			methodCode.append( "($$); }" );

			// all done
			return methodCode.toString();

		}catch (final Exception e){
			throw new WCForwarderMethodException(
					"Exception caught during forwarding method construction", e );
		}
	}

	public synchronized final void init(final IRoleOperation roleOperation) {
		this.init(
				roleOperation.getAgentProxy().getClass().getName(),
				roleOperation.getRole().getClass().getName(),
				roleOperation.getRoleImplementationAccessKey() );
	}

	public void init(final String proxyClassName, final String roleClassName,
						final String key) {
		// store the key of the hashmap
		hashMapKey = key;

		// store also the classname of the role
		this.roleClassName = roleClassName;
	}

}
