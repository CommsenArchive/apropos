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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents a package of properties. In APropOS a package is simply a container of
 * {@link Property} objects. A package has name and may also have description, parent and child
 * packages.
 * 
 * @author Milen Dyankov
 * 
 */

public class PropertyPackage implements Cloneable {

	/**
	 * the name of the package
	 */
	private String name;

	/**
	 * the description of the package
	 */
	private String description;

	/**
	 * reference to the parent
	 */
	private PropertyPackage parent = null;

	/**
	 * properties
	 */
	private Map<String, Property> properties = new LinkedHashMap<String, Property>();

	/**
	 * list of child packages
	 */
	private List<PropertyPackage> children = new LinkedList<PropertyPackage>();


	/**
	 * Constructs new package
	 * 
	 * @param name the name, this is required field
	 * @param description the description
	 * @param parent the parent package, if not provided it will become first level package
	 * @throws IllegalArgumentException if name is <code>null</code> or blank
	 */
	public PropertyPackage(String name, String description, PropertyPackage parent) {
		if (StringUtils.isBlank(name)) throw new IllegalArgumentException("name can not be null");
		this.name = name.trim();
		this.description = description;
		if (parent != null) {
			this.parent = parent;
			parent.children.add(this);
		}
	}


	/**
	 * Constructs new first level package (without parent)
	 * 
	 * @param name the name, this is required field
	 * @param description the description
	 */
	public PropertyPackage(String name, String description) {
		this(name, description, null);
	}


	/**
	 * Constructs new package
	 * 
	 * @param name the name, this is required field
	 * @param parent the parent package, if not provided it will become first level package
	 */
	public PropertyPackage(String name, PropertyPackage parent) {
		this(name, null, parent);
	}


	/**
	 * Constructs new package
	 * 
	 * @param name the name, this is required field
	 */
	public PropertyPackage(String name) {
		this(name, null, null);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		PropertyPackage result = (PropertyPackage) super.clone();
		result.properties = new LinkedHashMap<String, Property>();
		if (!properties.isEmpty()) {
			for (Property property : properties.values()) {
				result.properties.put(property.getName(), (Property) property.clone());
			}
		}
		return result;
	}


	/**
	 * Adds new property to this package.
	 * 
	 * @param property the {@link Property} to be added. This is a required parameter
	 * @return reference do the added property
	 * @throws PropertiesException if property with such name already exists
	 * @throws IllegalArgumentException if <code>property</code> is null
	 */
	public Property addProperty(Property property) throws PropertiesException {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (properties.containsKey(property.getName())) throw new PropertiesException("Property called " + property.getName() + " already exists!");
		return properties.put(property.getName(), property);
	}


