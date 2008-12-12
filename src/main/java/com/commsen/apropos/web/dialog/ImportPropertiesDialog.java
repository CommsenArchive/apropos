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
import org.wings.SButton;
import org.wings.SCheckBox;
import org.wings.SComboBox;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SFileChooser;
import org.wings.SGridLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;

import com.commsen.apropos.web.AproposSession;
import com.commsen.apropos.wings.AproposGridLaoyut;

/**
 * @author Milen Dyankov
 * 
 */
public class ImportPropertiesDialog extends AproposBaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final SFileChooser fileChooser = new SFileChooser();
	private final SComboBox fileTypeSelector = new SComboBox();
	private final SCheckBox overwriteCheckbox = new SCheckBox("Overwrite existing properties");


	/**
	 * 
	 */
	public ImportPropertiesDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Import properties into current package");
		setLayout(new SBorderLayout());
		prepareComponents();
	}


	/**
	 * 
	 */
	public void prepareComponents() {

		AproposGridLaoyut centerPanelLaoyout = new AproposGridLaoyut(2);
		centerPanelLaoyout.setHgap(2);
		centerPanelLaoyout.setVgap(2);
		SPanel center = new SPanel(centerPanelLaoyout);
		add(center, SBorderLayout.CENTER);

		prepareRow(center, "file", fileChooser);
		prepareRow(center, "type", fileTypeSelector);
		prepareRow(center, null, overwriteCheckbox);

		fileChooser.setPreferredSize(SDimension.FULLWIDTH);

		fileTypeSelector.addItem("properties");
		fileTypeSelector.addItem("XML");
		getSession().getRootFrame().setFocus(fileChooser);

		SPanel bottom = new SPanel();
		SGridLayout bottomPanelLayout = new SGridLayout(1, 2, 10, 10);
		bottomPanelLayout.setColumns(2);
		bottom.setLayout(bottomPanelLayout);
		bottom.setPreferredSize(new SDimension("200", "*"));
		bottom.setHorizontalAlignment(SConstants.RIGHT);
		add(bottom, SBorderLayout.SOUTH);

		SButton submitButton = new SButton("Upload");
		submitButton.setPreferredSize(SDimension.FULLWIDTH);
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (fileChooser.getSelectedFile() != null) {
						Properties properties = new Properties();
						FileInputStream propertiesFileStream = new FileInputStream(fileChooser.getSelectedFile());
						try {
							if (fileTypeSelector.getSelectedItem().equals("properties")) {
								properties.load(propertiesFileStream);
							} else if (fileTypeSelector.getSelectedItem().equals("XML")) {
								properties.loadFromXML(propertiesFileStream);
							}
						} finally {
							propertiesFileStream.close();
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
		bottom.add(submitButton);

		SButton closeButton = new SButton("Close");
		closeButton.setPreferredSize(SDimension.FULLWIDTH);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		bottom.add(closeButton);

	}
}
