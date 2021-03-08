import processing.core.PImage;

import java.util.List;

public abstract class Octo extends Entity{
    public Octo(EntityKind kind, String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod, PathingStrategy pathingStrategy) {
        super(kind, id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, pathingStrategy);
    }

    public Point nextPositionOcto(WorldModel world,
                                   Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - getPosition().getX());
        Point newPos = new Point(getPosition().getX() + horiz,
                getPosition().getY());

        if (horiz == 0 || world.isOccupied( newPos))
        {
            int vert = Integer.signum(destPos.getY() - getPosition().getY());
            newPos = new Point(getPosition().getX(),
                    getPosition().getY() + vert);

            if (vert == 0 || world.isOccupied( newPos))
            {
                newPos = getPosition();
            }
        }

        return newPos;
    }
}
