/* 
 * WhiteCat - A dynamic role injector for agents.
 *
 * This project represents a new implementation of the so called BlackCat,
 * a project I made during my thesis degree. For more information about such project please see:
 * 
 *   G. Cabri, L. Ferrari, L. Leonardi,
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
package whitecat.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.SecureClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IClonableAgentProxy;
import whitecat.core.agents.IMethodForwarderGenerator;
import whitecat.core.agents.WCAgent;
import whitecat.core.annotations.PUBLICROLE;
import whitecat.core.annotations.ROLE;
import whitecat.core.exceptions.WCForwarderMethodException;
import whitecat.core.role.IManipulatedClass;
import whitecat.core.role.IRole;

/**
 * This is the central role engine of the White Cat approach. Each role
 * operation is performed starting from this class.
 * 
 * The idea is the following: once an agent requires a role injection, the role
 * is applied to the proxy of the agent, that must be passed to the role engine
 * itself. From the proxy it is created a subclass (compatible with the proxy
 * class itself) and this subclass is forced to implement the role interface.
 * Each method of such implementation is used as a forwarder to the real role
 * instance, that is owned by the agent itself.
 * 
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 * 
 */
public class RoleBooster extends SecureClassLoader implements IRoleBooster {

	/**
	 * The logger for this class loader.
	 */
	private static Logger				logger						= org.apache.log4j.Logger
																			.getLogger( RoleBooster.class );

	// configure the logger
	static{
		DOMConfigurator.configure( "whitecat.log4j.xml" );
	}

	/**
	 * The current role operation the role booster is working on or will work as
	 * next.
	 */
	private IRoleOperation				currentRoleOperation		= null;

	/**
	 * The class loader parent of this loader.
	 */
	private ClassLoader					parentLoader				= null;

	/**
	 * The parent class pool.
	 */
	private final ClassPool				myPool						= null;

	/**
	 * The method forwarder generator that must be used for the current
	 * manipulation.
	 */
	private IMethodForwarderGenerator	methodForwarderGenerator	= null;

	/**
	 * A counter for the number of manipulation performed by this engine. Such
	 * number will be used to create the name of the subclass that the engine is
	 * manipulating, this to prevent two classes to have the same name and thus
	 * to prevent a linkage error.
	 */
	private int							manipulationCounter			= 0;

	/**
	 * The default constructor for this role booster. Used from the spring
	 * framework.
	 * 
	 * @deprecated use the WhiteCat class in order to get a new instance of the
	 *             role booster
	 */
	@Deprecated
	public RoleBooster() {
		super();
		parentLoader = this.getClass().getClassLoader();
	}

	/**
	 * Constructs this loader as a child of a parent loader.
	 * 
	 * @param parentLoader
	 *            the parent class loader
	 * @deprecated use the WhiteCat class to get a new role booster and
	 *             configure it thru the spring framework
	 */
	@Deprecated
	public RoleBooster(final ClassLoader parentLoader) {
		super( parentLoader );

	}

	/**
	 * Creates this booster associating a parent booster to use a chained-Java
	 * class loader.
	 * 
	 * @param proxyClassName
	 *            the proxy to use
	 * @param parent
	 *            the parent role booster
	 * @throws WCException
	 * @{@link Deprecated} use the WhiteCat class to get a new role booster
	 *         instance, and configure it thru the Spring framework
	 */
	@Deprecated
	public RoleBooster(final String proxyClassName, final RoleBooster parent) throws WCException {
		super();
		try{
			parentLoader = parent;
		}catch (final Exception e){
			throw new WCException( e );
		}
	}

