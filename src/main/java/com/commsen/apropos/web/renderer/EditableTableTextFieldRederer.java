/**
 * 
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
