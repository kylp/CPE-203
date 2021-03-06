import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   private final int numRows;
   private final int numCols;
   private final Background[][] background;
   private final Entity[][] occupancy;
   private final Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }
   public void addEntity( Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         this.entities.add(entity);
      }
   }

   public void moveEntity( Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds( pos) && !pos.equals(oldPos))
      {
         setOccupancyCell( oldPos, null);
         removeEntityAt( pos);
         setOccupancyCell( pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity( Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   private  void removeEntityAt( Point pos)
   {
      if (this.withinBounds( pos)
              && getOccupancyCell( pos) != null)
      {
         Entity entity = getOccupancyCell( pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         this.entities.remove(entity);
         setOccupancyCell( pos, null);
      }
   }
   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }
   public void setBackground( Point pos,
                                    Background background)
   {
      if (this.withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public Optional<Entity> getOccupant( Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   private Entity getOccupancyCell( Point pos)
   {
      return this.occupancy[pos.getY()][pos.getX()];
   }

   private void setOccupancyCell( Point pos,
                                       Entity entity)
   {
      this.occupancy[pos.getY()][pos.getX()] = entity;
   }

   private Background getBackgroundCell( Point pos)
   {
      return this.background[pos.getY()][pos.getX()];
   }

   private void setBackgroundCell( Point pos,
                                        Background background)
   {
      this.background[pos.getY()][pos.getX()] = background;
   }

   boolean withinBounds(Point pos)
   {
      return pos.getY() >= 0 && pos.getY() < this.numRows &&
              pos.getX() >= 0 && pos.getX() < this.numCols;
   }

   public boolean isOccupied( Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }
   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds( pos))
      {
         return Optional.of(ImageStore.getCurrentImage(getBackgroundCell( pos)));
      }
      else
      {
         return Optional.empty();
      }
   }
   public Optional<Entity> findNearest( Point pos,
                                              EntityKind kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : this.entities)
      {
         if (entity.getKind() == kind)
         {
            ofType.add(entity);
         }
      }

      return Entity.nearestEntity(ofType, pos);
   }
   public  Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -Action.BERRY_REACH; dy <= Action.BERRY_REACH; dy++)
      {
         for (int dx = -Action.BERRY_REACH; dx <= Action.BERRY_REACH; dx++)
         {
            Point newPt = new Point(pos.getX() + dx, pos.getY() + dy);
            if (this.withinBounds( newPt) &&
                    !this.isOccupied( newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }


   public Set<Entity> getEntities() {
      return entities;
   }
}
