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
package com.commsen.apropos.wings;

import org.wings.SConstants;
import org.wings.SGridLayout;
import org.wings.plaf.css.GridLayoutCG;

/**
 * @author Milen Dyankov
 * 
 */
public class AproposGridLaoyut extends SGridLayout {

	public AproposGridLaoyut() {
		super();
		changeVerticalAligment();
	}


	public AproposGridLaoyut(int rows, int cols, int hgap, int vgap) {
		super(rows, cols, hgap, vgap);
		changeVerticalAligment();
	}


	public AproposGridLaoyut(int rows, int cols) {
		super(rows, cols);
		changeVerticalAligment();
	}


	public AproposGridLaoyut(int cols) {
		super(cols);
		changeVerticalAligment();
	}


	/**
	 * 
	 */
	private void changeVerticalAligment() {
		setCG(new GridLayoutCG() {
			@Override
			public int getDefaultLayoutCellVAlignment() {
				return SConstants.TOP;
			}
		});
	}

}
