package com.simplelib.events;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.simplelib.builder.PathBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EventHandler implements Iterable<IEvent>, IEvent {
    public static final String DEFAULT_HANDLER_ID = "default_handler";

    private static HashMap<String, EventHandler> handlerMap;

    public static final synchronized HashMap<String, EventHandler> getHandlerMap() {
        if (handlerMap == null)
            handlerMap = new HashMap<>();
        return handlerMap;
    }

    public static final synchronized EventHandler get(String handlerId) {
        if (handlerId == null) return null;

        HashMap<String, EventHandler> handlerMap = getHandlerMap();
        if (handlerMap == null) return null;

        EventHandler handler = null;
        if (handlerMap.containsKey(handlerId)) {
            handler = handlerMap.get(handlerId);
        }
        if (handler == null) {
            handler = new EventHandler();
            handlerMap.put(handlerId, handler);
        }
        return handler;
    }

    public static final synchronized EventHandler get() {
        return get(DEFAULT_HANDLER_ID);
    }

    public static final synchronized boolean invoke(String handlerId, String key, Bundle args) {
        if (handlerId == null) return false;

        EventHandler handler = get(handlerId);
        if (handler == null) return false;

        return handler.invoke(key, args);
    }

    // EventHandler
    private List<IEvent> events;

    public EventHandler() {
        this.events = new ArrayList<>();
    }

    @NonNull
    @Override
    public Iterator<IEvent> iterator() {
        synchronized (this) {
            return events.iterator();
        }
    }

    public final synchronized List<IEvent> getEvents() {
        return events;
    }

    public final void clearEvents() {
        synchronized (this) {
            events.clear();
        }
    }

    public final void addEvent(IEvent event) {
        if (event == null) return;
        synchronized (this) {
            if (!events.contains(event))
                events.add(event);
        }
    }

    public final void removeEvent(IEvent event) {
        if (event == null) return;
        synchronized (this) {
            if (events.contains(event))
                events.remove(event);
        }
    }

    @Override
    public boolean invoke(String key, Bundle args) {
        key = PathBuilder.format(key);
        if (args == null)
            args = new Bundle();

        boolean handled = false;
        synchronized (this) {
            for (IEvent event : events) {
                if (event == null)
                    continue;

                try {
                    handled |= event.invoke(key, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return handled;
    }
}