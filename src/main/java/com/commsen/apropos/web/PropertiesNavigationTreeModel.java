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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertiesNavigationTreeModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @param root
	 */
	public PropertiesNavigationTreeModel() {
		super(buildNodes());
	}


	private static TreeNode buildNodes() {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("properties");
		List<PropertyPackage> propertPackageNames = PropertiesManager.getRootPropertyPackages();
		if (!propertPackageNames.isEmpty()) {
			for (PropertyPackage propertyPackage : propertPackageNames) {
				makeNode(root, propertyPackage);
			}
		}
		return root;
	}


	private static void makeNode(DefaultMutableTreeNode parent, PropertyPackage propertyPackage) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(propertyPackage.getName());
		parent.add(node);
		if (propertyPackage.getChildren() != null) {
			for (PropertyPackage child : propertyPackage.getChildren()) {
				makeNode(node, child);
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.commsen.apropos.web.event.EventListener#handleEvent(com.commsen.apropos.web.event.Event)
	 */
	public void updateTree() {
		setRoot(buildNodes());
	}

}
