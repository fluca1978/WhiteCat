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
package whitecat.test;


import org.junit.Before;
import org.junit.Test;

import whitecat.core.IProxyStorage;
import whitecat.core.ProxyStorageImpl;
import whitecat.core.WhiteCat;
import whitecat.core.agents.AgentProxy;
import whitecat.example.DBAgent;
import whitecat.example.DBProxy;


import static org.junit.Assert.*;

/**
 * A test for the locking mechanism.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class LockingTest implements Runnable {


	private long start = 0, end = 0;
	private long sleeping = 5600;
	private Thread unlockingThread = null;

	private IProxyStorage storage = null;
	private DBProxy   proxy = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		proxy = new DBProxy( new DBAgent() );
		storage = WhiteCat.getProxyStorage();

		unlockingThread = new Thread( this );
		unlockingThread.start();
	}

	@Test
	public void testBlocking(){


		// store the proxy in the storage
		storage.storeAgentProxy(proxy);

		// the proxy should not be locked now!
		if( storage.isAgentProxyLocked(proxy) )
			fail("Proxy locked as soon as stored!");

		// lock the proxy
		storage.lockAgentProxy(proxy, false, -1);
		if(!  storage.isAgentProxyLocked(proxy) )
			fail("Proxy status should be locked now! " + System.currentTimeMillis());

		// do a method call to a blocking method
		start = System.currentTimeMillis();
		int result = proxy.lockableMethod( 3 );
		end   = System.currentTimeMillis();

		if( result != 3*2 )
			fail("Result incorrect");

		if( (end - start) < sleeping  )
			fail("Locking time was incorrect " + (end - start) + " " + end + " " + start );



	}

	public void run() {

		System.out.println("Unlocking thread sleeping for " + this.sleeping);
		try {
			Thread.currentThread().sleep(sleeping);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Unlocking the proxy " + System.currentTimeMillis());
		this.storage.unlockAgentProxy(proxy, true);

	}
}
