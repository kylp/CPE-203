import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Crab extends Entity {
    public Crab(String id, Point position,
                int actionPeriod, int animationPeriod, List<PImage> images, PathingStrategy pathingStrategy) {
        super(EntityKind.CRAB, id, position, images,
                0, 0, actionPeriod, animationPeriod, pathingStrategy);
    }

    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> crabTarget = world.findNearest(getPosition(), EntityKind.SGRASS);
        long nextPeriod = getActionPeriod();

        PathingStrategy path = new SingleStepPathingStrategy();

        if (crabTarget.isPresent()) {
            Point tgtPos = crabTarget.get().getPosition();

            if (moveToCrab(world, crabTarget.get(), scheduler)) {
                Entity quake = new Quake(tgtPos,
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

    private  boolean moveToCrab(WorldModel world,
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
            Point nextPos = this.nextPositionCrab( world, target.getPosition());

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

    private Point nextPositionCrab( WorldModel world,
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
        SingleStepPathingStrategy pathingStrategy = new SingleStepPathingStrategy();

        List<Point> nextPoints = pathingStrategy.computePath(getPosition(), destPos, canPassThrough(world),
                Entity::withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if(nextPoints.size() == 0){
            return getPosition();
        }
        return nextPoints.get(0);

    }
}
