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

import static org.junit.Assert.*;


import java.lang.annotation.Annotation;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import whitecat.core.IProxyHandler;
import whitecat.core.RoleBooster;
import whitecat.core.WCException;
import whitecat.core.WhiteCat;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.ProxyHandlerFactory;
import whitecat.core.agents.WCAgent;
import whitecat.core.exceptions.WCProxyException;
import whitecat.core.role.IRole;
import whitecat.example.DBAgent;
import whitecat.example.DBProxy;
import whitecat.example.DatabaseAdministrator;
import whitecat.example.DatabaseUser;
import whitecat.example.IDatabaseAdministrator;
import whitecat.example.LoggerRole;
import whitecat.core.*;

/**
 * A test to check the working of the Role Booster.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class RoleBoosterTest {

    /**
     * The role booster under test.
     */
    private IRoleBooster booster = null;
    
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	//this.booster = new RoleBooster( this.getClass().getClassLoader() );
	this.booster = WhiteCat.getRoleBooster();
    }

    @Test
    public void testPublicRoleInjection() throws WCException {
	// an agent proxy now should not have any public role interface
	AgentProxy proxy = new DBProxy();
	if( this.booster.hasPublicRoleInterface(proxy) )
	    fail("A new created proxy should not have any public role interface!");
	
	// now inject a role
	WCAgent agent = new DBAgent();
	DatabaseAdministrator dba = new DatabaseAdministrator();
	proxy = (DBProxy) this.booster.injectPublicRole( agent, proxy, dba );
	
	// the proxy must implement the role interface
	if( !(proxy instanceof IDatabaseAdministrator) )
	    fail("The proxy has not the idatabase administrator interface!");
	
	// the proxy must have the original class as parent class
	if( !( proxy.getClass().getSuperclass().equals( DBProxy.class) ) )
	    fail("The proxy does not have the original proxy as superclass!");
	
	// remove the role
	this.booster = WhiteCat.getRoleBooster();
	proxy = this.booster.removeUntilRole(agent, proxy, dba);
	
	// the proxy now should not have the role, so it should not implement the interface
	if( (proxy instanceof IDatabaseAdministrator) )
	    fail("The proxy has still the public role!");
	
	
	// the proxy class now should be the original one
	if( ! proxy.getClass().equals( DBProxy.class ) )
	    fail("The agent proxy without the role is not the same as we start with!");
	
    }

    
    @Test
    public void testVisibleRoleInjection() throws WCException {
	// create a new proxy and a new booster
	AgentProxy proxy = new DBProxy();
	WCAgent agent = new DBAgent();
	this.booster = WhiteCat.getRoleBooster();
	
	// the proxy now should not have any role property
	if( this.booster.hasPublicRoleAnnotation(proxy) || this.booster.hasPublicRoleInterface(proxy) )
	    fail("Unmanipulated proxy with interface/annotation for a role!");
	
	// create a visible role
	IRole visibleRole = new LoggerRole();
	
	// inject the visible role
	proxy = this.booster.injectVisibleRole( agent, proxy, visibleRole );
	

	
	// 1) the new proxy must have the original class as parent
	if( proxy.getClass().equals( DBProxy.class) || (! proxy.getClass().getSuperclass().equals( DBProxy.class ) ) )
	    fail("The proxy with the public role has not the right inheritance chain!");
	
	// 2) the new proxy should have a role annotation but not a public one
	if(  this.booster.hasPublicRoleAnnotation(proxy) )
	    fail("The proxy with the visible role has a public role annotation!");
	
	if( ! this.booster.hasRoleAnnotation(proxy) )
	    fail("The proxy does not have a role annotation!" );
	
    }
    
    
  
}
