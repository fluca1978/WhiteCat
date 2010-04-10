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
package whitecat.core.role;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import whitecat.core.Configuration;
import whitecat.core.RoleBooster;
import whitecat.core.exceptions.WCRoleRepositoryException;
import whitecat.core.role.descriptors.RoleDescriptor;

/**
 * The interface for a generic role repository. A repository stores
 * available roles in the platform by means of their role descriptors (and event & action
 * descriptors) and provides them on demand.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public abstract class RoleRepository {
    
    /**
     * The logger for this class loader.
     */
    protected static Logger logger = org.apache.log4j.Logger.getLogger(RoleRepository.class);
    
    // configure the logger
    static{
	DOMConfigurator.configure("conf/log4j.xml");
    }

    /**
     * Installs a new role descriptor in the system. The descriptor must be providen as well as the role
     * implementation itself.
     * @param descriptor the role descriptor to bind the role implementatio to 
     * @param role the role implementation
     * @param overrideIfExists if true, the role will be associated with the specified role descriptor even
     * if the same role/role descriptor has been used before, if false, in the case the same role has been
     * associated with a different role descriptor, the installation will not succeed
     * @return true if the role has been installed, false otherwise
     * @throws WCRoleRepositoryException if something goes wrong with the role installation
     */
    public abstract boolean installRole(RoleDescriptor descriptor, IRole role, boolean overrideIfExists) throws WCRoleRepositoryException;
    
    /**
     * Removes a role from the repository.
     * @param role the role to remove
     * @return true if the role has been removed, false if not
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public abstract boolean removeRole(IRole role) throws WCRoleRepositoryException;
    
    /**
     * Deletes all the roles associated with this descriptor.
     * @param descriptor the role descriptor to delete
     * @return true if the role has been deleted, false otherwise
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public abstract boolean removeRole(RoleDescriptor descriptor) throws WCRoleRepositoryException;
    
    /**
     * Searches for a role implementation starting from a role descriptor.
     * @param descriptor the descriptor to search for the role
     * @return the role if present in the repository
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public abstract IRole findRole(RoleDescriptor descriptor) throws WCRoleRepositoryException;
    
    /**
     * Provides the list of the role descriptors available in the repository. This list should not be
     * modified directly to change the content of the role descriptor.
     * @return the list of available role descriptors
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public abstract List<RoleDescriptor> getAvailableRoleDescriptors() throws WCRoleRepositoryException;
    
    
    /**
     * A singleton reference to the only repository avaialble in the system.
     */
    private static RoleRepository mySelf = null;
    
    
    /**
     * Creates and returns the correct instance of the role repository to adopt depending
     * on the configuration of the system.
     * @return the role repository to use.
     */
    public static synchronized RoleRepository getInstance(){
	try{
	    
	    if( mySelf == null ){
		// get the configuration
		Configuration config = Configuration.getInstance();

		// get the name of the repository to use
		String repositoryName = config.getProperty( Configuration.DEFAULT_ROLE_REPOSITORY );

		// create the role repository
		mySelf = (RoleRepository) Class.forName(repositoryName).newInstance();
	    }

	    // return the current instance of the role repository
	    return mySelf;

	    
	}catch(Exception e){
	    logger.error("Exception caught while creating the role repository", e);
	    return null;
	}
    }
}
