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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SGridLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.SScrollPane;
import org.wings.STree;
import org.wings.border.SLineBorder;
import org.wings.tree.SDefaultTreeCellRenderer;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.dialog.AddPropertyPackageDialog;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventListener;
import com.commsen.apropos.web.event.EventManager;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertyPackagesNavigator extends SPanel implements EventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PropertiesNavigationTreeModel treeModel = new PropertiesNavigationTreeModel();

	final STree tree = new STree(treeModel);


	/**
	 * 
	 */
	public PropertyPackagesNavigator() {
		super();
		buildPanel();
		EventManager.getInstance().addListener(Event.PACKAGE_ADDED, this);
		EventManager.getInstance().addListener(Event.PACKAGE_DELETED, this);

	}


	private void buildPanel() {
		setLayout(new SBorderLayout());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setVerticalAlignment(SConstants.TOP_ALIGN);
		tree.setRootVisible(false);

		SDefaultTreeCellRenderer cellRenderer = (SDefaultTreeCellRenderer) tree.getCellRenderer();
		cellRenderer.setOpenIcon(cellRenderer.getLeafIcon());
		cellRenderer.setClosedIcon(cellRenderer.getLeafIcon());

		expandAllTreeNodes();

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node != null) {
					PropertyPackage propertyPackage = PropertiesManager.getPropertyPackage((String) node.getUserObject());
					AproposSession.setCurrentPropertyPackage(propertyPackage);
					EventManager.getInstance().sendEvent(Event.CURRENT_PACKAGE_CHANGED);
				}
			}
		});
		tree.setPreferredSize(SDimension.FULLWIDTH);

		SScrollPane scrollPane = new SScrollPane();
		scrollPane.setViewportView(tree);
		scrollPane.setMode(SScrollPane.MODE_COMPLETE);
		scrollPane.setPreferredSize(SDimension.FULLAREA);
		scrollPane.setVerticalAlignment(SConstants.TOP_ALIGN);

		setPreferredSize(new SDimension("250px", "100%"));
		setBackground(new Color(235, 235, 235));
		SLineBorder border = new SLineBorder(Color.GRAY, 0);
		border.setThickness(2, SConstants.RIGHT);
		setBorder(border);

		add(scrollPane, SBorderLayout.CENTER);
		add(makeButtonsPanel(), SBorderLayout.NORTH);

	}


	private SPanel makeButtonsPanel() {
		SPanel result = new SPanel();
		result.setLayout(new SGridLayout(2));

		SButton addButton = new SButton("Add");
		addButton.setPreferredSize(SDimension.FULLAREA);
		result.add(addButton);

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new AddPropertyPackageDialog().show(null);
			}
		});

		final SButton deleteButton = new SButton("Delete");
		deleteButton.setPreferredSize(SDimension.FULLAREA);
		result.add(deleteButton);

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SOptionPane.showQuestionDialog(deleteButton, "Going to delete properties package \"" + AproposSession.getCurrentPropertyPackage().getName()
				        + "\" together with all sub-packages! Are you sure?", "Deleting selected package", new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if (event.getActionCommand().equals(SOptionPane.OK_ACTION)) {
							try {
								PropertyPackage currentPackage = AproposSession.getCurrentPropertyPackage();
								PropertiesManager.deletePropertyPackage(currentPackage.getName());
								AproposSession.setCurrentPropertyPackage(currentPackage.getParent());
								EventManager.getInstance().sendEvent(Event.PACKAGE_DELETED);
							} catch (PropertiesException e) {
								SOptionPane.showMessageDialog(deleteButton, e.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
			}
		});
		return result;
	}


	public final void expandAllTreeNodes() {
		for (int i = 0; i < tree.getRowCount(); ++i) {
			tree.expandRow(i);
			PropertyPackage currentPackage = AproposSession.getCurrentPropertyPackage();
			if (currentPackage != null) {
				String useObject = (String) ((DefaultMutableTreeNode) tree.getPathForRow(i).getLastPathComponent()).getUserObject();
				if (currentPackage.getName().equals(useObject)) {
					tree.setSelectionRow(i);
				}
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.commsen.apropos.web.event.EventListener#handleEvent(com.commsen.apropos.web.event.Event)
	 */
	public void handleEvent(Event event) {
		treeModel.updateTree();
		expandAllTreeNodes();
	}

}
