package com.simplelib.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.simplelib.builder.PathBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EventGroup extends Event implements Iterable<IEvent> {
    private List<IEvent> events;

    public EventGroup() {
        super();

        this.events = new ArrayList<>();
    }

    public EventGroup(String key) {
        super(key);

        this.events = new ArrayList<>();
    }

    public EventGroup(List<IEvent> events) {
        super();

        if (events == null)
            events = new ArrayList<>();
        this.events = events;
    }

    public EventGroup(String key, List<IEvent> events) {
        super(key);

        if (events == null)
            events = new ArrayList<>();
        this.events = events;
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

    public final void addEvents(Collection<IEvent> events) {
        if (events == null) return;
        synchronized (this) {
            Iterator<IEvent> eventIterator = events.iterator();
            while (eventIterator.hasNext()) {
                IEvent event = eventIterator.next();
                if (event != null && !events.contains(event))
                    events.add(event);
            }
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
    public boolean onDispatchEvent(@NonNull String key, @NonNull Bundle args) {
        key = PathBuilder.format(key);
        if (!noKey) {
            if (TextUtils.isEmpty(key))
                return false;

            String part = PathBuilder.get(key);
            if (TextUtils.isEmpty(part) || !part.equals(this.key))
                return false;
        }

        boolean handled = false;
        if (events != null) {
            if (!noKey) {
                String subKey = PathBuilder.nextPath(key);
                handled |= invokeEvents(subKey, args);
            } else {
                handled |= invokeEvents(key, args);
            }
        }

        try {
            handled |= super.onDispatchEvent(key, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return handled;
    }

    public boolean invokeEvents(@NonNull String key, @NonNull Bundle args) {
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
