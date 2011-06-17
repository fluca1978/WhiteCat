package whitecat.core.role;

import java.util.List;

import whitecat.core.exceptions.WCRoleRepositoryException;
import whitecat.core.role.descriptors.IRoleDescriptorBuilder;
import whitecat.core.role.descriptors.RoleDescriptor;

public interface IRoleRepository {

	/**
	 * Searches for a role implementation starting from a role descriptor.
	 * 
	 * @param descriptor
	 *            the descriptor to search for the role
	 * @return the role if present in the repository
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong
	 */
	public IRole findRole(RoleDescriptor descriptor)
													throws WCRoleRepositoryException;

	/**
	 * Provides the list of the role descriptors available in the repository.
	 * This list should not be modified directly to change the content of the
	 * role descriptor.
	 * 
	 * @return the list of available role descriptors
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong
	 */
	public List<RoleDescriptor> getAvailableRoleDescriptors()
																throws WCRoleRepositoryException;

	/**
	 * Provides the role descriptor associated to the specified role. If the
	 * role is not installed, a null value is returned.
	 * 
	 * @param role
	 *            the associated descriptor to the specified role
	 * @return
	 */
	public RoleDescriptor getRoleDescriptor(IRole role);

	/**
	 * Install a role in the repository without a role descriptor. This method
	 * can be used only if the role repository is using a good role descriptor
	 * builder that can infer the role descriptor analyzing the role itself.
	 * 
	 * @param role
	 *            the role to install
	 * @param overrideIfExsist
	 *            true if the role must override any other instance in the
	 *            repository
	 * @return true if the role has been added, false otherwise
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong (e.g., the role descriptor builder
	 *             cannot build the role descriptor)
	 */
	public boolean installRole(IRole role, boolean overrideIfExsist)
																	throws WCRoleRepositoryException;

	/**
	 * Installs a new role descriptor in the system. The descriptor must be
	 * providen as well as the role implementation itself.
	 * 
	 * @param descriptor
	 *            the role descriptor to bind the role implementatio to
	 * @param role
	 *            the role implementation
	 * @param overrideIfExists
	 *            if true, the role will be associated with the specified role
	 *            descriptor even if the same role/role descriptor has been used
	 *            before, if false, in the case the same role has been
	 *            associated with a different role descriptor, the installation
	 *            will not succeed
	 * @return true if the role has been installed, false otherwise
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong with the role installation
	 */
	public boolean installRole(RoleDescriptor descriptor, IRole role,
								boolean overrideIfExists)
															throws WCRoleRepositoryException;

	/**
	 * Removes a role from the repository.
	 * 
	 * @param role
	 *            the role to remove
	 * @return true if the role has been removed, false if not
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong
	 */
	public boolean removeRole(IRole role) throws WCRoleRepositoryException;

	/**
	 * Deletes all the roles associated with this descriptor.
	 * 
	 * @param descriptor
	 *            the role descriptor to delete
	 * @return true if the role has been deleted, false otherwise
	 * @throws WCRoleRepositoryException
	 *             if something goes wrong
	 */
	public boolean removeRole(RoleDescriptor descriptor)
														throws WCRoleRepositoryException;

	/**
	 * Sets a role descriptor builder, if needed. The role descriptor builder is
	 * used to add a role descriptor when a new role is installed without a role
	 * descriptor.
	 * 
	 * @param builder
	 *            the builder to use to specify the role descriptor
	 */
	public void setRoleDescriptorBuilder(IRoleDescriptorBuilder builder);

}