import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class SpinBirbFull extends SpinBirb {
    public SpinBirbFull(String id, int resourceLimit,
                        Point position, int actionPeriod, int animationPeriod,
                        List<PImage> images, PathingStrategy pathingStrategy){
        super(EntityKind.BIRB_FULL, id, position, images,
                resourceLimit, resourceLimit, actionPeriod, animationPeriod, pathingStrategy);
    }

    @Override
    public void executeActivityAction(WorldModel world,
                                        ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(),
                EntityKind.ATLANTIS);

        if (fullTarget.isPresent() &&
                moveToFull( world, fullTarget.get(), scheduler)) {
            //at atlantis trigger animation
            scheduler.scheduleActions(fullTarget.get(), world, imageStore);

            //transform to unfull
            transformFull(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }
    }

    private  boolean moveToFull( WorldModel world,
                                 Entity target, EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = this.nextPositionBirb( world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity( this, nextPos);
            }
            return false;
        }
    }
//    private Point nextPositionOcto( WorldModel world,
//                                    Point destPos)
//    {
//        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
//        Point newPos = new Point(getPosition().getX() + horiz,
//                getPosition().getY());
//
//        if (horiz == 0 || world.isOccupied( newPos))
//        {
//            int vert = Integer.signum(destPos.getY() - getPosition().getY());
//            newPos = new Point(getPosition().getX(),
//                    getPosition().getY() + vert);
//
//            if (vert == 0 || world.isOccupied( newPos))
//            {
//                newPos = getPosition();
//            }
//        }
//
//        return newPos;
//    }
    private  void transformFull( WorldModel world,
                                 EventScheduler scheduler, ImageStore imageStore)
    {
        PathingStrategy path = new AStarPathingStrategy();
        //todo: fix this
        Entity birb = new SpinBirbNotFull(this.getId(), this.getResourceLimit(),
                this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(),
                this.getImages(), path);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(birb);
        scheduler.scheduleActions(birb, world, imageStore);
    }
}
