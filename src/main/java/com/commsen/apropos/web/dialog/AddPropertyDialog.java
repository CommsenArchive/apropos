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

import org.wings.SBorderLayout;
import org.wings.SBoxLayout;
import org.wings.SButton;
import org.wings.SDialog;
import org.wings.SForm;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextField;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.Property;
import com.commsen.apropos.web.AproposSession;

/**
 * @author Milen Dyankov
 * 
 */
public class AddPropertyDialog extends SDialog {

	private STextField groupField = new STextField();
	private STextField propertyField = new STextField();
	private STextField valueField = new STextField();
	private STextField descriptionField = new STextField();

	private SForm panel = new SForm();

	private boolean autoClose = false;


	/**
	 * 
	 */
	public AddPropertyDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Add property");

		add(panel);
		prepareComponents();
	}


	/**
	 * 
	 */
	public void prepareComponents() {
		SPanel center = new SPanel(new SGridLayout(2));
		add(center, SBorderLayout.CENTER);

		SPanel bottom = new SPanel(new SBoxLayout(SBorderLayout.HORIZONTAL));
		add(bottom, SBorderLayout.SOUTH);

		propareRow(center, "group", groupField);
		propareRow(center, "property", propertyField);
		propareRow(center, "value", valueField);
		propareRow(center, "description", descriptionField);
		getSession().getRootFrame().setFocus(groupField);

		SButton addButton = new SButton("add");
		bottom.add(addButton);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String group = groupField.getText();
				String name = propertyField.getText();
				String value = valueField.getText();
				String description = descriptionField.getText();
				try {
					AproposSession.addProperty(new Property(name, value, description, group));
					// groupField.setText(null);
					propertyField.setText(null);
					valueField.setText(null);
					descriptionField.setText(null);
				} catch (PropertiesException e) {
					SOptionPane.showMessageDialog(null, e.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
				}
				getSession().getRootFrame().setFocus(propertyField);
				if (autoClose) hide();
			}
		});

		SButton closeButton = new SButton("close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				hide();
			}
		});
		bottom.add(closeButton);
	}


	/**
	 * @param parent
	 */
	private void propareRow(SPanel parent, String label, STextField field) {
		SLabel l = new SLabel(label);
		parent.add(l);
		parent.add(field);

	}


	/**
	 * @return the groupField
	 */
	public STextField getGroupField() {
		return this.groupField;
	}


	/**
	 * @return the propertyField
	 */
	public STextField getPropertyField() {
		return this.propertyField;
	}


	/**
	 * @return the valueField
	 */
	public STextField getValueField() {
		return this.valueField;
	}


	/**
	 * @return the descriptionField
	 */
	public STextField getDescriptionField() {
		return this.descriptionField;
	}


	/**
	 * @return the autoClose
	 */
	public boolean isAutoClose() {
		return this.autoClose;
	}


	/**
	 * @param autoClose the autoClose to set
	 */
	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

}
