import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Sgrass extends Entity{
    public Sgrass(String id, Point position, int actionPeriod,
                  List<PImage> images){
        super(EntityKind.SGRASS, id, position, images, 0, 0,
                actionPeriod, 0);
    }

    @Override
    public  void executeActivityAction(WorldModel world,
                                       ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent()) {
            Entity fish = new Fish(Action.FISH_ID_PREFIX + getId(),
                    openPt.get(), Action.FISH_CORRUPT_MIN +
                            Functions.rand.nextInt(Action.FISH_CORRUPT_MAX - Action.FISH_CORRUPT_MIN),
                    imageStore.getImageList( Action.FISH_KEY));
            world.addEntity(fish);
            scheduler.scheduleActions(fish, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                Action.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
