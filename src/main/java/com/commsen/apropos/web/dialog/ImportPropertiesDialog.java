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
import java.io.FileInputStream;
import java.util.Properties;

import org.wings.SBorderLayout;
import org.wings.SBoxLayout;
import org.wings.SButton;
import org.wings.SCheckBox;
import org.wings.SComboBox;
import org.wings.SDialog;
import org.wings.SFileChooser;
import org.wings.SForm;
import org.wings.SLabel;
import org.wings.SOptionPane;
import org.wings.SPanel;

import com.commsen.apropos.web.AproposSession;

/**
 * @author Milen Dyankov
 * 
 */
public class ImportPropertiesDialog extends SDialog {

	private SFileChooser fileChooser = new SFileChooser();

	private SForm panel = new SForm();


	/**
	 * 
	 */
	public ImportPropertiesDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Import properties");
		setLayout(new SBorderLayout());
		add(panel);
		prepareComponents();
	}


	/**
	 * 
	 */
	public void prepareComponents() {
		add(fileChooser, SBorderLayout.NORTH);

		SPanel optionsPanel = new SPanel(new SBoxLayout(SBorderLayout.VERTICAL));
		add(optionsPanel);

		final SCheckBox overwriteCheckbox = new SCheckBox("Overwrite existing properties");
		optionsPanel.add(overwriteCheckbox);

		SPanel p = new SPanel(new SBoxLayout(SBoxLayout.HORIZONTAL));
		p.add(new SLabel("File type: "));

		final SComboBox fileType = new SComboBox();
		fileType.addItem("properties");
		fileType.addItem("XML");
		p.add(fileType);

		optionsPanel.add(p);

		p = new SPanel(new SBoxLayout(SBoxLayout.HORIZONTAL));

		SButton submitButton = new SButton("Upload");
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (fileChooser.getSelectedFile() != null) {
						Properties properties = new Properties();
						if (fileType.getSelectedItem().equals("properties")) {
							properties.load(new FileInputStream(fileChooser.getSelectedFile()));
						} else if (fileType.getSelectedItem().equals("XML")) {
							properties.loadFromXML(new FileInputStream(fileChooser.getSelectedFile()));
						}
						AproposSession.importProperties(properties, overwriteCheckbox.isSelected());
						hide();
					} else {
						SOptionPane.showMessageDialog(fileChooser.getParent(), "No file chosen", "Error", SOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					SOptionPane.showMessageDialog(fileChooser.getParent(), ex.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
				}
			}
		});
		p.add(submitButton);

		SButton closeButton = new SButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		p.add(closeButton);

		add(p, SBorderLayout.SOUTH);

	}
}
