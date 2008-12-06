/**
 * 
 */
package com.commsen.apropos.web.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SDialog;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextArea;
import org.wings.STextField;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventManager;

/**
 * @author Milen Dyankov
 * 
 */
public class AddPropertyPackageDialog extends SDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public AddPropertyPackageDialog() {
		setModal(true);
		setDraggable(true);
		setTitle("Create properties set");
		SPanel panel = new SPanel(new SGridLayout(2));

		final STextField nameField = new STextField();
		panel.add(new SLabel("name"));
		panel.add(nameField);

		final STextArea descriptionField = new STextArea();
		panel.add(new SLabel("description"));
		panel.add(descriptionField);

		final SComboBox parentField = new SComboBox();
		if (!CollectionUtils.isEmpty(PropertiesManager.getPropertyPackagesNames())) {
			parentField.addItem(null);
			for (String name : PropertiesManager.getPropertyPackagesNames()) {
				parentField.addItem(name);
			}
		}
		panel.add(new SLabel("parent"));
		panel.add(parentField);

		SButton cancelButton = new SButton("cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			};
		});

		SButton okButton = new SButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (StringUtils.isBlank(nameField.getText())) {
					showError("Please provide a name for this property set");
					return;
				}

				PropertyPackage parent = null;
				if (parentField.getSelectedItem() != null) {
					parent = PropertiesManager.getPropertyPackage((String) parentField.getSelectedItem());
					if (parent == null) {
						showError("Can not find parent " + parent);
						return;
					}
				}

				PropertyPackage propertyPackage = new PropertyPackage(nameField.getText(), descriptionField.getText(), parent);
				try {
					PropertiesManager.addPropertyPackage(propertyPackage);
				} catch (PropertiesException e1) {
					showError(e1.getMessage());
					return;
				}

				EventManager.getInstance().sendEvent(Event.SET_ADDED);
				hide();
			};
		});

		panel.add(cancelButton);
		panel.add(okButton);

		add(panel);

		getSession().getRootFrame().setFocus(nameField);

	}


	private void showError(String message) {
		SOptionPane.showMessageDialog(this, message, "Error", SOptionPane.ERROR_MESSAGE);
	}

}
