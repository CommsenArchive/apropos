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

import org.wings.SComponent;
import org.wings.SDialog;
import org.wings.SFrame;
import org.wings.SLabel;
import org.wings.SPanel;

/**
 * @author Milen Dyankov
 * 
 */
public abstract class AproposBaseDialog extends SDialog {

	/**
	 * 
	 */
	public AproposBaseDialog() {
		super();
	}


	/**
	 * @param owner
	 */
	public AproposBaseDialog(SFrame owner) {
		super(owner);
	}


	/**
	 * @param owner
	 * @param modal
	 */
	public AproposBaseDialog(SFrame owner, boolean modal) {
		super(owner, modal);
	}


	/**
	 * @param owner
	 * @param title
	 */
	public AproposBaseDialog(SFrame owner, String title) {
		super(owner, title);
	}


	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public AproposBaseDialog(SFrame owner, String title, boolean modal) {
		super(owner, title, modal);
	}


	protected void prepareRow(SPanel parent, String label, SComponent field) {
		SLabel l = new SLabel(label);
		l.setStyle("dialogLabel");
		parent.add(l);

		field.setStyle("dialogField");
		parent.add(field);
	}

}