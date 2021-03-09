/*
Action: ideally what our various entities might do in our virutal world
 */

import processing.core.PImage;

import java.util.List;

final class Action {

   private final ActionKind kind;
   private final Entity entity;
   private final WorldModel world;
   private final ImageStore imageStore;
   private final int repeatCount;


   public static final String BIRB_KEY = "birb";
   public static final int BIRB_NUM_PROPERTIES = 7;
   public static final int BIRB_ID = 1;
   public static final int BIRB_COL = 2;
   public static final int BIRB_ROW = 3;
   public static final int BIRB_LIMIT = 4;
   public static final int BIRB_ACTION_PERIOD = 5;
   public static final int BIRB_ANIMATION_PERIOD = 6;

   public static final String STUMP_KEY = "stump";
   public static final int STUMP_NUM_PROPERTIES = 4;
   public static final int STUMP_ID = 1;
   public static final int STUMP_COL = 2;
   public static final int STUMP_ROW = 3;

   public static final String BERRY_KEY = "berry";
   public static final int BERRY_NUM_PROPERTIES = 5;
   public static final int BERRY_ID = 1;
   public static final int BERRY_COL = 2;
   public static final int BERRY_ROW = 3;
   public static final int BERRY_ACTION_PERIOD = 4;

   public static final String HOUSE_KEY = "house";
   public static final int HOUSE_NUM_PROPERTIES = 4;
   public static final int HOUSE_ID = 1;
   public static final int HOUSE_COL = 2;
   public static final int HOUSE_ROW = 3;
   public static final int HOUSE_ANIMATION_PERIOD = 70;
   public static final int HOUSE_ANIMATION_REPEAT_COUNT = 7;

   public static final String BUSH_KEY = "bush";
   public static final int BUSH_NUM_PROPERTIES = 5;
   public static final int BUSH_ID = 1;
   public static final int BUSH_COL = 2;
   public static final int BUSH_ROW = 3;
   public static final int BUSH_ACTION_PERIOD = 4;

   public static final String WOLF_KEY = "wolf";
   public static final String WOLF_ID_SUFFIX = " -- wolf";
   public static final int WOLF_PERIOD_SCALE = 4;
   public static final int WOLF_ANIMATION_MIN = 50;
   public static final int WOLF_ANIMATION_MAX = 150;

   public static final String EXPLO = "explo";
   public static final String EXPLO_ID = "explo";
   public static final int EXPLO_ACTION_PERIOD = 1100;
   public static final int EXPLO_ANIMATION_PERIOD = 100;
   public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;


   public static final String BERRY_ID_PREFIX = "berry  -- ";
   public static final int BERRY_CORRUPT_MIN = 20000;
   public static final int BERRY_CORRUPT_MAX = 30000;
   public static final int BERRY_REACH = 1;

   public static final String FOX_ID ="fox";
   public static final int FOX_ACTION_PERIOD = 4;
   public static final int FOX_ANIMATION_PERIOD = 100;

   public static final String CAT_ID ="cat";
   public static final int CAT_ACTION_PERIOD = 4;
   public static final int CAT_ANIMATION_PERIOD = 100;

   public static final String DIRT_ID = "dirt";

   public Action(ActionKind kind, Entity entity, WorldModel world,
                 ImageStore imageStore, int repeatCount) {
      this.kind = kind;
      this.entity = entity;
      this.world = world;
      this.imageStore = imageStore;
      this.repeatCount = repeatCount;
   }

   public static Action createAnimationAction(Entity entity, int repeatCount) {
      return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
   }

   public static Action createActivityAction(Entity entity, WorldModel world,
                                             ImageStore imageStore) {
      return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
   }

//   public static Entity createAtlantis(String id, Point position,
//                                       List<PImage> images) {
//      return new Entity(EntityKind.ATLANTIS, id, position, images,
//              0, 0, 0, 0);
//   }

   public void executeAction(EventScheduler scheduler) {
      switch (this.kind) {
         case ACTIVITY:
            executeActivityAction(scheduler);
            break;

         case ANIMATION:
            executeAnimationAction(scheduler);
            break;
      }
   }

   private void executeAnimationAction(EventScheduler scheduler) {
      this.entity.nextImage();

      if (this.repeatCount != 1) {
         scheduler.scheduleEvent(this.entity,
                 createAnimationAction(this.entity,
                         Math.max(this.repeatCount - 1, 0)),
                 this.entity.getAnimationPeriod());
      }
   }

   private void executeActivityAction(EventScheduler scheduler) {
      this.entity.executeActivityAction(this.world, this.imageStore, scheduler);
      //todo: implement all classes
//      switch (this.entity.getKind()) {
//         case OCTO_FULL:
//            this.entity.executeOctoFullActivity( this.world,
//                    this.imageStore, scheduler);
//            break;
//
//         case OCTO_NOT_FULL:
//            this.entity.executeOctoNotFullActivity( this.world,
//                    this.imageStore, scheduler);
//            break;
//
//         case FISH:
//            this.entity.executeFishActivity( this.world, this.imageStore,
//                    scheduler);
//            break;
//
//         case CRAB:
//            this.entity.executeCrabActivity( this.world,
//                    this.imageStore, scheduler);
//            break;
//
//         case QUAKE:
//            this.entity.executeQuakeActivity(this.world,
//                    scheduler);
//            break;
//
//         case SGRASS:
//            this.entity.executeSgrassActivity(this.world, this.imageStore,
//                    scheduler);
//            break;
//
//         case ATLANTIS:
//            this.entity.executeAtlantisActivity( this.world,
//                    scheduler);
//            break;
//
//         default:
//            throw new UnsupportedOperationException(
//                    String.format("executeActivityAction not supported for %s",
//                            this.entity.getKind()));
//      }
   }


}