import processing.core.PImage;

import java.util.List;

public class Quake extends Entity{
    public Quake(EntityKind kind, String id, Point position,
                 List<PImage> images, int resourceLimit, int resourceCount,
                 int actionPeriod, int animationPeriod){
        super(EntityKind.QUAKE, Action.QUAKE_ID, position, images,
                0, 0, Action.QUAKE_ACTION_PERIOD, Action.QUAKE_ANIMATION_PERIOD);
    }
    @Override
    public void executeActivityAction(){}
}
