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
package whitecat.core.role.task.scheduling;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.AgentProxyID;
import whitecat.core.exceptions.WCSchedulingException;
import whitecat.core.role.task.IRoleTask;
import whitecat.core.role.task.ITaskExecutionResult;

/**
 * The scheduler for the execution of tasks.
 * A task can be scheduled for the execution by one or more agents at the role assumption
 * or before the role release.
 * This allows agents to asynchonously cooperate each other asking for services
 * to be executed as soon as possible.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public interface ITaskScheduler {


    /**
     * Schedules a new task for the execution by the specified agent proxy at the specified event.
     * @param toExecute the task to execute
     * @param executor the agent proxy that will execute the task (or null if any agent proxy can execute
     * the task)
     * @param executorMatchPolicy the type of executor match (only one agent proxy or any agent proxy)
     * @param executionInstancePolicy when the task must be executed
     * @param result the task execution result to use for getting back the task execution result
     * @return true if the task has been scheduled, false if it cannot be scheduled
     * @throws WCSchedulingException if something bad goes with the scheduling
     */
    public boolean scheduleTask( IRoleTask toExecute, 
                                 AgentProxyID executor, 
                                 TaskSchedulingExecutor executorMatchPolicy, 
                                 TaskSchedulingInstant executionInstancePolicy, 
                                 ITaskExecutionResult result) throws WCSchedulingException;
    
    /**
     * Removes the scheduled task (if found).
     * @param toExecute the task to execute
     * @param executor the agent proxy that will execute the task (or null if any agent proxy can execute
     * the task)
     * @param executorMatchPolicy the type of executor match (only one agent proxy or any agent proxy)
     * @param executionInstancePolicy when the task must be executed
     * @param result the task execution result to use for getting back the task execution result
     * @return true if the task has been removed, false if it cannot be found
     * @throws WCSchedulingException if something bad goes with the scheduling
     */
    public boolean removeScheduledTask( IRoleTask toExecute, 
                                        AgentProxyID executor, 
                                        TaskSchedulingExecutor executorMatchPolicy, 
                                        TaskSchedulingInstant executionInstancePolicy, 
                                        ITaskExecutionResult result);
}
