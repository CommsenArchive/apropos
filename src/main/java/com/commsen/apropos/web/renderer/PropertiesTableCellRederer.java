/**
 * 
 */
package com.commsen.apropos.web.renderer;

import java.awt.Color;

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
		PropertyTableModel p = (PropertyTableModel) table.getModel();
		if (p.isParentProperty(row)) {
			setForeground(Color.LIGHT_GRAY);
		} else {
			if (p.isSameAsParent(row)) {
				setForeground(Color.RED);
			} else {
				setForeground(Color.BLACK);
			}
		}
		return super.getTableCellRendererComponent(table, value, selected, row, col);
	}

}
