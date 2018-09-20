package com.simplelib.events;

import android.os.Bundle;

import java.util.ArrayList;

public class EventHandler {
    //Static
    private static EventHandler handler;

    public static EventHandler get() {
        if (handler == null)
            handler = new EventHandler();
        return handler;
    }

    //EventHandler
    private ArrayList<Event> events;

    public EventHandler() {
        this.events = new ArrayList<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void clear() {
        try {
            events.clear();
        } catch (Exception e) {
        }
    }

    public ArrayList<Event> getById(Object id) {
        ArrayList<Event> list = new ArrayList<>();
        try {
            for (Event event : events) {
                if (event != null && event.hasId(id))
                    list.add(event);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public boolean add(Event event) {
        try {
            if (event != null && !events.contains(event)) {
                event.setEventHandler(this);
                events.add(event);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean remove(Event event) {
        try {
            if (event != null && events.contains(event)) {
                event.setEventHandler(this);
                events.remove(event);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void removeById(Object id) {
        try {
            events.removeAll(getById(id));
        } catch (Exception e) {
        }
    }

    public void run() {
        run(null);
    }

    public void run(Object id) {
        run(id, null);
    }

    public void run(Object id, Bundle extras) {
        try {
            for (Event event : events) {
                if (event != null)
                    event.post(id, extras);
            }
        } catch (Exception e) {
        }
    }
}