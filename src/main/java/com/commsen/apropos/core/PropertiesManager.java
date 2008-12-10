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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import com.thoughtworks.xstream.XStream;

/**
 * The class provides static synchronized methods for common operations on {@link Property} and
 * {@link PropertyPackage} like add, delete, update, etc. It is also responsible for persisting data
 * after any change and loading data form storage on startup.
 * 
 * @author Milen Dyankov
 * 
 */
public class PropertiesManager {

	/**
	 * A folder to persists data into
	 */
	private static final File storage = new File(SystemUtils.getUserHome(), ".apropos");

	/**
	 * The name of the file containing data
	 */
	private static final File dataFile = new File(storage, "data.xml");

	/**
	 * 
	 */
	private static XStream xStream = new XStream();

	/**
	 * Map of all packages. Key is package name
	 */
	private static Map<String, PropertyPackage> allPackages = new HashMap<String, PropertyPackage>();

	/**
	 * The singletone instance used to persist data into file
	 */
	private static PropertiesManager instance = new PropertiesManager();

	static {
		xStream.alias("package", PropertyPackage.class);

		try {
			FileUtils.forceMkdir(storage);
		} catch (IOException e) {
		}
		load();
	}

	/**
	 * List of all top level packages
	 */
	private List<PropertyPackage> rootPackages = new LinkedList<PropertyPackage>();


	/**
	 * Private constructor.
	 */
	private PropertiesManager() {
		// private constructor
	}


	/**
	 * Loads properties form {@value #dataFile} and fills {@link #rootPackages} and
	 * {@value #allPackages}. This method is called only once - during class initialization (from a
	 * static block)
	 */
	private static void load() {
		if (dataFile.exists() && dataFile.isFile()) {
			try {
				instance = (PropertiesManager) xStream.fromXML(new FileInputStream(dataFile));
			} catch (FileNotFoundException e) {
				throw new InternalError(e.getMessage());
			}
			for (PropertyPackage rootPackage : instance.rootPackages) {
				addToAllPackages(rootPackage);
			}
		}
	}


	/**
	 * Adds a package and all of it's children to {@link #allPackages}.
	 * 
	 * @param propertyPackage the package to add
	 */
	private static void addToAllPackages(PropertyPackage propertyPackage) {
		if (propertyPackage == null) return;
		allPackages.put(propertyPackage.getName(), propertyPackage);
		if (propertyPackage.getChildren() != null) {
			for (PropertyPackage child : propertyPackage.getChildren()) {
				addToAllPackages(child);
			}
		}
	}


	/**
	 * Deletes a package and all of it's children from {@link #allPackages}.
	 * 
	 * @param propertyPackage the package to add
	 */
	private static void deleteFromAllPackages(PropertyPackage propertyPackage) {
		if (propertyPackage == null) return;
		if (propertyPackage.getChildren() != null) {
			for (PropertyPackage child : propertyPackage.getChildren()) {
				deleteFromAllPackages(child);
			}
		}
		allPackages.remove(propertyPackage.getName());
	}


	/**
	 * Saves data into {@link #dataFile}. This method is called by other static methods of this
	 * class after modifying any data.
	 */
	private static void save() {
		try {
			xStream.toXML(instance, new FileOutputStream(dataFile));
		} catch (FileNotFoundException e) {
			throw new InternalError(e.getMessage());
		}
	}


