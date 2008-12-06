/**
 * 
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
 * @author Milen Dyankov
 * 
 */
public class PropertiesManager {

	private static final File storage = new File(SystemUtils.getUserHome(), ".apropos");
	private static final File dataFile = new File(storage, "data.xml");

	private static XStream xStream = new XStream();

	private static Map<String, PropertyPackage> allPackages = new HashMap<String, PropertyPackage>();

	private static PropertiesManager instance = new PropertiesManager();

	static {
		xStream.alias("package", PropertyPackage.class);

		try {
			FileUtils.forceMkdir(storage);
		} catch (IOException e) {
		}
		load();
	}

	private List<PropertyPackage> rootPackages = new LinkedList<PropertyPackage>();


	private PropertiesManager() {
	}


	private static void load() {
		if (dataFile.exists() && dataFile.isFile()) {
			try {
				instance = (PropertiesManager) xStream.fromXML(new FileInputStream(dataFile));
			} catch (FileNotFoundException e) {
				throw new InternalError(e.getMessage());
			}
			for (PropertyPackage rootPackage : instance.rootPackages) {
				fillAllPackages(rootPackage);
			}
		}
	}


	private static void fillAllPackages(PropertyPackage propertyPackage) {
		if (propertyPackage == null) return;
		allPackages.put(propertyPackage.getName(), propertyPackage);
		if (propertyPackage.getChildren() != null) {
			for (PropertyPackage child : propertyPackage.getChildren()) {
				fillAllPackages(child);
			}
		}
	}


	private static void save() {
		try {
			xStream.toXML(instance, new FileOutputStream(dataFile));
		} catch (FileNotFoundException e) {
			throw new InternalError(e.getMessage());
		}
	}


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


	public static List<PropertyPackage> getRootPropertyPackages() {
		return Collections.unmodifiableList(instance.rootPackages);
	}


	public static List<String> getPropertyPackagesNames() {
		LinkedList<String> result = new LinkedList<String>(allPackages.keySet());
		Collections.sort(result);
		return Collections.unmodifiableList(result);
	}


	public static PropertyPackage getPropertyPackage(String name) {
		try {
			return (PropertyPackage) allPackages.get(name).clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}


	public static List<PropertyPackage> getPropertyPackages() throws PropertiesException {
		List<PropertyPackage> result = new LinkedList<PropertyPackage>();
		for (PropertyPackage propertyPackage : allPackages.values()) {
			try {
				result.add((PropertyPackage) propertyPackage.clone());
			} catch (CloneNotSupportedException e) {
				throw new PropertiesException("Error occured while getting properties", e);
			}
		}
		return result;
	}


	public static synchronized PropertyPackage addProperty(String packageName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
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


	public static synchronized PropertyPackage updateProperty(String packageName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
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


	public static synchronized PropertyPackage updateProperty(String packageName, String oldPropertyName, Property property) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
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


	public static synchronized PropertyPackage deleteProperty(String packageName, String propertyName) throws PropertiesException {
		if (StringUtils.isBlank(packageName)) throw new IllegalArgumentException("package name can not be null nor empty string");
		if (StringUtils.isBlank(propertyName)) throw new IllegalArgumentException("property can not be null nor empty string");
		if (!allPackages.containsKey(packageName)) throw new PropertiesException("No package called " + packageName + " found ");
		PropertyPackage propertyPackage = allPackages.get(packageName);
		propertyPackage.deleteProperty(propertyName);
		save();
		try {
			return (PropertyPackage) propertyPackage.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

}