	/**
	 * This is the horse-power method used to add a public role to an agent
	 * (proxy). The method performs the following main steps: 1) obtains a proxy
	 * handler from the proxy handler factory in order to handle the proxy's
	 * state 2) obtains a method forwarder generator in order to construct the
	 * method forwarders for the role public methods. In its simpler form, the
	 * method forwarder will construct a method body that simply forwards the
	 * method call from the proxy (role) interface to the role one. 3) loads the
	 * new class with the new role interface added 4) copies the status of the
	 * previous proxy and complete the method forwarders.
	 * 
	 * @param publicRoleInterface
	 *            the interface that will be added to the proxy
	 * @param proxy
	 *            the proxy on which the method is going to work on
	 * @param role
	 *            the role to add to the proxy
	 * @param addition
	 *            true if the role is going to be added
	 * @return the new proxy instance
	 * @throws WCException
	 *             if something goes wrong
	 */
	private final AgentProxy addRoleToProxy(final Class publicRoleInterface,
											final AgentProxy proxy,
											final IRole role,
											final boolean addition)
																	throws WCException {

		try{

			// set the current role operation data
			currentRoleOperation.setPublicRoleInterface( publicRoleInterface );
			currentRoleOperation.setAgentProxy( proxy );
			currentRoleOperation.setRole( role );
			currentRoleOperation
					.setRoleInjectionType( RoleInjectionType.ROLE_PUBLIC_INTERFACE_ADDITION_TO_PROXY );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_STARTED );

			// obtain the proxy handler for this operation
			final IProxyHandler proxyHandler = currentRoleOperation
					.getAgentProxyHandler();
			// initialize the proxy handler with the proxy to use
			proxyHandler.setSourceProxy( proxy );

			// get the method forwarder generator to use for the operation
			final IMethodForwarderGenerator mGenerator = currentRoleOperation
					.getMethodForwarderGenerator();
			// initialize the method forwarder
			mGenerator.init( currentRoleOperation );
			// store it for further use within the findClass method
			setMethodForwarderGenerator( mGenerator );

			final Class newProxyClass = findClass( currentRoleOperation
					.getAgentProxy().getClass().getName() );
			final AgentProxy newProxy = (AgentProxy) newProxyClass
					.newInstance();
			// copy the proxy status
			newProxy.initializeByCopy( proxy );

			// if the old proxy instance is clonable, clone it now!
			// Please note that if the original agent proxy is clonable, also
			// the new one must be, but we check
			// for it in the case something in the manipulation process has
			// going bad!
			if ((proxy instanceof IClonableAgentProxy)
					&& (newProxy instanceof IClonableAgentProxy))
				((IClonableAgentProxy) newProxy)
						.cloneAgentProxyState( (IClonableAgentProxy) proxy );

			// store the role implementation as key for the proxy
			mGenerator.bindReferences( newProxy, role );

			// update the proxy
			proxyHandler.setDestinationProxy( newProxy );
			proxyHandler.updateProxy();

			// all done
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_SUCCESFUL );
			return newProxy;
		}catch (final Exception e){
			logger.error( "Exception caught while weaving a role ", e );
			logger.error( "Public role interface was " + publicRoleInterface );
			logger.error( "Agent proxy was " + proxy );
			logger.error( "Role was " + role );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE );

			throw new WCException( e );
		}

	}

	/**
	 * Creates the bytecode for the class of a proxy with the role annotation
	 * injected.
	 * 
	 * @param pool
	 *            the class pool to use
	 * @param baseProxyClass
	 *            the base proxy class to use as the superclass of the new proxy
	 *            class created
	 * @param subProxyClassName
	 *            the name for the subclass
	 * @return the byte array with the bytecode of the class with the annotation
	 * @throws CannotCompileException
	 *             if some problem occurs with javassist
	 * @throws IOException
	 *             if a problem occurs when converting the stream of a classfile
	 *             to a byte array
	 */
	private byte[] addRoleToProxyThruAnnotation(final ClassPool pool,
												final CtClass baseProxyClass,
												final String subProxyClassName)
																				throws CannotCompileException,
																				IOException {

		// create a new class that will be the subclass of the current base
		// proxy class
		final CtClass newProxyClass = pool.makeClass( subProxyClassName );
		// set the superclass of the class to the base proxy
		newProxyClass.setSuperclass( baseProxyClass );
		logger.debug( "Creating the new empty class: " + subProxyClassName
				+ " (superclass " + baseProxyClass.getName() + ")" );

		// place a default constructor in the new class (important, or
		// reflection will not be able to
		// create an instance of this class)
		logger.debug( "Creating a new empty constructor" );
		final CtConstructor constructor = new CtConstructor( null,
				newProxyClass );
		constructor.setBody( ";" );
		newProxyClass.addConstructor( constructor );

		// add the manipulated interface
		try{
			final CtClass manipulatedClassInterface = pool
					.get( "whitecat.core.role.IManipulatedClass" );
			newProxyClass.addInterface( manipulatedClassInterface );
		}catch (final NotFoundException e){
			logger.error(
					"Exception caught while loading the base manipulating tag interface",
					e );

		}

		// get the class file and add the annotation
		logger.debug( "Creating the class file for the created class and adding the annotation "
				+ currentRoleOperation.getRoleAnnotationClass() );
		final ClassFile classFile = newProxyClass.getClassFile();
		final ConstPool constantPool = classFile.getConstPool();
		final AnnotationsAttribute attr = new AnnotationsAttribute(
				constantPool, AnnotationsAttribute.visibleTag );
		final javassist.bytecode.annotation.Annotation a = new javassist.bytecode.annotation.Annotation(
				currentRoleOperation.getRoleAnnotationClass().getName(),
				constantPool );
		attr.setAnnotation( a );
		classFile.addAttribute( attr );
		classFile.setVersionToJava5();

		// transform the classfile into bytecode
		logger.debug( "Converting the class file output stream to a bytecode" );
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final DataOutputStream os = new DataOutputStream( bos );
		classFile.write( os );
		os.close();

		// all done
		return bos.toByteArray();
	}

	/**
	 * This method is used to add a role to the proxy of an agent. This method
	 * creates a subclass of the proxy and forces this class to implement the
	 * role itnerface. Each method of the interface is implemented as a
	 * forwarder to the role instance.
	 * 
	 * @param pool
	 *            the class pool used for the bytecode manipulation
	 * @param baseProxyClass
	 *            the proxy class that must be subclassed
	 * @param subProxyName
	 *            the fully qualified name of the proxy subclass, that is the
	 *            name which will be used in the Java system for such class
	 * @param publicRoleInterfaceName
	 *            the name of the public interface to inject into the proxy
	 *            class
	 * @param roleInstanceAccessKey
	 *            the string that will be used as a key for the role access thru
	 *            the proxy
	 * @param roleClassName
	 *            the name of the class of the concrete role implementation,
	 *            used to make a cast to right type of the object when the
	 *            forwarder methods will be created
	 * @return the Javassist CtClass object that represents the subclass of the
	 *         proxy
	 * @throws NotFoundException
	 *             if a class is not found in the Java system
	 * @throws CannotCompileException
	 *             if there is a problem with the compilation of the code
	 */
	private CtClass addRoleToProxyThruSubClass(	final ClassPool pool,
												final CtClass baseProxyClass,
												final String subProxyName,
												final String publicRoleInterfaceName,
												final String roleInstanceAccessKey,
												final String roleClassName,
												final IMethodForwarderGenerator methodForwarder)
																								throws NotFoundException,
																								CannotCompileException,
																								WCForwarderMethodException {

		// debug info
		if (logger.isDebugEnabled()){
			logger.debug( "addRoleToProxyThruSubClass: class pool is " + pool );
			logger.debug( "The base proxy class is: " + baseProxyClass );
			logger.debug( "The public role interface is "
					+ publicRoleInterfaceName );
			logger.debug( "The generated proxy subclass will have a name: "
					+ subProxyName );
			logger.debug( "The role instance access key is "
					+ roleInstanceAccessKey );
		}

		// as first step I need to get the role interface class object, so that
		// it is possible to analyze the role public interface as it is
		final CtClass roleInterface = pool.get( publicRoleInterfaceName );
		logger.info( "Loaded the public role interface " + roleInterface );

		// create a new empty class that will be the subclass of the current
		// proxy, and
		// will have the superclass set to the current proxy class
		final CtClass subProxy = pool.makeClass( subProxyName );
		// set the superclass of this class to the current proxy class
		subProxy.setSuperclass( baseProxyClass );
		// add the public interface to the new created class
		subProxy.addInterface( roleInterface );
		logger.info( "Created a new empty class subclass of the current proxy" );

		// So far, the new created proxy subclass is implementing the role
		// public interfcae,
		// but all its methods are abstract and need to be implemented. The
		// implementation
		// must forward each call to the concrete role implementation, so that
		// once
		// the method X is called on the proxy, this will forward the call (with
		// all the
		// arguments) to the method X of the role instance (owned by the agent).
		// To construct the forwarding methods, I need to iterate over each
		// method of the role
		// interface, and build a new method (with source code) that will be
		// applied to the new proxy
		// subclass.

		// iterate over each method of the interface
		for (final CtMethod interfaceMethod : roleInterface
				.getDeclaredMethods()){
			logger.debug( "Analyzing the interface " + roleInterface.getName() );
			logger.debug( "Creating the method forwared for the method "
					+ interfaceMethod );
			/*
			 * // construct the Java source code for the new method StringBuffer
			 * methodCode = new StringBuffer(200);
			 * methodCode.append(" public ");
			 * methodCode.append(interfaceMethod.getReturnType().getName());
			 * methodCode.append(" ");
			 * methodCode.append(interfaceMethod.getName());
			 * methodCode.append("( "); CtClass params[] =
			 * interfaceMethod.getParameterTypes(); for(int parNo = 0; params !=
			 * null && parNo < params.length; parNo++ ){ if( parNo > 0 )
			 * methodCode.append(","); // more than one parameter, separate them
			 * with comma
			 * 
			 * methodCode.append( params[parNo].getName() ); // parameter type
			 * methodCode.append(" param" + parNo); // parameter identifier }
			 * 
			 * methodCode.append(" ) ");
			 * 
			 * // body definition: I need to cast the reference extracted from
			 * the proxy // to an object of the type of the role, and than I
			 * need to invoke the method // passing all the argument of the
			 * forwarder one. If the method has a return type // different from
			 * void, that I also need to insert a return statement.
			 * methodCode.append("{ ");
			 * 
			 * if( ! "void".equals(interfaceMethod.getReturnType().getName()) )
			 * methodCode.append(" return "); methodCode.append(" ((");
			 * methodCode.append( roleClassName ); methodCode.append(" ) ");
			 * methodCode.append(" this.roleMap.get(\""); methodCode.append(
			 * roleInstanceAccessKey ); methodCode.append("\")).");
			 * methodCode.append(interfaceMethod.getName());
			 * methodCode.append("($$); }");
			 */
			// generate the method source code
			final String methodCode = methodForwarder
					.getMethodForwarderCode( interfaceMethod );

			// debug info
			if (logger.isDebugEnabled()){
				logger.debug( "Creating a forwarder method for the role method "
						+ interfaceMethod );
				logger.debug( "The forwarder method code is \n\t" + methodCode );
			}

			// compile the dynamic method and add it to the subProxy class
			final CtMethod definedMethod = CtMethod.make( methodCode, subProxy );
			subProxy.addMethod( definedMethod );
		}

		logger.info( "Definition of the subproxy class completed" );
		return subProxy;
	}

	/**
	 * An utility method to see if a specific annotation is annotated with
	 * another class. This method uses reflection in a particular way to load
	 * the annotation thru its symbolic name, since the annotation injected from
	 * the role booster belong to a special class <i>$proxy</i> and so it is not
	 * possible to use the same reflective methods.
	 * 
	 * @param toAnalize
	 *            the annotation to analize
	 * @param annotationToSearchFor
	 *            the class of the other annotation to search for
	 * @return true if the annotation is found in this annotation (or its
	 *         annotations)
	 */
	private boolean annotationIsAnnotated(final Annotation toAnalize,
											final Class annotationToSearchFor) {

		// create the name of the annotation: skip java.lang annotations and
		// remove special chars
		String annotationClassName = toAnalize.toString();
		if (annotationClassName.contains( "java.lang" ))
			return false;

		annotationClassName = annotationClassName.replace( '@', ' ' );
		annotationClassName = annotationClassName.replace( '(', ' ' );
		annotationClassName = annotationClassName.replace( ')', ' ' );
		annotationClassName = annotationClassName.trim();
		Class nestedAnnotationClass;

		try{
			// load the class from its name
			nestedAnnotationClass = Class.forName( annotationClassName );

			// search for the annotation
			if (nestedAnnotationClass
					.isAnnotationPresent( annotationToSearchFor ))
				return true;
			else
			// if here not found, so loop recursively among nested annotations
			for (final Annotation ann : nestedAnnotationClass.getAnnotations())
				if (annotationIsAnnotated( ann, annotationToSearchFor ))
					return true;

		}catch (final ClassNotFoundException e){
			// cannot get access to the annotations
			logger.error( "Exception while iterating thru exceptions", e );
			return false;
		}

		return false;
	}

	@Override
	protected Class findClass(final String name) throws ClassNotFoundException {
		try{
			// get the class pool for working with classes and modifying them on
			// the fly
			final ClassPool pool = ClassPool.getDefault();
			CtClass baseProxyClass = null;

			// the array that will store the in-memory byte code
			byte[] bytecode = null;

			String subProxyClassName = null;

			if (isManipulationActive()){
				// it is required to weave the role to the proxy.
				logger.debug( "Loading a class manipulating it - role injection type="
						+ currentRoleOperation.getRoleInjectionType() );

				// construct the pool for loading and manipulating the classes
				logger.info( "Obtaining the proxy class from the Javassist pool...."
						+ currentRoleOperation.getAgentProxy().getClass()
								.getName() );
				logger.info( "My pool is " + pool.getClass() + " hash "
						+ pool.hashCode() );
				baseProxyClass = pool.get( currentRoleOperation.getAgentProxy()
						.getClass().getName() );
				logger.info( "Base proxy class obtained from the pool "
						+ baseProxyClass.hashCode() );
				subProxyClassName = baseProxyClass.getName();

				if (currentRoleOperation.getRoleInjectionType() == RoleInjectionType.ROLE_PUBLIC_INTERFACE_ADDITION_TO_PROXY){
					// the subclass will have a name composed by a dynamic
					// identifier
					// with the fixed string _roled and the id of this role
					// engine (getID).
					// For instance, if the proxy is whitecat.example.DBProxy,
					// it will become
					// whitecat.example.DBProxy_roled3663
					subProxyClassName += getSubClassNameSuffix();
					baseProxyClass = addRoleToProxyThruSubClass( pool, // the
																		// pool
																		// to
																		// use
																		// for
																		// loadtime
																		// manipulation
							baseProxyClass, // the current proxy class
							subProxyClassName, // the name of the subclass of
												// the proxy that will be
												// created
							currentRoleOperation.getPublicRoleInterface()
									.getName(), // the name of the public role
												// interface
							currentRoleOperation
									.getRoleImplementationAccessKey(), // the
																		// access
																		// key
																		// to
																		// use
																		// for
																		// the
																		// retriving
																		// the
																		// role
																		// instance
							currentRoleOperation.getRole().getClass().getName(), // the
																					// name
																					// of
																					// the
																					// concrete
																					// role
																					// implementation
							currentRoleOperation.getMethodForwarderGenerator() // the
																				// method
																				// forwarder
																				// generator
					);
				}else if (currentRoleOperation.getRoleInjectionType() == RoleInjectionType.ROLE_PUBLIC_INTERFACE_REMOVAL_FROM_PROXY){

					// get the current class of the proxy
					final Class currentClass = currentRoleOperation
							.getAgentProxy().getClass();

					final Class interfaces[] = currentClass.getInterfaces();
					for (int i = 0; (interfaces != null)
							&& (i < (interfaces.length)); i++){
						final Class currentInterface = interfaces[i];
						// is the current interface a role interface?
						if (!currentInterface.getName().equals(
								currentRoleOperation.getPublicRoleInterface()
										.getName() )){
							// this proxy level does not have the role that must
							// be removed,
							// so inject the role in the new proxy class
							subProxyClassName += getSubClassNameSuffix();
							final String interfaceName = currentRoleOperation
									.getPublicRoleInterface().getName();
							final String roleImplementationAccessKey = currentRoleOperation
									.getRoleImplementationAccessKey();
							currentRoleOperation.getMethodForwarderGenerator()
									.init(
											baseProxyClass.getName(),
											interfaceName,
											roleImplementationAccessKey );
							baseProxyClass = addRoleToProxyThruSubClass( pool, // the
																				// pool
																				// to
																				// use
																				// for
																				// loadtime
																				// manipulation
									baseProxyClass, // the current proxy class
									subProxyClassName, // the name of the
														// subclass of the proxy
														// that will be created
									interfaceName, // the name of the public
													// role interface
									roleImplementationAccessKey, // the access
																	// key to
																	// use for
																	// the
																	// retriving
																	// the role
																	// instance
									interfaceName, // the name of the concrete
													// role implementation
									currentRoleOperation
											.getMethodForwarderGenerator() // the
																			// method
																			// forwarder
																			// generator
							);
						}
					}

				}else if (currentRoleOperation.getRoleInjectionType() == RoleInjectionType.ROLE_ANNOTATION_ADDITION_TO_PROXY){
					// create a subclass of the current proxy and add the role
					// annotation to it
					subProxyClassName += getSubClassNameSuffix();
					bytecode = addRoleToProxyThruAnnotation(
							pool,
							baseProxyClass,
							subProxyClassName );
					baseProxyClass = null;
				}else if (currentRoleOperation.getRoleInjectionType() == RoleInjectionType.ROLE_ANNOTATION_REMOVAL_FROM_PROXY){
					// get the current class of the proxy
					Class currentClass = currentRoleOperation.getAgentProxy()
							.getClass();
					Class originalProxyClass = null;

					// now start from this point in the inheritance chain, and
					// add each
					// role annotation that is different from the one I need to
					// exclude
					final List<Annotation> roleAnnotationsToKeep = new LinkedList<Annotation>();

					do{
						logger.debug( "Checking the class " + currentClass );

						// check if the current class has an annotation that is
						// a role and must
						// be kept (i.e., if it is a @Role annotated annotation
						// and the name is not
						// the one I must remove
						for (final Annotation anno : currentClass
								.getAnnotations()){
							logger.debug( "Analyzing annotation " + anno );
							if (isRoleAnnotation( anno )
									&& (!(anno.annotationType().getName()
											.equals( currentRoleOperation
													.getRoleAnnotationClass()
													.getName() )))){
								logger.debug( "Keeping the role annotation "
										+ anno );
								roleAnnotationsToKeep.add( anno );
							}
						}

						// is this the original proxy class?
						if (!isManipulatedClass( currentClass )
								&& (originalProxyClass == null))
							originalProxyClass = currentClass;

						// go to the superclass
						currentClass = currentClass.getSuperclass();

					}while ((currentClass != null)
							&& (currentClass != Object.class));

					// now I know each role annotation I must keep, and I can
					// start again
					// adding all the roles to the proxy class
					if (!roleAnnotationsToKeep.isEmpty()){
						final Iterator<Annotation> iter = roleAnnotationsToKeep
								.iterator();
						while (iter.hasNext()){
							final Annotation toAdd = iter.next();
							currentRoleOperation
									.setRoleAnnotationClass( Class
											.forName( toAdd.annotationType()
													.getName() ) );
							subProxyClassName += getSubClassNameSuffix();
							bytecode = addRoleToProxyThruAnnotation(
									pool,
									baseProxyClass,
									subProxyClassName );
							baseProxyClass = null;
						}
					}else{
						// no annotations to keep, so return the original proxy
						// class
						return originalProxyClass;
					}

				}else{
					// TODO this should never be used
					subProxyClassName = baseProxyClass.getSuperclass()
							.getName();
					baseProxyClass = removeRoleFromProxyThruSuperclass(
							pool,
							baseProxyClass,
							name );
				}

			}else
			// load the class without manipulation
			baseProxyClass = pool.get( name );

			if ((parentLoader == null) || isManipulationActive()){
				if (!isAnnotationInjection())
					bytecode = baseProxyClass.toBytecode();

				logger.info( "Defining the class manipulated name="
						+ subProxyClassName );
				return this.defineClass(
						subProxyClassName,
						bytecode,
						0,
						bytecode.length );
			}else{
				logger.debug( "Asking the parent class loader to load the class "
						+ name );
				return super.loadClass( name );
			}

			// return defineClass(name, b, 0, b.length);
		}catch (final NotFoundException e){
			logger.error( "Exception caught while defining a class", e );
			throw new ClassNotFoundException();
		}catch (final IOException e){
			throw new ClassNotFoundException();
		}catch (final CannotCompileException e){
			logger.error(
					"Cannot compile exception caught while removing a role",
					e );
			throw new ClassNotFoundException();
		}catch (final WCForwarderMethodException e){
			logger.error( "Method forwarding exception!", e );
			throw new ClassNotFoundException();
		}
	}

	public synchronized IRoleOperation getCurrentRoleOperation() {
		return currentRoleOperation;
	}

	/**
	 * Returns an identifier of this Role Engine. The default implementation is
	 * to return the Java object hash code as identifier. This method is used to
	 * construct an unique name of role implementation key.
	 * 
	 * @return the identifier of this role engine instance
	 */
	protected final int getID() {
		return hashCode();
	}

	/**
	 * Provides the value of the manipulationCounter field. Please note that the
	 * manipulation counter is automatically increased by this operation, thus
	 * the counter acts like an SQL sequence: once it has been read, it is also
	 * increased.
	 * 
	 * @return the manipulationCounter
	 */
	protected synchronized final int getManipulationCounter() {
		return ++manipulationCounter;
	}

	/**
	 * Provides the value of the methodForwarderGenerator field.
	 * 
	 * @return the methodForwarderGenerator
	 */
	protected synchronized final IMethodForwarderGenerator getMethodForwarderGenerator() {
		return methodForwarderGenerator;
	}

	/**
	 * Provides the value of the myPool field.
	 * 
	 * @return the myPool
	 */
	protected synchronized final ClassPool getPool() {
		return myPool;
	}

	/**
	 * Provides the class object of the public role annotation, loading it thru
	 * reflection.
	 * 
	 * @param role
	 *            the role to analyze
	 * @return the class of the annotation or null
	 */
	public final Class getPublicRoleAnnotationClass(final IRole role) {
		// load the class of the annotation
		try{
			return Class.forName( this.getPublicRoleAnnotationName( role ) );
		}catch (final ClassNotFoundException e){
			logger.error(
					"Cannot load the annotation class, maybe it is wrong?",
					e );
			return null;
		}
	}

	/**
	 * Provides the name of the annotation of a public role.
	 * 
	 * @param proxy
	 *            the proxy to check
	 * @return the fully qualified name of the annotation for the role
	 */
	public final String getPublicRoleAnnotationName(final AgentProxy proxy) {
		// check arguments
		if ((proxy == null) || (!(hasPublicRole( proxy, true ))))
			return "";

		// get the annotation from the proxy and return the annotation class
		// name
		final PUBLICROLE annotation = proxy.getClass().getAnnotation(
				PUBLICROLE.class );
		return annotation.roleAnnotation();
	}

	/**
	 * Provides the role annotation class name, that is contained as a string
	 * (fully qualified name) into the PublicRole annotation of a role.
	 * 
	 * @param role
	 *            the role to analyze
	 * @return the annotation fully qualified name
	 */
	public final String getPublicRoleAnnotationName(final IRole role) {
		// check arguments
		if ((role == null)
				|| (!(role.getClass().isAnnotationPresent( PUBLICROLE.class ))))
			return null;

		// get the annotation
		final PUBLICROLE pRole = role.getClass().getAnnotation(
				PUBLICROLE.class );
		return pRole.roleAnnotation();
	}

	/**
	 * Provides the name of the role class for a public role. It searches the @PublicRole
	 * annotation and returns the name indicated into it.
	 * 
	 * @param proxy
	 *            the proxy to search for
	 * @return the fully qualified name of the role class
	 */
	public final String getPublicRoleClassName(final AgentProxy proxy) {
		// check arguments
		if ((proxy == null) || (!(hasPublicRole( proxy, false ))))
			return "";

		// get the annotation role, it must a public role annotation
		// (@PublicRole)
		final PUBLICROLE annotation = proxy.getClass().getAnnotation(
				PUBLICROLE.class );
		return annotation.roleClass();
	}

	/**
	 * An utility method to load the public role interface name from the role
	 * implementation.
	 * 
	 * @param role
	 *            the role implemenetation
	 * @return the class of the interface tied to the role
	 */
	public final Class getPublicRoleInterfaceClass(final IRole role) {
		try{
			return Class.forName( this.getPublicRoleInterfaceName( role ) );
		}catch (final ClassNotFoundException e){
			logger.error( "Cannot load the interface role class", e );
			return null;
		}
	}

	/**
	 * Provides the name of the interface marked as public interface for a
	 * public role. It searches the @PublicRole annotation and returns the name
	 * indicated into it.
	 * 
	 * @param proxy
	 *            the proxy to search for
	 * @return the fully qualified name of the role interface
	 */
	public final String getPublicRoleInterfaceName(final AgentProxy proxy) {
		// check arguments
		if ((proxy == null) || (!(hasPublicRole( proxy, false ))))
			return "";

		// get the annotation role, it must a public role annotation
		// (@PublicRole)

		final PUBLICROLE annotation = proxy.getClass().getAnnotation(
				PUBLICROLE.class );
		return annotation.roleInterface();
	}

	/**
	 * Provides the public role interface name, that is the interface that must
	 * be applied to the agent proxy.
	 * 
	 * @param role
	 *            the role implementation, that must be annotated with the @PublicRole
	 *            annotation
	 * @return the name of the interface or null, if the role does not provide a
	 *         public interface
	 */
	public final String getPublicRoleInterfaceName(final IRole role) {
		// check arguments
		if ((role == null)
				|| (!(role.getClass().isAnnotationPresent( PUBLICROLE.class ))))
			return null;

		// get all the annotation of this class and search for a public role
		// annotation
		final PUBLICROLE pRole = role.getClass().getAnnotation(
				PUBLICROLE.class );
		return pRole.roleInterface();
	}

	/**
	 * Returns the name suffix to append to a class name in order to get a new
	 * name for a class. The postfix is built with the fixed string "_roled_"
	 * and the manipulation counter (i.e., how many classes this engione has
	 * manipulated) and the id of the engine itself.
	 * 
	 * @return the string that must be used as suffix for the class name
	 */
	protected final String getSubClassNameSuffix() {
		return "_roled_" + getManipulationCounter() + "_" + getID();
	}

	/**
	 * This method checks for the validity of a role annotation. The method
	 * checks if the proxy is annotated with the PublicRole annotation, and then
	 * checks if the annotation has a not-null value for the role itnerface or
	 * the role annotation.
	 * 
	 * @param roled
	 *            the proxy to check
	 * @param annotation
	 *            true if the PublicRole.annotationClass() must be not-empty,
	 *            false if the PublicRole.roleInterface() must be not-empty.
	 * @return true if the proxy has the role with the specified settings
	 */
	private final boolean hasPublicRole(final AgentProxy roled,
										final boolean annotation) {
		if (this.hasRoleAnnotation( roled, PUBLICROLE.class )){
			final PUBLICROLE pRole = roled.getClass().getAnnotation(
					PUBLICROLE.class );
			if (annotation)
				return (!pRole.roleAnnotation().equals( "" ));
			else return (!pRole.roleInterface().equals( "" ));

		}else return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#hasPublicRoleAnnotation(whitecat.core.agents
	 * .AgentProxy)
	 */
	public final boolean hasPublicRoleAnnotation(final AgentProxy proxy) {
		return hasPublicRole( proxy, true );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#hasPublicRoleInterface(whitecat.core.agents
	 * .AgentProxy)
	 */
	public final boolean hasPublicRoleInterface(final AgentProxy proxy) {
		return hasPublicRole( proxy, false );
	}

	/**
	 * Checks if the current proxy has the role annotation. This implies that
	 * the proxy has at least a visible role.
	 * 
	 * @param proxy
	 *            the proxy instance to analyze.
	 * @return true if the proxy has a role annotation or is annotated with an
	 *         annotation that has a role annotation
	 */
	public final boolean hasRoleAnnotation(final AgentProxy proxy) {
		return this.hasRoleAnnotation( proxy, ROLE.class );
	}

	/**
	 * This method searches for a role type within a proxy of an agent. The
	 * method searches between the annotations of the proxy to see if the
	 * specified annotation type is present between those of the proxy.
	 * 
	 * @param roled
	 *            the proxy to analyze
	 * @param annotationClass
	 *            the type of annotation (and thus of role visibility) to search
	 *            for
	 * @return true if the proxy has the specified role
	 */
	final boolean hasRoleAnnotation(final AgentProxy roled,
									final Class annotationClass) {
		// check arguments
		if (roled == null)
			return false;

		// search among the (possibly present) annotations of the agent proxy
		// to see if the agent has a role or not
		final Class proxyClass = roled.getClass();

		// check if the class has the specified annotation
		if (proxyClass.isAnnotationPresent( annotationClass ))
			return true;
		else{
			// if here the current proxy class does not have any role
			// annotation, but this does not mean
			// that the proxy does not has a role, I need to search among the
			// annotations of the proxy
			// to see if one of them has the role annotation
			for (final Annotation annotation : proxyClass.getAnnotations())
				if (annotationIsAnnotated( annotation, annotationClass ))
					return true;

			// if here the annotation has not been found even in the annotations
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#injectPublicRole(whitecat.core.agents.WCAgent,
	 * whitecat.core.agents.AgentProxy, whitecat.core.role.IRole)
	 */
	public final synchronized AgentProxy injectPublicRole(	final WCAgent agent,
															final AgentProxy proxy,
															final IRole role)
																				throws WCException {
		// check arguments

		// 1) don't proceed if the agent or the role have not been specified
		if (role == null)
			return proxy;

		try{
			// get the public part of the role to apply to the agent
			final String publicRoleInterfaceName = this
					.getPublicRoleInterfaceName( role );
			// if I've got a role interface, then I have to add it to the proxy
			if (publicRoleInterfaceName != null){
				// here I have to weave the proxy with the public interface
				for (final Class publicRoleInterface : role.getClass()
						.getInterfaces())
					if (publicRoleInterface.getName().equals(
							publicRoleInterfaceName )){
						return this.addRoleToProxy(
								publicRoleInterface,
								proxy,
								role,
								true );
					}
			}
		}catch (final WCException e){
			logger.error( "Exception caught while adding the role interface", e );
		}

		return proxy;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#injectVisibleRole(whitecat.core.agents.WCAgent
	 * , whitecat.core.agents.AgentProxy, whitecat.core.role.IRole)
	 */
	public final synchronized AgentProxy injectVisibleRole(	final WCAgent agent,
															final AgentProxy proxy,
															final IRole role) {
		// check params
		if ((proxy == null) || (role == null))
			return proxy;

		// set the data for the current operation
		currentRoleOperation.setAgentProxy( proxy );
		currentRoleOperation
				.setRoleAnnotationClass( getPublicRoleAnnotationClass( role ) );
		currentRoleOperation
				.setPublicRoleInterface( getPublicRoleInterfaceClass( role ) );
		currentRoleOperation
				.setRoleInjectionType( RoleInjectionType.ROLE_ANNOTATION_ADDITION_TO_PROXY );
		currentRoleOperation
				.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_STARTED );

		try{
			// use the proxy handler for this operation
			final IProxyHandler proxyHandler = currentRoleOperation
					.getAgentProxyHandler();
			proxyHandler.setSourceProxy( proxy );

			// load the manipulated proxy class
			final Class newProxyClass = findClass( proxy.getClass().getName() );
			// create a new instance of the manipulated proxy
			final AgentProxy newProxy = (AgentProxy) newProxyClass
					.newInstance();
			proxyHandler.setDestinationProxy( newProxy );

			// update the proxies
			proxyHandler.updateProxy();

			// all done
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_SUCCESFUL );
			return newProxy;

		}catch (final Exception e){
			// TODO Auto-generated catch block
			logger.error(
					"Exception caught during role addition thru annotation",
					e );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE );
			return proxy;
		}
	}

	/**
	 * This method returns true if the role engine is involved in a manipulation
	 * that includes an annotation. The check is performed using the current
	 * operation, that contains and wraps all the information about the status
	 * of the currently undergoing operation.
	 * 
	 * @return true if the role engine is adding or removing an annotation from
	 *         a role
	 */
	protected final boolean isAnnotationInjection() {
		if ((currentRoleOperation == null)
				|| ((currentRoleOperation.getRoleInjectionType() != RoleInjectionType.ROLE_ANNOTATION_ADDITION_TO_PROXY) && (currentRoleOperation
						.getRoleInjectionType() != RoleInjectionType.ROLE_ANNOTATION_REMOVAL_FROM_PROXY)))
			return false;
		else return true;
	}

	/**
	 * Checks if the specified class has been manipulated by the role engine. A
	 * class is manipulated if it implements the IManipulatedClass interface.
	 * 
	 * @param classToCheck
	 *            the class to check
	 * @return true if the class implements the IManipulatedClass interface
	 */
	public final boolean isManipulatedClass(final Class classToCheck) {
		if (classToCheck == null)
			return false;

		for (final Class interfaces : classToCheck.getInterfaces())
			if (interfaces.equals( IManipulatedClass.class ))
				return true;

		return false;
	}

	/**
	 * Checks if the role booster is currently doing a manipulation. The check
	 * is performed thru the current operation, that contains the information
	 * about the status of the current operation itself. This method is used as
	 * a check in the findClass() one, so that if a manipulation is not active
	 * (i.e., started) then there is nothing to manipulate and so the class
	 * loader works as a standard class loader.
	 * 
	 * @return true if there is an undergoing operation, false otherwise
	 */
	private synchronized final boolean isManipulationActive() {
		if ((currentRoleOperation == null)
				|| (currentRoleOperation.getOperationStatus() != RoleOperationStatus.ROLE_OPERATION_STARTED))
			return false;
		else return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#isRoleAnnotation(java.lang.annotation.Annotation
	 * )
	 */
	public final boolean isRoleAnnotation(final Annotation annotation) {
		if (annotation == null)
			return false;

		final Class annotationType = annotation.annotationType();
		for (final Annotation subAnnotation : annotationType.getAnnotations())
			if (subAnnotation.annotationType().equals( ROLE.class ))
				return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#removePublicRole(whitecat.core.agents.WCAgent,
	 * whitecat.core.agents.AgentProxy, whitecat.core.role.IRole)
	 */
	public synchronized AgentProxy removePublicRole(final WCAgent agent,
													AgentProxy proxy,
													final IRole role)
																		throws WCException {
		// check params
		if ((agent == null) || (role == null))
			return null;

		// prepare the operation data
		currentRoleOperation.setAgentProxy( proxy );
		currentRoleOperation.setRole( role );
		currentRoleOperation.setPublicRoleInterface( role.getClass() );
		currentRoleOperation
				.setRoleInjectionType( RoleInjectionType.ROLE_PUBLIC_INTERFACE_REMOVAL_FROM_PROXY );
		currentRoleOperation
				.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_STARTED );

		try{
			// get a method forwarder for this operation
			final IMethodForwarderGenerator mGenerator = currentRoleOperation
					.getMethodForwarderGenerator();
			// do not initialize it, it will be done in the find class method
			setMethodForwarderGenerator( mGenerator );

			final Class currentClass = findClass( currentRoleOperation
					.getAgentProxy().getClass().getName() );
			final AgentProxy newProxy = (AgentProxy) currentClass.newInstance();

			// get a proxy handler to handle copies
			final ProxyHandler<AgentProxy> handler = (ProxyHandler<AgentProxy>) currentRoleOperation
					.getAgentProxyHandler();
			handler.setSourceProxy( proxy );
			handler.setDestinationProxy( proxy );
			handler.updateProxy();

			proxy = newProxy;

			// all done
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_SUCCESFUL );
			return proxy;

		}catch (final Exception e){
			logger.error(
					"Exception caught while removing a role from a proxy",
					e );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE );
			throw new WCException( e );
		}

	}

	public CtClass removeRoleFromProxyThruSuperclass(	final ClassPool pool,
														final CtClass nowProxyClass,
														final String proxyName)
																				throws NotFoundException,
																				CannotCompileException {
		for (final CtClass interf : nowProxyClass.getInterfaces())
			System.err.println( "Interfaccia " + interf );

		System.err.println( "Numero di interfacce implementate:"
				+ nowProxyClass.getInterfaces().length );
		return nowProxyClass.getSuperclass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#removeUntilRole(whitecat.core.agents.WCAgent,
	 * whitecat.core.agents.AgentProxy, whitecat.core.role.IRole)
	 */
	public synchronized AgentProxy removeUntilRole(final WCAgent agent,
													final AgentProxy proxy,
													final IRole role)
																		throws WCException {
		// check params
		if ((agent == null) || (role == null))
			return null;

		// prepare the operation data
		currentRoleOperation.setAgentProxy( proxy );
		currentRoleOperation.setRole( role );
		currentRoleOperation
				.setPublicRoleInterface( getPublicRoleInterfaceClass( role ) );
		currentRoleOperation
				.setRoleInjectionType( RoleInjectionType.ROLE_PUBLIC_INTERFACE_REMOVAL_FROM_PROXY );
		currentRoleOperation
				.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_STARTED );

		// I need to find the first superclass that is not implementing the role
		// interface, such superclass if the class I'm looking for
		Class currentClass = proxy.getClass();
		boolean found = false;

		do{
			// iterate on each interface of the current class
			final Class interfaces[] = currentClass.getInterfaces();
			for (int i = 0; (interfaces != null) && (i < interfaces.length)
					&& (!found); i++)
				if (interfaces[i].getName()
						.equals(
								currentRoleOperation.getPublicRoleInterface()
										.getName() ))
					found = true;

			// if not found, go with the superclass
			// ATTENTION: it could happen that the proxy has assumed only one
			// role, so only one interface!
			// In this case the class should be the superclass, because this
			// means that the subclass/current class
			// is the roled one!
			if ((!found) || (interfaces.length == 1))
				currentClass = currentClass.getSuperclass();

		}while (found == false);

		try{
			final AgentProxy newProxy = (AgentProxy) currentClass.newInstance();

			// get the proxy handler for this operation
			final IProxyHandler proxyHandler = currentRoleOperation
					.getAgentProxyHandler();
			// initialize the handler and synchronize the proxies
			proxyHandler.setSourceProxy( proxy );
			proxyHandler.setDestinationProxy( newProxy );
			proxyHandler.updateProxy();

			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_SUCCESFUL );

			return newProxy;

		}catch (final Exception e){
			logger.error(
					"Exception caught while removing a role from a proxy",
					e );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE );
			throw new WCException( e );
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.IRoleBooster#removeVisibleRole(whitecat.core.agents.AgentProxy
	 * , whitecat.core.role.IRole)
	 */
	public AgentProxy removeVisibleRole(final AgentProxy proxy, final IRole role) {
		try{

			// prepare data for the current operation
			currentRoleOperation.setAgentProxy( proxy );
			currentRoleOperation.setRole( role );
			currentRoleOperation
					.setRoleInjectionType( RoleInjectionType.ROLE_ANNOTATION_REMOVAL_FROM_PROXY );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_STARTED );

			// use the proxy handler for this operation
			final IProxyHandler proxyHandler = currentRoleOperation
					.getAgentProxyHandler();
			proxyHandler.setSourceProxy( proxy );

			final Class newProxyClass = findClass( proxy.getClass().getName() );
			final AgentProxy newproxy = (AgentProxy) newProxyClass
					.newInstance();

			// update the proxy
			proxyHandler.setDestinationProxy( newproxy );
			proxyHandler.updateProxy();

			// all done
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_SUCCESFUL );
			return newproxy;

		}catch (final Exception e){
			logger.error(
					"Exception cuaght while removing a role of annotation",
					e );
			currentRoleOperation
					.setOperationStatus( RoleOperationStatus.ROLE_OPERATION_COMPLETED_FAILURE );
			return proxy;
		}

	}

	public void setCurrentRoleOperation(final IRoleOperation operation) {
		currentRoleOperation = operation;
	}

	/**
	 * Sets the value of the methodForwarderGenerator field as specified by the
	 * value of methodForwarderGenerator.
	 * 
	 * @param methodForwarderGenerator
	 *            the methodForwarderGenerator to set
	 */
	protected synchronized final void setMethodForwarderGenerator(	final IMethodForwarderGenerator methodForwarderGenerator) {
		this.methodForwarderGenerator = methodForwarderGenerator;
	}

}
