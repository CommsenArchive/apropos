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
package com.commsen.apropos.web.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.wings.session.SessionManager;

/**
 * @author Milen Dyankov
 * 
 */
public class EventManager {

	Map<Event, Set<EventListener>> allListeners = new HashMap<Event, Set<EventListener>>();
	Set<EventListener> globalListeners = new HashSet<EventListener>();


	public static final EventManager getInstance() {
		EventManager eventManager;
		if (SessionManager.getSession().containsProperty("com.commsen.apropos.web.event.EventManager")) {
			eventManager = (EventManager) SessionManager.getSession().getProperty("com.commsen.apropos.web.event.EventManager");
		} else {
			eventManager = new EventManager();
			SessionManager.getSession().setProperty("com.commsen.apropos.web.event.EventManager", eventManager);
		}
		return eventManager;
	}


	/**
	 * 
	 */
	private EventManager() {
		// private constructor
	}


	public void addListener(EventListener listener) {
		globalListeners.add(listener);
	}


	public void addListener(Event event, EventListener listener) {
		Set<EventListener> listeners = allListeners.get(event);
		if (listeners == null) {
			listeners = new HashSet<EventListener>();
			allListeners.put(event, listeners);
		}
		listeners.add(listener);
	}


	public void sendEvent(Event event) {
		Set<EventListener> listeners = allListeners.get(event);
		if (listeners != null) {
			for (EventListener eventListener : listeners) {
				eventListener.handleEvent(event);
			}
		}
		for (EventListener eventListener : globalListeners) {
			eventListener.handleEvent(event);
		}

	}
}
