import java.util.List;
import java.util.Optional;

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



   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
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
   }

   public abstract void executeActivityAction(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler);


   public int getAnimationPeriod()
   {
      switch (kind)
      {
         case OCTO_FULL:
         case OCTO_NOT_FULL:
         case CRAB:
         case QUAKE:
         case ATLANTIS:
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

   private static Entity createOctoFull(String id, int resourceLimit,
                                       Point position, int actionPeriod, int animationPeriod,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   public static Entity createOctoNotFull(String id, int resourceLimit,
                                          Point position, int actionPeriod, int animationPeriod,
                                          List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   public static Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }

//   public static Entity createFish(String id, Point position, int actionPeriod,
//                                   List<PImage> images)
//   {
//      return new Entity(EntityKind.FISH, id, position, images, 0, 0,
//              actionPeriod, 0);
//   }

//   public static Entity createCrab(String id, Point position,
//                                   int actionPeriod, int animationPeriod, List<PImage> images)
//   {
//      return new Entity(EntityKind.CRAB, id, position, images,
//              0, 0, actionPeriod, animationPeriod);
//   }

//   public static Entity createQuake(Point position, List<PImage> images)
//   {
//      return new Entity(EntityKind.QUAKE, Action.QUAKE_ID, position, images,
//              0, 0, Action.QUAKE_ACTION_PERIOD, Action.QUAKE_ANIMATION_PERIOD);
//   }
//
//   public static Entity createSgrass(String id, Point position, int actionPeriod,
//                                     List<PImage> images)
//   {
//      return new Entity(EntityKind.SGRASS, id, position, images, 0, 0,
//              actionPeriod, 0);
//   }
   private Point nextPositionOcto( WorldModel world,
                                        Point destPos)
   {
      int horiz = Integer.signum(destPos.getX() - this.position.getX());
      Point newPos = new Point(this.position.getX() + horiz,
              this.position.getY());

      if (horiz == 0 || world.isOccupied( newPos))
      {
         int vert = Integer.signum(destPos.getY() - this.position.getY());
         newPos = new Point(this.position.getX(),
                 this.position.getY() + vert);

         if (vert == 0 || world.isOccupied( newPos))
         {
            newPos = this.position;
         }
      }

      return newPos;
   }

//   private Point nextPositionCrab( WorldModel world,
//                                        Point destPos)
//   {
//      int horiz = Integer.signum(destPos.getX() - this.position.getX());
//      Point newPos = new Point(this.position.getX() + horiz,
//              this.position.getY());
//
//      Optional<Entity> occupant = world.getOccupant( newPos);
//
//      if (horiz == 0 ||
//              (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
//      {
//         int vert = Integer.signum(destPos.getY() - this.position.getY());
//         newPos = new Point(this.position.getX(), this.position.getY() + vert);
//         occupant = world.getOccupant( newPos);
//
//         if (vert == 0 ||
//                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
//         {
//            newPos = this.position;
//         }
//      }
//
//      return newPos;
//   }
   private  boolean transformNotFull(WorldModel world,
                                          EventScheduler scheduler, ImageStore imageStore)
   {
      if (this.resourceCount >= this.resourceLimit)
      {
         Entity octo = createOctoFull(this.id, this.resourceLimit,
                 this.position, this.actionPeriod, this.animationPeriod,
                 this.images);

         world.removeEntity( this);
         scheduler.unscheduleAllEvents(this);

         world.addEntity( octo);
         scheduler.scheduleActions(octo, world, imageStore);

         return true;
      }

      return false;
   }

   private  void transformFull( WorldModel world,
                                    EventScheduler scheduler, ImageStore imageStore)
   {
      Entity octo = createOctoNotFull(this.id, this.resourceLimit,
              this.position, this.actionPeriod, this.animationPeriod,
              this.images);

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      world.addEntity(octo);
      scheduler.scheduleActions(octo, world, imageStore);
   }

   private  boolean moveToNotFull( WorldModel world,
                                       Entity target, EventScheduler scheduler)
   {
      if (this.position.adjacent(target.position))
      {
         this.resourceCount += 1;
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = this.nextPositionOcto(world, target.position);

         if (!this.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(this, nextPos);
         }
         return false;
      }
   }

   private  boolean moveToFull( WorldModel world,
                                    Entity target, EventScheduler scheduler)
   {
      if (this.position.adjacent(target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = this.nextPositionOcto( world, target.position);

         if (!this.position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity( this, nextPos);
         }
         return false;
      }
   }

//   private  boolean moveToCrab(WorldModel world,
//                                    Entity target, EventScheduler scheduler)
//   {
//      if (this.position.adjacent(target.position))
//      {
//         world.removeEntity(target);
//         scheduler.unscheduleAllEvents(target);
//         return true;
//      }
//      else
//      {
//         Point nextPos = this.nextPositionCrab( world, target.position);
//
//         if (!this.position.equals(nextPos))
//         {
//            Optional<Entity> occupant = world.getOccupant( nextPos);
//            if (occupant.isPresent())
//            {
//               scheduler.unscheduleAllEvents(occupant.get());
//            }
//
//            world.moveEntity( this, nextPos);
//         }
//         return false;
//      }
//   }
   public void executeOctoFullActivity(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler) {
      Optional<Entity> fullTarget = world.findNearest(this.position,
              EntityKind.ATLANTIS);

      if (fullTarget.isPresent() &&
              moveToFull( world, fullTarget.get(), scheduler)) {
         //at atlantis trigger animation
         scheduler.scheduleActions(fullTarget.get(), world, imageStore);

         //transform to unfull
         transformFull(world, scheduler, imageStore);
      } else {
         scheduler.scheduleEvent(this,
                 Action.createActivityAction(this, world, imageStore),
                 this.actionPeriod);
      }
   }

   public void executeOctoNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
      Optional<Entity> notFullTarget = world.findNearest(this.position,
              EntityKind.FISH);

      if (!notFullTarget.isPresent() ||
              !moveToNotFull( world, notFullTarget.get(), scheduler) ||
              !transformNotFull(world, scheduler, imageStore)) {
         scheduler.scheduleEvent(this,
                 Action.createActivityAction(this, world, imageStore),
                 this.actionPeriod);
      }
   }

//   public  void executeFishActivity( WorldModel world,
//                                          ImageStore imageStore, EventScheduler scheduler) {
//      Point pos = this.position;  // store current position before removing
//
//      world.removeEntity(this);
//      scheduler.unscheduleAllEvents(this);
//
//      Entity crab = createCrab(this.id + Action.CRAB_ID_SUFFIX,
//              pos, this.actionPeriod / Action.CRAB_PERIOD_SCALE,
//              Action.CRAB_ANIMATION_MIN +
//                      Functions.rand.nextInt(Action.CRAB_ANIMATION_MAX - Action.CRAB_ANIMATION_MIN),
//              imageStore.getImageList(Action.CRAB_KEY));
//
//      world.addEntity(crab);
//      scheduler.scheduleActions(crab, world, imageStore);
//   }

//   public  void executeCrabActivity(WorldModel world,
//                                          ImageStore imageStore, EventScheduler scheduler) {
//      Optional<Entity> crabTarget = world.findNearest(this.position, EntityKind.SGRASS);
//      long nextPeriod = this.actionPeriod;
//
//      if (crabTarget.isPresent()) {
//         Point tgtPos = crabTarget.get().position;
//
//         if (moveToCrab(world, crabTarget.get(), scheduler)) {
//            Entity quake = createQuake(tgtPos,
//                    imageStore.getImageList( Action.QUAKE_KEY));
//
//            world.addEntity(quake);
//            nextPeriod += this.actionPeriod;
//            scheduler.scheduleActions(quake, world, imageStore);
//         }
//      }
//
//      scheduler.scheduleEvent(this,
//              Action.createActivityAction(this, world, imageStore),
//              nextPeriod);
//   }

   public  void executeQuakeActivity(WorldModel world, EventScheduler scheduler) {
      scheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   public  void executeAtlantisActivity(WorldModel world, EventScheduler scheduler) {
      scheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

//   public  void executeSgrassActivity(WorldModel world,
//                                            ImageStore imageStore, EventScheduler scheduler) {
//      Optional<Point> openPt = world.findOpenAround(this.position);
//
//      if (openPt.isPresent()) {
//         Entity fish = createFish(Action.FISH_ID_PREFIX + this.id,
//                 openPt.get(), Action.FISH_CORRUPT_MIN +
//                         Functions.rand.nextInt(Action.FISH_CORRUPT_MAX - Action.FISH_CORRUPT_MIN),
//                 imageStore.getImageList( Action.FISH_KEY));
//         world.addEntity(fish);
//         scheduler.scheduleActions(fish, world, imageStore);
//      }
//
//      scheduler.scheduleEvent(this,
//              Action.createActivityAction(this, world, imageStore),
//              this.actionPeriod);
//   }
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
}


