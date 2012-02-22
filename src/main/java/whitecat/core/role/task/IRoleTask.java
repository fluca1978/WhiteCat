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
 * Copyright (C) Luca Ferrari 2006-2012 - fluca1978 (at) gmail.com
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
package whitecat.core.role.task;

import whitecat.core.WCException;

/**
 * A role task is an operation associated to a role that can be used to achieve
 * the role aim(s). The idea is that each role has one or more tasks associated
 * to it that can be used to dynamically execute one or more role operations.
 * Tasks can be nested together, so that complex tasks (i.e., complex
 * operations) can be executed at once. A task can be configured (if needed)
 * adding, one at the time, all the parameters (values) that must be used at the
 * execution time.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public interface IRoleTask {

	/**
	 * Adds another task to this one, so that it will be executed along with the
	 * main task. This allows the task to be composed by several simpler tasks.
	 * Please note that there should be a check to avoid loops and adding
	 * several times the same task. Please note that the tasks will be executed
	 * in the order they are added!
	 * 
	 * @param toAdd
	 *            the task to add to this one
	 * @return true if the sub task has been added, false otherwise
	 */
	public boolean addSubTask(IRoleTask toAdd);

	/**
	 * Adds a parameter for the task execution. This method should be used to
	 * prepare parameters before the task is effectively executed. Please note
	 * that this method must be called with the right sequence in order to
	 * respect the sequence of parameters. This means that if you have to add,
	 * for instance, two parameters called <i>par1</i> and <i>par2</i> you
	 * should use this method as follows: <br>
	 * <code>
	 * task.addTaskParameter( par1 );
	 * task.addTaskParameter( par2 );
	 * task.execute();
	 * </code>
	 * 
	 * @param parameter
	 *            the parameter to add
	 */
	public void addTaskParameter(Object parameter);

	/**
	 * Executes this task (and all its sub-tasks) returning the result.
	 * 
	 * @return the result of the task (and subtasks) execution wrapped by a role
	 *         task execution result
	 * @throws WCException
	 *             if something goes wrong with the task execution
	 */
	public ITaskExecutionResult execute() throws WCException;

	/**
	 * Removes a task from the list of subtasks. This also means that the
	 * execution order will change accordingly.
	 * 
	 * @param toRemove
	 *            the task to remove
	 * @return true if the task has been removed, false otherwise
	 */
	public boolean removeSubTask(IRoleTask toRemove);

	/**
	 * Resets all the parameters, and allows the preparation of this task for
	 * another execution.
	 */
	public void resetParameters();
}