	/**
	 * Adds new {@link PropertyPackage}. NOTE: The object passed is cloned internally.
	 * 
	 * @param propertyPackage the {@link PropertyPackage} to be added. Can not be null.
	 * @throws PropertiesException if package with that name already exists or if circular
	 *         parent/child relation is detected.
	 * @throws IllegalArgumentException if <code>propertyPackage</code> is null
	 */
	public static synchronized void addPropertyPackage(PropertyPackage propertyPackage) throws PropertiesException {
		if (propertyPackage == null) throw new IllegalArgumentException("propertySet can not be null");
		String key = propertyPackage.getName();
		if (allPackages.containsKey(key)) throw new PropertiesException("Configuration called " + key + " already exists");

		String parentPackageName = null;
		if (propertyPackage.getParent() != null) {
			parentPackageName = propertyPackage.getParent().getName();
			propertyPackage.setParent(null);
		}

		PropertyPackage clonedPackage = null;
		try {
			clonedPackage = (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}

		clonedPackage.setParent(allPackages.get(parentPackageName));
		allPackages.put(key, clonedPackage);
		if (clonedPackage.getParent() == null) {
			instance.rootPackages.add(clonedPackage);
		}
		// packages.add(propertyPackage);
		save();
	}


	/**
	 * Returns unmodifiable (read only) list of all top level packages
	 * 
	 * @return unmodifiable (read only) list of all top level packages
	 */
	public static List<PropertyPackage> getRootPropertyPackages() {
		return Collections.unmodifiableList(instance.rootPackages);
	}


	/**
	 * Returns unmodifiable (read only) list of all package names
	 * 
	 * @return unmodifiable (read only) list of all package names
	 */
	public static List<String> getPropertyPackagesNames() {
		LinkedList<String> result = new LinkedList<String>(allPackages.keySet());
		Collections.sort(result);
		return Collections.unmodifiableList(result);
	}


	/**
	 * Returns a clone of {@link PropertyPackage} called <code>name</code>. If no such
	 * {@link PropertyPackage} exists it will return <code>null</code>.
	 * 
	 * @return a {@link PropertyPackage} called <code>name</code> or <code>null</code>.
	 */
	public static PropertyPackage getPropertyPackage(String name) {
		try {
			PropertyPackage propertyPackage = allPackages.get(name);
			return propertyPackage == null ? null : (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}


	/**
	 * Deletes the {@link PropertyPackage} specified by <code>packageName</code> and all it's
	 * children.
	 * 
	 * @param packageName
	 * @throws PropertiesException
	 */
	public static synchronized void deletePropertyPackage(String packageName) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		if (propertyPackage.getParent() != null) {
			propertyPackage.setParent(null); // to delete it from parent's collection
		} else {
			instance.rootPackages.remove(propertyPackage);
		}
		deleteFromAllPackages(propertyPackage);
		save();
	}


	/**
	 * Adds new {@link Property} in {@link PropertyPackage} called <code>packageName</code> by
	 * calling {@link PropertyPackage#addProperty(Property)}.
	 * 
	 * @param packageName the name of the package. Can not be <code>null</code> or empty.
	 * @param property the {@link Property} to add. Can not be <code>null</code>
	 * @return a new clone of the {@link PropertyPackage} made after the property is added
	 * @throws PropertiesException if no package called <code>packageName</code> found or if such
	 *         {@link Property} already exists in this package
	 * @throws IllegalArgumentException if any of the arguments is <code>null</code>
	 */
	public static synchronized PropertyPackage addProperty(String packageName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		try {
			propertyPackage.addProperty((Property) property.clone());
			save();
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}


	/**
	 * Updates {@link Property} in {@link PropertyPackage} called <code>packageName</code>. The
	 * actual update is handled by {@link PropertyPackage#updateProperty(Property)}
	 * 
	 * @param packageName the name of the package. Can not be <code>null</code> or empty.
	 * @param property the {@link Property} to be updated. Can not be <code>null</code>
	 * @return a new clone of the {@link PropertyPackage} made after the property is updated
	 * @throws PropertiesException if no package called <code>packageName</code> found or if no
	 *         such {@link Property} exists in this package
	 * @throws IllegalArgumentException if any of the arguments is <code>null</code>
	 */
	public static synchronized PropertyPackage updateProperty(String packageName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		try {
			propertyPackage.updateProperty((Property) property.clone());
			save();
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}


	/**
	 * Updates existing {@link Property} called <code>oldPropertyName</code> in
	 * {@link PropertyPackage} called <code>packageName</code> with values from
	 * <code>property</code>. The actual update is handled by
	 * {@link PropertyPackage#updateProperty(Property)}.
	 * 
	 * @param packageName the name of the package. Can not be <code>null</code> or empty.
	 * @param oldPropertyName the name of the property to be updated
	 * @param property the {@link Property} object to get the values from. Can not be
	 *        <code>null</code>
	 * @return a new clone of the {@link PropertyPackage} made after the property is updated
	 * @throws PropertiesException if no package called <code>packageName</code> found or if no
	 *         such {@link Property} exists in this package
	 * @throws IllegalArgumentException if any of the arguments is <code>null</code>
	 */
	public static synchronized PropertyPackage updateProperty(String packageName, String oldPropertyName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (property == null) throw new IllegalArgumentException("property can not be null");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		try {
			propertyPackage.updateProperty(oldPropertyName, (Property) property.clone());
			save();
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}


	/**
	 * Imports external properties into {@link PropertyPackage} called <code>packageName</code> .
	 * The actual import is handled by {@link PropertyPackage#importProperties(Properties, boolean)}.
	 * 
	 * @param packageName the name of the package. Can not be <code>null</code> or empty.
	 * @param externalProperties the properties to import. This can not be <code>null</code>
	 * @param overwrite boolean flag indicates whether to override existing properties
	 * @return a new clone of the {@link PropertyPackage} made after the properties are imported
	 * @throws PropertiesException if there is no package called <code>packageName</code>
	 * @throws IllegalArgumentException if any of the arguments is <code>null</code>
	 */
	public static synchronized PropertyPackage importProperties(String packageName, Properties externalProperties, boolean overwrite) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		try {
			propertyPackage.importProperties(externalProperties, overwrite);
			save();
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}


	/**
	 * Deletes {@link Property} called <code>propertyName</code> from {@link PropertyPackage}
	 * called <code>packageName</code>. The actual deletion is handled by
	 * {@link PropertyPackage#removeProperty(String)}.
	 * 
	 * @param packageName the name of the package. Can not be <code>null</code> or empty.
	 * @param propertyName the name of the property to be updated
	 * @return a new clone of the {@link PropertyPackage} made after the property is deleted
	 * @throws PropertiesException if there is no package called <code>packageName</code> or no
	 *         property called <code>propertyName</code>
	 * @throws IllegalArgumentException if any of the arguments is <code>null</code>
	 */
	public static synchronized PropertyPackage deleteProperty(String packageName, String propertyName) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (StringUtils.isBlank(propertyName)) throw new IllegalArgumentException("property can not be null nor empty string");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		propertyPackage.removeProperty(propertyName);
		save();
		try {
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

}
