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
package whitecat.core.role.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import whitecat.core.WCException;
import whitecat.core.role.IRole;

/**
 * A IRoleTask executor that performs a task as a single method call.
 * This executor can be used with Java reflection, passing the method object
 * to use as a task.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class MethodTaskExecutor implements IRoleTask, IRole {

    /**
     * The method to execute as task.
     */
    private Method methodToExecute = null;
    
    
    /**
     * Collection of uniques tasks to execute within this one.
     */
    private Set<IRoleTask> subTasks = null;
    
    /**
     * A list of parameters to use when executing this task.
     */
    private List parameters = null;
    
    
    /**
     * Default constructor.
     */
    public MethodTaskExecutor(){
	super();
	this.parameters = new LinkedList();
	this.subTasks   = new HashSet<IRoleTask>();
    }
    
    
    
    /**
     * Provides the value of the methodToExecute field.
     * @return the methodToExecute
     */
    public synchronized final Method getMethodToExecute() {
        return this.methodToExecute;
    }

    /**
     * Sets the value of the methodToExecute field as specified
     * by the value of methodToExecute.
     * @param methodToExecute the methodToExecute to set
     */
    public synchronized final void setMethodToExecute(Method methodToExecute) {
        this.methodToExecute = methodToExecute;
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.task.IRoleTask#execute()
     */
    public Object execute() throws WCException {
	// check arguments
	if( this.methodToExecute == null )
	    throw new WCException("Cannot execute a task without the method to invoke!");
	
	
	try {
	    // execute the method: if there are not subtasks, do only this,
	    // otherwise invoke all the subtasks and keep them in an array
	    if( this.subTasks.size() == 0 )
		return this.methodToExecute.invoke( this.parameters.toArray() );
	    else{
		Object[] returns = new Object[ this.subTasks.size() + 1 ];
		returns[0] = this.methodToExecute.invoke( this.parameters.toArray() );
		
		Iterator<IRoleTask> iter = this.subTasks.iterator();
		int i = 1;
		while( iter.hasNext() ){
		    returns[i] = iter.next().execute();
		    i++;
		}
		    
		
		// all done
		return returns;
	    }
	} catch (IllegalArgumentException e) {
	    throw new WCException("Wrong number of parameters or type!");
	} catch (IllegalAccessException e) {
	    throw new WCException("Cannot invoke the method!");
	} catch (InvocationTargetException e) {
	    throw new WCException("Cannot invoke the method on the target !");
	}
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.task.IRoleTask#addSubTask(whitecat.core.role.task.IRoleTask)
     */
    public synchronized final boolean addSubTask(IRoleTask toAdd) {
	if( ! this.subTasks.contains(toAdd) ){
	    this.subTasks.add(toAdd);
	    return true;
	}
	else
	    return false;
    }

    public synchronized final void addTaskParameter(Object parameter) {
	this.parameters.add( parameter );
    }

    public synchronized final void resetParameters() {
	this.parameters.clear();
    }



    public synchronized boolean removeSubTask(IRoleTask toRemove) {
	if( this.subTasks.contains(toRemove) ){
	    this.subTasks.remove(toRemove);
	    return true;
	}
	else
	    return false;
	    
    }

}