	/**
	 * Updates a property in this package.
	 * 
	 * @param property the {@link Property} to be updated. This is a required parameter
	 * @return reference do the added property
	 * @throws PropertiesException if no such property exists
	 * @throws IllegalArgumentException if <code>property</code> is null
	 */
	public Property updateProperty(Property property) throws PropertiesException {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!properties.containsKey(property.getName())) throw new PropertiesException("Property called " + property.getName() + " does not exists!");
		return properties.put(property.getName(), property);
	}


	/**
	 * Updates a property called <code>oldName</code> in this package with values from
	 * <code>property</code>. If <code>oldName</code> is null this method behaves exactly like
	 * {@link #updateProperty(Property)}
	 * 
	 * @param oldName the name of the property to be updated
	 * @param property the property to get values from
	 * @return reference do the added property
	 * @throws PropertiesException if no such property exists
	 * @throws IllegalArgumentException if <code>property</code> is null
	 */
	public Property updateProperty(String oldName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(oldName)) return updateProperty(property);
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!properties.containsKey(oldName)) throw new PropertiesException("Property called " + oldName + " does not exists!");
		if (!oldName.equals(property.getName())) {
			properties.remove(oldName);
		}
		return properties.put(property.getName(), property);
	}


	/**
	 * Removes property from this package
	 * 
	 * @param property the property to be removed
	 * @return reference to removed property
	 */
	public Property removeProperty(Property property) {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		return properties.remove(property.getName());
	}


	/**
	 * Removes property called <code>propertyName</code> from this package
	 * 
	 * @param propertyName the name of the property to be removed
	 * @return reference to removed property
	 */
	public Property removeProperty(String propertyName) {
		if (propertyName == null) throw new IllegalArgumentException("property name can not be null or empty string");
		return properties.remove(propertyName);
	}


	/**
	 * Reads external {@link Properties} and adds them all to this package.
	 * 
	 * @param externalProperties the external properties to be added
	 * @param overwrite boolean flag indicating whether to overwrite existing properties
	 * @throws IllegalArgumentException if <code>externalProperties</code> is null
	 */
	public void importProperties(Properties externalProperties, boolean overwrite) {
		if (externalProperties == null) throw new IllegalArgumentException("properties can not be null");
		for (Object key : externalProperties.keySet()) {
			String propertyName = (String) key;
			if (!overwrite && this.properties.containsKey(propertyName)) continue;
			try {
				this.properties.put(propertyName, new Property(propertyName, externalProperties.getProperty(propertyName), null, null));
			} catch (PropertiesException e) {
				/*
				 * this should never happen since the key in Properties can not be null and that is
				 * the only case where new Property() throws PropertiesException
				 */
				throw new InternalError(e.getMessage());
			}
		}
	}


	/**
	 * Converts this package to {@link Properties} object. The result object contains all properties
	 * from all parents as returned by {@link #getAllProperties()}.
	 * 
	 * @return {@link Properties} object
	 */
	public Properties asProperties() {
		Properties result = new Properties();
		for (Property property : getAllProperties().values()) {
			result.setProperty(property.getName(), property.getValue());
		}
		return result;
	}


	/**
	 * Returns a map of all properties in this package which overwrite same properties from parent
	 * packages.
	 * 
	 * @return the properties a map of all properties in this package which overwrite same
	 *         properties from parent packages.
	 */
	public Map<String, Property> getOverwritenProperties() {
		Map<String, Property> parentProperties = new HashMap<String, Property>();
		if (parent != null) parentProperties.putAll(parent.getAllProperties());

		Map<String, Property> result = new HashMap<String, Property>();
		for (String propertyName : properties.keySet()) {
			if (parentProperties.containsKey(propertyName)) {
				result.put(propertyName, parentProperties.get(propertyName));
			}
		}
		return result;
	}


	/**
	 * Returns a map of all properties from this package and all parent packages.
	 * 
	 * @return a map of all properties from this package and all parent packages.
	 */
	public Map<String, Property> getAllProperties() {
		Map<String, Property> result = new HashMap<String, Property>();
		if (parent != null) result.putAll(parent.getAllProperties());
		result.putAll(properties);
		return result;
	}


	/**
	 * Checks if <code>propertyPackage</code> is a child package of this package.
	 * 
	 * @param propertyPackage 0 the package to be checked
	 * @return <code>true</code> if <code>propertyPackage</code> is a child package of this
	 *         package, <code>false</code> otherwise.
	 */
	public boolean containsChild(PropertyPackage propertyPackage) {
		if (propertyPackage == null) return false;
		if (children.contains(propertyPackage)) return true;
		for (PropertyPackage child : children) {
			if (child.containsChild(propertyPackage)) return true;
		}
		return false;
	}


	/**
	 * @return the properties
	 */
	public Map<String, Property> getProperties() {
		return properties;
	}


	/**
	 * @return the parent
	 */
	public PropertyPackage getParent() {
		return this.parent;
	}


	/**
	 * @param parent the parent to set
	 * @throws PropertiesException
	 */
	public void setParent(PropertyPackage parent) throws PropertiesException {
		if (this.parent == parent) return;
		if (containsChild(parent)) {
			throw new PropertiesException("Package " + parent.getName() + " is child of " + name + " and can not be set as it's parent!");
		}
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
		this.parent = parent;
		if (this.parent != null) {
			this.parent.children.add(this);
		}
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
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * @return the children
	 */
	public List<PropertyPackage> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

}
