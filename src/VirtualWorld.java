import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet {
   public static final int TIMER_ACTION_PERIOD = 100;

   public static final int VIEW_WIDTH = 640;
   public static final int VIEW_HEIGHT = 480;
   public static final int TILE_WIDTH = 32;
   public static final int TILE_HEIGHT = 32;
   public static final int WORLD_WIDTH_SCALE = 2;
   public static final int WORLD_HEIGHT_SCALE = 2;

   public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   public static final String IMAGE_LIST_FILE_NAME = "imagelist";
   public static final String DEFAULT_IMAGE_NAME = "background_default";
   public static final int DEFAULT_IMAGE_COLOR = 0x808080;

   public static final String LOAD_FILE_NAME = "world.sav";

   public static final String FAST_FLAG = "-fast";
   public static final String FASTER_FLAG = "-faster";
   public static final String FASTEST_FLAG = "-fastest";
   public static final double FAST_SCALE = 0.5;
   public static final double FASTER_SCALE = 0.25;
   public static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private Cat theCat = null;

   private long next_time;


   public void settings() {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup() {
      this.imageStore = new ImageStore(
              createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
              createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
              TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw() {
      long time = System.currentTimeMillis();
      if (time >= next_time) {
         scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      view.drawViewport();
   }
   public void mousePressed(){
      //TODO: create new entity,


      Point spawnPoint = view.mouseToWorld(mouseX,mouseY);
      Point berryPoint = new Point (spawnPoint.x + 1, spawnPoint.y);
      if (theCat == null && Entity.canPassThrough(world).test(spawnPoint)) {
         theCat = new Cat(spawnPoint, imageStore.getImageList(Action.CAT_ID), new AStarPathingStrategy());
         world.addEntity(theCat);
         scheduler.scheduleActions(theCat, world, imageStore);
      }
      PathingStrategy path = new AStarPathingStrategy();
      if(Entity.canPassThrough(world).test(berryPoint)) {
         Entity berry = new Berry(Action.BERRY_ID_PREFIX + Action.CAT_ID,
                 berryPoint, Action.BERRY_CORRUPT_MIN +
                 Entity.rand.nextInt(Action.BERRY_CORRUPT_MAX - Action.BERRY_CORRUPT_MIN),
                 imageStore.getImageList(Action.BERRY_KEY), path);
         world.addEntity(berry);
         scheduler.scheduleActions(berry, world, imageStore);
      }
   }

   public void keyPressed() {
      if (key == CODED) {
         int dx = 0;
         int dy = 0;

         switch (keyCode) {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         view.shiftView(dx, dy);
         if (theCat != null) {
            Point oldPos = theCat.getPosition();
            Point newPos = new Point(oldPos.x + dx, oldPos.y + dy);
            if (world.isOccupied(newPos)) {
               Entity blocker = world.getOccupant(newPos).get();
               if (blocker instanceof SpinBirb) {
                  world.removeEntity(blocker);
                  world.moveEntity(theCat, newPos);
               }
            } else {
               world.moveEntity(theCat, newPos);
            }
         }
      }
   }

   private Background createDefaultBackground(ImageStore imageStore) {
      return new Background(DEFAULT_IMAGE_NAME,
              imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static PImage createImageColored(int width, int height, int color) {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++) {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private void loadImages(String filename, ImageStore imageStore,
                           PApplet screen) {
      try {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      } catch (FileNotFoundException e) {
         System.err.println(e.getMessage());
      }
   }

   private void loadWorld(WorldModel world, String filename,
                         ImageStore imageStore) {
      try {
         Scanner in = new Scanner(new File(filename));
         imageStore.load(in, world);
      } catch (FileNotFoundException e) {
         System.err.println(e.getMessage());
      }
   }

   private void scheduleActions(WorldModel world,
                               EventScheduler scheduler, ImageStore imageStore) {
      for (Entity entity : world.getEntities()) {
         //Only start actions for entities that include action (not those with just animations)
         if (entity.getActionPeriod() > 0)
            scheduler.scheduleActions(entity, world, imageStore);
      }
   }

   private static void parseCommandLine(String[] args) {
      for (String arg : args) {
         switch (arg) {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String[] args) {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }

}
