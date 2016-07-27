package map;

import static java.lang.Double.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import map.tiles.Tile;
import util.Vec2;

public class Pathfinding {

    public static class GridPoint {

        public final int x, y;

        GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        GridPoint(Vec2 v) {
            this((int) (v.x / Tile.RESOLUTION), (int) (v.y / Tile.RESOLUTION));
        }

        public double distanceTo(GridPoint other) {
            return new Vec2(x - other.x, y - other.y).length();
        }

        public Vec2 toVec2() {
            return new Vec2(x, y).add(new Vec2(.5)).multiply(Tile.RESOLUTION);
        }
    }

    private static class PointData implements Comparable {

        GridPoint point;
        PointData parent;
//        Vec2 parentV;
        double distance;
        double priority;

        PointData(GridPoint point) {
            this.point = point;
            distance = POSITIVE_INFINITY;
        }

        @Override
        public int compareTo(Object other) {
            return (int) Math.signum(priority - ((PointData) other).priority);
        }

        @Override
        public boolean equals(Object other) {
            return ((PointData) other).point == point;
        }
    }

    private static boolean blocked(GridPoint gp) {
        return Terrain.terrainVis.terMap[gp.x][gp.y].isSolid();
    }

    public static ArrayList<Vec2> findPath(Vec2 startV, Vec2 goalV, double size) {
        GridPoint start = new GridPoint(startV);
        GridPoint goal = new GridPoint(goalV);
        if (blocked(start) || blocked(goal)) {
            return null;
        }

        PriorityQueue<PointData> open = new PriorityQueue();
        HashSet<GridPoint> closed = new HashSet();
        HashMap<GridPoint, PointData> dataMap = new HashMap();

        PointData startData = new PointData(start);
        startData.distance = 0;
        startData.parent = startData;
//        startData.parentV = startV;

        open.add(startData);
        dataMap.put(start, startData);

        while (!open.isEmpty()) {
//            System.out.println("1");
            PointData pd = open.poll();
            if (pd.point == goal) {
                ArrayList<Vec2> r = new ArrayList();
                r.add(pd.point.toVec2());
//                r.add(pd.parentV);
                while (pd.parent != pd) {
//                    System.out.println("2");
                    pd = pd.parent;
                    r.add(pd.point.toVec2());
                }
                r.add(startV);
                return r;
            }
            closed.add(pd.point);
            for (GridPoint n : neighbors(pd.point, size)) {
                if (!closed.contains(n)) {
                    PointData nd = dataMap.get(n);
                    if (nd == null) {
                        nd = new PointData(n);
                        dataMap.put(n, nd);
                    }
                    if (visible(pd.parent.point.toVec2(), n.toVec2(), size)) {
                        if (pd.parent.distance + pd.parent.point.distanceTo(n) < nd.distance) {
                            nd.parent = pd.parent;
//                            nd.parentV = pd.parentV;
                            nd.distance = pd.parent.distance + pd.parent.point.distanceTo(n);
                            if (open.contains(n)) {
                                open.remove(n);
                            }
                            nd.priority = nd.distance + n.distanceTo(goal);
                            open.add(nd);
                        }
                    } else if (pd.distance + pd.point.distanceTo(n) < nd.distance) {
                        nd.parent = pd;
//                            nd.parentV = pd.point.toVec2();
                        nd.distance = pd.distance + pd.point.distanceTo(n);
                        if (open.contains(n)) {
                            open.remove(n);
                        }
                        nd.priority = nd.distance + n.distanceTo(goal);
                        open.add(nd);
                    }
                }
            }
        }
        return null;
    }

    private static ArrayList<GridPoint> neighbors(GridPoint gp, double size) {
        ArrayList<GridPoint> r = new ArrayList();
        for (int i = gp.x - 1; i <= gp.x + 1; i++) {
            for (int j = gp.y - 1; j <= gp.y + 1; j++) {
                if (i != gp.x || j != gp.y) {
                    if (i >= 0 && i < Terrain.terrain.getWidth() && j >= 0 && j < Terrain.terrain.getHeight()) {
                        if (visible(gp.toVec2(), new GridPoint(i, j).toVec2(), size)) {
                            r.add(new GridPoint(i, j));
                        }
                    }
                }
            }
        }
        return r;
    }

    private static boolean visible(Vec2 v0, Vec2 v1, double size) {
        Vec2 width;
        if (v0.quadrant(v1) % 2 == 1) {
            width = new Vec2(size, -size);
        } else {
            width = new Vec2(-size, size);
        }
//        width = width.multiply(1.1);
//        Vec2 width = v1.subtract(v0).setLength(sc.size * Math.sqrt(2)).normal();
        return visibleVec(v0.add(width), v1.add(width)) && visibleVec(v0.subtract(width), v1.subtract(width));
    }

    private static boolean visibleVec(Vec2 v0, Vec2 v1) {
        double x0 = v0.x / Tile.RESOLUTION;
        double y0 = v0.y / Tile.RESOLUTION;
        double x1 = v1.x / Tile.RESOLUTION;
        double y1 = v1.y / Tile.RESOLUTION;
        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);

        int x = (int) Math.floor(x0);
        int y = (int) Math.floor(y0);

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0) {
            x_inc = 0;
            error = Double.POSITIVE_INFINITY;
        } else if (x1 > x0) {
            x_inc = 1;
            n += Math.floor(x1) - x;
            error = (Math.floor(x0) + 1 - x0) * dy;
        } else {
            x_inc = -1;
            n += x - Math.floor(x1);
            error = (x0 - Math.floor(x0)) * dy;
        }

        if (dy == 0) {
            y_inc = 0;
            error = Double.NEGATIVE_INFINITY;
        } else if (y1 > y0) {
            y_inc = 1;
            n += Math.floor(y1) - y;
            error -= (Math.floor(y0) + 1 - y0) * dx;
        } else {
            y_inc = -1;
            n += y - Math.floor(y1);
            error -= (y0 - Math.floor(y0)) * dx;
        }

        for (; n > 0; --n) {
            if (blocked(new GridPoint(x, y))) {
                return false;
            }

            if (error > 0) {
                y += y_inc;
                error -= dx;
            } else {
                x += x_inc;
                error += dy;
            }
        }
        return true;
    }

    private static boolean visibleInt(GridPoint gp1, GridPoint gp2) {
        int x0 = gp1.x;
        int y0 = gp1.y;
        int x1 = gp2.x;
        int y1 = gp2.y;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n) {
            if (blocked(new GridPoint(x, y))) {
                return false;
            }

            if (error > 0) {
                x += x_inc;
                error -= dy;
            } else {
                y += y_inc;
                error += dx;
            }
        }
        return true;
    }
}
