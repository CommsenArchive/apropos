/**
 * 
 */
package com.commsen.apropos.core;

import org.apache.commons.lang.StringUtils;

/**
 * @author Milen Dyankov
 * 
 */
public class Property implements Cloneable {

	private String name;

	private String value;

	private String description;

	private String group;


	public Property(String name) throws PropertiesException {
		this(name, null, null, null);
	}


	public Property(String name, String value, String description, String group) throws PropertiesException {
		setName(name);
		setValue(value);
		setDescription(description);
		setGroup(group);
	}


	public boolean sameAs(Property property) {
		return StringUtils.equals(group, property.getGroup()) && StringUtils.equals(name, property.getName()) && StringUtils.equals(value, property.getValue())
		        && StringUtils.equals(description, property.getDescription());
	}


	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj.getClass().equals(this.getClass()))) return false;
		return ((Property) obj).name.equals(name);
	}


	/*
	 * (non-Javadoc)
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
	 * @param name the name to set
	 * @throws PropertiesException
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
