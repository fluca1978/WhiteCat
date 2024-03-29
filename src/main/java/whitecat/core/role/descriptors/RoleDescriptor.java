/**
 * R4R -- Roles for Resources
 *
 * Copyright (C) Luca Ferrari 2007 - cat4hire@users.sourceforge.net
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
package whitecat.core.role.descriptors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import whitecat.core.role.task.IRoleTask;

/**
 * A descriptor for a role instance. A descriptor specified what a role does,
 * including which tasks and events are tied to itself, without specifying
 * directly what it does.
 * 
 * This is an implementation of role descriptor based on what you can read in
 * the article <i> Injecting roles in Java agents through runtime bytecode
 * manipulation IBM SYSTEMS JOURNAL, VOL 44, NO 1, 2005 </i>. The main
 * difference between this implementation and the one presented in the above
 * article is for a better modularity and adoption of collections instead of
 * arrays.
 * 
 * @author Luca Ferrari - cat4hire@users.sourceforge.net
 * 
 * 
 */
public class RoleDescriptor extends AbstractDescriptor {

	/**
	 * Factory method to create a role descriptor.
	 * 
	 * @param name
	 *            the name of the role
	 * @param aim
	 *            the aim of the role
	 * @param tasks
	 *            the list of tasks tied to such role
	 * @param events
	 *            the events directly tied to the role
	 * @param keywords
	 *            the list of keywords for the role
	 * @return the role descriptor.
	 */
	public static final RoleDescriptor getInstance(	final String name,
													final String aim,
													final Map<IRoleTask, TaskDescriptor> tasks,
													final List<EventDescriptor> events,
													final Set<String> keywords) {
		final RoleDescriptor descriptor = new RoleDescriptor();
		descriptor.setName( name );
		descriptor.setAim( aim );
		if (tasks == null)
			descriptor.tasks = new HashMap<IRoleTask, TaskDescriptor>();
		else descriptor.tasks = new HashMap<IRoleTask, TaskDescriptor>( tasks );

		descriptor.setEventDscriptors( events );
		descriptor.setKeywords( keywords );

		return descriptor;
	}

	/**
	 * A map that contains all the tasks this role can execute. An IRoleTask is
	 * the executable unit of a role, and is described by a task descriptor that
	 * provides information about what the task does by a semantic way.
	 */
	private Map<IRoleTask, TaskDescriptor>	tasks				= null;

	/**
	 * A list of events untied from a particular task of this role.
	 */
	private List<EventDescriptor>			eventDescriptors	= null;

	/**
	 * The default constructor.
	 * 
	 */
	protected RoleDescriptor() {
		super();
	}

	/**
	 * Searches for a specific task among those of this role descriptor.
	 * 
	 * @param task
	 *            the task to search for
	 * @return true if the task is included
	 */
	public final synchronized boolean containsTask(final IRoleTask task) {
		return tasks.containsKey( task );
	}

	/**
	 * The number of the event descriptors directly tied to this role
	 * descriptor.
	 * 
	 * @return the number of event descriptors tied to this role descriptor.
	 */
	public final synchronized int eventDescriptorsCount() {
		return eventDescriptors.size();
	}

	/**
	 * Returns the total number of event descriptors, including those directly
	 * tied to the role descriptor and those tied to each task.
	 * 
	 * @return the total count of the event descriptors contained in this role
	 */
	public final synchronized int eventDescriptorsTotalCount() {
		int count = 0;
		count += eventDescriptors.size();

		for (final IRoleTask rt : tasks.keySet())
			count += tasks.get( rt ).eventDescriptorsCount();

		return count;
	}

	/**
	 * Provides all the event descriptors contained in this role descriptor,
	 * that is the descriptors tied to the role itself as well as the event
	 * descriptors tied to each single task.
	 * 
	 * @return the list of all the event descriptor, with no particular order.
	 *         Please note that the same event could be included several time in
	 *         the list, since it could be embedded by either the role or one
	 *         (or more) task.
	 */
	public final synchronized List<EventDescriptor> getAllEventDescriptors() {
		final LinkedList<EventDescriptor> descriptors = new LinkedList<EventDescriptor>();

		if ((eventDescriptors != null) && (eventDescriptors.size() > 0))
			descriptors.addAll( eventDescriptors );

		for (final IRoleTask rt : tasks.keySet())
			descriptors.addAll( tasks.get( rt ).getEventDescriptors() );

		return descriptors;
	}

	/**
	 * This method can be used to set the value of the eventDscriptors field,
	 * providing thus read access to the eventDscriptors property. Please note
	 * that this method will return only the events associated to this role
	 * descriptor, not the role of the tasks.
	 * 
	 * @return the eventDscriptors value.
	 */
	public final List<EventDescriptor> getEventDscriptors() {
		return new LinkedList<EventDescriptor>( eventDescriptors );
	}

	/**
	 * Provides the role descriptor from a task object.
	 * 
	 * @param task
	 *            the task that is going to be executed
	 * @return the role descriptor associated
	 */
	public final synchronized TaskDescriptor getTaskDescriptor(	final IRoleTask task) {
		return tasks.get( task );
	}

	/**
	 * This method can be used to get all the available task descriptors.
	 * 
	 * @return a list with the available task descriptors
	 */
	public final List<TaskDescriptor> getTaskDescriptors() {
		final LinkedList<TaskDescriptor> descriptors = new LinkedList<TaskDescriptor>();
		for (final IRoleTask task : tasks.keySet())
			descriptors.add( tasks.get( task ) );

		return descriptors;
	}

	/**
	 * Provides the list of tasks
	 * 
	 * @return the list of all available tasks for this role descriptor
	 */
	public final synchronized List<IRoleTask> getTasks() {
		final List<IRoleTask> tasks = new LinkedList<IRoleTask>();

		if (this.tasks != null)
			for (final IRoleTask task : this.tasks.keySet())
				tasks.add( task );

		return tasks;

	}

	/**
	 * This method can be used to set the value of the eventDscriptors field,
	 * providing thus write access to the eventDscriptors property.
	 * 
	 * @param eventDscriptors
	 *            the eventDscriptors to set with the specified value.
	 */
	protected synchronized final void setEventDscriptors(	final List<EventDescriptor> eventDscriptors) {
		eventDescriptors = eventDscriptors;
	}

	/**
	 * How many tasks this role descriptor (and therefore this role) has.
	 * 
	 * @return the number of tasks
	 */
	public final synchronized int tasksCount() {
		return (tasks == null ? 0 : tasks.size());
	}

}
