import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


 abstract class Entity
{
   private final EntityKind kind;
   private final String id;
   private Point position;
   private final List<PImage> images;
   private int imageIndex;
   private final int resourceLimit;
   private int resourceCount;
   private final int actionPeriod;
   private final int animationPeriod;
   private PathingStrategy pathingStrategy;


   public static Predicate<Point> canPassThrough(WorldModel world){
      return p -> (world.withinBounds(p) && !world.isOccupied(p));
   }
   public static boolean withinReach(Point a, Point b) {
   return a.adjacent(b);
   }





   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod, PathingStrategy pathingStrategy)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
      this.pathingStrategy = pathingStrategy;
   }
      public static final Random rand = new Random();

   public abstract void executeActivityAction(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler);


   public int getAnimationPeriod()
   {
      switch (kind)
      {
         case BIRB_FULL:
         case OCTO_NOT_FULL:
         case WOLF:
         case EXPLO:
         case ATLANTIS:
         case CAT:
            return animationPeriod;
         default:
            throw new UnsupportedOperationException(
                    String.format("getAnimationPeriod not supported for %s",
                            kind));
      }
   }

//   abstract Entity createEntity(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod, List<PImage> images);


   public void nextImage()
   {
      imageIndex = (imageIndex + 1) % images.size();
   }


   public static Optional<Entity> nearestEntity(List<Entity> entities,
                                                Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = nearest.getPosition().distanceSquared(pos);

         for (Entity other : entities)
         {
            int otherDistance = other.getPosition().distanceSquared(pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }
   public int getResourceLimit(){return resourceLimit;}

   public int getResourceCount(){return resourceCount;}

   public EntityKind getKind() {
      return kind;
   }

   public Point getPosition() {
      return position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

  public List<PImage> getImages() {
      return images;
   }

   public String getId(){return id;}

   public int getImageIndex() {
      return imageIndex;
   }

   public int getActionPeriod() {
      return actionPeriod;
   }

   public void setResourceCount(int resourceCount) {
      this.resourceCount = resourceCount;
   }
}


