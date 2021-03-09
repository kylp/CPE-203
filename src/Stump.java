import processing.core.PImage;

import java.util.List;

public class Stump extends Entity{

    public Stump(String id, Point position,
                 List<PImage> images, PathingStrategy pathingStrategy)
    {
        super(EntityKind.OBSTACLE, id, position, images,
                0, 0, 0, 0, pathingStrategy);
    }
    @Override
    public void executeActivityAction(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }
}
