/*
 * Copyright 2008 COMMSEN International
 *
 * This file is part of APropOS.
 * 
 * APropOS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * APropOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with APropOS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.commsen.apropos.core;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents property. In APropOS a property is a very simple object having name, value,
 * description and group it belongs to.
 * 
 * @author Milen Dyankov
 * 
 */
public class Property implements Cloneable {

	/**
	 * the name
	 */
	private String name;

	/**
	 * the value
	 */
	private String value;

	/**
	 * the description
	 */
	private String description;

	/**
	 * the group this property belongs to
	 */
	private String group;


	/**
	 * Creates new property with given <code>name</code>
	 * 
	 * @param name the name of the property
	 * @throws PropertiesException if <code>name</code> is <code>null</code> or blank
	 */
	public Property(String name) throws PropertiesException {
		this(name, null, null, null);
	}


	/**
	 * Creates new property object and sets all fields with appropriate values
	 * 
	 * @param name the property name
	 * @param value the property value
	 * @param description the property description
	 * @param group the group this property belongs to
	 * @throws PropertiesException if <code>name</code> is <code>null</code> or blank
	 */
	public Property(String name, String value, String description, String group) throws PropertiesException {
		setName(name);
		setValue(value);
		setDescription(description);
		setGroup(group);
	}


	/**
	 * Checks if given <code>property</code> is the same as the one represented by this object.
	 * This method will return <code>true</code> if ALL fields have exactly the same values and
	 * <code>false</code> otherwise
	 * 
	 * @param property the property to compare
	 * @return <code>true</code> if both object have exactly the same values in all fields and
	 *         <code>false</code> otherwise
	 */
	public boolean sameAs(Property property) {
		return StringUtils.equals(group, property.getGroup()) && StringUtils.equals(name, property.getName()) && StringUtils.equals(value, property.getValue())
		        && StringUtils.equals(description, property.getDescription());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	/**
	 * Returns true if <code>obj.name.equals(name)</code>
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj.getClass().equals(this.getClass()))) return false;
		return ((Property) obj).name.equals(name);
	}


	/**
	 * Returns hashCode based on {@link #name} to conform to modified {@link #equals(Object)}
	 * method.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == name ? 0 : name.hashCode());
		return hash;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * @param name the name of the property
	 * @throws PropertiesException if <code>name</code> is <code>null</code> or blank
	 */
	public void setName(String name) throws PropertiesException {
		if (StringUtils.isBlank(name)) throw new PropertiesException("Can not create property without name!");
		this.name = name;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}


	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

}
