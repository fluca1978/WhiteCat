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
package whitecat.core.role.task;

import whitecat.core.WCException;

/**
 * Implements the result of a task execution.
 * A task can be deferred (i.e., scheduled), and in such case
 * the task execution result implements a future reply pattern, that is
 * it synchronizes waiting for the reply to be set.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public interface ITaskExecutionResult {

    /**
     * Provides the task execution result waiting if it is not ready. 
     * @param blocking true if the caller must wait until the reply is ready
     * @return the object that is the task result return or null if the result is
     * still not ready
     */
    public Object getTaskResult( boolean blocking ) throws WCException;
    
    /**
     * Gets the task execution result waiting for a timeout before
     * giving away.
     * @param timeout the number of milliseconds to wait before the caller
     * is unlocked
     * @return the object result of the task execution or null if the timeout
     * has experied
     */
    public Object getTaskResult( long timeout ) throws WCException ;
    
    /**
     * Notifies if the task result is ready. This method can be used
     * to get the task result without being blocked.
     * @return true if the task result is ready
     */
    public boolean isResultAvailable();
    
    /**
     * Sets the execution result for this task result wrapper.
     * The result should be set only once.
     * @param result the task execution result
     * @return true if the result is set, false otherwise
     */
    public boolean setResult(Object result );
    
    /**
     * Cancel this task execution result, that is this result is no more
     * required and waiting callers should be notified.
     */
    public void cancel();
}
