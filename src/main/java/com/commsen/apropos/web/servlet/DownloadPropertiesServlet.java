/**
 * 
 */
package com.commsen.apropos.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.commsen.apropos.core.PropertiesManager;
import com.commsen.apropos.core.PropertyPackage;

/**
 * @author Milen Dyankov
 * 
 */
public class DownloadPropertiesServlet extends HttpServlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		if (StringUtils.isBlank(name)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing package name!");
			return;
		}

		PropertyPackage propertyPackage = PropertiesManager.getPropertyPackage(name.trim());
		if (propertyPackage == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Property package called " + name + " was not found!");
			return;
		}

		String type = req.getParameter("type");
		if (StringUtils.isBlank(type)) {
			type = "properties";
		}

		if (type.toLowerCase().equals("properties")) {
			resp.setContentType("text/plain; charset=ASCI");
			propertyPackage.asProperties().store(resp.getOutputStream(), "Property file obtained from " + req.getLocalName());
		} else if (type.toLowerCase().equals("xml")) {
			resp.setContentType("text/xml; charset=UTF-8");
			propertyPackage.asProperties().storeToXML(resp.getOutputStream(), "Property file obtained from " + req.getRequestURI());
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported type: " + type + " !");
			return;
		}

	}
}
