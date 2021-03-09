import processing.core.PImage;

import java.util.List;

public class Cat extends Entity{

    public Cat(Point position, List<PImage> images, PathingStrategy pathingStrategy) {
        super(EntityKind.CAT, Action.CAT_ID, position, images,
                0, 0, Action.CAT_ACTION_PERIOD, Action.CAT_ANIMATION_PERIOD, pathingStrategy);
    }

    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {



        scheduler.scheduleEvent(this, Action.createActivityAction(this, world, imageStore), getActionPeriod());
    }
}
