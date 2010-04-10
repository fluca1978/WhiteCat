/**
 * R4R -- Roles for Resources
 *
 * Copyright (C) Luca Ferrari 2007 - cat4hire@users.sourceforge.net
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
package whitecat.core.role.descriptors;

import java.security.Permission;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A descriptor for a role operation. A role operation is something an agent can perform
 * while it ows (plays) the role that contains such operation. The operation descriptor
 * describes what an operation does but not how.
 *
 * This is an implementation of operation descriptor based on what you can read in the
 * article <i> Injecting roles in Java agents through runtime bytecode manipulation IBM SYSTEMS JOURNAL, VOL 44, NO 1, 2005 </i>.
 * The main difference between this implementation and the one presented in the above article
 * is for a better modularity and adoption of collections instead of arrays.

 * @author Luca Ferrari - cat4hire@users.sourceforge.net
 *
 *
 */
public class OperationDescriptor extends AbstractDescriptor {

    /**
     * A list of events that are tied to this operation (e.g., events that are sent
     * or received after executing the operation tied to this descriptor).
     * It is a list and not a set because the operation can have the same events
     * in multiple positions.
     */
    private List<EventDescriptor> eventDescriptors = new LinkedList<EventDescriptor>();
    
    /**
     * The return type of the operation, by default a void.
     */
    private Class returnType = java.lang.Void.class;
    
    
    /**
     * A list of parameters for the current operation.
     * It is a list and not a set because the operation can have duplicated parameters
     * in different positions.
     */
    private List<Class> parameters = new LinkedList<Class>();
    
    /**
     * A set of unique permissions associated to the operation.
     */
    private Set<Permission> permissions = new HashSet<Permission>();

    /**
     * This method can be used to set the value of the eventDescriptors field,
     * providing thus read access to the eventDescriptors property.
     * @return the eventDescriptors value.
     */
    public final List<EventDescriptor> getEventDescriptors() {
        return new LinkedList<EventDescriptor>(eventDescriptors);
    }

    /**
     * This method can be used to set the value of the parameters field,
     * providing thus read access to the parameters property.
     * @return the parameters value.
     */
    public final List<Class> getParameters() {
        return new LinkedList<Class>(parameters);
    }

    /**
     * This method can be used to set the value of the permissions field,
     * providing thus read access to the permissions property.
     * @return the permissions value.
     */
    public final Set<Permission> getPermissions() {
        return new HashSet<Permission>(permissions);
    }

    /**
     * This method can be used to set the value of the returnType field,
     * providing thus read access to the returnType property.
     * @return the returnType value.
     */
    public synchronized final Class getReturnType() {
        return returnType;
    }

    /**
     * This method can be used to set the value of the returnType field, providing
     * thus write access to the returnType property.
     * @param returnType the returnType to set with the specified value.
     */
    protected synchronized final void setReturnType(Class returnType) {
        this.returnType = returnType;
    }
    
    
    /**
     * Adds a permission to the set of permissions of this operation descriptor.
     * @param toAdd the permission that must be added
     * @return true if the permission has been added, false otherwise (maybe the permission
     * is already contained in the set).
     */
    protected synchronized final boolean addPermission(Permission toAdd){
	return this.permissions.add(toAdd);
    }
    
    /**
     * Removes the specified permisssion from the set of this operation descriptor.
     * @param toRemove the permission to remove
     * @return true if the permission has been removed, false otherwise (maybe the permission
     * is not included in the set).
     */
    protected synchronized final boolean removePermission(Permission toRemove){
	return this.permissions.remove(toRemove);
    }
    
    /**
     * Adds the specified event descriptor at the descriptor list.
     * @param toAdd the event descriptor to add
     * @return true if the descriptor has been added
     */
    protected synchronized final boolean addEventDescriptor(EventDescriptor toAdd){
	return this.eventDescriptors.add(toAdd);
    }
    
    /**
     * Removes the specified event descriptor from the list of descriptors.
     * @param toRemove the event descriptor to remove
     * @return true if the event descriptor has been removed
     */
    protected synchronized final boolean removeEventDescriptor(EventDescriptor toRemove){
	return this.eventDescriptors.remove(toRemove);
    }
    
