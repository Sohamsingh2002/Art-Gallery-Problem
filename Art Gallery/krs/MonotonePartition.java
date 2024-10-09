package krs;

import java.awt.Point;
import java.util.*;

public class MonotonePartition {
    public DCEL dcel;
    public ArrayList<LineSegment> monotonepartLines;
    public ArrayList<Edge> monotonePartDiag;

    public MonotonePartition() {
        monotonepartLines = new ArrayList<>();
        monotonePartDiag = new ArrayList<>();
    }

    public void generate() {
        monotonepartLines.clear();
        monotonePartDiag.clear();

        TreeSet<Edge> SList = new TreeSet<>();
        TreeMap<Edge, Vertex> helper = new TreeMap<>();
        for (Vertex v : dcel.vertices) {
            if (v.type == vType.start) {
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.end) {
                Edge eprev = v.incidentEdge.prevEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);
            } else if (v.type == vType.split) {
                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                addEdge(v, helper.get(prev));
                helper.put(prev, v);
                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.merge) {
                Edge eprev = v.incidentEdge.prevEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                if (helper.get(prev).type == vType.merge) {
                    addEdge(v, helper.get(prev));
                }
                helper.put(prev, v);
            } else if (v.type == vType.regL) {
                Edge eprev = v.incidentEdge.prevEdge;
                if (helper.get(eprev).type == vType.merge) {
                    addEdge(v, helper.get(eprev));
                }
                helper.remove(eprev);
                SList.remove(eprev);

                SList.add(v.incidentEdge);
                helper.put(v.incidentEdge, v);
            } else if (v.type == vType.regR) {
                Edge dummyEdge = new Edge(v, v);
                Edge prev = SList.floor(dummyEdge);
                if (helper.get(prev).type == vType.merge) {
                    addEdge(v, helper.get(prev));
                }
                helper.put(prev, v);
            } else {
                Edge b1 = v.incidentEdge;
                Edge b2 = v.incidentEdge.prevEdge;
                Edge eprev = null;
                if (b1.dest.coord.y == b1.origin.coord.y) {
                    eprev = b2;
                    if (SList.contains(eprev)) {
                        if (helper.get(eprev).type == vType.merge) {
                            addEdge(v, helper.get(eprev));
                        }
                        helper.remove(eprev);
                        SList.remove(eprev);
                    }
                } else {
                    eprev = b1;
                    if (SList.contains(eprev))
                        System.out.println("Not possible");
                    else {
                        SList.add(eprev);
                        helper.put(eprev, v);
                    }
                }
            }
        }
        dcel.addEdges(monotonePartDiag);
    }

    public void printAllFaces() {
        System.out.println("Total Faces: " + dcel.faces.size());
        for (Face face : dcel.faces) {
            Edge edge = face.edge;
            System.out.println("\n Face from " + edge + ":\n");
            if (face == edge.lFace) {
                System.out.println(edge.origin + ", ");
                edge = edge.prevEdge;
            } else {
                System.out.println(edge.dest + ", ");
                edge = edge.nextEdge;
            }
            while (edge != face.edge) {
                if (face == edge.lFace) {
                    System.out.println(edge.origin + ", ");
                    edge = edge.prevEdge;
                } else {
                    System.out.println(edge.dest + ", ");
                    edge = edge.nextEdge;
                }
            }
        }
    }

    public ArrayList<DCEL> getSeparateDCEL() {
        ArrayList<DCEL> dcelArrayList = new ArrayList<>();
        int excount = 0;
        for (Face face : dcel.faces) {
            ArrayList<Point> pointList = new ArrayList<>();
            Edge edge = face.edge;
            if (face == edge.lFace) {
                pointList.add(edge.origin.toPoint());
                edge = edge.prevEdge;
            } else {
                pointList.add(edge.dest.toPoint());
                edge = edge.nextEdge;
            }
            while (edge != face.edge) {
                if (face == edge.lFace) {
                    pointList.add(edge.origin.toPoint());
                    edge = edge.prevEdge;
                } else {
                    pointList.add(edge.dest.toPoint());
                    edge = edge.nextEdge;
                }
            }
            if (pointList.size() == dcel.vertices.size() && excount == 0) {
                excount++;
                continue;
            }
            DCEL dcel = new DCEL();
            PolygonGen polyGen = new PolygonGen();
            polyGen.dcel = dcel;
            polyGen.pointList = pointList;
            polyGen.generatePolygon2();
            dcelArrayList.add(dcel);
        }
        return dcelArrayList;
    }

    private void addEdge(Vertex v1, Vertex v2) {
        monotonepartLines.add(new LineSegment(v1.toPoint(), v2.toPoint()));
        monotonePartDiag.add(new Edge(v1, v2));
    }

    public DCEL mergePartitions(ArrayList<Edge> partitionDiagonals, ArrayList<DCEL> dcelArrayList) {
        for (Edge diag : partitionDiagonals) {
            DCEL dcel1 = null, dcel2 = null;
            for (DCEL dcel : dcelArrayList) {
                if (dcel.isPolygonEdge(diag) && dcel1 == null) {
                    dcel1 = dcel;
                } else if (dcel.isPolygonEdge(diag) && dcel2 == null) {
                    dcel2 = dcel;
                } else if (dcel.isPolygonEdge(diag)) {
                    System.out.println("Not Possible");
                }
            }
            DCEL merged = merge(diag, dcel1, dcel2);
            dcelArrayList.remove(dcel1);
            dcelArrayList.remove(dcel2);
            dcelArrayList.add(merged);
        }
        if (dcelArrayList.size() > 1) {
            System.out.println("Something Wrong");
        }
        return dcelArrayList.get(0);
    }

    public DCEL merge(Edge diag, DCEL dcel1, DCEL dcel2) {
        DCEL dcel = new DCEL();
        dcel.edges = (TreeSet<Edge>) dcel1.edges.clone();
        dcel.vertices = (TreeSet<Vertex>) dcel1.vertices.clone();

        for (Edge edge : dcel2.edges) {
            dcel.addEdge(edge);
        }
        for (Vertex vertex : dcel2.vertices) {
            dcel.vertices.add(vertex);
        }

        Edge edge1 = dcel1.getSameEdge(diag);
        Edge edge2 = dcel2.getSameEdge(diag);

        Face of1 = null;
        Face of2 = null;

        if (dcel1.vertices.size() == 3) {
            if (dcel1.faces.first().edge.lFace == dcel1.faces.first()) {
                dcel.faces.add(dcel1.faces.first());
                of1 = dcel1.faces.last();
            } else {
                dcel.faces.add(dcel1.faces.last());
                of1 = dcel1.faces.first();
            }
        } else {
            for (Face face : dcel1.faces) {
                int fnumV = face.getNumVertices();
                if (fnumV != dcel1.vertices.size()) {
                    dcel.faces.add(face);
                } else {
                    of1 = face;
                }
            }
        }

        if (dcel2.vertices.size() == 3) {
            if (dcel2.faces.first().edge.lFace == dcel2.faces.first()) {
                dcel.faces.add(dcel2.faces.first());
                of2 = dcel2.faces.last();
            } else {
                dcel.faces.add(dcel2.faces.last());
                of2 = dcel2.faces.first();
            }
        } else {
            for (Face face : dcel2.faces) {
                int fnumV = face.getNumVertices();
                if (fnumV != dcel2.vertices.size()) {
                    dcel.faces.add(face);
                } else {
                    of2 = face;
                }
            }
        }

        dcel.faces.add(of1);
        of1.edge = edge1.nextEdge;

        Edge cur = edge2;
        if (cur.lFace == of2) {
            cur.lFace = of1;
            cur = cur.prevEdge;
        } else {
            cur.rFace = of1;
            cur = cur.nextEdge;
        }
        Edge e2first = cur;
        Edge e2last = null;
        while (cur != edge2) {
            e2last = cur;
            if (cur.lFace == of2) {
                cur.lFace = of1;
                cur = cur.prevEdge;
            } else {
                cur.rFace = of1;
                cur = cur.nextEdge;
            }
        }

        e2first.prevEdge = edge1.prevEdge;
        edge1.prevEdge = e2first;
        e2last.nextEdge = edge1;
        edge1.lFace = of1;

        return dcel;
    }
}
