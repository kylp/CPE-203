import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    static class PriorityPoint implements Comparable<PriorityPoint>{
        double priority;
        Point point;

        public PriorityPoint(double priority, Point point) {
            this.priority = priority;
            this.point = point;
        }

        @Override
        public int compareTo(PriorityPoint o) {
            return Double.compare(priority, o.priority);
        }
    }
    @Override
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors) {


        Queue<PriorityPoint> openList = new PriorityQueue<PriorityPoint>();
        List<Point> finalPathList = new ArrayList<>();

        HashMap<Point, Point> previous = new HashMap<>();
        HashMap<Point, Integer> distanceTraveled = new HashMap<>();

        HashSet<Point> closedList = new HashSet<>();


        openList.add(new PriorityPoint(dist(start,end), start));

        while (!openList.isEmpty()){
            PriorityPoint current = openList.poll();
            if(current.point.equals(end)){
                //TODO: return the final path
                break;
            }
            List<Point> neighbors =
                    CARDINAL_NEIGHBORS.apply(current.point)
                            .filter(p -> !closedList.contains(p))
                            .filter(canPassThrough)
                            .collect(Collectors.toList());
            for (Point p : neighbors) {
                distanceTraveled.put(p, distanceTraveled.get(current.point));

                PriorityPoint pp = new PriorityPoint(dist(p, end)+distanceTraveled.get(p), p);
                openList.add(pp);
                closedList.add(p);
                previous.put(p, current.point);
            }
        }
        if (closedList.contains(end)){
            Point current = end;


            while (!current.equals(end)){
                finalPathList.add(current);
                current = previous.get(current);
            }

            Collections.reverse(finalPathList);
        }
        else {
            finalPathList.add(start);
        }
        return finalPathList;









        //step 1, add current neighbors and point to open list
        //step 2, compute f, g and h values of all points in openList
        //step 3, take the next point that has the lowest f value and make it current
        //step 4, repeat process until the current point is within reach of the goal


    }

    public double dist(Point point, Point goal){
        return Math.sqrt((goal.y - point.y) ^2 + (goal.x - point.x)^2);
    }

}
