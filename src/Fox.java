import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Fox extends Entity {
    public Fox(String id, Point position,
               int actionPeriod, int animationPeriod, List<PImage> images, PathingStrategy pathingStrategy) {
        super(EntityKind.FOX, id, position, images,
                0, 0, actionPeriod, animationPeriod, pathingStrategy);
    }

    private  boolean moveToFox(WorldModel world,
                                Entity target, EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition()))
        {
//            world.removeEntity(target);
//            scheduler.unscheduleAllEvents(target);
//            return true;
            return false;
        }
        else
        {
            Point nextPos = this.nextPositionFox( world, target.getPosition());

            if (!getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity( this, nextPos);
            }
            return false;
        }
    }

    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> foxTarget = world.findNearest(getPosition(), EntityKind.CAT);
        long nextPeriod = getActionPeriod();

        PathingStrategy path = new SingleStepPathingStrategy();

        if (foxTarget.isPresent()) {
            Point tgtPos = foxTarget.get().getPosition();

            if (moveToFox(world, foxTarget.get(), scheduler)) {
                Entity explo = new Explo(tgtPos,
                        imageStore.getImageList(Action.EXPLO), path);

                world.addEntity(explo);
                nextPeriod += getActionPeriod();
                scheduler.scheduleActions(explo, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                Action.createActivityAction(this, world, imageStore),
                nextPeriod);
    }

    private Point nextPositionFox(WorldModel world,
                                   Point destPos) {

        List<Point> nextPoints = pathingStrategy.computePath(getPosition(), destPos, canPassThrough(world),
                Entity::withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if (nextPoints.size() == 0) {
            return getPosition();
        }
        return nextPoints.get(0);

    }
}