/**
 * 
 */
package com.commsen.apropos.web.renderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SOptionPane;
import org.wings.STable;
import org.wings.table.STableCellRenderer;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.Property;
import com.commsen.apropos.web.AproposSession;

/**
 * @author Milen Dyankov
 * 
 */
public class PropertiesTableButtonsRenderer extends SButton implements STableCellRenderer {

	/**
	 * @see org.wings.table.STableCellRenderer#getTableCellRendererComponent(org.wings.STable,
	 *      java.lang.Object, boolean, int, int)
	 */
	public SComponent getTableCellRendererComponent(final STable table, final Object value, final boolean isSelected, final int row, final int column) {
		if (row == 0) {
			setText("add");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					String group = (String) table.getValueAt(0, 0);
					String name = (String) table.getValueAt(0, 1);
					String value = (String) table.getValueAt(0, 2);
					String description = (String) table.getValueAt(0, 3);
					String currentPackageName = AproposSession.getCurrentPropertyPackage().getName();
					try {
						PropertiesManager.addProperty(currentPackageName, new Property(name, value, description, group));
						AproposSession.setCurrentPropertyPackage(PropertiesManager.getPropertyPackage(currentPackageName));
						table.reload();
					} catch (PropertiesException e) {
						SOptionPane.showMessageDialog(table, e.toString(), "Error", SOptionPane.ERROR_MESSAGE);
					}
				}
			});
		} else {
			setText("delete");
			final ActionListener confirmationListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (event.getActionCommand().equals(SOptionPane.OK_ACTION)) {
						String currentPackageName = AproposSession.getCurrentPropertyPackage().getName();
						try {
							PropertiesManager.deleteProperty(currentPackageName, (String) table.getValueAt(row, 1));
							AproposSession.setCurrentPropertyPackage(PropertiesManager.getPropertyPackage(currentPackageName));
							table.reload();
						} catch (PropertiesException e) {
							SOptionPane.showMessageDialog(table, e.toString(), "Error", SOptionPane.ERROR_MESSAGE);
						}
					}
				}
			};
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					SOptionPane.showQuestionDialog(table, "Deleting property! Are you sure?", "Please congirm", confirmationListener);
				}
			});

		}

		return this;
	}

}