    /**
     * Adds the specified operation parameter at the end of the list of 
     * the parameters.
     * @param toAdd the parameter to add
     * @return true if the parameter has been added
     */
    protected synchronized final boolean addParameter(Class toAdd){
	return this.parameters.add(toAdd);
    }
    
    /**
     * Removes the specified parameter from the list of the parameters.
     * @param toRemove the parameter to remove
     * @return true if the parameter has been removed
     */
    protected synchronized final boolean removeParameter(Class toRemove){
	return this.parameters.remove(toRemove);
    }
    
    /**
     * Adds the specified operation parameter at the specified index in the
     * parameter list.
     * @param toAdd the parameter to add
     * @param position the position at which add the paramter
     */
    protected synchronized final void addParameter(Class toAdd, int position){
	this.parameters.add(position, toAdd);
    }
    
    /**
     * Removes a parameter depending on its position.
     * @param position the index at which removing the parameter
     * @return the object that was present at such position
     */
    protected synchronized final Class  removeParameter(int position){
	return this.parameters.remove(position);
    }
    
    
    
    
    /**
     * This method can be used to set the value of the eventDescriptors field, providing
     * thus write access to the eventDescriptors property.
     * @param eventDescriptors the eventDescriptors to set with the specified value.
     */
    protected synchronized final void setEventDescriptors(List<EventDescriptor> eventDescriptors) {
        this.eventDescriptors = eventDescriptors;
    }

    /**
     * This method can be used to set the value of the parameters field, providing
     * thus write access to the parameters property.
     * @param parameters the parameters to set with the specified value.
     */
    protected synchronized final void setParameters(List<Class> parameters) {
        this.parameters = parameters;
    }

    /**
     * This method can be used to set the value of the permissions field, providing
     * thus write access to the permissions property.
     * @param permissions the permissions to set with the specified value.
     */
    protected synchronized final void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Default constructor, used for serialization.
     *
     */
    private OperationDescriptor(){
	super();
    }
    
    
    /**
     * Testes if this object is equal to another descriptor.
     * Overridden version.
     * @overrides @see it.unimo.polaris.rolex.roles.AbstractDescriptor#equals(java.lang.Object)
     */
    public boolean equals(Object o){
	if( ! (o instanceof OperationDescriptor) )
	    return false;
	else{
	    OperationDescriptor desc = (OperationDescriptor) o;

	    // if super object are equals then test the properties of this object
	    if( super.equals(desc) )
		return (    this.permissions.equals(desc.permissions) 
			 && this.parameters.equals(desc.parameters) 
			 && this.returnType.equals(desc.returnType) 
			 && this.eventDescriptors.equals(desc.eventDescriptors));
	    else
		return false;
	}
	
    }
    
    
    /**
     * Calculates the hash code of this object.
     * Overridden version.
     * @overrides @see it.unimo.polaris.rolex.roles.AbstractDescriptor#hashCode()
     */
    public int hashCode(){
	int hash = super.hashCode();
	hash = 31 * hash + (this.parameters == null ? 0 : this.parameters.hashCode());
	hash = 31 * hash + (this.permissions == null ? 0 : this.permissions.hashCode());
	hash = 31 * hash + (this.returnType == null ? 0 : this.returnType.hashCode());
	hash = 31 * hash + (this.eventDescriptors == null ? 0 : this.eventDescriptors.hashCode());
	
	return hash;
    }
    

    /**
     * Provides an instance of a specified OperationDescriptor.
     * @param name the name of the operation
     * @param aim the aim of the operation descriptor
     * @param returnType the return type of the operation descriptor
     * @param parameters the list of parameters of the operation descriptor
     * @return the operation descritpor
     */
    public static final OperationDescriptor getInstance(String name, String aim, Class returnType, List<Class> parameters){
	OperationDescriptor descriptor = new OperationDescriptor();
	descriptor.setName(name);
	descriptor.setAim(aim);
	descriptor.setReturnType(returnType);
	descriptor.setParameters(parameters);
	return descriptor;
    }

    /**
     * The number of events tied to this operation descriptor.
     * @return the number of event descriptors
     */
    public synchronized int eventDescriptorsCount() {
	return this.eventDescriptors.size();
    }
    

}
