import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class SpinBirbNotFull extends SpinBirb {
    public SpinBirbNotFull(String id, int resourceLimit,
                           Point position, int actionPeriod, int animationPeriod,
                           List<PImage> images, PathingStrategy pathingStrategy)
    {
         super(EntityKind.OCTO_NOT_FULL, id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod, pathingStrategy);
    }

    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(),
                EntityKind.BERRY);

        if (!notFullTarget.isPresent() ||
                !moveToNotFull( world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this,
                    Action.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }
    }
    private  boolean moveToNotFull( WorldModel world,
                                    Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {

            //todo: verify this works
            //this.getResourceCount() += 1;
            this.setResourceCount(getResourceCount()+1);
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else
        {
            Point nextPos = this.nextPositionBirb(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    private  boolean transformNotFull(WorldModel world,
                                      EventScheduler scheduler, ImageStore imageStore)
    {
        if (this.getResourceCount() >= this.getResourceLimit())
        {
            PathingStrategy path = new SingleStepPathingStrategy();
            Entity birb = new SpinBirbFull(this.getId(), this.getResourceLimit(),
                    this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(),
                    this.getImages(), path);

            world.removeEntity( this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity( birb);
            scheduler.scheduleActions(birb, world, imageStore);

            return true;
        }

        return false;
    }
}
