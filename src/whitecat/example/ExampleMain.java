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
package whitecat.example;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import whitecat.core.IProxyHandler;
import whitecat.core.IRoleBooster;
import whitecat.core.ProxyStorage;
import whitecat.core.RoleBooster;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.ProxyHandlerFactory;
import whitecat.core.annotation.Role;
import whitecat.core.event.Event;
import whitecat.core.event.EventDispatcher;
import whitecat.core.event.EventListener;
import whitecat.core.role.RoleRepository;
import javassist.*;
import whitecat.core.role.descriptors.*;
/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */



public class ExampleMain extends DBProxy implements Runnable, EventListener{

    
    public void run(){
	System.out.println("Executing...");
	System.out.println("Thread: " + Thread.currentThread());
	System.out.println("Context loader: " + Thread.currentThread().getContextClassLoader() );

	try{
	    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
	    RoleBooster root = new RoleBooster( contextLoader );

	    Class roleClass = root.loadClass("whitecat.example.DatabaseAdministrator");
	    DatabaseAdministrator dbaRole = (DatabaseAdministrator) roleClass.newInstance();
	    Class agentClass = root.loadClass("whitecat.example.DBAgent");
	    DBAgent agent = (DBAgent) agentClass.newInstance();
	    Class proxyClass = root.loadClass("whitecat.example.DBProxy");
	    DBProxy proxy = (DBProxy) proxyClass.newInstance();
	    Class interfaceClass = root.loadClass("whitecat.example.IDatabaseAdministrator");
	    Class role2Class = root.loadClass("whitecat.example.LoggerRole");
	    LoggerRole loggerRole = (LoggerRole) role2Class.newInstance();
	    DatabaseUser dbu = (DatabaseUser) root.loadClass("whitecat.example.DatabaseUser").newInstance();

	    proxy.setMyAgent(agent);
	    boolean registered = proxy.addEventListener( this );
	    System.out.println("\tproxy registered " + registered);
	    System.err.println("\tproxy id " + proxy.getAgentProxyID());

	    //proxy.dump();

	    IRoleBooster engine = new RoleBooster(root);
	    AgentProxy newproxy = engine.injectPublicRole(agent, proxy, dbaRole);
	    
	    if( newproxy instanceof DBProxy )
		System.out.println("the proxy is still there!!");
	    
	    System.out.println("Role Engine loaded and manipulated the proxy " + newproxy + " class " + newproxy.getClass());

	    
	    IDatabaseAdministrator dba2 = (IDatabaseAdministrator) newproxy;

	    if( dba2 instanceof IDatabaseAdministrator ){
		System.out.println("The loaded proxy can be considered as an instance of IDatabaseAdministrato");
		System.out.println("The proxy class is " + newproxy.getClass() + " -> " + newproxy.getClass().getSuperclass());
		
	    }
	    
	    
	    dba2.createDatabase("RoleDB");


	    // remove the role from the proxy
	    IRoleBooster engine2 = new RoleBooster(root);
	    newproxy = engine2.removeUntilRole(agent, newproxy, dbaRole);
	    System.err.println("Id del proxy " + newproxy.getAgentProxyID());

	    if( newproxy instanceof IDatabaseAdministrator ){
		System.out.println("Role not removed from the proxy " + newproxy.getClass().hashCode() + " - " + newproxy.getClass().getSuperclass().hashCode());
		//((IDatabaseAdministrator)newproxy).backupDatabase("NOT_WORKING");
	    }
	    else
		System.out.println("\n\tProxy type " + newproxy.getClass());

	    if( newproxy instanceof DBProxy )
		((DBProxy) newproxy).dump();
	    
	    
	    newproxy = engine2.injectPublicRole(agent, newproxy, loggerRole);
	    if( newproxy instanceof ILogger )
		((ILogger) newproxy).log("Succesfully logging....");
	    
	    
	    
	    RoleBooster engine3 = new RoleBooster( root );
	    engine3 = root;
	    loggerRole = (LoggerRole)engine3.loadClass("whitecat.example.LoggerRole").newInstance();
	    newproxy = engine3.removePublicRole(agent, newproxy, dbaRole);
	    
	    if( newproxy instanceof IDatabaseAdministrator ){
		System.out.println("Still a database administrator");
		((IDatabaseAdministrator) newproxy).backupDatabase("DBLUCA");
	    }
	    if( newproxy instanceof ILogger ){
		((ILogger) newproxy).log("Simply a logger...");
	    }
		
	    
	    IRoleBooster engine4 = new RoleBooster(root);
	    newproxy = (DBProxy) proxyClass.newInstance();
	    
	    //engine4.loadClass( newproxy.getClass().getName());
	    newproxy = engine4.injectVisibleRole(agent, newproxy, dbaRole);
	   

	    /*System.out.println("New proxy created");
	    System.out.println("Classe " + newproxy.getClass() + " superclass "  + newproxy.getClass().getSuperclass());
	    System.out.println("Annotazioni "  + newproxy.getClass().getAnnotations().length);
	    for(Annotation a : newproxy.getClass().getAnnotations()){
		System.out.println("Annotazione => " + a);
		if( a.getClass().isAnnotationPresent(whitecat.core.annotation.Role.class) )
		    System.out.println("\n\t\tannotazione di ruolo\n\n");
		else
		    System.out.println("annotazione non di ruolo");
		
		for( Annotation b : a.getClass().getAnnotations() )
		    System.out.println("\tAnnotazione dell'annotazione " + b);

	    }
	    */
	    /*
	    System.out.println("Classe " + newproxy.getClass().getSuperclass());
	    System.out.println("Annotazioni "  + newproxy.getClass().getSuperclass().getAnnotations().length);
	    for(Annotation a : newproxy.getClass().getSuperclass().getAnnotations()){
		System.out.println("Annotazione => " + a);
		if( a.getClass().isAnnotationPresent(whitecat.core.annotation.Role.class) )
		    System.out.println("\n\t\tannotazione di ruolo\n\n");
		else
		    System.out.println("annotazione non di ruolo");
	    }
	    
	    */
	    
	    
	    newproxy = engine4.removeVisibleRole(newproxy, dbaRole);
	    System.out.println("New proxy class " + newproxy + " now analyzing annotations");
	    
	    for(Annotation a : newproxy.getClass().getAnnotations()){
		System.out.println("Annotation => " + a);
		if( a.getClass().isAnnotationPresent(whitecat.core.annotation.Role.class) )
		    System.out.println("\n\tRole annotation found " + a);
		
		for( Annotation b : a.getClass().getAnnotations() )
		    System.out.println("\tAnnotation nested " + b);

	    }

	    
	    
	    IProxyHandler handler = ProxyHandlerFactory.getProxyHandler();

    
	}
	catch(Exception e){
	    System.err.println("Exception " + e);
	    e.printStackTrace();
	}
	
	
	// dump the map
	ProxyStorage storage = ProxyStorage.getInstance();
	storage.dump( System.out );
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{

	RoleRepository repository = whitecat.core.role.RoleRepository.getInstance();
	repository.installRole( RoleDescriptor.getInstance("Luca", "An aim", null, null, null), new DatabaseAdministrator(), true);
	
	ExampleMain example = new ExampleMain();
	Thread t = new Thread(example, "Thread-Example-Main");
	t.start();
	/*
	Class r = DBRoleAnnotation.class;
	System.out.println("Annotation " + r + " has annotation " + r.getAnnotations().length);
	System.out.println("Exampe class " + example.getClass());
	for(Annotation a : example.getClass().getAnnotations()){
	    System.out.println("Annotation " + a + " has annotations " + a.getClass().getAnnotations().length);
	    System.out.println("Name " + a.annotationType());
	    System.out.println("Annotation class " + a.getClass() + " should be " + r);
	    Class annotationClass = Class.forName(a.getClass().getName());
	    System.out.println("Annotation right class " + annotationClass);
	    for( Annotation b: a.annotationType().getAnnotations() )
		System.out.println("\tAnnotation " + b);
	}
	*/    
	

    }

    public void handleEvent(Event event) {
	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	System.out.println("\tRECEIVED EVENT " + event);
	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	
    }

}
