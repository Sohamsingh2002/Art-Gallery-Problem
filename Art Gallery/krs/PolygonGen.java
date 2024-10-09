package krs;

import java.awt.Point;
import java.util.*;

public class PolygonGen {

    public DCEL dcel;
    public ArrayList<Point> pointList;
    public ArrayList<Point> circularPointList;

    public void generatePolygon() {
        pointList.sort((o1, o2) -> {
            if (o1.y < o2.y) return 1;
            else if (o2.y < o1.y) return -1;
            else {
                if (o1.x < o2.x) return 1;
                else return -1;
            }
        });

        Point minPoint = pointList.get(0);
        ArrayList<Point> nxtList = (ArrayList<Point>) pointList.clone();
        nxtList.remove(0);
        nxtList.sort((o1, o2) -> {
            double slope1 = Math.atan2(o1.y - minPoint.y, o1.x - minPoint.x);
            if (slope1 == Math.PI)
                slope1 = -Math.PI;
            double slope2 = Math.atan2(o2.y - minPoint.y, o2.x - minPoint.x);
            if (slope2 == Math.PI)
                slope2 = -Math.PI;
            if (slope1 < slope2) return 1;
            else return -1;
        });
        nxtList.add(minPoint);
        Collections.reverse(nxtList);
        circularPointList = (ArrayList<Point>) nxtList.clone();

        Face iFace = new Face(null);
        Face oFace = new Face(null);

        List<Vertex> vList = new ArrayList<Vertex>();
        List<Edge> eList = new ArrayList<Edge>();
        for (Point p : nxtList) {
            Vertex tVertex = new Vertex(krs.Point.toPoint(p), null);
            vList.add(tVertex);
        }

        for (int i = 0; i < vList.size(); i++) {
            if (i != vList.size() - 1) {
                eList.add(new Edge(vList.get(i), vList.get(i + 1), null, null, iFace, oFace));
            } else {
                eList.add(new Edge(vList.get(i), vList.get(0), null, null, iFace, oFace));
            }
        }

        for (int i = 0; i < vList.size(); i++) {
            vList.get(i).incidentEdge = eList.get(i);
        }

        for (int i = 0; i < eList.size(); i++) {
            if (i == 0) {
                eList.get(i).nextEdge = eList.get(i + 1);
                eList.get(i).prevEdge = eList.get(eList.size() - 1);
            } else if (i == eList.size() - 1) {
                eList.get(i).nextEdge = eList.get(0);
                eList.get(i).prevEdge = eList.get(i - 1);
            } else {
                eList.get(i).nextEdge = eList.get(i + 1);
                eList.get(i).prevEdge = eList.get(i - 1);
            }
        }

        iFace.edge = eList.get(0);
        oFace.edge = eList.get(0);

        dcel.edges = new TreeSet<>(eList);
        dcel.vertices = new TreeSet<>(vList);
        dcel.faces = new TreeSet<>();
        dcel.faces.add(iFace);
        dcel.faces.add(oFace);
    }

    public void generatePolygon2() {
        ArrayList<Point> nxtList = (ArrayList<Point>) pointList.clone();
        Collections.reverse(nxtList);
        circularPointList = (ArrayList<Point>) nxtList.clone();

        Face iFace = new Face(null);
        Face oFace = new Face(null);

        List<Vertex> vList = new ArrayList<Vertex>();
        List<Edge> eList = new ArrayList<Edge>();
        for (Point p : nxtList) {
            Vertex tVertex = new Vertex(krs.Point.toPoint(p), null);
            vList.add(tVertex);
        }

        for (int i = 0; i < vList.size(); i++) {
            if (i != vList.size() - 1) {
                eList.add(new Edge(vList.get(i), vList.get(i + 1), null, null, iFace, oFace));
            } else {
                eList.add(new Edge(vList.get(i), vList.get(0), null, null, iFace, oFace));
            }
        }

        for (int i = 0; i < vList.size(); i++) {
            vList.get(i).incidentEdge = eList.get(i);
        }

        for (int i = 0; i < eList.size(); i++) {
            if (i == 0) {
                eList.get(i).nextEdge = eList.get(i + 1);
                eList.get(i).prevEdge = eList.get(eList.size() - 1);
            } else if (i == eList.size() - 1) {
                eList.get(i).nextEdge = eList.get(0);
                eList.get(i).prevEdge = eList.get(i - 1);
            } else {
                eList.get(i).nextEdge = eList.get(i + 1);
                eList.get(i).prevEdge = eList.get(i - 1);
            }
        }

        iFace.edge = eList.get(0);
        oFace.edge = eList.get(0);

        dcel.edges = new TreeSet<>(eList);
        dcel.vertices = new TreeSet<>(vList);
        dcel.faces = new TreeSet<>();
        dcel.faces.add(iFace);
        dcel.faces.add(oFace);
    }
}
