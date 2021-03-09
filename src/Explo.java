import processing.core.PImage;

import java.util.List;

public class Explo extends Entity{
    public Explo(Point position, List<PImage> images, PathingStrategy pathingStrategy){
        super(EntityKind.EXPLO, Action.EXPLO_ID, position, images,
                0, 0, Action.EXPLO_ACTION_PERIOD, Action.EXPLO_ANIMATION_PERIOD, pathingStrategy);
    }
    @Override
    public  void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }
}
