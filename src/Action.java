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


   public static final String OCTO_KEY = "octo";
   public static final int OCTO_NUM_PROPERTIES = 7;
   public static final int OCTO_ID = 1;
   public static final int OCTO_COL = 2;
   public static final int OCTO_ROW = 3;
   public static final int OCTO_LIMIT = 4;
   public static final int OCTO_ACTION_PERIOD = 5;
   public static final int OCTO_ANIMATION_PERIOD = 6;

   public static final String OBSTACLE_KEY = "obstacle";
   public static final int OBSTACLE_NUM_PROPERTIES = 4;
   public static final int OBSTACLE_ID = 1;
   public static final int OBSTACLE_COL = 2;
   public static final int OBSTACLE_ROW = 3;

   public static final String BERRY_KEY = "fish";
   public static final int FISH_NUM_PROPERTIES = 5;
   public static final int FISH_ID = 1;
   public static final int FISH_COL = 2;
   public static final int FISH_ROW = 3;
   public static final int FISH_ACTION_PERIOD = 4;

   public static final String ATLANTIS_KEY = "atlantis";
   public static final int ATLANTIS_NUM_PROPERTIES = 4;
   public static final int ATLANTIS_ID = 1;
   public static final int ATLANTIS_COL = 2;
   public static final int ATLANTIS_ROW = 3;
   public static final int ATLANTIS_ANIMATION_PERIOD = 70;
   public static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;

   public static final String SGRASS_KEY = "seaGrass";
   public static final int SGRASS_NUM_PROPERTIES = 5;
   public static final int SGRASS_ID = 1;
   public static final int SGRASS_COL = 2;
   public static final int SGRASS_ROW = 3;
   public static final int SGRASS_ACTION_PERIOD = 4;

   public static final String WOLF_KEY = "crab";
   public static final String WOLF_ID_SUFFIX = " -- crab";
   public static final int WOLF_PERIOD_SCALE = 4;
   public static final int WOLF_ANIMATION_MIN = 50;
   public static final int WOLF_ANIMATION_MAX = 150;

   public static final String QUAKE_KEY = "quake";
   public static final String EXPLO_ID = "quake";
   public static final int EXPLO_ACTION_PERIOD = 1100;
   public static final int EXPLO_ANIMATION_PERIOD = 100;
   public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;


   public static final String BERRY_ID_PREFIX = "fish -- ";
   public static final int BERRY_CORRUPT_MIN = 20000;
   public static final int BERRY_CORRUPT_MAX = 30000;
   public static final int FISH_REACH = 1;

   public static final String CAT_ID ="cat";
   public static final int CAT_ACTION_PERIOD = 4;
   public static final int CAT_ANIMATION_PERIOD = 100;

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