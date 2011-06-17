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
package whitecat.example;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import whitecat.core.agents.LocalAgentProxy;
import whitecat.core.annotations.Lock;

/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
@ProxyAnnotation()
public class DBProxy extends LocalAgentProxy {

	private int		property1	= 10;
	private String	property2	= "AGENTPROXY_NEW";
	private DBAgent	myAgent		= null;

	public DBProxy() {
		this( null );
	}

	public DBProxy(final DBAgent agent) {
		super();
		myAgent = agent;
	}

	/**
	 * Dumps the content of the properties.
	 */
	public void dump() {
		System.out.println( "AgentProxy " + this.getClass() + " hashcode "
				+ this.getClass().hashCode() );
		System.out.println( "Property 1 = " + property1 );
		System.out.println( "Property 2 = " + property2 );
		System.out.println( "Agent owned = " + myAgent );

		System.out.println( "Method list: " );
		for (final Method m : this.getClass().getMethods())
			System.out.println( "\tname " + m.getName() + "\tmodifier "
					+ m.getModifiers() );

		System.out.println( "Annotation List:" );
		for (final Annotation a : this.getClass().getAnnotations())
			System.out.println( "\tname " + a + " class " + a.getClass() );

	}

	/**
	 * Provides the value of the property1 field.
	 * 
	 * @return the property1
	 */
	public synchronized final int getProperty1() {
		return property1;
	}

	/**
	 * Provides the value of the property2 field.
	 * 
	 * @return the property2
	 */
	public synchronized final String getProperty2() {
		return property2;
	}

	/**
	 * An example of lockable method: the method cannot be called while a
	 * manipulation is active.
	 * 
	 * @param value
	 * @return
	 */
	@Lock(blocking = "true")
	public int lockableMethod(final int value) {
		return value * 2;
	}

	/**
	 * Sets the value of the myAgent field as specified by the value of myAgent.
	 * 
	 * @param myAgent
	 *            the myAgent to set
	 */
	public synchronized final void setMyAgent(final DBAgent myAgent) {
		this.myAgent = myAgent;
	}

	/**
	 * Sets the value of the property1 field as specified by the value of
	 * property1.
	 * 
	 * @param property1
	 *            the property1 to set
	 */
	public synchronized final void setProperty1(final int property1) {
		this.property1 = property1;
	}

	/**
	 * Sets the value of the property2 field as specified by the value of
	 * property2.
	 * 
	 * @param property2
	 *            the property2 to set
	 */
	public synchronized final void setProperty2(final String property2) {
		this.property2 = property2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.agents.AgentProxy#update()
	 */
	/*
	 * public AgentProxy update() { DBProxy proxy = new DBProxy(this.myAgent);
	 * proxy.property1 = this.property1; proxy.property2 = this.property2;
	 * proxy.myAgent = this.myAgent; return proxy; }
	 */
}
