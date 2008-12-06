/**
 * 
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
 * @author Milen Dyankov
 * 
 */

public class PropertyPackage implements Cloneable {

	private String name;

	private String description;

	private PropertyPackage parent = null;

	private Map<String, Property> properties = new LinkedHashMap<String, Property>();

	private List<PropertyPackage> children = new LinkedList<PropertyPackage>();


	public PropertyPackage(String name, String description, PropertyPackage parent) {
		this.name = name.trim();
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
		this.description = description;
	}


	public PropertyPackage(String name, String description) {
		this(name, description, null);
	}


	public PropertyPackage(String name, PropertyPackage parent) {
		this(name, null, parent);
	}


	public PropertyPackage(String name) {
		this(name, null, null);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
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


	public Property addProperty(Property property) throws PropertiesException {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (properties.containsKey(property.getName())) throw new PropertiesException("Property called " + property.getName() + " already exists!");
		return properties.put(property.getName(), property);
	}


	public Property updateProperty(Property property) throws PropertiesException {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!properties.containsKey(property.getName())) throw new PropertiesException("Property called " + property.getName() + " does not exists!");
		return properties.put(property.getName(), property);
	}


	public Property updateProperty(String oldName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(oldName)) return updateProperty(property);
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!properties.containsKey(oldName)) throw new PropertiesException("Property called " + oldName + " does not exists!");
		if (!oldName.equals(property.getName())) {
			properties.remove(oldName);
		}
		return properties.put(property.getName(), property);
	}


	public Property deleteProperty(Property property) {
		if (property == null) throw new IllegalArgumentException("property can not be null");
		return properties.remove(property.getName());
	}


	public Property deleteProperty(String propertyName) {
		if (propertyName == null) throw new IllegalArgumentException("property name can not be null or empty string");
		return properties.remove(propertyName);
	}


	public void importProperties(Properties externalProperties, boolean overwrite) throws PropertiesException {
		if (externalProperties == null) throw new IllegalArgumentException("properties can not be null");
		for (Object key : externalProperties.keySet()) {
			String propertyName = (String) key;
			if (!overwrite && this.properties.containsKey(propertyName)) continue;
			this.properties.put(propertyName, new Property(propertyName, externalProperties.getProperty(propertyName), null, null));
		}
	}


	public Properties asProperties() {
		Properties result = new Properties();
		for (Property property : getAllProperties().values()) {
			result.setProperty(property.getName(), property.getValue());
		}
		return result;
	}


	/**
	 * @return the properties
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
	 * @return the properties
	 */
	public Map<String, Property> getAllProperties() {
		Map<String, Property> result = new HashMap<String, Property>();
		if (parent != null) result.putAll(parent.getAllProperties());
		result.putAll(properties);
		return result;
	}


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
		if (this.parent != null) this.parent.children.remove(this);
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
