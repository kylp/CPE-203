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

  //      System.out.println("A*: " + start + " -> " + end);

        Queue<PriorityPoint> openList = new PriorityQueue<PriorityPoint>();
        List<Point> finalPathList = new ArrayList<>();

        HashMap<Point, Point> previous = new HashMap<>();
        HashMap<Point, Integer> distanceTraveled = new HashMap<>();

        HashSet<Point> closedList = new HashSet<>();


        openList.add(new PriorityPoint(dist(start,end), start));
        distanceTraveled.put(start, 0);

        Point realEnd = null;

        while (!openList.isEmpty()){
            PriorityPoint current = openList.poll();
//            System.out.println("A*: check " + current.point);
            if(withinReach.test(current.point, end)){
                realEnd = current.point;
                break;
            }
            List<Point> neighbors =
                    potentialNeighbors.apply(current.point)
                            .filter(p -> !closedList.contains(p))
                            .filter(canPassThrough.or(end::equals))
                            .collect(Collectors.toList());
            for (Point p : neighbors) {
                distanceTraveled.put(p, distanceTraveled.get(current.point)+1);

                PriorityPoint pp = new PriorityPoint(dist(p, end)+distanceTraveled.get(p), p);
                openList.add(pp);
                closedList.add(p);
                previous.put(p, current.point);
            }
        }
        if (realEnd != null){
            Point current = realEnd;
            finalPathList.add(current);

            while (true) {
                current = previous.get(current);
                if (!current.equals(start)) {
                    finalPathList.add(current);
                } else {
                    break;
                }
            }

            Collections.reverse(finalPathList);

          //  System.out.println("Found Path: " + finalPathList);
        }
//        else {
//            finalPathList.add(start);
//        }
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
