package com.simplelib.builder;

import com.simplelib.events.Event;
import com.simplelib.events.EventGroup;
import com.simplelib.events.IEvent;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {
    public static final EventBuilder builder() {
        return new EventBuilder();
    }

    private IEvent event;

    public EventBuilder() {
    }

    public EventB<EventBuilder> buildEvent() {
        EventB<EventBuilder> eventBuilder = new EventB<>();
        eventBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<Event>() {
            @Override
            public void onResult(Event result) {
                event = result;
            }
        });
        return eventBuilder;
    }

    public EventB<EventBuilder> buildEvent(String key) {
        EventB<EventBuilder> eventBuilder = new EventB<>(key);
        eventBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<Event>() {
            @Override
            public void onResult(Event result) {
                event = result;
            }
        });
        return eventBuilder;
    }

    public GroupB<EventBuilder> buildEventGroup() {
        GroupB<EventBuilder> groupBuilder = new GroupB<>();
        groupBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<EventGroup>() {
            @Override
            public void onResult(EventGroup result) {
                event = result;
            }
        });
        return groupBuilder;
    }

    public GroupB<EventBuilder> buildEventGroup(String key) {
        GroupB<EventBuilder> groupBuilder = new GroupB<>(key);
        groupBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<EventGroup>() {
            @Override
            public void onResult(EventGroup result) {
                event = result;
            }
        });
        return groupBuilder;
    }

    public IEvent build() {
        return event;
    }

    public class EventB<T> extends NestedBuilder<T, Event> {
        private String key;
        private boolean noKey;

        private Event.OnEventListener eventListener;

        private EventB() {
            withNoKey();
        }

        private EventB(String key) {
            withKey(key);
        }

        public EventB<T> withKey(String key) {
            this.key = key;
            this.noKey = false;
            return this;
        }

        public EventB<T> withNoKey() {
            this.key = null;
            this.noKey = true;
            return this;
        }

        public EventB<T> withEventListener(Event.OnEventListener eventListener) {
            this.eventListener = eventListener;
            return this;
        }

        @Override
        public Event build() {
            Event event;
            if (!this.noKey)
                event = new Event(this.key);
            else
                event = new Event();
            event.setOnEventListener(this.eventListener);
            return event;
        }
    }

    public class GroupB<T> extends NestedBuilder<T, EventGroup> {
        private String key;
        private boolean noKey;

        private Event.OnEventListener eventListener;

        private List<IEvent> events;

        private GroupB() {
            withNoKey();
            this.events = new ArrayList<>();
        }

        private GroupB(String key) {
            withKey(key);
            this.events = new ArrayList<>();
        }

        public GroupB<T> withKey(String key) {
            this.key = key;
            this.noKey = false;
            return this;
        }

        public GroupB<T> withNoKey() {
            this.key = null;
            this.noKey = true;
            return this;
        }

        public GroupB<T> withEventListener(Event.OnEventListener eventListener) {
            this.eventListener = eventListener;
            return this;
        }

        public GroupB<T> add(IEvent event) {
            if (event != null && !events.contains(event))
                events.add(event);
            return this;
        }

        public EventB<GroupB<T>> addEvent() {
            EventB<GroupB<T>> eventBuilder = new EventB<>();
            eventBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<Event>() {
                @Override
                public void onResult(Event result) {
                    if (result != null)
                        add(result);
                }
            });
            return eventBuilder;
        }

        public EventB<GroupB<T>> addEvent(String key) {
            EventB<GroupB<T>> eventBuilder = new EventB<>(key);
            eventBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<Event>() {
                @Override
                public void onResult(Event result) {
                    if (result != null)
                        add(result);
                }
            });
            return eventBuilder;
        }

        public GroupB<GroupB<T>> addGroup() {
            GroupB<GroupB<T>> groupBuilder = new GroupB<>();
            groupBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<EventGroup>() {
                @Override
                public void onResult(EventGroup result) {
                    if (result != null)
                        add(result);
                }
            });
            return groupBuilder;
        }

        public GroupB<GroupB<T>> addGroup(String key) {
            GroupB<GroupB<T>> groupBuilder = new GroupB<>(key);
            groupBuilder.withParentBuilder(this, new NestedBuilder.OnResultListener<EventGroup>() {
                @Override
                public void onResult(EventGroup result) {
                    if (result != null)
                        add(result);
                }
            });
            return groupBuilder;
        }

        @Override
        public EventGroup build() {
            EventGroup group;
            if (!this.noKey)
                group = new EventGroup(this.key, events);
            else
                group = new EventGroup(events);
            group.setOnEventListener(this.eventListener);
            return group;
        }
    }
}
