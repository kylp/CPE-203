import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Wolf extends Entity {
    public Wolf(String id, Point position,
                int actionPeriod, int animationPeriod, List<PImage> images, PathingStrategy pathingStrategy) {
        super(EntityKind.WOLF, id, position, images,
                0, 0, actionPeriod, animationPeriod, pathingStrategy);
    }

    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> wolfTarget = world.findNearest(getPosition(), EntityKind.BUSH);
        long nextPeriod = getActionPeriod();

        PathingStrategy path = new SingleStepPathingStrategy();

        if (wolfTarget.isPresent()) {
            Point tgtPos = wolfTarget.get().getPosition();

            if (moveToWolf(world, wolfTarget.get(), scheduler)) {
                Entity quake = new Explo(tgtPos,
                        imageStore.getImageList( Action.QUAKE_KEY), path);

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                scheduler.scheduleActions(quake, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                Action.createActivityAction(this, world, imageStore),
                nextPeriod);
    }

    private  boolean moveToWolf(WorldModel world,
                                Entity target, EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = this.nextPositionWolf( world, target.getPosition());

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

    private Point nextPositionWolf(WorldModel world,
                                   Point destPos)
    {

//        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
//        Point newPos = new Point(getPosition().getX() + horiz,
//                getPosition().getY());
//
//        Optional<Entity> occupant = world.getOccupant( newPos);
//
//        if (horiz == 0 ||
//                (occupant.isPresent() && !(occupant.get().getKind() == EntityKind.FISH)))
//        {
//            int vert = Integer.signum(destPos.getY() - getPosition().getY());
//            newPos = new Point(getPosition().getX(), getPosition().getY() + vert);
//            occupant = world.getOccupant( newPos);
//
//            if (vert == 0 ||
//                    (occupant.isPresent() && !(occupant.get().getKind() == EntityKind.FISH)))
//            {
//                newPos = getPosition();
//            }
//        }
        PathingStrategy pathingStrategy = new AStarPathingStrategy();

        List<Point> nextPoints = pathingStrategy.computePath(getPosition(), destPos, canPassThrough(world),
                Entity::withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if(nextPoints.size() == 0){
            return getPosition();
        }
        return nextPoints.get(0);

    }
}
