import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {

        int g;
        int h;
        int f;
        Queue<Point> openList = new PriorityQueue<Point>();
        List<Point> closedList = new ArrayList<>();
        List<Point> finalPathList = new ArrayList<>();

        openList = CARDINAL_NEIGHBORS.apply(start).collect(Collectors.toCollection((PriorityQueue::new)));

        Stream<Point> neighbors = CARDINAL_NEIGHBORS.apply(start);








        //step 1, add current neighbors and point to open list
        //step 2, compute f, g and h values of all points in openList
        //step 3, take the next point that has the lowest f value and make it current
        //step 4, repeat process until the current point is within reach of the goal


    }

    public double dist(Point point, Point goal){
        return Math.sqrt((goal.y - point.y) ^2 + (goal.x - point.x)^2);
    }

}
