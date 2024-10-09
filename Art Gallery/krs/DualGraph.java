package krs;

import java.awt.Point;
import java.util.*;

public class DualGraph {
    public ArrayList<DCEL> dcelList;
    public ArrayList<Edge> partitionDiagonalsList;
    public ArrayList<LineSegment> dualEdgeList;
    public HashMap<Face, ArrayList<Face>> adjFaceMap;
    public ArrayList<Vertex> dualVertexArray;
    public TreeMap<Face, Integer> adjCountMap;
    public Face outerFace;
    public Face stFace;
    public HashMap<Point, Integer> clr;
    public int[] ct = new int[4];
    public int minc;

    public void generate() {
        dualEdgeList = new ArrayList<>();
        adjFaceMap = new HashMap<>();
        dualVertexArray = new ArrayList<>();
        adjCountMap = new TreeMap<>();

        for (DCEL dcel : dcelList) {
            for (Face face : dcel.faces) {
                if (face.getNumVertices() == dcel.vertices.size() && face.edge.rFace == face) {
                    outerFace = face;
                    continue;
                }
                dualVertexArray.add(face.getMidPoint());
                adjFaceMap.put(face, new ArrayList<>());
                stFace = face;
                adjCountMap.put(face, 0);
            }

            for (Edge edge : dcel.edges) {
                if (edge.rFace != outerFace) {
                    Vertex v1 = edge.rFace.getMidPoint();
                    Vertex v2 = edge.lFace.getMidPoint();
                    dualEdgeList.add(new LineSegment(v1.toPoint(), v2.toPoint()));

                    adjFaceMap.get(edge.rFace).add(edge.lFace);
                    adjFaceMap.get(edge.lFace).add(edge.rFace);
                }
            }
        }

        Face face1 = null, face2 = null;
        for (Edge diag : partitionDiagonalsList) {
            face1 = null;
            face2 = null;
            for (DCEL dcel : dcelList) {
                if (dcel.isPolygonEdge(diag) && face1 == null) {
                    face1 = dcel.isEdgeReturn(diag).lFace;
                } else if (dcel.isPolygonEdge(diag) && face2 == null) {
                    face2 = dcel.isEdgeReturn(diag).lFace;
                } else if (dcel.isPolygonEdge(diag)) {
                    System.out.println("Not Possible");
                }
            }

            Vertex v1 = face1.getMidPoint();
            Vertex v2 = face2.getMidPoint();
            dualEdgeList.add(new LineSegment(v1.toPoint(), v2.toPoint()));

            adjFaceMap.get(face1).add(face2);
            adjFaceMap.get(face2).add(face1);
        }
    }

    public void solve() {
        clr = new HashMap<>();

        for (DCEL dcel : dcelList) {
            for (Vertex ver : dcel.vertices) {
                Point v = ver.toPoint();
                clr.put(v, 0);
            }
        }

        ct = new int[4];
        ArrayList<Vertex> vList = stFace.getVertices();
        int i = 1;
        for (Vertex ver : vList) {
            Point v = ver.toPoint();
            clr.put(v, i);
            ct[i]++;
            i++;
        }
        dfs(stFace, null);

        int min = Integer.MAX_VALUE;
        for (int j = 1; j <= 3; j++) {
            if (ct[j] < min) {
                min = ct[j];
                minc = j;
            }
        }
    }

    private void dfs(Face face, Face par) {
        Point rem = null;
        Boolean b1, b2, b3;
        for (Face ftemp : adjFaceMap.get(face)) {
            if (ftemp != par) {
                ArrayList<Vertex> vList = ftemp.getVertices();
                b1 = false;
                b2 = false;
                b3 = false;
                for (Vertex ver : vList) {
                    Point v = ver.toPoint();
                    if (clr.get(v) == 0)
                        rem = v;
                    else if (clr.get(v) == 1)
                        b1 = true;
                    else if (clr.get(v) == 2)
                        b2 = true;
                    else
                        b3 = true;
                }
                if (!b1) {
                    clr.put(rem, 1);
                    ct[1]++;
                }
                if (!b2) {
                    clr.put(rem, 2);
                    ct[2]++;
                }
                if (!b3) {
                    clr.put(rem, 3);
                    ct[3]++;
                }

                dfs(ftemp, face);
            }
        }
    }
}
