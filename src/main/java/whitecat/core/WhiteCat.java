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
 * Copyright (C) Luca Ferrari 2006-2013 - fluca1978 (at) gmail.com
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
import whitecat.core.role.IRoleRepository;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.task.ITaskExecutionResult;
import whitecat.core.role.task.scheduling.ITaskScheduler;

/**
 * This is the main class of the whole system. This class has been created to
 * act as a front-end for the whole system.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class WhiteCat {

	/**
	 * The spring xml bean factory, used to instantiate the beans.
	 */
	private static XmlBeanFactory	xmlBeanFactory	= null;

	static{
		// configure the spring resource in order to get it available for the
		// beans configurations. Please note that the configuration file must be
		// in the classpath.
		final String springConfigurationPath = "whitecat.spring-beans.xml";
		final ClassPathResource classPathResource = new ClassPathResource(
				springConfigurationPath );
		xmlBeanFactory = new XmlBeanFactory( classPathResource );

	}

	/**
	 * Returns the default method forwarder generator.
	 * 
	 * @return a new method generator instance
	 */
	public final static IMethodForwarderGenerator getMethodForwarderGenerator() {
		return (IMethodForwarderGenerator) xmlBeanFactory
				.getBean( IMethodForwarderGenerator.class.getSimpleName() );
	}

	/**
	 * Provides a new role operation. Please note that the role booster is
	 * always initialized with a new role operation, so you don't have to create
	 * one in order to manipulate a proxy.
	 * 
	 * @return a new role operation
	 */
	public final static IRoleOperation getNewRoleOperation() {
		return (IRoleOperation) xmlBeanFactory.getBean( IRoleOperation.class
				.getSimpleName() );
	}

	/**
	 * Provides the default proxy handler for this configuration.
	 * 
	 * @return a new proxy handler instance
	 */
	public final static IProxyHandler getProxyHandler() {
		return (IProxyHandler) xmlBeanFactory.getBean( IProxyHandler.class
				.getSimpleName() );
	}

	/**
	 * Provides the unique proxy storage for the running system.
	 * 
	 * @return the proxy storage to use
	 */
	public final static IProxyStorage getProxyStorage() {
		return (IProxyStorage) xmlBeanFactory.getBean( IProxyStorage.class
				.getSimpleName() );
	}

	/**
	 * Provides a default role booster to use.
	 * 
	 * @return the role booster to use for role manipulations
	 */
	public final static IRoleBooster getRoleBooster() {
		return (IRoleBooster) xmlBeanFactory.getBean( IRoleBooster.class
				.getSimpleName() );
	}

	/**
	 * Provides the default role descriptor builder for the system.
	 * 
	 * @return the role descriptor builder
	 */
	public final static IRoleDescriptorBuilder getRoleDescriptorBuilder() {
		return (IRoleDescriptorBuilder) xmlBeanFactory
				.getBean( IRoleDescriptorBuilder.class.getSimpleName() );
	}

	/**
	 * Provides the unique role repository available in the system.
	 * 
	 * @return the role repository implementation
	 */
	public final static IRoleRepository getRoleRepository() {
		return (IRoleRepository) xmlBeanFactory.getBean( IRoleRepository.class
				.getSimpleName() );
	}

	/**
	 * Provides the task execution result instance to use for a specific task.
	 * 
	 * @return the implementation of a task execution result
	 */
	public final static ITaskExecutionResult getTaskExecutionResult() {
		return (ITaskExecutionResult) xmlBeanFactory
				.getBean( ITaskExecutionResult.class.getSimpleName() );
	}

	/**
	 * Provides the task scheduler for this installation.
	 * 
	 * @return the task scheduler
	 */
	public final static ITaskScheduler getTaskScheduler() {
		return (ITaskScheduler) xmlBeanFactory.getBean( ITaskScheduler.class
				.getSimpleName() );
	}
}
