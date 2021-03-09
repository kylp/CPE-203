import processing.core.PImage;

import java.util.List;

public class Berry extends Entity {
    public Berry(String id, Point position, int actionPeriod,
                 List<PImage> images, PathingStrategy pathingStrategy) {
        super(EntityKind.BERRY, id, position, images, 0, 0,
              actionPeriod, 0, pathingStrategy);
    }
@Override
    public  void executeActivityAction( WorldModel world,
                                      ImageStore imageStore, EventScheduler scheduler) {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        PathingStrategy path = new SingleStepPathingStrategy();

        Entity wolf = new Wolf(getId() + Action.WOLF_ID_SUFFIX,
                pos, getActionPeriod() / Action.WOLF_PERIOD_SCALE,
                Action.WOLF_ANIMATION_MIN +
                        rand.nextInt(Action.WOLF_ANIMATION_MAX - Action.WOLF_ANIMATION_MIN),
                imageStore.getImageList(Action.WOLF_KEY), path);

        world.addEntity(wolf);
        scheduler.scheduleActions(wolf, world, imageStore);
    }
}
