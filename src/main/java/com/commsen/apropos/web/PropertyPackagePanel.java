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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.wings.SAnchor;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SCheckBox;
import org.wings.SConstants;
import org.wings.SDefaultCellEditor;
import org.wings.SDimension;
import org.wings.SFlowLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.SScrollPane;
import org.wings.STable;
import org.wings.STextField;
import org.wings.border.SEmptyBorder;
import org.wings.event.SMouseEvent;
import org.wings.event.SMouseListener;
import org.wingx.XTable;
import org.wingx.table.XTableColumn;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.web.dialog.AddPropertyDialog;
import com.commsen.apropos.web.dialog.ImportPropertiesDialog;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventListener;
import com.commsen.apropos.web.event.EventManager;
import com.commsen.apropos.web.renderer.EditableTableTextFieldRederer;
import com.commsen.apropos.web.renderer.PropertiesTableCellRederer;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertyPackagePanel extends SPanel implements EventListener {

	private PropertyTableModel tableModel = new PropertyTableModel();

	private XTable table = new XTable(tableModel);

	private SPanel buttons = new SPanel();

	private final SCheckBox showAll = new SCheckBox("show parent properties", AproposSession.showParentProperties());

	private SAnchor export = new SAnchor();


	public PropertyPackagePanel() {
		super(new SBorderLayout());
		setPreferredSize(SDimension.FULLAREA);
		setBorder(new SEmptyBorder(new Insets(5, 5, 5, 5)));
		if (AproposSession.getCurrentPropertyPackage() == null) {
			setVisible(false);
		}

		SScrollPane scrollPane = new SScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setMode(SScrollPane.MODE_COMPLETE);
		scrollPane.setPreferredSize(SDimension.FULLAREA);
		scrollPane.setVerticalAlignment(SConstants.TOP_ALIGN);

		add(buttons, SBorderLayout.NORTH);
		// add(scrollPane, SBorderLayout.CENTER);
		add(table, SBorderLayout.CENTER);

		prepareButtons();
		prepareTable();

		EventManager.getInstance().addListener(Event.CURRENT_PACKAGE_CHANGED, this);
	}


	/**
	 * @see com.commsen.apropos.web.event.EventListener#handleEvent(com.commsen.apropos.web.event.Event)
	 */
	public void handleEvent(Event event) {
		if (AproposSession.getCurrentPropertyPackage() == null) {
			setVisible(false);
			return;
		}
		setVisible(true);
		showAll.setSelected(AproposSession.showParentProperties());
		HttpServletRequest request = getSession().getServletRequest();
		export.setURL(request.getContextPath() + "/get?name=" + AproposSession.getCurrentPropertyPackage().getName() + "&type=properties");
		export.setTarget("export");
		table.refresh();

	}


	private void prepareButtons() {
		buttons.setLayout(new SFlowLayout(SFlowLayout.LEFT));
		buttons.setPreferredSize(new SDimension("100%", "50px"));

		SButton addPropertyButton = new SButton("add property");
		addPropertyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddPropertyDialog().show(buttons);
			}
		});
		buttons.add(addPropertyButton);

		final SButton deleteSelectedButton = new SButton("delete selected");
		deleteSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				final int[] selectedRows = table.getSelectedRows();
				SOptionPane.showQuestionDialog(buttons, "Going to delete " + selectedRows.length + " entries! Are you sure?", "Deleting selected properties", new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if (event.getActionCommand().equals(SOptionPane.OK_ACTION)) {
							Set<String> toDelete = new HashSet<String>();
							for (int i : selectedRows) {
								toDelete.add((String) table.getValueAt(i, 1));
							}
							if (!toDelete.isEmpty()) {
								try {
									AproposSession.deleteProperties(toDelete);
								} catch (PropertiesException e) {
									SOptionPane.showMessageDialog(deleteSelectedButton, e.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
								}

							}
						}
					}
				});

			}
		});
		buttons.add(deleteSelectedButton);

		SButton importButton = new SButton("import");
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ImportPropertiesDialog().show(buttons);
			}
		});
		buttons.add(importButton);

		export.add(new SButton("export"));
		export.setLayout(new SFlowLayout(SConstants.LEFT, 0, 0));
		buttons.add(export);

		showAll.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (showAll.isSelected()) {
					AproposSession.setShowParentProperties(true);
				} else {
					AproposSession.setShowParentProperties(false);
				}
				EventManager.getInstance().sendEvent(Event.PROPERTY_UPDATED);
			}
		});
		buttons.add(showAll);
	}


	private void prepareTable() {
		table.setName("propertiesTable");
		table.setShowGrid(true);
		table.setSelectionMode(STable.NO_SELECTION);
		table.setEditable(true);
		table.setSelectable(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setPreferredSize(new SDimension("100%", null));
		table.setVerticalAlignment(SConstants.TOP);
		table.setNoDataAvailableLabel("no properties");

		table.addMouseListener(new SMouseListener() {
			public void mouseClicked(SMouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (tableModel.isParentProperty(row)) {
					e.consume();
					AddPropertyDialog addPropertyDialog = new AddPropertyDialog();
					addPropertyDialog.getGroupField().setText((String) tableModel.getValueAt(row, 0));
					addPropertyDialog.getPropertyField().setText((String) tableModel.getValueAt(row, 1));
					addPropertyDialog.getPropertyField().setEditable(false);
					addPropertyDialog.getValueField().setText((String) tableModel.getValueAt(row, 2));
					addPropertyDialog.getDescriptionField().setText((String) tableModel.getValueAt(row, 3));
					addPropertyDialog.setAutoClose(true);
					addPropertyDialog.show(table);
				}
			}
		});

		STextField editorField = new STextField();
		editorField.setPreferredSize(new SDimension("100%", null));

		SDefaultCellEditor defaultCellEditor = new SDefaultCellEditor(editorField);

		XTableColumn columnGroup = getColumn(table, 0);
		columnGroup.setSortable(true);
		columnGroup.setFilterRenderer(new EditableTableTextFieldRederer());
		columnGroup.setFilterable(true);
		columnGroup.setCellEditor(defaultCellEditor);
		columnGroup.setCellRenderer(new PropertiesTableCellRederer());
		columnGroup.setWidth("10%");

		XTableColumn columnName = getColumn(table, 1);
		columnName.setSortable(true);
		columnName.setFilterRenderer(new EditableTableTextFieldRederer());
		columnName.setFilterable(true);
		columnName.setCellEditor(defaultCellEditor);
		columnName.setCellRenderer(new PropertiesTableCellRederer());
		columnName.setWidth("20%");

		XTableColumn columnValue = getColumn(table, 2);
		columnValue.setSortable(true);
		columnValue.setFilterRenderer(new EditableTableTextFieldRederer());
		columnValue.setFilterable(true);
		columnValue.setCellEditor(defaultCellEditor);
		columnValue.setCellRenderer(new PropertiesTableCellRederer());
		columnValue.setWidth("35%");

		XTableColumn columnDescription = getColumn(table, 3);
		columnDescription.setSortable(true);
		columnDescription.setFilterRenderer(new EditableTableTextFieldRederer());
		columnDescription.setFilterable(true);
		columnDescription.setCellEditor(defaultCellEditor);
		columnDescription.setCellRenderer(new PropertiesTableCellRederer());
		columnDescription.setWidth("35%");

	}


	private XTableColumn getColumn(final XTable table, final int column) {
		return ((XTableColumn) table.getColumnModel().getColumn(column));
	}

}