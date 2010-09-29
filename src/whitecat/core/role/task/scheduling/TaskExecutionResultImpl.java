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
package whitecat.core.role.task.scheduling;

import org.apache.log4j.Logger;

import whitecat.core.WCException;

/**
 * The default implementation of the task execution result.
 * Implements a simple future reply mechanism.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class TaskExecutionResultImpl implements ITaskExecutionResult {
    
    /**
     * The result of the task execution.
     */
    private Object result = null;
    
    /**
     * Indicates if this task result has been cancelled.
     */
    private boolean isCancelled = false;

    /* (non-Javadoc)
     * @see whitecat.core.role.task.scheduling.ITaskExecutionResult#getTaskResult(boolean)
     */
    @Override
    public final synchronized Object getTaskResult(boolean blocking) throws WCException{
	// if the result is ready, do not wait at all
	if( this.isResultAvailable() )
	    return this.result;

	// the result is not ready yet, do I have to wait?
	if( ! blocking )
	    return null;
	else{
	    while( ! this.isResultAvailable() )
		try {
		    this.wait();
		} catch (InterruptedException e) {
		    throw new WCException("Exception caught while waiting for a task result", e);
		}
	    
	    return this.result;
	}
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.task.scheduling.ITaskExecutionResult#getTaskResult(long)
     */
    @Override
    public synchronized final Object getTaskResult(long timeout) throws WCException {
	// if the result is ready, do not wait at all
	if( this.isResultAvailable() )
	    return this.result;

	// the result is not ready yet, do I have to wait?
	if( timeout > 0 ){
	    try {
		this.wait(timeout);
	    } catch (InterruptedException e) {
		throw new WCException("Exception caught while waiting for a task result", e);
	    }
	    
	}
	
	return this.result;
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.task.scheduling.ITaskExecutionResult#isResultAvailable()
     */
    @Override
    public final synchronized boolean isResultAvailable() {
	return (this.result != null || this.isCancelled);
    }

    @Override
    public final synchronized boolean setResult(Object result) {
	// do not allow setting the result more than once
	if( this.result != null )
	    return false;
	else{
	    this.result = result;
	    this.notifyAll();
	    return true;
	}
    }

    
    public synchronized final void cancel(){
	this.isCancelled = true;
	this.notifyAll();
    }
}
