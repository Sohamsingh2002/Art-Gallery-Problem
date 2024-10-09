package krs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

enum vType {
    split, merge, start, end, regL, regR;
}

public class DCEL {
    public TreeSet<Vertex> vertices;
    public TreeSet<Edge> edges;
    public TreeSet<Face> faces;

    public DCEL() {
        vertices = new TreeSet<>();
        edges = new TreeSet<>();
        faces = new TreeSet<>();
    }

    public boolean isPolygonEdge(Vertex a, Vertex b) {
        Edge t = new Edge(a, b);
        for (Edge e : this.edges) {
            if (e.compareTo(t) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPolygonEdge(Edge edge) {
        for (Edge e : this.edges) {
            if (e.compareTo(edge) == 0) return true;
        }
        return false;
    }

    public Edge isEdgeReturn(Edge edge) {
        for (Edge e : this.edges) {
            if (e.compareTo(edge) == 0) return e;
        }
        return null;
    }

    public boolean addEdge(Edge edge) {
        if (isPolygonEdge(edge)) {
            return false;
        } else this.edges.add(edge);
        return true;
    }

    public boolean addEdges(ArrayList<Edge> diagonals) {
        HashSet<Vertex> isDiag = new HashSet<>();
        for (Edge diag : diagonals) {
            this.edges.add(diag);
            Face nFace = new Face(diag);
            nFace.edge = diag;
            this.faces.add(nFace);

            if (!isDiag.contains(diag.origin)) {
            } else if (!isDiag.contains(diag.dest)) {
                Vertex temp = diag.origin;
                diag.origin = diag.dest;
                diag.dest = temp;
            } else {
                System.out.println("NOT POSSIBLE");
                return false;
            }

            isDiag.add(diag.origin);
            isDiag.add(diag.dest);
            Edge init = diag.origin.incidentEdge.prevEdge;
            Face oFace = init.lFace;
            while (init.dest != diag.dest && init.origin != diag.dest) {
                if (oFace == init.lFace) {
                    init.lFace = nFace;
                    init = init.prevEdge;
                } else {
                    init.rFace = nFace;
                    init = init.nextEdge;
                }
            }
            if (oFace == init.lFace) {
                Edge temp = init.prevEdge;
                init.lFace = nFace;
                init.prevEdge = diag;
                init = temp;
            } else {
                Edge temp = init.nextEdge;
                init.rFace = nFace;
                init.nextEdge = diag;
                init = temp;
            }
            diag.lFace = nFace;
            diag.rFace = oFace;
            diag.prevEdge = diag.origin.incidentEdge.prevEdge;
            diag.nextEdge = init;
            diag.origin.incidentEdge.prevEdge = diag;
            oFace.edge = init;
        }
        return true;
    }

    public Edge getSameEdge(Edge edge) {
        for (Edge cur : this.edges) {
            if (cur.compareTo(edge) == 0) return cur;
        }
        return null;
    }
}

class Vertex implements Comparable<Vertex> {
    public Point coord;
    public Edge incidentEdge;
    public vType type;

    public Vertex(Point coord, Edge incidentEdge) {
        this.coord = coord;
        this.incidentEdge = incidentEdge;
    }

    public Vertex(java.awt.Point point) {
        this.coord = new Point(point.x, point.y);
    }

    public Vertex(int x, int y) {
        this.coord = new Point(x, y);
    }

    public Point getCoord() {
        return coord;
    }

    @Override
    public int compareTo(Vertex arg0) {
        return this.coord.compareTo(arg0.getCoord());
    }

    public String toString() {
        return coord.toString();
    }

    public java.awt.Point toPoint() {
        return new java.awt.Point((int) this.coord.x, (int) this.coord.y);
    }

    public static double slope(Vertex orig, Vertex dest) {
        return Math.atan2(dest.coord.y - orig.coord.y, dest.coord.x - orig.coord.x);
    }

    public boolean isReflex() {
        java.awt.Point v1 = incidentEdge.prevEdge.origin.toPoint();
        java.awt.Point v2 = this.toPoint();
        java.awt.Point v3 = incidentEdge.dest.toPoint();

        double ans = (v1.x * v2.y + v2.x * v3.y + v3.x * v1.y) - (v1.y * v2.x + v2.y * v3.x + v3.y * v1.x);
        return ans < 0;
    }
}

class Edge implements Comparable<Edge> {
    public Vertex origin, dest;
    public Edge nextEdge, prevEdge;
    public Face lFace, rFace;
    public double slope;

    public Edge(Vertex origin, Vertex dest) {
        this.origin = origin;
        this.dest = dest;
    }

    public Edge(Vertex origin, Vertex dest, Edge nextEdge, Edge prevEdge, Face lFace, Face rFace) {
        this.origin = origin;
        this.dest = dest;
        this.nextEdge = nextEdge;
        this.prevEdge = prevEdge;
        this.lFace = lFace;
        this.rFace = rFace;
    }

    public double getSlope() {
        this.slope = Math.atan2(dest.coord.y - origin.coord.y, dest.coord.x - origin.coord.x);
        return this.slope;
    }

    public double getReverseSlope() {
        return Math.atan2(origin.coord.y - dest.coord.y, origin.coord.x - dest.coord.x);
    }

    @Override
    public int compareTo(Edge o) {
        if (this.dest.compareTo(o.dest) == 0 && this.origin.compareTo(o.origin) == 0) return 0;
        if (this.origin.compareTo(o.dest) == 0 && this.dest.compareTo(o.origin) == 0) return 0;
        if (o.origin == this.dest) {
            int mul = 1;
            if (o.dest.coord.y > o.origin.coord.y && this.origin.coord.y > o.origin.coord.y)
                mul = 1;
            else mul = -1;
            if (o.getSlope() > this.getReverseSlope()) return mul;
            else return -mul;
        } else if (o.dest == this.origin) {
            int mul = 1;
            if (o.origin.coord.y > o.dest.coord.y && this.dest.coord.y > o.dest.coord.y)
                mul = -1;
            else mul = 1;
            if (o.getSlope() > this.getReverseSlope()) return -mul;
            else return mul;
        }
        Edge up;
        Edge down;
        int mul = 1;
        if (Math.max(o.dest.coord.y, o.origin.coord.y) > Math.max(this.dest.coord.y, this.origin.coord.y)) {
            up = o;
            down = this;
        } else {
            up = this;
            down = o;
            mul = -1;
        }

        double dmaxx, dmaxy;
        if (down.dest.coord.y < down.origin.coord.y) {
            dmaxx = down.origin.coord.x;
            dmaxy = down.origin.coord.y;
        } else {
            dmaxx = down.dest.coord.x;
            dmaxy = down.dest.coord.y;
        }

        double uminx, uminy;
        if (up.dest.coord.y < up.origin.coord.y) {
            uminx = up.dest.coord.x;
            uminy = up.dest.coord.y;
        } else {
            uminx = up.origin.coord.x;
            uminy = up.origin.coord.y;
        }

        if (dmaxy < uminy) return 1;

        java.awt.Point inter = Trapezoidalization.getYIntersection(up, dmaxy);

        if (inter.x < dmaxx) {
            return mul;
        } else return -mul;
    }

    public String toString() {
        return origin.toString() + "|" + dest.toString();
    }
}

class Face implements Comparable<Face> {
    public Edge edge;

    public Face(Edge edge) {
        this.edge = edge;
    }

    @Override
    public int compareTo(Face o) {
        return 1;
    }

    public int getNumVertices() {
        int ans = 0;
        Edge cur = edge;
        if (cur.lFace == this) {
            cur = cur.prevEdge;
        } else {
            cur = cur.nextEdge;
        }
        ans++;

        while (cur != edge) {
            if (cur.lFace == this) {
                cur = cur.prevEdge;
            } else {
                cur = cur.nextEdge;
            }
            ans++;
        }
        return ans;
    }

    public ArrayList<Vertex> getVertices() {
        ArrayList<Vertex> vList = new ArrayList<>();
        Edge cur = edge;
        if (cur.lFace == this) {
            vList.add(cur.origin);
            cur = cur.prevEdge;
        } else {
            vList.add(cur.dest);
            cur = cur.nextEdge;
        }

        while (cur != edge) {
            if (cur.lFace == this) {
                vList.add(cur.origin);
                cur = cur.prevEdge;
            } else {
                vList.add(cur.dest);
                cur = cur.nextEdge;
            }
        }

        return vList;
    }

    public Vertex getMidPoint() {
        double xsum = 0, ysum = 0;
        int nVert = 0;
        Edge cur = edge;
        if (cur.lFace == this) {
            xsum += cur.origin.coord.x;
            ysum += cur.origin.coord.y;
            cur = cur.prevEdge;
        } else {
            xsum += cur.dest.coord.x;
            ysum += cur.dest.coord.y;
            cur = cur.nextEdge;
        }
        nVert++;

        while (cur != edge) {
            if (cur.lFace == this) {
                xsum += cur.origin.coord.x;
                ysum += cur.origin.coord.y;
                cur = cur.prevEdge;
            } else {
                xsum += cur.dest.coord.x;
                ysum += cur.dest.coord.y;
                cur = cur.nextEdge;
            }
            nVert++;
        }

        return new Vertex((int) xsum / nVert, (int) ysum / nVert);
    }
}
