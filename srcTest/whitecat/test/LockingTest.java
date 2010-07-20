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
package whitecat.test;


import org.junit.Before;
import org.junit.Test;

import whitecat.core.ProxyStorage;
import whitecat.example.DBAgent;
import whitecat.example.DBProxy;


import static org.junit.Assert.*;

/**
 * A test for the locking mechanism.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class LockingTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBlocking(){
	DBProxy proxy = new DBProxy( new DBAgent() );
	
	// store the proxy in the storage
	ProxyStorage storage = ProxyStorage.getInstance();
	storage.storeAgentProxy(proxy);
	
	// the proxy should not be locked now!
	if( storage.isAgentProxyLocked(proxy) )
	    fail("Proxy locked as soon as stored!");
	
	// lock the proxy
	storage.lockAgentProxy(proxy, false, -1);
	if(!  storage.isAgentProxyLocked(proxy) )
	    fail("Proxy status should be locked now!");
	
	// do a method call to a blocking method
	long start = System.currentTimeMillis();
	int result = proxy.lockableMethod( 3 );
	long end   = System.currentTimeMillis();
	
	if( result != 3*2 )
	    fail("Result incorrect");
	
	if( (end - start) < 9000 )
	    fail("Locking time was incorrect " + (end - start) );
	
	
	
    }
}
