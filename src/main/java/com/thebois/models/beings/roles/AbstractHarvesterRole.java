package com.thebois.models.beings.roles;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.thebois.abstractions.IResourceFinder;
import com.thebois.models.Position;
import com.thebois.models.beings.IActionPerformer;
import com.thebois.models.beings.actions.ActionFactory;
import com.thebois.models.beings.actions.IAction;
import com.thebois.models.beings.actions.IActionGenerator;
import com.thebois.models.world.IWorld;
import com.thebois.models.world.resources.IResource;
import com.thebois.models.world.resources.ResourceType;

/**
 * Represents a role that mainly harvests items from resources.
 */
abstract class AbstractHarvesterRole extends AbstractRole {

    private final IResourceFinder finder;
    private final IWorld world;
    private final ResourceType resourceType;

    /**
     * Instantiates with a way of finding resources.
     *
     * @param finder       The locator of resources.
     * @param world        The world in which the resources are located.
     * @param resourceType The type of resource to gather.
     */
    AbstractHarvesterRole(
        final IResourceFinder finder, final IWorld world, final ResourceType resourceType) {
        this.finder = finder;
        this.world = world;
        this.resourceType = resourceType;
    }

    @Override
    protected Collection<IActionGenerator> getTaskGenerators() {
        return List.of(this::createMoveToResource, this::createHarvestResource);
    }

    protected IAction createMoveToResource(final IActionPerformer performer) {
        final Optional<IResource> maybeResource = findNearbyResource(performer);
        if (maybeResource.isEmpty()) return ActionFactory.createDoNothing();

        final IResource resource = maybeResource.get();

        final Position position = performer.getPosition();
        final Optional<Position> closestSpotNextToResource = world.getClosestNeighbourOf(resource,
                                                                                         position);

        if (closestSpotNextToResource.isEmpty()) return ActionFactory.createDoNothing();

        return ActionFactory.createMoveTo(closestSpotNextToResource.get());
    }

    protected IAction createHarvestResource(final IActionPerformer performer) {
        final Optional<IResource> maybeTree = findNearbyResource(performer);
        if (maybeTree.isEmpty()) return ActionFactory.createDoNothing();

        final IResource tree = maybeTree.get();

        return ActionFactory.createHarvest(tree);
    }

    protected Optional<IResource> findNearbyResource(final IActionPerformer performer) {
        final Position position = performer.getPosition();
        return finder.getNearbyOfType(position, resourceType);
    }

}