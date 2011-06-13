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
package whitecat.test;


import static org.junit.Assert.*;

import javax.management.RuntimeErrorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import whitecat.core.WCException;
import whitecat.core.WhiteCat;
import whitecat.core.role.task.ITaskExecutionResult;

/**
 * A test for the execution result.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class TaskExecutionResultTest  {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testExecutionResult() throws WCException, InterruptedException{
	// get a task result
	ITaskExecutionResult result = WhiteCat.getTaskExecutionResult();

	// it should not be null and not be the same for any other call
	if( result == null )
	    fail("Execution result null!");
	if( result == WhiteCat.getTaskExecutionResult() || result.equals( WhiteCat.getTaskExecutionResult()) )
	    fail("Obtained two equals task execution results!");

	// now the result should not be available
	if( result.isResultAvailable() )
	    fail("Result should not be available now!");

	result.setResult( new String("RESULT") );
	if( ! result.isResultAvailable() )
	    fail("Result should be available now!");

	// now the sync..
	long start = System.currentTimeMillis();
	Object innerResult = result.getTaskResult( 36600 );
	long end = System.currentTimeMillis();
	if( (end - start) > 1 )
	    fail("With a result available we should not wait!");



	final ITaskExecutionResult result2 = WhiteCat.getTaskExecutionResult();
	Runnable waitingThread = new Runnable(){

	    public void run() {
		while( ! result2.isResultAvailable() ){
		    Object rr;
		    try {
			rr = result2.getTaskResult( true );
			if( rr == null )
			    throw new RuntimeException("Do not get the result!");
		    } catch (WCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		}

	    }

	};

	// start a new thread waiting for the result
	Thread t = new Thread( waitingThread );
	t.start();
	Thread.currentThread().sleep( 15000 );
	result2.setResult( new String("Hello") );

	// we should not be able to set the result twice or more
	if( result.setResult( new String("Second result")) )
	    fail("Cannot set the result twice!");

    }

}
