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

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * The base for all the descriptors tied to roles. This base provides the main
 * functionalities for each descriptor, as described in the article <i>
 * Injecting roles in Java agents through runtime bytecode manipulation IBM
 * SYSTEMS JOURNAL, VOL 44, NO 1, 2005 </i>.
 * 
 * Even if this implementation is inspired by the above article, it presents a
 * better modularity and organization of methods and properties, as well as the
 * adoption of a collections instead of plain arrays.
 * 
 * Please note that this class is abstract and that the properties can be
 * publically read, but not written, in order to not allow an external process
 * to change the descriptor.
 * 
 * @author Luca Ferrari - cat4hire@users.sourceforge.net
 * 
 * 
 */
public abstract class AbstractDescriptor {

	/**
	 * The name of this descriptor, used for basic lookup of the descriptor.
	 */
	private String		name			= null;
	/**
	 * The aim of the role this descriptor is tied to.
	 */
	private String		aim				= null;
	/**
	 * The version of this descriptor, used to understand if the role descriptor
	 * is up-to-date. By default the version of this descriptors is 2.
	 */
	private float		version			= 2;
	/**
	 * The creation date of this descriptor.
	 */
	private Date		creationDate	= null;
	/**
	 * A set of keywords tied to this descriptor.
	 */
	private Set<String>	keywords		= null;

	/**
	 * Adds the specified keyword to the keyword of this descriptor.
	 * 
	 * @param key
	 *            the keyword to add
	 * @return true if the key has been added
	 * @see java.util.Set#add(java.lang.Object)
	 */
	protected final synchronized boolean addKeyword(final String key) {
		return keywords.add( key );
	}

	/**
	 * Checks if the specified keyword is contained in the current keyword set.
	 * 
	 * @param key
	 *            the key to search for
	 * @return true if the key is contained
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public final synchronized boolean containsKeyword(final String key) {
		return keywords.contains( key );
	}

	/**
	 * Searches for multiple keywords in this descriptor.
	 * 
	 * @param keys
	 *            the list of keywords to search for
	 * @return true if all the keywords are contained in this descriptor
	 */
	public final synchronized boolean containsKeywords(final List<String> keys) {
		return keywords.containsAll( keys );
	}

	/**
	 * Deeply compares this descriptor with another descriptor. Overridden
	 * version.
	 * 
	 * @overrides @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof AbstractDescriptor))
			return false;
		else{
			// deeply compare the properties of the two descriptors
			final AbstractDescriptor descriptor = (AbstractDescriptor) o;

			if ((aim != null) && (!(aim.equals( descriptor.aim ))))
				return false;

			if (!(version == descriptor.version))
				return false;

			if ((creationDate != null)
					&& (!(creationDate.equals( descriptor.creationDate ))))
				return false;

			if ((name != null) && (!(name.equals( descriptor.name ))))
				return false;

			if ((keywords != null)
					&& (!(keywords.equals( descriptor.keywords ))))
				return false;

			// if here, it is all the same data!
			return true;
		}
	}

	/**
	 * This method can be used to set the value of the aim field, providing thus
	 * read access to the aim property.
	 * 
	 * @return the aim value.
	 */
	public final String getAim() {
		return aim;
	}

	/**
	 * This method can be used to set the value of the creationDate field,
	 * providing thus read access to the creationDate property.
	 * 
	 * @return the creationDate value.
	 */
	public final Date getCreationDate() {
		return creationDate;
	}

	/**
	 * This method can be used to set the value of the keywords field, providing
	 * thus read access to the keywords property.
	 * 
	 * @return the keywords value iterator
	 */
	public final Iterator<String> getKeywords() {
		return keywords.iterator();
	}

	/**
	 * This method can be used to set the value of the name field, providing
	 * thus read access to the name property.
	 * 
	 * @return the name value.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * This method can be used to set the value of the version field, providing
	 * thus read access to the version property.
	 * 
	 * @return the version value.
	 */
	public final float getVersion() {
		return version;
	}

	/**
	 * Provides the hash value for this descriptor, calculated starting from the
	 * hash codes of the properties. Overridden version.
	 * 
	 * @overrides @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = (int) (version * 100);

		hash = 31 * hash + (aim == null ? 0 : aim.hashCode());
		hash = 31 * hash + (keywords == null ? 0 : keywords.hashCode());
		hash = 31 * hash + (name == null ? 0 : name.hashCode());
		hash = 31 * hash + (creationDate == null ? 0 : creationDate.hashCode());
		return hash;
	}

	/**
	 * Returns the number of keywords currently associated to this descriptor.
	 * 
	 * @return the number of keywords
	 * @see java.util.Set#size()
	 */
	public final synchronized int keywordCount() {
		return keywords.size();
	}

	@Deprecated
	public final synchronized boolean matchKeyword(final String key) {
		return containsKeyword( key );
	}

	/**
	 * Searches for an array of keywords.
	 * 
	 * @param keys
	 *            the keywords to search for
	 * @return true if the keys are contained in the keywords of this
	 *         descriptor.
	 */
	@Deprecated
	public final synchronized boolean matchKeywords(final String[] keys) {
		final List<String> keywords = Arrays.asList( keys );
		return containsKeywords( keywords );
	}

	/**
	 * Removes the current keyword from the set of keywords of this descriptor.
	 * 
	 * @param key
	 *            the keyword to remove from the descriptor
	 * @return true if the keyword have been removed
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	protected final synchronized boolean removeKeyword(final String key) {
		return keywords.remove( key );
	}

	/**
	 * This method can be used to set the value of the aim field, providing thus
	 * write access to the aim property.
	 * 
	 * @param aim
	 *            the aim to set with the specified value.
	 */
	protected final void setAim(final String aim) {
		this.aim = aim;
	}

	/**
	 * This method can be used to set the value of the creationDate field,
	 * providing thus write access to the creationDate property.
	 * 
	 * @param creationDate
	 *            the creationDate to set with the specified value.
	 */
	protected final void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * This method can be used to set the value of the keywords field, providing
	 * thus write access to the keywords property.
	 * 
	 * @param keywords
	 *            the keywords to set with the specified value.
	 */
	protected final void setKeywords(final Set<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * This method can be used to set the value of the name field, providing
	 * thus write access to the name property.
	 * 
	 * @param name
	 *            the name to set with the specified value.
	 */
	protected final void setName(final String name) {
		this.name = name;
	}

	/**
	 * This method can be used to set the value of the version field, providing
	 * thus write access to the version property.
	 * 
	 * @param version
	 *            the version to set with the specified value.
	 */
	protected final void setVersion(final float version) {
		this.version = version;
	}

}