package com.thebois.listeners;

import com.thebois.listeners.events.IEvent;

/**
 * Represents a source of events that can be listened to.
 *
 * @param <TEvent> The type of the events generated
 *
 * @author Martin
 */
public interface IEventSource<TEvent extends IEvent> {

    /**
     * Registers a new listener that will be notified about new events.
     *
     * @param listener The listener to register
     */
    void registerListener(IEventListener<TEvent> listener);

    /**
     * Unregisters a listener so that it will no longer be notified about new events.
     *
     * @param listener The listener to unregister
     *
     * @throws IllegalArgumentException When trying to unregister a listener that is not registered
     */
    void removeListener(IEventListener<TEvent> listener);

}
