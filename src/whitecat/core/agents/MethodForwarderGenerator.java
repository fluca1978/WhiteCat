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
package whitecat.core.agents;

import whitecat.core.exceptions.WCForwarderMethodException;
import whitecat.core.role.IRole;
import javassist.CtMethod;

/**
 * The interface provides utility method for the generation of forwarders method, that are
 * methods in the agent proxy that must forward request to the method on the role itself.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public interface MethodForwarderGenerator {

    
    /**
     * Inits this method generator with the proxy class name it will be used onto and
     * a key calculated from the role engine.
     * @param proxyClassName the real (not manipulated) proxy class name used for the manipulations. It can be
     * used to know properties of the proxy itself.
     * @param key a key computed by the role engine during the manipulation, used as an identifier
     * for the manipulation itself.
     * @param roleClassName the name of the role class, used for knowing properties of the role itself
     */
    public void init(String proxyClassName, String roleClassName, String key);
    
    /**
     * Generates source code for a method forwarder. Given a method in the interface,
     * this method returns the source code that implements such method.
     * @param interfaceMethod the source method from the role interface
     * @return the code source for the method that implements the interface method one.
     * @exception WCForwarderMethodException if something goes wrong
     */
    public String getMethodForwarderCode(CtMethod interfaceMethod) throws WCForwarderMethodException;
    
    /**
     * This method is called at the end of the generation of the new proxy instance. Place
     * in this method code that serves for binding parameters used for method forwarding calls to
     * the role instance (for instance the binding of a key in the hashmap for the references).
     * @return true if the bind has been done
     * @throws WCForwarderMethodException if something goes wrong
     * @param roleInstance the role reference (if available)
     * @param proxy the agent proxy created from the manipulation
     */
    public boolean bindReferences(AgentProxy proxy, IRole roleInstance) throws WCForwarderMethodException;
}