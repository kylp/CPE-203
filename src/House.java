import processing.core.PImage;

import java.util.List;

public class House extends Entity{
    public House(String id, Point position,
                 List<PImage> images, PathingStrategy pathingStrategy){
        super(EntityKind.HOUSE, id, position, images,
                0, 0, 0, 0, pathingStrategy);
    }
    @Override
    public  void executeActivityAction(WorldModel world, ImageStore images, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }
}
