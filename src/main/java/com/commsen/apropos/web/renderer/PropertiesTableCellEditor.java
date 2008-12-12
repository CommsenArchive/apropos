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
package com.commsen.apropos.web.renderer;

import org.wings.SDefaultCellEditor;
import org.wings.SDimension;
import org.wings.STextField;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertiesTableCellEditor extends SDefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public PropertiesTableCellEditor() {
		super(new STextField());
		editorComponent.setPreferredSize(new SDimension("300px", "*"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.SDefaultCellEditor#getTableCellEditorComponent(org.wings.STable,
	 *      java.lang.Object, boolean, int, int)
	 */
	// @Override
	// public SComponent getTableCellEditorComponent(STable table, Object value, boolean isSelected,
	// int row, int column) {
	// SComponent orig = super.getTableCellEditorComponent(table, value, isSelected, row, column);
	// orig.setPreferredSize(SDimension.FULLAREA);
	// return orig;
	// }
}
