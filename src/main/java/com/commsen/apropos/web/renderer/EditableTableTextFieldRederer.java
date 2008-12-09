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

import org.wings.LowLevelEventListener;
import org.wings.SComponent;
import org.wings.STable;
import org.wings.STextField;
import org.wingx.table.EditableTableCellRenderer;

/**
 * @author Milen Dyankov
 * 
 */
public class EditableTableTextFieldRederer extends STextField implements EditableTableCellRenderer {

	/**
	 * @see org.wingx.table.EditableTableCellRenderer#getLowLevelEventListener(org.wings.STable,
	 *      int, int)
	 */
	public LowLevelEventListener getLowLevelEventListener(STable table, int row, int column) {
		return this;
	}


	/**
	 * @see org.wingx.table.EditableTableCellRenderer#getValue()
	 */
	public Object getValue() {
		return getText();
	}


	/**
	 * @see org.wings.table.STableCellRenderer#getTableCellRendererComponent(org.wings.STable,
	 *      java.lang.Object, boolean, int, int)
	 */
	public SComponent getTableCellRendererComponent(STable table, Object value, boolean isSelected, int row, int column) {
		setText(value != null ? value.toString() : null);
		return this;
	}

}
