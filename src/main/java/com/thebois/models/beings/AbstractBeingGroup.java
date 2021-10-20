package com.thebois.models.beings;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.eventbus.Subscribe;

import com.thebois.Pawntastic;
import com.thebois.listeners.events.OnDeathEvent;

/**
 * An abstract implementation of IBeingGroup.
 */
public abstract class AbstractBeingGroup implements IBeingGroup {

    private Collection<IBeing> beings = new ArrayList<>();
    private Collection<IBeing> deadBeings = new ArrayList<>();

    protected AbstractBeingGroup() {
        Pawntastic.getEventBus().register(this);
    }

    /**
     * Adds a being to the internal collection of beings.
     *
     * @param being IBeing to be added.
     */
    protected void addBeing(final IBeing being) {
        beings.add(being);
    }

    @Override
    public void update(final float deltaTime) {
        beings.removeAll(deadBeings);
        deadBeings = new ArrayList<>();
        beings.forEach(being -> being.update(deltaTime));
    }

    @Override
    public Collection<IBeing> getBeings() {
        final Collection<IBeing> clonedBeings = new ArrayList<>(beings.size());
        clonedBeings.addAll(beings);
        return clonedBeings;
    }

    /**
     * Listens to the OnDeathEvent in order to remove dead beings from the colony.
     *
     * @param event The published event.
     */
    @Subscribe
    public void onDeathEvent(final OnDeathEvent event) {
        final IBeing deadBeing = event.getDeadBeing();
        deadBeings.add(deadBeing);
    }

    protected void setBeings(final Collection<IBeing> beings) {
        this.beings = beings;
    }

}
