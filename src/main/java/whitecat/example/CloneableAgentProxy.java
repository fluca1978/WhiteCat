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
package whitecat.example;

import java.util.Random;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IClonableAgentProxy;
import whitecat.core.agents.LocalAgentProxy;
import whitecat.core.agents.WCAgent;

/**
 * An example of a cloneable agent proxy.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class CloneableAgentProxy extends LocalAgentProxy implements
		IClonableAgentProxy {

	// the extended state of the agent proxy
	private String	stringVariable	= null;
	private int		integerVariable	= 0;
	private int[]	integerArray	= null;

	public CloneableAgentProxy(final WCAgent agent) {
		super();
		super.setMyAgent( agent );

		final Random ran = new Random();
		int random = ran.nextInt();
		while (random < 0)
			random += 1000;
		while (random > 10000)
			random /= 100;

		stringVariable = "StringVariable " + random;
		integerVariable = random;

		integerArray = new int[integerVariable];
		for (int i = 0; i < integerArray.length; i++)
			integerArray[i] = ran.nextInt();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.agents.IClonableAgentProxy#cloneAgentProxyState(whitecat
	 * .core.agents.IClonableAgentProxy)
	 */
	public void cloneAgentProxyState(final IClonableAgentProxy sourceAgentProxy) {
		// check if the agent proxy is of my same type!
		if (!(sourceAgentProxy instanceof CloneableAgentProxy))
			return;

		final CloneableAgentProxy sourceProxy = (CloneableAgentProxy) sourceAgentProxy;

		// copy each single variable of the extended state
		stringVariable = new String( sourceProxy.stringVariable );
		integerVariable = sourceProxy.integerVariable;
		integerArray = new int[sourceProxy.integerArray.length];
		System.arraycopy(
				sourceProxy.integerArray,
				0,
				integerArray,
				0,
				sourceProxy.integerArray.length );

	}

	/**
	 * Provides the value of the integerArray field.
	 * 
	 * @return the integerArray
	 */
	public synchronized final int[] getIntegerArray() {
		return integerArray;
	}

	/**
	 * Provides the value of the integerVariable field.
	 * 
	 * @return the integerVariable
	 */
	public synchronized final int getIntegerVariable() {
		return integerVariable;
	}

	/**
	 * Provides the value of the stringVariable field.
	 * 
	 * @return the stringVariable
	 */
	public synchronized final String getStringVariable() {
		return stringVariable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.agents.AgentProxy#update()
	 */
	@Override
	public AgentProxy update() {
		// TODO Auto-generated method stub
		return null;
	}

}
