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
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SDialog;
import org.wings.SGridLayout;
import org.wings.SLabel;
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

/**
 * This class represents a dialog window used to add new properties package.
 * 
 * @author Milen Dyankov
 * 
 */
public class AddPropertyPackageDialog extends SDialog {

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
			parentField.setSelectedItem(AproposSession.getCurrentPropertyPackage().getName());
		}
	}


	/**
	 * Constructs new dialog
	 */
	public AddPropertyPackageDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Create properties set");
		SPanel panel = new SPanel(new SGridLayout(2));

		panel.add(new SLabel("name"));
		panel.add(nameField);

		panel.add(new SLabel("description"));
		panel.add(descriptionField);

		panel.add(new SLabel("parent"));
		panel.add(parentField);

		SButton cancelButton = new SButton("cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			};
		});

		SButton okButton = new SButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (StringUtils.isBlank(nameField.getText())) {
					showError("Please provide a name for this property set");
					return;
				}

				PropertyPackage parent = null;
				if (parentField.getSelectedItem() != null) {
					parent = PropertiesManager.getPropertyPackage((String) parentField.getSelectedItem());
					if (parent == null) {
						showError("Can not find parent " + parent);
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

		panel.add(cancelButton);
		panel.add(okButton);

		add(panel);

		getSession().getRootFrame().setFocus(nameField);

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
