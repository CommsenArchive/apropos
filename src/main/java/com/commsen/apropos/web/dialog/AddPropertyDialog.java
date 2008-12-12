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
import org.wings.SButton;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SGridLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextArea;
import org.wings.STextField;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.Property;
import com.commsen.apropos.web.AproposSession;
import com.commsen.apropos.wings.AproposGridLaoyut;

/**
 * @author Milen Dyankov
 * 
 */
public class AddPropertyDialog extends AproposBaseDialog {

	private STextField groupField = new STextField();
	private STextField propertyField = new STextField();
	private STextField valueField = new STextField();
	private STextArea descriptionField = new STextArea();

	public enum PropertyDialogField {
		GROUP, NAME, VALUE, DESCRIPTION
	}

	private boolean autoClose = false;


	public AddPropertyDialog() {
		this("Add new property");
	}


	/**
	 * 
	 */
	public AddPropertyDialog(String title) {
		setModal(true);
		setDraggable(true);
		setTitle(title);
		init();
	}


	/**
	 * 
	 */
	public void init() {
		setLayout(new SBorderLayout());

		AproposGridLaoyut centerPanelLaoyout = new AproposGridLaoyut(2);
		centerPanelLaoyout.setHgap(2);
		centerPanelLaoyout.setVgap(2);
		SPanel center = new SPanel(centerPanelLaoyout);
		add(center, SBorderLayout.CENTER);

		prepareRow(center, "group", groupField);
		prepareRow(center, "property", propertyField);
		prepareRow(center, "value", valueField);
		prepareRow(center, "description", descriptionField);
		getSession().getRootFrame().setFocus(groupField);

		SPanel bottom = new SPanel();
		SGridLayout bottomPanelLayout = new SGridLayout(1, 2, 10, 10);
		bottomPanelLayout.setColumns(2);
		bottom.setLayout(bottomPanelLayout);
		bottom.setPreferredSize(new SDimension("200", "*"));
		bottom.setHorizontalAlignment(SConstants.RIGHT);
		add(bottom, SBorderLayout.SOUTH);

		// prepare and add the "Add" button
		SButton addButton = new SButton("Add");
		addButton.setPreferredSize(SDimension.FULLAREA);
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
		bottom.add(addButton);

		// prepare and add the "Close" button
		SButton closeButton = new SButton("Close");
		closeButton.setPreferredSize(SDimension.FULLAREA);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				hide();
			}
		});
		bottom.add(closeButton);
	}


	public void setField(PropertyDialogField field, String value) {
		setField(field, value, true);
	}


	/**
	 * @return the groupField
	 */
	public void setField(PropertyDialogField field, String value, boolean enabled) {
		switch (field) {
			case GROUP:
				groupField.setText(value);
				groupField.setEnabled(enabled);
				groupField.setEditable(enabled);
				break;
			case NAME:
				propertyField.setText(value);
				propertyField.setEnabled(enabled);
				propertyField.setEditable(enabled);
				break;
			case VALUE:
				valueField.setText(value);
				valueField.setEnabled(enabled);
				valueField.setEditable(enabled);
				break;
			case DESCRIPTION:
				descriptionField.setText(value);
				descriptionField.setEnabled(enabled);
				descriptionField.setEditable(enabled);
				break;
		}
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
