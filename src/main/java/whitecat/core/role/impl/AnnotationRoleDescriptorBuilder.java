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
package whitecat.core.role.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import whitecat.core.role.IRole;
import whitecat.core.role.descriptors.EventDescriptor;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.descriptors.RoleDescriptor;
import whitecat.core.role.descriptors.TaskDescriptor;
import whitecat.core.role.descriptors.annotation.AnnotationEventDescriptor;
import whitecat.core.role.descriptors.annotation.AnnotationRoleDescriptor;
import whitecat.core.role.descriptors.annotation.AnnotationTaskDescriptor;
import whitecat.core.role.task.IRoleTask;
import whitecat.core.role.task.MethodTaskExecutor;

/**
 * A role descriptor builder that can analyze a role and build a role descriptor
 * from its annotations.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class AnnotationRoleDescriptorBuilder implements IRoleDescriptorBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.role.descriptors.IRoleDescriptorBuilder#buildRoleDescriptor
	 * (whitecat.core.role.IRole)
	 */
	public RoleDescriptor buildRoleDescriptor(final IRole role) {
		// check arguments
		if (role == null)
			return null;

		// get the class
		final Class clazz = role.getClass();

		// see if the class has a role annotation
		if (!clazz.isAnnotationPresent( AnnotationRoleDescriptor.class ))
			return null; // cannot proceed

		// a map of the tasks, used to combine subtasks
		final Map<String, MethodTaskExecutor> tasks = new HashMap<String, MethodTaskExecutor>();

		// a map of tasks and task descriptors used for the role descriptor
		final Map<IRoleTask, TaskDescriptor> roleTasks = new HashMap<IRoleTask, TaskDescriptor>();

		// get all the methods for the role
		for (final Method method : clazz.getMethods()){

			MethodTaskExecutor executor = null;
			TaskDescriptor taskDescriptor = null;

			// if the method is annotated as a task, this must be a task
			if (method.isAnnotationPresent( AnnotationTaskDescriptor.class )){
				// this method belongs to a task
				final AnnotationTaskDescriptor atd = method
						.getAnnotation( AnnotationTaskDescriptor.class );

				// construct a task executor thru reflection
				executor = new MethodTaskExecutor();
				executor.setMethodToExecute( method );
				executor.setExecutingRole( role );
				final String taskID = atd.taskID();

				// must this task need to be added to another task?
				if (tasks.containsKey( atd.addToTaskID() )){
					final IRoleTask mainTask = tasks.get( atd.addToTaskID() );
					mainTask.addSubTask( executor );
				}else
				// single task
				tasks.put( taskID, executor );

				// build a task descriptor for this task
				// construct the task descriptor
				taskDescriptor = TaskDescriptor.getInstance(
						atd.name(),
						atd.aim(),
						this.getKeywords( atd ),
						method.getReturnType(),
						parametersAsList( method.getParameterTypes() ) );

				// if I've got an event descriptor, add it to the task
				// descriptor
				if (method
						.isAnnotationPresent( AnnotationEventDescriptor.class )){

					final AnnotationEventDescriptor aed = method
							.getAnnotation( AnnotationEventDescriptor.class );

					// build an event descriptor and add to the task descriptor
					final EventDescriptor eventDescriptor = EventDescriptor
							.getInstance(
									aed.name(),
									aed.aim(),
									aed.issuing(),
									aed.receiving() );
					taskDescriptor.addEventDescriptor( eventDescriptor );
				}

				// now add the task and its descriptor to the tasks of the role
				// descriptor
				roleTasks.put( executor, taskDescriptor );

			}

		}

		// now get the annotation descriptor
		final AnnotationRoleDescriptor aDesc = (AnnotationRoleDescriptor) clazz
				.getAnnotation( AnnotationRoleDescriptor.class );

		// does this role has events?
		final List<EventDescriptor> events = null;
		if (clazz.isAnnotationPresent( AnnotationEventDescriptor.class )){
			final AnnotationEventDescriptor aed = (AnnotationEventDescriptor) clazz
					.getAnnotation( AnnotationEventDescriptor.class );

			// build an event descriptor
			final EventDescriptor ed = EventDescriptor.getInstance(
					aed.name(),
					aed.aim(),
					aed.issuing(),
					aed.receiving() );
			// add the event to the role events
			events.add( ed );
		}

		// build the role descriptor
		final RoleDescriptor roleDescriptor = RoleDescriptor.getInstance(
				aDesc.name(),
				aDesc.aim(),
				roleTasks,
				events,
				this.getKeywords( aDesc ) );

		// all done
		return roleDescriptor;

	}

	/**
	 * Provides the set of keywords from an annotation descriptor.
	 * 
	 * @param ad
	 *            the annotation descriptor to analyze
	 * @return the set of keywords or an empty list
	 */
	private Set<String> getKeywords(final AnnotationRoleDescriptor ad) {
		final Set<String> keywords = new HashSet<String>();
		if ((ad.keywords() != null) && (ad.keywords().length() > 0)){
			final StringTokenizer tokenizer = new StringTokenizer(
					ad.keywords(), ad.keywordsSeparator() );
			while (tokenizer.hasMoreTokens())
				keywords.add( tokenizer.nextToken().trim() );
		}

		return keywords;
	}

	/**
	 * Provides the set of keywords from an annotation task descriptor.
	 * 
	 * @param ad
	 *            the annotation descriptor to analyze
	 * @return the set of keywords or an empty list
	 */
	private Set<String> getKeywords(final AnnotationTaskDescriptor ad) {
		final Set<String> keywords = new HashSet<String>();
		if ((ad.keywords() != null) && (ad.keywords().length() > 0)){
			final StringTokenizer tokenizer = new StringTokenizer(
					ad.keywords(), ad.keywordsSeparator() );
			while (tokenizer.hasMoreTokens())
				keywords.add( tokenizer.nextToken().trim() );
		}

		return keywords;
	}

	/**
	 * Converts an array of class to a list.
	 * 
	 * @param pars
	 *            the parameters
	 * @return the list of parameters
	 */
	private List<Class> parametersAsList(final Class[] pars) {
		final List<Class> parameters = new LinkedList<Class>();

		for (final Class p : pars)
			parameters.add( p );

		return parameters;
	}

}
