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
package whitecat.core;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import whitecat.core.agents.IMethodForwarderGenerator;

/**
 * This is the main class of the whole system. 
 * This class has been created to act as a front-end for the whole system.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class WhiteCat {
    
    /**
     * The spring xml bean factory, used to instantiate the beans.
     */
    private static XmlBeanFactory xmlBeanFactory = null;
    
    static{
	 // configure the spring resource in order to get it available for the
        // beans configurations. Please note that the configuration file must be
	// in the classpath.
        String springConfigurationPath = "spring.xml";
        ClassPathResource classPathResource = new ClassPathResource( springConfigurationPath );
        xmlBeanFactory = new XmlBeanFactory(classPathResource);

    }
    
    
    /**
     * Provides a default role booster to use.
     * @return the role booster to use for role manipulations
     */
    public final static IRoleBooster getRoleBooster(  ){
	return (IRoleBooster) xmlBeanFactory.getBean( IRoleBooster.class.getSimpleName() );
    }
    
    /**
     * Provides the default proxy handler for this configuration.
     * @return a new proxy handler instance
     */
    public final static IProxyHandler getProxyHandler(){
	return (IProxyHandler) xmlBeanFactory.getBean( IProxyHandler.class.getSimpleName() );
    }
    
    
    /**
     * Returns the default method forwarder generator.
     * @return a new method generator instance
     */
    public final static IMethodForwarderGenerator getMethodForwarderGenerator(){
	return (IMethodForwarderGenerator) xmlBeanFactory.getBean( IMethodForwarderGenerator.class.getSimpleName() );
    }
    
    
    /**
     * Provides a new role operation. Please note that the role booster is always
     * initialized with a new role operation, so you don't have to create one in order
     * to manipulate a proxy.
     * @return a new role operation
     */
    public final static IRoleOperation getNewRoleOperation(){
	return (IRoleOperation) xmlBeanFactory.getBean( IRoleOperation.class.getSimpleName() );
    }

}
