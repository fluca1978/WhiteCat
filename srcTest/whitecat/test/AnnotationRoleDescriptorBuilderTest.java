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
package whitecat.test;


import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import whitecat.core.WhiteCat;
import whitecat.core.exceptions.WCRoleRepositoryException;
import whitecat.core.role.IRole;
import whitecat.core.role.IRoleRepository;
import whitecat.core.role.descriptors.EventDescriptor;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.descriptors.RoleDescriptor;
import whitecat.core.role.descriptors.TaskDescriptor;
import whitecat.core.role.task.IRoleTask;
import whitecat.core.role.task.MethodTaskExecutor;
import whitecat.example.AnnotatedRoleExample;

/**
 * A test for the annotation builder role descriptor.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class AnnotationRoleDescriptorBuilderTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    
    @Test
    public void testAnnotationBuilder(){
	// get the role
	IRole role = new AnnotatedRoleExample();
	
	// get the builder
	IRoleDescriptorBuilder builder = WhiteCat.getRoleDescriptorBuilder();
	
	if( builder == null )
	    fail("Got a null role descriptor builder! ");
	
	// build the role descriptor
	RoleDescriptor desc = builder.buildRoleDescriptor( role );
	
	// if the name and the aim do not correspond...
	String name = desc.getName();
	String aim  = desc.getAim();
	if( ! aim.contains("AIM") || ! name.contains("NAME") )
	    fail("Error in the name/aim built!");
	
	// there must be no more than 3 tasks
	if( desc.getTaskDescriptors().size() != 3 )
	    fail("There are not 3 tasks!");
	
	// each task must have the name and the aim correct
	for( TaskDescriptor td : desc.getTaskDescriptors() ){
	    name = td.getName();
	    aim  = td.getAim();
	    
	    if( ! aim.contains("AIM") || ! name.contains("NAME") )
		    fail("Error in the name/aim built!");
	    
	}
	
	// get the tasks
	for( IRoleTask task : desc.getTasks() ){
	    // check the type of the task
	    if( !(task instanceof MethodTaskExecutor) )
		fail("The task should be a method task executor!");
	    
	    // check the task has a descriptor
	    if( desc.getTaskDescriptor(task) == null )
		fail("The task has not a descriptor!");
	}
	
	// there must be one event descriptor, that is issuing
	List<EventDescriptor> events = desc.getAllEventDescriptors();
	if( events.size() != 1 )
	    fail("There must be a single event descriptor!");
	
	EventDescriptor event = events.get(0);
	if( event.isIssuing() == false || event.isReceiving() == true )
	    fail("The event must be issuing!");

	   
	    
    }
    
    @Test
    public void testRoleRepository() throws WCRoleRepositoryException{
	// get the role
	IRole role = new AnnotatedRoleExample();
	
	// get the role repository
	IRoleRepository repo = WhiteCat.getRoleRepository();
	
	// install the role
	repo.installRole(role, false);
	
	// get back the role descriptor
	RoleDescriptor desc = repo.getAvailableRoleDescriptors().get(0);
	
	// if the name and the aim do not correspond...
	String name = desc.getName();
	String aim  = desc.getAim();
	if( ! aim.contains("AIM") || ! name.contains("NAME") )
	    fail("Error in the name/aim built!");
	
	// there must be no more than 3 tasks
	if( desc.getTaskDescriptors().size() != 3 )
	    fail("There are not 3 tasks!");
	
	// each task must have the name and the aim correct
	for( TaskDescriptor td : desc.getTaskDescriptors() ){
	    name = td.getName();
	    aim  = td.getAim();
	    
	    if( ! aim.contains("AIM") || ! name.contains("NAME") )
		    fail("Error in the name/aim built!");
	    
	}
	
	// get the tasks
	for( IRoleTask task : desc.getTasks() ){
	    // check the type of the task
	    if( !(task instanceof MethodTaskExecutor) )
		fail("The task should be a method task executor!");
	    
	    // check the task has a descriptor
	    if( desc.getTaskDescriptor(task) == null )
		fail("The task has not a descriptor!");
	}
	
	// there must be one event descriptor, that is issuing
	List<EventDescriptor> events = desc.getAllEventDescriptors();
	if( events.size() != 1 )
	    fail("There must be a single event descriptor!");
	
	EventDescriptor event = events.get(0);
	if( event.isIssuing() == false || event.isReceiving() == true )
	    fail("The event must be issuing!");

    }

}
