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
package whitecat.core.role.descriptors.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A task descriptor defined thru an annotation.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationTaskDescriptor {

    /**
     * The aim of the role.
     * @return a string description of the role aim
     */
    public String aim() default "";
    
    /**
     * The name of the role.
     * @return the name of the role.
     */
    public String name() default "";

    
    /**
     * The keywords list separator.
     * @return the keywords list separator, by default ','
     */
    public String keywordsSeparator() default ",";
    
    /**
     * A list of keywords, separated by the keywordSeparator().
     * @return the list of the keywords
     */
    public String keywords() default "";

    
    
    
    /**
     * A task id, used to group tasks together to compose complex tasks.
     * @return the task id.
     */
    public String taskID() default "task1";
    
    
    /**
     * The task id to which this task must be added.
     * @return the task id of the task for which the current task is a subtask
     */
    public String addToTaskID() default "";
}
