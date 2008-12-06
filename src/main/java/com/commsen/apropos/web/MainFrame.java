/**
 * 
 */
package com.commsen.apropos.web;

import java.awt.Color;
import java.awt.Insets;

import org.wings.SBorderLayout;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SFlowDownLayout;
import org.wings.SFlowLayout;
import org.wings.SFont;
import org.wings.SFrame;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.border.SEmptyBorder;
import org.wings.header.StyleSheetHeader;
import org.wings.plaf.css.Utils;
import org.wings.style.CSSProperty;

/**
 * @author Milen Dyankov
 * 
 */
public class MainFrame extends SFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PropertyPackagesNavigator propertyPackagesNavigator = new PropertyPackagesNavigator();
	private PropertyPackagePanel propertyPackagePanel = new PropertyPackagePanel();


	/**
	 * 
	 */
	public MainFrame() {
		super();

		SPanel panel = new SPanel(new SBorderLayout());
		panel.setPreferredSize(SDimension.FULLAREA);

		panel.add(makeHeadPanel(), SBorderLayout.NORTH);
		panel.add(propertyPackagesNavigator, SBorderLayout.WEST);
		panel.add(propertyPackagePanel, SBorderLayout.CENTER);

		getContentPane().add(panel, SBorderLayout.CENTER);
		getContentPane().setPreferredSize(SDimension.FULLAREA);
		if (!Utils.isMSIE(this)) {
			getContentPane().setAttribute(CSSProperty.POSITION, "absolute");
			getContentPane().setAttribute(CSSProperty.HEIGHT, "100%");
			getContentPane().setAttribute(CSSProperty.WIDTH, "100%");
		}
		setAttribute(CSSProperty.POSITION, "absolute");
		setAttribute(CSSProperty.HEIGHT, "100%");
		setAttribute(CSSProperty.WIDTH, "100%");

		addHeader(new StyleSheetHeader("../css/wingset2.css"));
	}


	private SPanel makeHeadPanel() {
		SPanel result = new SPanel();
		result.setLayout(new SFlowDownLayout(SFlowLayout.LEFT));
		result.setBackground(Color.GRAY);
		result.setPreferredSize(new SDimension("100%", "50px"));
		result.setBorder(new SEmptyBorder(new Insets(5, 5, 5, 5)));
		result.setVerticalAlignment(SConstants.CENTER);

		SLabel title = new SLabel("aPropOS");
		title.setFont(new SFont("Verdana", SFont.BOLD, 16));
		title.setForeground(Color.WHITE);
		title.setVerticalAlignment(SConstants.CENTER);
		result.add(title);

		SLabel name = new SLabel("Application's properties organizing system");
		name.setFont(new SFont("Verdana", SFont.BOLD, 10));
		name.setForeground(Color.WHITE);
		name.setVerticalAlignment(SConstants.CENTER);
		result.add(name);

		return result;
	}

}
