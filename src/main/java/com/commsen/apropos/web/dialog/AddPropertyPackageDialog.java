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
package com.commsen.apropos.web.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SGridLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextArea;
import org.wings.STextField;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.AproposSession;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventManager;
import com.commsen.apropos.wings.AproposGridLaoyut;

/**
 * This class represents a dialog window used to add new properties package.
 * 
 * @author Milen Dyankov
 * 
 */
public class AddPropertyPackageDialog extends AproposBaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Text field for package's name
	 */
	final STextField nameField = new STextField();

	/**
	 * Text area for package's description
	 */
	final STextArea descriptionField = new STextArea();

	/**
	 * ComoboBox with all possible parents
	 */
	final SComboBox parentField = new SComboBox();


	/**
	 * Method called to (re)initialize the dialog according to current application state. Currently
	 * it only rebuilds the {@link #parentField} to fill it with all possible parents.
	 */
	public void initialize() {
		parentField.removeAllItems();
		if (!CollectionUtils.isEmpty(PropertiesManager.getPropertyPackagesNames())) {
			parentField.addItem(null);
			for (String name : PropertiesManager.getPropertyPackagesNames()) {
				parentField.addItem(name);
			}
			PropertyPackage currePackage = AproposSession.getCurrentPropertyPackage();
			parentField.setSelectedItem(currePackage == null ? null : currePackage.getName());
		}
	}


	/**
	 * Constructs new dialog
	 */
	public AddPropertyPackageDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Create new properties package");
		setLayout(new SBorderLayout(10, 10));

		AproposGridLaoyut centerPanelLaoyout = new AproposGridLaoyut(2);
		centerPanelLaoyout.setHgap(2);
		centerPanelLaoyout.setVgap(2);
		SPanel panel = new SPanel(centerPanelLaoyout);
		add(panel, SBorderLayout.CENTER);

		prepareRow(panel, "name", nameField);
		prepareRow(panel, "description", descriptionField);
		prepareRow(panel, "parent", parentField);

		getSession().getRootFrame().setFocus(nameField);

		SPanel bottom = new SPanel();
		SGridLayout bottomPanelLayout = new SGridLayout(1, 2, 10, 10);
		bottomPanelLayout.setColumns(2);
		bottom.setLayout(bottomPanelLayout);
		bottom.setPreferredSize(new SDimension("200", "*"));
		bottom.setHorizontalAlignment(SConstants.RIGHT);
		add(bottom, SBorderLayout.SOUTH);

		SButton cancelButton = new SButton("Cancel");
		cancelButton.setPreferredSize(SDimension.FULLWIDTH);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			};
		});
		bottom.add(cancelButton);

		SButton okButton = new SButton("OK");
		okButton.setPreferredSize(SDimension.FULLWIDTH);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (StringUtils.isBlank(nameField.getText())) {
					showError("Please provide a name for this property set");
					return;
				}

				PropertyPackage parent = null;
				if (parentField.getSelectedItem() != null) {
					String parentName = (String) parentField.getSelectedItem();
					parent = PropertiesManager.getPropertyPackage(parentName);
					if (parent == null) {
						showError("Can not find parent " + parentName);
						return;
					}
				}

				PropertyPackage propertyPackage = new PropertyPackage(nameField.getText(), descriptionField.getText(), parent);
				try {
					PropertiesManager.addPropertyPackage(propertyPackage);
				} catch (PropertiesException e1) {
					showError(e1.getMessage());
					return;
				}

				AproposSession.setCurrentPropertyPackage(propertyPackage);

				EventManager.getInstance().sendEvent(Event.PACKAGE_ADDED);
				hide();
			};
		});

		bottom.add(okButton);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show(SComponent c) {
		initialize();
		super.show(c);
	}


	/**
	 * Helper method to display error message
	 * 
	 * @param message the actual message
	 */
	private void showError(String message) {
		SOptionPane.showMessageDialog(this, message, "Error", SOptionPane.ERROR_MESSAGE);
	}

}
