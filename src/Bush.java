import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Bush extends Entity{
    public Bush(String id, Point position, int actionPeriod,
                List<PImage> images, PathingStrategy pathingStrategy){
        super(EntityKind.BUSH, id, position, images, 0, 0,
                actionPeriod, 0, pathingStrategy);
    }

    @Override
    public  void executeActivityAction(WorldModel world,
                                       ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        PathingStrategy path = new SingleStepPathingStrategy();

        if (openPt.isPresent()) {
            Entity berry = new Berry(Action.BERRY_ID_PREFIX + getId(),
                    openPt.get(), Action.BERRY_CORRUPT_MIN +
                            rand.nextInt(Action.BERRY_CORRUPT_MAX - Action.BERRY_CORRUPT_MIN),
                    imageStore.getImageList( Action.BERRY_KEY), path);
            world.addEntity(berry);
            scheduler.scheduleActions(berry, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                Action.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
