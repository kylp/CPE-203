import java.util.*;

/*
EventScheduler: ideally our way of controlling what happens in our virtual world
 */

final class EventScheduler
{
   private final PriorityQueue<Event> eventQueue;
   private final Map<Entity, List<Event>> pendingEvents;
   private final double timeScale;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * this.timeScale);
      Event event = new Event(action, time, entity);

      this.eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = this.pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      this.pendingEvents.put(entity, pending);
   }

   public  void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = this.pendingEvents.remove(entity);

      if (pending != null)
      {
         for (Event event : pending)
         {
            this.eventQueue.remove(event);
         }
      }
   }

   private void removePendingEvent(Event event)
   {
      List<Event> pending = this.pendingEvents.get(event.getEntity());

      if (pending != null)
      {
         pending.remove(event);
      }
   }

   public  void updateOnTime(long time)
   {
      while (!this.eventQueue.isEmpty() &&
              this.eventQueue.peek().getTime() < time)
      {
         Event next = this.eventQueue.poll();

         removePendingEvent(next);

         next.getAction().executeAction(this);
      }
   }
   public  void scheduleActions(Entity entity, WorldModel world, ImageStore imageStore) {
      switch (entity.getKind()) {
         case BIRB_FULL:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            scheduleEvent(entity, Action.createAnimationAction(entity, 0),
                    entity.getAnimationPeriod());
            break;

         case OCTO_NOT_FULL:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            scheduleEvent(entity,
                    Action.createAnimationAction(entity, 0), entity.getAnimationPeriod());
            break;

         case BERRY:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            break;

         case WOLF:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            scheduleEvent(entity,
                    Action.createAnimationAction(entity, 0), entity.getAnimationPeriod());
            break;

         case EXPLO:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            scheduleEvent(entity,
                    Action.createAnimationAction(entity, Action.QUAKE_ANIMATION_REPEAT_COUNT),
                    entity.getAnimationPeriod());
            break;

         case BUSH:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            break;
         case ATLANTIS:
            scheduleEvent(entity,
                    Action.createAnimationAction(entity, Action.ATLANTIS_ANIMATION_REPEAT_COUNT),
                    entity.getAnimationPeriod());
            break;

         case CAT:
            scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.getActionPeriod());
            scheduleEvent(entity,
                    Action.createAnimationAction(entity, 0),
                    entity.getAnimationPeriod());
            break;

         default:
      }
   }


}
