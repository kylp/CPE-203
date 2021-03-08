import processing.core.PImage;

import java.util.List;

public class Atlantis extends Entity{
    public Atlantis(String id, Point position,
                    List<PImage> images, PathingStrategy pathingStrategy){
        super(EntityKind.ATLANTIS, id, position, images,
                0, 0, 0, 0, pathingStrategy);
    }
    @Override
    public  void executeActivityAction(WorldModel world, ImageStore images, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }
}
