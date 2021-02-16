import processing.core.PImage;

import java.util.List;

public class Quake extends Entity{
    public Quake(Point position, List<PImage> images){
        super(EntityKind.QUAKE, Action.QUAKE_ID, position, images,
                0, 0, Action.QUAKE_ACTION_PERIOD, Action.QUAKE_ANIMATION_PERIOD);
    }
    @Override
    public  void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }
}
