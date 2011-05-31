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
package whitecat.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
@ROLE()
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PUBLICROLE{

    /**
     * The class for this role. This must always be specified for a role.
     * @return the fully qualified role class name
     */
    public String roleClass() default "";
    
    /**
     * The fully qualified role interface name (if present and available). If not specified
     * the role has no public visibility part (i.e., a proxy cannot be see thru the role it is
     * playing).
     * @return the fully qualified role interface name
     */
    public String roleInterface() default "";
    
    /**
     * The annotation to be applied to the proxy class, if the role must be visible
     * from an external point of view but not available with its services.
     * @return the fully qualified name of the annotation class
     */
    public String roleAnnotation() default "";
}
