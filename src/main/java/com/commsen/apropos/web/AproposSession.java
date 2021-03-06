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
package com.commsen.apropos.web;

import java.util.Properties;
import java.util.Set;

import org.wings.session.SessionManager;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.Property;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventManager;

/**
 * @author Milen Dyankov
 * 
 */
public class AproposSession {

	private static final String base = "com.commsen.apropos.";
	public static final String CURRENT_PACKAGE = base + "currentPackage";
	public static final String SHOW_PARENT_PROPERTIES = base + "showParentProperties";


	public static PropertyPackage getCurrentPropertyPackage() {
		return (PropertyPackage) SessionManager.getSession().getProperty(CURRENT_PACKAGE);
	}


	public static void setCurrentPropertyPackage(PropertyPackage propertyPackage) {
		SessionManager.getSession().setProperty(CURRENT_PACKAGE, propertyPackage);
	}


	public static void addProperty(Property property) throws PropertiesException {
		PropertyPackage updatedPackage = PropertiesManager.addProperty(getCurrentPropertyPackage().getName(), property);
		AproposSession.setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.PROPERTY_ADDED);
	}


	public static void updateProperty(Property property) throws PropertiesException {
		PropertyPackage updatedPackage = PropertiesManager.updateProperty(getCurrentPropertyPackage().getName(), property);
		AproposSession.setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.PROPERTY_UPDATED);
	}


	public static void updateProperty(String oldName, Property property) throws PropertiesException {
		PropertyPackage updatedPackage = PropertiesManager.updateProperty(getCurrentPropertyPackage().getName(), oldName, property);
		AproposSession.setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.PROPERTY_UPDATED);
	}


	public static void importProperties(Properties externalProperties, boolean overwrite) throws PropertiesException {
		PropertyPackage updatedPackage = PropertiesManager.importProperties(getCurrentPropertyPackage().getName(), externalProperties, overwrite);
		AproposSession.setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.CURRENT_PACKAGE_CHANGED);
	}


	public static void deleteProperty(String property) throws PropertiesException {
		PropertyPackage updatedPackage = PropertiesManager.deleteProperty(getCurrentPropertyPackage().getName(), property);
		AproposSession.setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.PROPERTY_DELETED);
	}


	public static void deleteProperties(Set<String> properties) throws PropertiesException {
		PropertyPackage updatedPackage = getCurrentPropertyPackage();
		for (String property : properties) {
			updatedPackage = PropertiesManager.deleteProperty(getCurrentPropertyPackage().getName(), property);
		}
		setCurrentPropertyPackage(updatedPackage);
		EventManager.getInstance().sendEvent(Event.PROPERTY_DELETED);
	}


	public static void setShowParentProperties(boolean show) {
		SessionManager.getSession().setProperty(SHOW_PARENT_PROPERTIES + "." + getCurrentPropertyPackage().getName(), show);
	}


	public static boolean showParentProperties() {
		if (getCurrentPropertyPackage() == null) return false;
		Boolean result = (Boolean) SessionManager.getSession().getProperty(SHOW_PARENT_PROPERTIES + "." + getCurrentPropertyPackage().getName());
		return result == null ? false : result;
	}

}
