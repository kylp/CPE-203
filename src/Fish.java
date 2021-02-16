import processing.core.PImage;

import java.util.List;

public class Fish extends Entity {
    public Fish(String id, Point position, int actionPeriod,
                                   List<PImage> images) {
        super(EntityKind.FISH, id, position, images, 0, 0,
              actionPeriod, 0);
    }
@Override
    public  void executeActivityAction( WorldModel world,
                                      ImageStore imageStore, EventScheduler scheduler) {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity crab = new Crab(getId() + Action.CRAB_ID_SUFFIX,
                pos, getActionPeriod() / Action.CRAB_PERIOD_SCALE,
                Action.CRAB_ANIMATION_MIN +
                        Functions.rand.nextInt(Action.CRAB_ANIMATION_MAX - Action.CRAB_ANIMATION_MIN),
                imageStore.getImageList(Action.CRAB_KEY));

        world.addEntity(crab);
        scheduler.scheduleActions(crab, world, imageStore);
    }
}
