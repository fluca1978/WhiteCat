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
package whitecat.core;

/**
 * All the supported types of role injection.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public enum RoleInjectionType {

    /**
     * Role addition: the role public interface must be added to the proxy.
     */
    ROLE_PUBLIC_INTERFACE_ADDITION_TO_PROXY,	
    
    /**
     * Role removal: the role public interface must be removed from the proxy.
     */
    ROLE_PUBLIC_INTERFACE_REMOVAL_FROM_PROXY,
    
    /**
     * Role addition: the role annotation must be added to the proxy.
     */
    ROLE_ANNOTATION_ADDITION_TO_PROXY,
    
    /**
     * Role removal: the role annotation must be removed from the proxy.
     */
    ROLE_ANNOTATION_REMOVAL_FROM_PROXY,
    
    /**
     * Nothing to do.
     */
    NONE
    
}
