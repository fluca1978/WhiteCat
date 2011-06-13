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
package whitecat.example;

import whitecat.core.annotations.PUBLICROLE;
import whitecat.core.annotations.ROLE;
import whitecat.core.role.IRole;
import whitecat.core.role.descriptors.annotation.AnnotationEventDescriptor;
import whitecat.core.role.descriptors.annotation.AnnotationRoleDescriptor;
import whitecat.core.role.descriptors.annotation.AnnotationTaskDescriptor;

/**
 * An example of role built with annotations, that can be used to provide
 * a role descriptor automatically.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
@ROLE()		// to mark this as a role object

// here I specify the part of the role that can be visible
// to outsider and that will be applied to the proxy
@PUBLICROLE(roleInterface = "whitecat.example.AnnotatedPublicRoleInterface",
roleAnnotation = "whitecat.example.ExampleRoleAnnotation"
)

@AnnotationRoleDescriptor(
	aim = "Example AIM",
	name = "Example NAME",
	keywords = "keyword1, keyword2, keyword 3"
	)
public class AnnotatedRoleExample implements IRole, AnnotatedPublicRoleInterface {

    /**
     * A simple task. Does nothing on the value, returns it as it was provided.
     */
    @AnnotationTaskDescriptor(
	    aim = "Task AIM",
	    name = "Task NAME",
	    keywords = "taks1",
	    taskID = "id1"
	    )
    public int exampleTask1( int value){
	return value;
    }
    
    
    /**
     * Another task.
     * @return a string
     */
    @AnnotationTaskDescriptor(
	    aim = "Task2 AIM",
	    name = "Task2 NAME",
	    keywords = "taks2",
	    taskID = "id2"
	    )
    public String exampleTask2(){
	return "Task2";
    }
    
    
    /**
     * A task that is a subtask of task 2.
     */
    @AnnotationTaskDescriptor(
	    aim = "Task3 AIM",
	    name = "Task3 NAME",
	    keywords = "taks3",
	    taskID = "id3",
	    addToTaskID = "id2"
    )
    @AnnotationEventDescriptor(
	    aim = "Event AIM",
	    name = "Event NAME",
	    issuing = true,
	    receiving = false
    )
    public String exampleTask3(){
	System.out.println("Executing task 3");
	return "Task3";
    }
    
}
