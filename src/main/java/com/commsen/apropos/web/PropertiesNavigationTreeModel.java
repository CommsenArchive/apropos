/**
 * 
 */
package com.commsen.apropos.web;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventListener;
import com.commsen.apropos.web.event.EventManager;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertiesNavigationTreeModel extends DefaultTreeModel implements EventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @param root
	 */
	public PropertiesNavigationTreeModel() {
		super(buildNodes());
		EventManager.getInstance().addListener(Event.SET_ADDED, this);
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
	public void handleEvent(Event event) {
		setRoot(buildNodes());

	}

}
