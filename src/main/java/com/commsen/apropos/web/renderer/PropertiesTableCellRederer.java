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

import org.wings.SComponent;
import org.wings.STable;
import org.wings.table.SDefaultTableCellRenderer;

import com.commsen.apropos.web.PropertyTableModel;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertiesTableCellRederer extends SDefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.table.SDefaultTableCellRenderer#getTableCellRendererComponent(org.wings.STable,
	 *      java.lang.Object, boolean, int, int)
	 */
	@Override
	public SComponent getTableCellRendererComponent(STable table, Object value, boolean selected, int row, int col) {

		switch (col) {
			case 0:
				setStyle("propertyGroup");
				break;
			case 1:
				setStyle("propertyName");
				break;
			case 2:
				setStyle("propertyValue");
				decorate(table, row);
				break;
			case 3:
				setStyle("propertyDescription");
				break;

			default:
		}

		return super.getTableCellRendererComponent(table, value, selected, row, col);
	}


	/**
	 * @param row
	 * @param p
	 */
	private void decorate(STable table, int row) {
		PropertyTableModel p = (PropertyTableModel) table.getModel();
		if (p.isParentProperty(row)) {
			addStyle("parentProperty");
		} else if (p.isSameAsParent(row)) {
			addStyle("sameAsParentProperty");
		}
	}

}
