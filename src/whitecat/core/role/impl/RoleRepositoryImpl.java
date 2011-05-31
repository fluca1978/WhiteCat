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
package whitecat.core.role.impl;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import whitecat.core.exceptions.WCRoleRepositoryException;
import whitecat.core.role.IRole;
import whitecat.core.role.IRoleRepository;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * A concrete implementation of a role repository.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class RoleRepositoryImpl implements IRoleRepository {

    
    /**
     * A role descriptor builder used to build descriptors when no one is provided.
     */
    private IRoleDescriptorBuilder roleDescriptorBuilder = null;

    /**
     * An hashmap with the installed roles. The map is keyed by the descriptors
     * and has each implementation of the roles.
     */
    private HashMap<RoleDescriptor, IRole> roles = null;
    
    
    public RoleRepositoryImpl(){
	super();
	this.roles = new HashMap<RoleDescriptor, IRole>();
    }
    
    /* (non-Javadoc)
     * @see whitecat.core.role.RoleRepository#findRole(whitecat.core.role.descriptors.RoleDescriptor)
     */
    public IRole findRole(RoleDescriptor descriptor)
	    throws WCRoleRepositoryException {
	// check if the role and the descriptor are valid
	if( descriptor == null )
	    throw new WCRoleRepositoryException("Cannot search with a null role descriptor");
	
	return this.roles.get(descriptor);
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.RoleRepository#getAvailableRoleDescriptors()
     */
    public List<RoleDescriptor> getAvailableRoleDescriptors()
	    throws WCRoleRepositoryException {
	return new LinkedList<RoleDescriptor>( this.roles.keySet() );
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.RoleRepository#installRole(whitecat.core.role.descriptors.RoleDescriptor, whitecat.core.role.IRole, boolean)
     */
    public boolean installRole(RoleDescriptor descriptor, IRole role,
	    boolean overrideIfExists) throws WCRoleRepositoryException {

	if(! overrideIfExists ){
	    // check if the role is already contained in the hashmap
	    if( this.roles.containsValue(role) ){
		// get the key (the role descriptor) this role is associated with 
		Set<RoleDescriptor> descriptors = this.roles.keySet();
		for (Iterator iterator = descriptors.iterator(); iterator.hasNext();) {
		    RoleDescriptor roleDescriptor = (RoleDescriptor) iterator.next();
		    
		    if( this.roles.get(roleDescriptor).equals(role) && roleDescriptor.equals(descriptor) )
			return true;
		    
		}
		
		// if here the same role has been installed with a different role descriptor
		return false;
	    }
		
	}
	
	// install the role
	this.roles.put(descriptor, role);
	return true;
    }

    /* (non-Javadoc)
     * @see whitecat.core.role.RoleRepository#removeRole(whitecat.core.role.IRole)
     */
    public boolean removeRole(IRole role) throws WCRoleRepositoryException {
	if( this.roles.containsValue(role) ){
	    Set<RoleDescriptor> descriptors = this.roles.keySet();
	    for (Iterator iterator = descriptors.iterator(); iterator.hasNext();) {
		RoleDescriptor roleDescriptor = (RoleDescriptor) iterator.next();
		if( this.roles.get(roleDescriptor).equals(role) ){
		    this.roles.remove(roleDescriptor);
		    return true;
		}
		
	    }
	    
	    // should never happen
	    return false;
	}
	else
	    // the role is not contained
	    return false;
	    
    }

    
    /**
     * Removes a role depending on its descriptor.
     */
    public boolean removeRole(RoleDescriptor descriptor)
	    throws WCRoleRepositoryException {
	if( this.roles.containsKey(descriptor) ){
	    this.roles.remove(descriptor);
	    return true;
	}
	else return false;
    }

    public synchronized void setRoleDescriptorBuilder(IRoleDescriptorBuilder builder) {
	this.roleDescriptorBuilder = builder;
    }

    public synchronized  boolean installRole(IRole role, boolean overrideIfExsist)
	    throws WCRoleRepositoryException {
	// check arguments
	if( role == null )
	    return false;
	
	if( this.roleDescriptorBuilder == null )
	    throw new WCRoleRepositoryException("Cannot install a role without the role descriptor and a descriptor builder");
	
	// get the role descriptor
	RoleDescriptor desc = this.roleDescriptorBuilder.buildRoleDescriptor(role);
	if( desc == null )
	    throw new WCRoleRepositoryException("Cannot build the descriptor for the specified role");
	
	// now install the role
	return this.installRole(desc, role, overrideIfExsist);
    }

    @Override
    public synchronized final RoleDescriptor getRoleDescriptor(IRole role) {
	// check arguments
	if( role == null || this.roles.isEmpty() || this.roles.containsValue(role) == false )
	    return null;
	
	// now search the key for the specified role
	for( RoleDescriptor desc : this.roles.keySet() ){
	    IRole keyRole = this.roles.get(desc);
	    if( keyRole.equals(role) )
		return desc;
	}
	    
	
	return null;

    }

}
