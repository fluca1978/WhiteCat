package whitecat.core.role;

import java.util.List;

import whitecat.core.exceptions.WCRoleRepositoryException;
import whitecat.core.role.descriptors.RoleDescriptor;

public interface IRoleRepository {

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
    public boolean installRole(RoleDescriptor descriptor, IRole role,
	    boolean overrideIfExists) throws WCRoleRepositoryException;

    /**
     * Removes a role from the repository.
     * @param role the role to remove
     * @return true if the role has been removed, false if not
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public boolean removeRole(IRole role) throws WCRoleRepositoryException;

    /**
     * Deletes all the roles associated with this descriptor.
     * @param descriptor the role descriptor to delete
     * @return true if the role has been deleted, false otherwise
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public boolean removeRole(RoleDescriptor descriptor)
	    throws WCRoleRepositoryException;

    /**
     * Searches for a role implementation starting from a role descriptor.
     * @param descriptor the descriptor to search for the role
     * @return the role if present in the repository
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public IRole findRole(RoleDescriptor descriptor)
	    throws WCRoleRepositoryException;

    /**
     * Provides the list of the role descriptors available in the repository. This list should not be
     * modified directly to change the content of the role descriptor.
     * @return the list of available role descriptors
     * @throws WCRoleRepositoryException if something goes wrong
     */
    public List<RoleDescriptor> getAvailableRoleDescriptors()
	    throws WCRoleRepositoryException;

}