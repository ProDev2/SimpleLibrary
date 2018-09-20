package com.simplelib.events;

import android.os.Bundle;

public abstract class Event {
    private Object id;
    private Bundle extras;

    private EventHandler handler;

    private int times;

    public Event() {
        this(null);
    }

    public Event(Object id) {
        this(id, EventHandler.get());
    }

    public Event(Object id, EventHandler handler) {
        this.id = id;
        this.extras = new Bundle();

        this.handler = handler;

        this.times = -1;
    }

    public Object getId() {
        return id;
    }

    public Bundle getExtras() {
        return extras;
    }

    public EventHandler getEventHandler() {
        return handler;
    }

    public void setEventHandler(EventHandler handler) {
        if (handler != null)
            this.handler = handler;
    }

    public boolean addSelf() {
        if (handler == null)
            return false;
        return handler.add(this);
    }

    public boolean removeSelf() {
        if (handler == null)
            return false;
        return handler.remove(this);
    }

    public void post(Object id, Bundle extras) {
        if (hasId(id)) post(extras);
    }

    public void post(Bundle extras) {
        if (times != 0) {
            if (times > 0) times--;
            if (times == 0) removeSelf();

            if (extras != null)
                this.extras = new Bundle(extras);

            try {
                run(this.extras);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            removeSelf();
        }
    }

    public boolean hasId(Object id) {
        return (this.id != null && id != null && this.id.equals(id)) || (this.id == null && id == null);
    }

    public void setSingleExecution() {
        setExecutionTimes(1);
    }

    public void setExecutionTimes(int times) {
        this.times = times;
    }

    public abstract void run(Bundle extras);
}