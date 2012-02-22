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
 * Copyright (C) Luca Ferrari 2006-2012 - fluca1978 (at) gmail.com
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
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 * 
 */
public class RoleRepositoryImpl implements IRoleRepository {

	/**
	 * A role descriptor builder used to build descriptors when no one is
	 * provided.
	 */
	private IRoleDescriptorBuilder			roleDescriptorBuilder	= null;

	/**
	 * An hashmap with the installed roles. The map is keyed by the descriptors
	 * and has each implementation of the roles.
	 */
	private HashMap<RoleDescriptor, IRole>	roles					= null;

	public RoleRepositoryImpl() {
		super();
		roles = new HashMap<RoleDescriptor, IRole>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.role.RoleRepository#findRole(whitecat.core.role.descriptors
	 * .RoleDescriptor)
	 */
	public IRole findRole(final RoleDescriptor descriptor)
															throws WCRoleRepositoryException {
		// check if the role and the descriptor are valid
		if (descriptor == null)
			throw new WCRoleRepositoryException(
					"Cannot search with a null role descriptor" );

		return roles.get( descriptor );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see whitecat.core.role.RoleRepository#getAvailableRoleDescriptors()
	 */
	public List<RoleDescriptor> getAvailableRoleDescriptors()
																throws WCRoleRepositoryException {
		return new LinkedList<RoleDescriptor>( roles.keySet() );
	}

	public synchronized final RoleDescriptor getRoleDescriptor(final IRole role) {
		// check arguments
		if ((role == null) || roles.isEmpty()
				|| (roles.containsValue( role ) == false))
			return null;

		// now search the key for the specified role
		for (final RoleDescriptor desc : roles.keySet()){
			final IRole keyRole = roles.get( desc );
			if (keyRole.equals( role ))
				return desc;
		}

		return null;

	}

	public synchronized boolean installRole(final IRole role,
											final boolean overrideIfExsist)
																			throws WCRoleRepositoryException {
		// check arguments
		if (role == null)
			return false;

		if (roleDescriptorBuilder == null)
			throw new WCRoleRepositoryException(
					"Cannot install a role without the role descriptor and a descriptor builder" );

		// get the role descriptor
		final RoleDescriptor desc = roleDescriptorBuilder
				.buildRoleDescriptor( role );
		if (desc == null)
			throw new WCRoleRepositoryException(
					"Cannot build the descriptor for the specified role" );

		// now install the role
		return this.installRole( desc, role, overrideIfExsist );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.role.RoleRepository#installRole(whitecat.core.role.descriptors
	 * .RoleDescriptor, whitecat.core.role.IRole, boolean)
	 */
	public boolean installRole(final RoleDescriptor descriptor,
								final IRole role, final boolean overrideIfExists)
																					throws WCRoleRepositoryException {

		if (!overrideIfExists){
			// check if the role is already contained in the hashmap
			if (roles.containsValue( role )){
				// get the key (the role descriptor) this role is associated
				// with
				final Set<RoleDescriptor> descriptors = roles.keySet();
				for (final Object element : descriptors){
					final RoleDescriptor roleDescriptor = (RoleDescriptor) element;

					if (roles.get( roleDescriptor ).equals( role )
							&& roleDescriptor.equals( descriptor ))
						return true;

				}

				// if here the same role has been installed with a different
				// role descriptor
				return false;
			}

		}

		// install the role
		roles.put( descriptor, role );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * whitecat.core.role.RoleRepository#removeRole(whitecat.core.role.IRole)
	 */
	public boolean removeRole(final IRole role)
												throws WCRoleRepositoryException {
		if (roles.containsValue( role )){
			final Set<RoleDescriptor> descriptors = roles.keySet();
			for (final Object element : descriptors){
				final RoleDescriptor roleDescriptor = (RoleDescriptor) element;
				if (roles.get( roleDescriptor ).equals( role )){
					roles.remove( roleDescriptor );
					return true;
				}

			}

			// should never happen
			return false;
		}else
		// the role is not contained
		return false;

	}

	/**
	 * Removes a role depending on its descriptor.
	 */
	public boolean removeRole(final RoleDescriptor descriptor)
																throws WCRoleRepositoryException {
		if (roles.containsKey( descriptor )){
			roles.remove( descriptor );
			return true;
		}else return false;
	}

	public synchronized void setRoleDescriptorBuilder(	final IRoleDescriptorBuilder builder) {
		roleDescriptorBuilder = builder;
	}

}
