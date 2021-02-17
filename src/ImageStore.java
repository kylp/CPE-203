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
            case Action.OCTO_KEY:
               return parseOcto(properties, world);
            case Action.OBSTACLE_KEY:
               return parseObstacle(properties, world);
            case Action.FISH_KEY:
               return parseFish(properties, world);
            case Action.ATLANTIS_KEY:
               return parseAtlantis(properties, world);
            case Action.SGRASS_KEY:
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
         world.setBackground(pt,
                 new Background(id, getImageList(id)));
      }

      return properties.length == BGND_NUM_PROPERTIES;
   }

   private  boolean parseOcto(String [] properties, WorldModel world)
   {
      if (properties.length == Action.OCTO_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Action.OCTO_COL]),
                 Integer.parseInt(properties[Action.OCTO_ROW]));
         Entity entity = new OctoNotFull(properties[Action.OCTO_ID],
                 Integer.parseInt(properties[Action.OCTO_LIMIT]),
                 pt,
                 Integer.parseInt(properties[Action.OCTO_ACTION_PERIOD]),
                 Integer.parseInt(properties[Action.OCTO_ANIMATION_PERIOD]),
                 getImageList( Action.OCTO_KEY));
         world.tryAddEntity( entity);
      }

      return properties.length == Action.OCTO_NUM_PROPERTIES;
   }

   private  boolean parseObstacle(String [] properties, WorldModel world)
   {
      if (properties.length == Action.OBSTACLE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[Action.OBSTACLE_COL]),
                 Integer.parseInt(properties[Action.OBSTACLE_ROW]));
         Entity entity = new Obstacle(properties[Action.OBSTACLE_ID],
                 pt, getImageList(Action.OBSTACLE_KEY));
         world.tryAddEntity( entity);
      }

      return properties.length == Action.OBSTACLE_NUM_PROPERTIES;
   }

   private  boolean parseFish(String [] properties, WorldModel world)
   {
      if (properties.length == Action.FISH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Action.FISH_COL]),
                 Integer.parseInt(properties[Action.FISH_ROW]));
         Entity entity = new Fish(properties[Action.FISH_ID],
                 pt, Integer.parseInt(properties[Action.FISH_ACTION_PERIOD]),
                 getImageList(Action.FISH_KEY));
         world.tryAddEntity( entity);
      }

      return properties.length == Action.FISH_NUM_PROPERTIES;
   }

   private  boolean parseAtlantis(String [] properties, WorldModel world)
   {
      if (properties.length == Action.ATLANTIS_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Action.ATLANTIS_COL]),
                 Integer.parseInt(properties[Action.ATLANTIS_ROW]));
         Entity entity = new Atlantis(properties[Action.ATLANTIS_ID],
                 pt, getImageList(Action.ATLANTIS_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Action.ATLANTIS_NUM_PROPERTIES;
   }

   private  boolean parseSgrass(String [] properties, WorldModel world
                                     )
   {
      if (properties.length == Action.SGRASS_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[Action.SGRASS_COL]),
                 Integer.parseInt(properties[Action.SGRASS_ROW]));
         Entity entity = new Sgrass(properties[Action.SGRASS_ID],
                 pt,
                 Integer.parseInt(properties[Action.SGRASS_ACTION_PERIOD]),
                 getImageList(Action.SGRASS_KEY));
         world.tryAddEntity(entity);
      }

      return properties.length == Action.SGRASS_NUM_PROPERTIES;
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
