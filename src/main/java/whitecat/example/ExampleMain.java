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

import whitecat.core.IProxyStorage;
import whitecat.core.IRoleBooster;
import whitecat.core.ProxyStorageImpl;
import whitecat.core.RoleBooster;
import whitecat.core.WhiteCat;
import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.ProxyHandlerFactory;
import whitecat.core.event.Event;
import whitecat.core.event.EventListener;
import whitecat.core.role.IRoleRepository;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */

public class ExampleMain extends DBProxy implements Runnable, EventListener {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final IRoleRepository repository = WhiteCat.getRoleRepository();
		repository
				.installRole( RoleDescriptor.getInstance(
						"Luca",
						"An aim",
						null,
						null,
						null ), new DatabaseAdministrator(), true );

		final ExampleMain example = new ExampleMain();
		final Thread t = new Thread( example, "Thread-Example-Main" );
		t.start();
		/*
		 * Class r = DBRoleAnnotation.class; System.out.println("Annotation " +
		 * r + " has annotation " + r.getAnnotations().length);
		 * System.out.println("Exampe class " + example.getClass());
		 * for(Annotation a : example.getClass().getAnnotations()){
		 * System.out.println("Annotation " + a + " has annotations " +
		 * a.getClass().getAnnotations().length); System.out.println("Name " +
		 * a.annotationType()); System.out.println("Annotation class " +
		 * a.getClass() + " should be " + r); Class annotationClass =
		 * Class.forName(a.getClass().getName());
		 * System.out.println("Annotation right class " + annotationClass); for(
		 * Annotation b: a.annotationType().getAnnotations() )
		 * System.out.println("\tAnnotation " + b); }
		 */

	}

	public void handleEvent(final Event event) {
		System.out.println( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		System.out.println( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		System.out.println( "\tRECEIVED EVENT " + event );
		System.out.println( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		System.out.println( "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );

	}

	public void run() {
		System.out.println( "Executing..." );
		System.out.println( "Thread: " + Thread.currentThread() );
		System.out.println( "Context loader: "
				+ Thread.currentThread().getContextClassLoader() );

		try{
			final ClassLoader contextLoader = Thread.currentThread()
					.getContextClassLoader();
			final RoleBooster root = new RoleBooster( contextLoader );

			final Class roleClass = root
					.loadClass( "whitecat.example.DatabaseAdministrator" );
			final DatabaseAdministrator dbaRole = (DatabaseAdministrator) roleClass
					.newInstance();

			final Class agentClass = root
					.loadClass( "whitecat.example.DBAgent" );
			final DBAgent agent = (DBAgent) agentClass.newInstance();
			final Class proxyClass = root
					.loadClass( "whitecat.example.DBProxy" );
			final DBProxy proxy = (DBProxy) proxyClass.newInstance();
			root.loadClass( "whitecat.example.IDatabaseAdministrator" );
			final Class role2Class = root
					.loadClass( "whitecat.example.LoggerRole" );
			LoggerRole loggerRole = (LoggerRole) role2Class.newInstance();
			root.loadClass( "whitecat.example.DatabaseUser" ).newInstance();

			proxy.setMyAgent( agent );
			final boolean registered = proxy.addEventListener( this );
			System.out.println( "\tproxy registered " + registered );
			System.err.println( "\tproxy id " + proxy.getAgentProxyID() );

			// proxy.dump();

			final IRoleBooster engine = new RoleBooster( root );
			AgentProxy newproxy = engine.injectPublicRole(
					agent,
					proxy,
					dbaRole );

			if (newproxy instanceof DBProxy)
				System.out.println( "the proxy is still there!!" );

			System.out.println( "Role Engine loaded and manipulated the proxy "
					+ newproxy + " class " + newproxy.getClass() );

			final IDatabaseAdministrator dba2 = (IDatabaseAdministrator) newproxy;

			if (dba2 instanceof IDatabaseAdministrator){
				System.out
						.println( "The loaded proxy can be considered as an instance of IDatabaseAdministrato" );
				System.out.println( "The proxy class is " + newproxy.getClass()
						+ " -> " + newproxy.getClass().getSuperclass() );

			}

			dba2.createDatabase( "RoleDB" );

			// remove the role from the proxy
			final IRoleBooster engine2 = new RoleBooster( root );
			newproxy = engine2.removeUntilRole( agent, newproxy, dbaRole );
			System.err.println( "Id del proxy " + newproxy.getAgentProxyID() );

			if (newproxy instanceof IDatabaseAdministrator){
				System.out.println( "Role not removed from the proxy "
						+ newproxy.getClass().hashCode() + " - "
						+ newproxy.getClass().getSuperclass().hashCode() );
				// ((IDatabaseAdministrator)newproxy).backupDatabase("NOT_WORKING");
			}else System.out.println( "\n\tProxy type " + newproxy.getClass() );

			if (newproxy instanceof DBProxy)
				((DBProxy) newproxy).dump();

			newproxy = engine2.injectPublicRole( agent, newproxy, loggerRole );
			if (newproxy instanceof ILogger)
				((ILogger) newproxy).log( "Succesfully logging...." );

			RoleBooster engine3 = new RoleBooster( root );
			engine3 = root;
			loggerRole = (LoggerRole) engine3.loadClass(
					"whitecat.example.LoggerRole" ).newInstance();
			newproxy = engine3.removePublicRole( agent, newproxy, dbaRole );

			if (newproxy instanceof IDatabaseAdministrator){
				System.out.println( "Still a database administrator" );
				((IDatabaseAdministrator) newproxy).backupDatabase( "DBLUCA" );
			}
			if (newproxy instanceof ILogger){
				((ILogger) newproxy).log( "Simply a logger..." );
			}

			final IRoleBooster engine4 = new RoleBooster( root );
			newproxy = (DBProxy) proxyClass.newInstance();

			// engine4.loadClass( newproxy.getClass().getName());
			newproxy = engine4.injectVisibleRole( agent, newproxy, dbaRole );

			/*
			 * System.out.println("New proxy created");
			 * System.out.println("Classe " + newproxy.getClass() +
			 * " superclass " + newproxy.getClass().getSuperclass());
			 * System.out.println("Annotazioni " +
			 * newproxy.getClass().getAnnotations().length); for(Annotation a :
			 * newproxy.getClass().getAnnotations()){
			 * System.out.println("Annotazione => " + a); if(
			 * a.getClass().isAnnotationPresent
			 * (whitecat.core.annotation.Role.class) )
			 * System.out.println("\n\t\tannotazione di ruolo\n\n"); else
			 * System.out.println("annotazione non di ruolo");
			 * 
			 * for( Annotation b : a.getClass().getAnnotations() )
			 * System.out.println("\tAnnotazione dell'annotazione " + b);
			 * 
			 * }
			 */
			/*
			 * System.out.println("Classe " +
			 * newproxy.getClass().getSuperclass());
			 * System.out.println("Annotazioni " +
			 * newproxy.getClass().getSuperclass().getAnnotations().length);
			 * for(Annotation a :
			 * newproxy.getClass().getSuperclass().getAnnotations()){
			 * System.out.println("Annotazione => " + a); if(
			 * a.getClass().isAnnotationPresent
			 * (whitecat.core.annotation.Role.class) )
			 * System.out.println("\n\t\tannotazione di ruolo\n\n"); else
			 * System.out.println("annotazione non di ruolo"); }
			 */

			newproxy = engine4.removeVisibleRole( newproxy, dbaRole );
			System.out.println( "New proxy class " + newproxy
					+ " now analyzing annotations" );

			for (final Annotation a : newproxy.getClass().getAnnotations()){
				System.out.println( "Annotation => " + a );
				if (a.getClass().isAnnotationPresent(
						whitecat.core.annotations.ROLE.class ))
					System.out.println( "\n\tRole annotation found " + a );

				for (final Annotation b : a.getClass().getAnnotations())
					System.out.println( "\tAnnotation nested " + b );

			}

			ProxyHandlerFactory.getProxyHandler();

		}catch (final Exception e){
			System.err.println( "Exception " + e );
			e.printStackTrace();
		}

		// dump the map
		final IProxyStorage storage = WhiteCat.getProxyStorage();
		((ProxyStorageImpl) storage).dump( System.out );
	}

}
