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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A descriptor for a role instance. A descriptor specified what a role does, including
 * which operation and events are tied to itself, without specifying directly what it does.
 * 
 * This is an implementation of role descriptor based on what you can read in the
 * article <i> Injecting roles in Java agents through runtime bytecode manipulation IBM SYSTEMS JOURNAL, VOL 44, NO 1, 2005 </i>.
 * The main difference between this implementation and the one presented in the above article
 * is for a better modularity and adoption of collections instead of arrays.
 *
 * @author Luca Ferrari - cat4hire@users.sourceforge.net
 *
 *
 */
public class RoleDescriptor extends AbstractDescriptor {
    
    /**
     * A list of operations that are tied to this role.
     */
    private List<OperationDescriptor> operationDescriptors = null;
    
    /**
     * A list of events untied from a particular operation of this role.
     */
    private List<EventDescriptor> eventDscriptors = null;

    /**
     * This method can be used to set the value of the eventDscriptors field,
     * providing thus read access to the eventDscriptors property. Please note that this
     * method will return only the events associated to this role descriptor, not the role
     * of the operations.
     * @return the eventDscriptors value.
     */
    public final  List<EventDescriptor> getEventDscriptors() {
        return new LinkedList<EventDescriptor>(eventDscriptors);
    }

    /**
     * This method can be used to set the value of the operationDescriptors field,
     * providing thus read access to the operationDescriptors property.
     * @return the operationDescriptors value.
     */
    public final  List<OperationDescriptor> getOperationDescriptors() {
        return new LinkedList<OperationDescriptor>(operationDescriptors);
    }

    
    /**
     * Provides all the event descriptors contained in this role descriptor, that is the
     * descriptors tied to the role itself as well as the event descriptors tied to each single
     * operation.
     * @return the list of all the event descriptor, with no particular order. Please note that the same
     * event could be included several time in the list, since it could be embedded by either the role
     * or one (or more) operations.
     */
    public final synchronized List<EventDescriptor> getAllEventDescriptors(){
	LinkedList<EventDescriptor> descriptors = new LinkedList<EventDescriptor>(this.eventDscriptors);
	for( OperationDescriptor op : this.operationDescriptors )
	    descriptors.addAll( op.getEventDescriptors() );
	
	return descriptors;
    }
    
    /**
     * How many operation descriptors this role descriptor has.
     * @return the number of operation descriptors
     */
    public final synchronized int operationDescriptorsCount(){
	return this.operationDescriptors.size();
    }
    
    /**
     * The number of the event descriptors directly tied to this role descriptor.
     * @return the number of event descriptors tied to this role descriptor.
     */
    public final synchronized int eventDescriptorsCount(){
	return this.eventDscriptors.size();
    }

    /**
     * Returns the total number of event descriptors, including those directly tied to the
     * role descriptor and thos tied to each operation.
     * @return the total count of the event descriptors contained in this role
     */
    public final synchronized int eventDescriptorsTotalCount(){
	int count = 0;
	count += this.eventDscriptors.size();
	
	for( OperationDescriptor op : this.operationDescriptors )
	    count += op.eventDescriptorsCount();
	
	return count;
    }

    /**
     * This method can be used to set the value of the eventDscriptors field, providing
     * thus write access to the eventDscriptors property.
     * @param eventDscriptors the eventDscriptors to set with the specified value.
     */
    protected synchronized final void setEventDscriptors(
    	List<EventDescriptor> eventDscriptors) {
        this.eventDscriptors = eventDscriptors;
    }

    /**
     * This method can be used to set the value of the operationDescriptors field, providing
     * thus write access to the operationDescriptors property.
     * @param operationDescriptors the operationDescriptors to set with the specified value.
     */
    protected synchronized final void setOperationDescriptors(
    	List<OperationDescriptor> operationDescriptors) {
        this.operationDescriptors = operationDescriptors;
    }
    
    
    /**
     * The default constructor.
     *
     */
    protected RoleDescriptor(){
	super();
    }
    
    /**
     * Factory method to create a role descriptor.
     * @param name the name of the role
     * @param aim the aim of the role 
     * @param operations the list of operations tied to such role
     * @param events the events directly tied to the role
     * @param keywords the list of keywords for the role
     * @return the role descriptor.
     */
    public static final RoleDescriptor getInstance(String name, String aim, List<OperationDescriptor> operations, List<EventDescriptor> events, Set<String> keywords){
	RoleDescriptor descriptor = new RoleDescriptor();
	descriptor.setName(name);
	descriptor.setAim(aim);
	descriptor.setOperationDescriptors(operations);
	descriptor.setEventDscriptors(events);
	descriptor.setKeywords(keywords);
	
	return descriptor;
    }
    
}