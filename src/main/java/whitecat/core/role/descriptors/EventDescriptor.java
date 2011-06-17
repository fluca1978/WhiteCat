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

/**
 * A descriptor of an event. The event descriptor is described in the article
 * <i> Injecting roles in Java agents through runtime bytecode manipulation IBM
 * SYSTEMS JOURNAL, VOL 44, NO 1, 2005 </i>.
 * 
 * The aim of an event descriptor is to describe an event that can be issued or
 * received thru the execution of an operation.
 * 
 * Even if this implementation is inspired by the above article, it presents a
 * better modularity and organization of methods and properties, as well as the
 * adoption of a collections instead of plain arrays.
 * 
 * @author Luca Ferrari - cat4hire@users.sourceforge.net
 * 
 * 
 */
public class EventDescriptor extends AbstractDescriptor {

	/**
	 * Builds up an event descriptor instance with the specified properties.
	 * 
	 * @param name
	 *            the name of the event
	 * @param aim
	 *            the aim of the event
	 * @param issuing
	 *            true if the event can be issued
	 * @param receiving
	 *            true if the event can be received
	 * @return the event descriptor instance
	 */
	public static final EventDescriptor getInstance(final String name,
													final String aim,
													final boolean issuing,
													final boolean receiving) {
		// create a new descriptor
		final EventDescriptor descriptor = new EventDescriptor();

		// set the values for this descriptor
		descriptor.setName( name );
		descriptor.setAim( aim );
		descriptor.setIssuing( issuing );
		descriptor.setReceiving( receiving );

		// all done
		return descriptor;
	}

	/**
	 * Specifies if the event can be received or is just sent out.
	 */
	private boolean	receiving	= false;

	/**
	 * Specifies if the event can be issued.
	 */
	private boolean	issuing		= true;

	/**
	 * Default constructor (for serialization).
	 * 
	 */
	private EventDescriptor() {
		super();
	}

	/**
	 * Tests the equality with another event descriptor. Overridden version.
	 * 
	 * @overrides @see
	 *            it.unimo.polaris.rolex.roles.AbstractDescriptor#equals(java
	 *            .lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof EventDescriptor))
			return false;
		else{
			final EventDescriptor descriptor = (EventDescriptor) o;

			if (issuing != descriptor.issuing)
				return false;

			if (receiving != descriptor.receiving)
				return false;

			// if here, compare the base properties
			return super.equals( descriptor );
		}
	}

	/**
	 * Builds up an hash code depending on the properties of this event
	 * descriptor and the base properties. Overridden version.
	 * 
	 * @overrides @see
	 *            it.unimo.polaris.rolex.roles.AbstractDescriptor#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + Boolean.toString( issuing ).hashCode();
		hash = hash * 31 + Boolean.toString( receiving ).hashCode();
		return hash;
	}

	/**
	 * This method can be used to set the value of the issuing field, providing
	 * thus read access to the issuing property.
	 * 
	 * @return the issuing value.
	 */
	public final boolean isIssuing() {
		return issuing;
	}

	/**
	 * This method can be used to set the value of the receiving field,
	 * providing thus read access to the receiving property.
	 * 
	 * @return the receiving value.
	 */
	public final boolean isReceiving() {
		return receiving;
	}

	/**
	 * This method can be used to set the value of the issuing field, providing
	 * thus write access to the issuing property.
	 * 
	 * @param issuing
	 *            the issuing to set with the specified value.
	 */
	protected final void setIssuing(final boolean issuing) {
		this.issuing = issuing;
	}

	/**
	 * This method can be used to set the value of the receiving field,
	 * providing thus write access to the receiving property.
	 * 
	 * @param receiving
	 *            the receiving to set with the specified value.
	 */
	protected final void setReceiving(final boolean receiving) {
		this.receiving = receiving;
	}
}
