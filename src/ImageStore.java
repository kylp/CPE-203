import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

/*
ImageStore: to ideally keep track of the images used in our virtual world
 */

final class ImageStore
{
   private static final int PROPERTY_KEY = 0;

   private final Map<String, List<PImage>> images;
   private final List<PImage> defaultImages;

   private static final String BGND_KEY = "background";
   private static final int BGND_NUM_PROPERTIES = 4;
   private static final int BGND_ID = 1;
   private static final int BGND_COL = 2;
   private static final int BGND_ROW = 3;

   private static final int COLOR_MASK = 0xffffff;
   private static final int KEYED_IMAGE_MIN = 5;
   private static final int KEYED_RED_IDX = 2;
   private static final int KEYED_GREEN_IDX = 3;
   private static final int KEYED_BLUE_IDX = 4;

   public ImageStore(PImage defaultImage)
   {
      this.images = new HashMap<>();
      defaultImages = new LinkedList<>();
      defaultImages.add(defaultImage);
   }


   public  void load(Scanner in, WorldModel world)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }

   private boolean processLine(String line, WorldModel world)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return parseBackground(properties, world);
            case Action.BIRB_KEY:
               return parseOcto(properties, world);
            case Action.STUMP_KEY:
               return parseObstacle(properties, world);
            case Action.BERRY_KEY:
               return parseFish(properties, world);
            case Action.HOUSE_KEY:
               return parseAtlantis(properties, world);
            case Action.BUSH_KEY:
               return parseSgrass(properties, world);
         }
      }

      return false;
   }
   public  List<PImage> getImageList(String key)
   {
      return this.images.getOrDefault(key, this.defaultImages);
   }

   public  void loadImages(Scanner in,  PApplet screen)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            processImageLine(this.images, in.nextLine(), screen);
         }
         catch (NumberFormatException e)
         {
            System.out.println(String.format("Image format error on line %d",
                    lineNumber));
         }
         lineNumber++;
      }
   }

   private  boolean parseBackground(String [] properties, WorldModel world)
   {
      if (properties.length == BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                 Integer.parseInt(properties[BGND_ROW]));
         String id = properties[BGND_ID];
         world. setBackground(pt,
                 new Background(id, getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   private  boolean parseOcto(String [] properties, WorldModel world)
   {
      if (properties.length == Action.BIRB_NUM_PROPERTIES)
      {
         PathingStrategy path = new SingleStepPathingStrategy();
         Point pt = new Point(Integer.parseInt(properties[Action.BIRB_COL]),
                 Integer.parseInt(properties[Action.BIRB_ROW]));
         Entity entity = new SpinBirbNotFull(properties[Action.BIRB_ID],
                 Integer.parseInt(properties[Action.BIRB_LIMIT]),
                 pt,
                 Integer.parseInt(properties[Action.BIRB_ACTION_PERIOD]),
                 Integer.parseInt(properties[Action.BIRB_ANIMATION_PERIOD]),
                 getImageList( Action.BIRB_KEY), path);
         world.tryAddEntity( entity);
      }

      return properties.length == Action.BIRB_NUM_PROPERTIES;
   }

   private  boolean parseObstacle(String [] properties, WorldModel world)
   {
      PathingStrategy path = new SingleStepPathingStrategy();
      if (properties.length == Action.STUMP_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[Action.STUMP_COL]),
                 Integer.parseInt(properties[Action.STUMP_ROW]));
         Entity entity = new Stump(properties[Action.STUMP_ID],
                 pt, getImageList(Action.STUMP_KEY), path);
         world.tryAddEntity( entity);
      }

      return properties.length == Action.STUMP_NUM_PROPERTIES;
   }

   private  boolean parseFish(String [] properties, WorldModel world)
   {
      if (properties.length == Action.BERRY_NUM_PROPERTIES)
      {
         PathingStrategy path = new SingleStepPathingStrategy();

         Point pt = new Point(Integer.parseInt(properties[Action.BERRY_COL]),
                 Integer.parseInt(properties[Action.BERRY_ROW]));
         Entity entity = new Berry(properties[Action.BERRY_ID],
                 pt, Integer.parseInt(properties[Action.BERRY_ACTION_PERIOD]),
                 getImageList(Action.BERRY_KEY), path);
         world.tryAddEntity( entity);
      }

      return properties.length == Action.BERRY_NUM_PROPERTIES;
   }

   private  boolean parseAtlantis(String [] properties, WorldModel world)
   {
      if (properties.length == Action.HOUSE_NUM_PROPERTIES)
      {
         PathingStrategy path = new SingleStepPathingStrategy();
         Point pt = new Point(Integer.parseInt(properties[Action.HOUSE_COL]),
                 Integer.parseInt(properties[Action.HOUSE_ROW]));
         Entity entity = new House(properties[Action.HOUSE_ID],
                 pt, getImageList(Action.HOUSE_KEY), path);
         world.tryAddEntity(entity);
      }

      return properties.length == Action.HOUSE_NUM_PROPERTIES;
   }

   private  boolean parseSgrass(String [] properties, WorldModel world
                                     )
   {
      if (properties.length == Action.BUSH_NUM_PROPERTIES)
      {
         PathingStrategy path = new SingleStepPathingStrategy();
         Point pt = new Point(Integer.parseInt(properties[Action.BUSH_COL]),
                 Integer.parseInt(properties[Action.BUSH_ROW]));
         Entity entity = new Bush(properties[Action.BUSH_ID],
                 pt,
                 Integer.parseInt(properties[Action.BUSH_ACTION_PERIOD]),
                 getImageList(Action.BUSH_KEY), path);
         world.tryAddEntity(entity);
      }

      return properties.length == Action.BUSH_NUM_PROPERTIES;
   }
   private  void processImageLine(Map<String, List<PImage>> images,
                                       String line, PApplet screen)
   {
      String[] attrs = line.split("\\s");
      if (attrs.length >= 2)
      {
         String key = attrs[0];
         PImage img = screen.loadImage(attrs[1]);
         if (img != null && img.width != -1)
         {
            List<PImage> imgs = getImages(images, key);
            imgs.add(img);

            if (attrs.length >= KEYED_IMAGE_MIN)
            {
               int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
               int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
               int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
               setAlpha(img, screen.color(r, g, b), 0);
            }
         }
      }
   }

   private  List<PImage> getImages(Map<String, List<PImage>> images,
                                        String key)
   {
      List<PImage> imgs = images.get(key);
      if (imgs == null)
      {
         imgs = new LinkedList<>();
         images.put(key, imgs);
      }
      return imgs;
   }

   /*
     Called with color for which alpha should be set and alpha value.
     setAlpha(img, color(255, 255, 255), 0));
   */
   private  void setAlpha(PImage img, int maskColor, int alpha)
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         if ((img.pixels[i] & COLOR_MASK) == nonAlpha)
         {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }
   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background) entity).getImages()
                 .get(((Background) entity).getImageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity) entity).getImages().get(((Entity) entity).getImageIndex());
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }

}
