/**
 * 
 */
package com.commsen.apropos.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wings.SOptionPane;
import org.wingx.table.XTableModel;

import com.commsen.apropos.core.PropertiesException;
import com.commsen.apropos.core.Property;
import com.commsen.apropos.core.PropertyPackage;
import com.commsen.apropos.web.event.Event;
import com.commsen.apropos.web.event.EventListener;
import com.commsen.apropos.web.event.EventManager;

public class PropertyTableModel extends XTableModel implements EventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Property> properties = new LinkedList<Property>();

	private List<Property> allProperties = new LinkedList<Property>();

	private Map<String, Property> overwritenProperties = new HashMap<String, Property>();

	private static final int COLUMN_GROUP = 0;
	private static final int COLUMN_NAME = 1;
	private static final int COLUMN_VALUE = 2;
	private static final int COLUMN_DESC = 3;

	private static final int COLUMN_COUNT = 4;


	/**
	 * @param propertyPackagePanel
	 */
	public PropertyTableModel() {
		PropertyPackage propertyPackage = AproposSession.getCurrentPropertyPackage();
		EventManager.getInstance().addListener(Event.PROPERTY_ADDED, this);
		EventManager.getInstance().addListener(Event.PROPERTY_DELETED, this);
		EventManager.getInstance().addListener(Event.PROPERTY_UPDATED, this);
		// if (propertyPackage != null) {
		// properties.addAll(propertyPackage.getProperties().values());
		// allProperties.addAll(propertyPackage.getAllProperties().values());
		// overwritenProperties.putAll(propertyPackage.getOverwritenProperties());
		// }
		refresh();
	}


	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}


	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return AproposSession.showParentProperties() ? allProperties.size() : properties.size();
	}


	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<Property> tmp = AproposSession.showParentProperties() ? allProperties : properties;
		switch (columnIndex) {
			case COLUMN_GROUP:
				return tmp.get(rowIndex).getGroup();
			case COLUMN_NAME:
				return tmp.get(rowIndex).getName();
			case COLUMN_VALUE:
				return tmp.get(rowIndex).getValue();
			case COLUMN_DESC:
				return tmp.get(rowIndex).getDescription();
		}
		return null;
	}


	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case COLUMN_GROUP:
				return "group";
			case COLUMN_NAME:
				return "property";
			case COLUMN_VALUE:
				return "value";
			case COLUMN_DESC:
				return "description";
		}
		return super.getColumnName(column);
	}


	/**
	 * @see org.wingx.table.RefreshableModel#refresh()
	 */
	public void refresh() {
		PropertyPackage propertyPackage = AproposSession.getCurrentPropertyPackage();
		if (propertyPackage != null) {
			properties = new LinkedList<Property>(propertyPackage.getProperties().values());
			allProperties = new LinkedList<Property>(propertyPackage.getAllProperties().values());
			overwritenProperties = new HashMap<String, Property>(propertyPackage.getOverwritenProperties());
		}
		List<Property> tmp = AproposSession.showParentProperties() ? allProperties : properties;

		PROPERTIES_LOOP: for (Iterator<Property> i = tmp.iterator(); i.hasNext();) {
			Property currentProperty = i.next();
			for (int currentCloumn = 0; currentCloumn < getColumnCount(); currentCloumn++) {
				String filter = (String) getFilter(currentCloumn);
				if (StringUtils.isBlank(filter)) continue;
				switch (currentCloumn) {
					case COLUMN_GROUP:
						if (currentProperty.getGroup() != null && !currentProperty.getGroup().contains(filter)) {
							i.remove();
							continue PROPERTIES_LOOP;
						}
						break;
					case COLUMN_NAME:
						if (currentProperty.getName() != null && !currentProperty.getName().contains(filter)) {
							i.remove();
							continue PROPERTIES_LOOP;
						}
						break;
					case COLUMN_VALUE:
						if (currentProperty.getValue() != null && !currentProperty.getValue().contains(filter)) {
							i.remove();
							continue PROPERTIES_LOOP;
						}
						break;
					case COLUMN_DESC:
						if (currentProperty.getDescription() != null && !currentProperty.getDescription().contains(filter)) {
							i.remove();
							continue PROPERTIES_LOOP;
						}
						break;
				}
			}
		}

		Collections.sort(tmp, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				for (int currentCloumn = 0; currentCloumn < getColumnCount(); currentCloumn++) {
					int sort = getSort(currentCloumn);
					if (sort != SORT_NONE) {
						switch (currentCloumn) {
							case COLUMN_GROUP:
								return compareFields(sort, o1.getGroup(), o2.getGroup());
							case COLUMN_NAME:
								return compareFields(sort, o1.getName(), o2.getName());
							case COLUMN_VALUE:
								return compareFields(sort, o1.getValue(), o2.getValue());
							case COLUMN_DESC:
								return compareFields(sort, o1.getDescription(), o2.getDescription());
						}
					}
				}
				return 0;
			}
		});

		fireTableDataChanged();

	}


	/**
	 * @param o1
	 * @param o2
	 * @param currentCloumn
	 */
	private int compareFields(int sort, String f1, String f2) {
		if (f1 == null) f1 = " ";
		if (f2 == null) f2 = " ";
		switch (sort) {
			case SORT_ASCENDING:
				return f1.compareTo(f2);
			case SORT_DESCENDING:
				return f2.compareTo(f1);
		}
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}


	/**
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}


	/**
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		List<Property> tmp = AproposSession.showParentProperties() ? allProperties : properties;
		Property p = tmp.get(rowIndex);
		String oldName = p.getName();
		switch (columnIndex) {
			case COLUMN_GROUP:
				p.setGroup(value.toString());
				break;
			case COLUMN_NAME:
				try {
					p.setName(value.toString());
				} catch (PropertiesException e) {
					SOptionPane.showMessageDialog(null, e.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
				}
				break;
			case COLUMN_VALUE:
				p.setValue(value.toString());
				break;
			case COLUMN_DESC:
				p.setDescription(value.toString());
				break;
		}

		try {
			AproposSession.updateProperty(oldName, p);
		} catch (PropertiesException e) {
			SOptionPane.showMessageDialog(null, e.getMessage(), "Error", SOptionPane.ERROR_MESSAGE);
		}
	}


	public boolean isParentProperty(int row) {
		List<Property> tmp = AproposSession.showParentProperties() ? allProperties : properties;
		return !properties.contains(tmp.get(row));
	}


	public boolean isSameAsParent(int row) {
		List<Property> tmp = AproposSession.showParentProperties() ? allProperties : properties;
		Property thisProperty = tmp.get(row);
		if (overwritenProperties.containsKey(thisProperty.getName())) {
			Property parentProperty = overwritenProperties.get(thisProperty.getName());
			return thisProperty.sameAs(parentProperty);
		} else {
			return false;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.commsen.apropos.web.event.EventListener#handleEvent(com.commsen.apropos.web.event.Event)
	 */
	public void handleEvent(Event event) {
		refresh();
	}

}