
package krs;

import java.util.ArrayList;
import javax.swing.JPanel;

import java.awt.*;
import java.awt.Point;
import java.util.HashMap;
import java.awt.event.*;
import java.awt.geom.AffineTransform;


public class Visualiser extends JPanel {
    private static DCEL dcel;
    private ArrayList<DCEL> dcelList;
    private ArrayList<Point> pointList;
    private ArrayList<Point> circularPointList;
    private ArrayList<LineSegment> trapezoidalLines;
    private ArrayList<LineSegment> partitionLines;
    private ArrayList<Edge> monotonePartDiag;
    private ArrayList<LineSegment> triangulationLines;
    private HashMap<Point, Integer> colour;
    Boolean finale;
    private PolygonGen polygonGen;
    private Trapezoidalization trapezoidalization;
    private MonotonePartition monotonePartition;
    private DualGraph dualGraph;

    private double scale = 1.0; // Zoom scale
    private int translateX = 0; // Translation on X-axis
    private int translateY = 0; // Translation on Y-axis



    public Visualiser() {
        dcel = new DCEL();
        polygonGen = new PolygonGen();
        trapezoidalization = new Trapezoidalization();
        monotonePartition = new MonotonePartition();
        dualGraph = new DualGraph();

        pointList = new ArrayList<>();
        circularPointList = new ArrayList<>();
        trapezoidalLines = new ArrayList<>();
        partitionLines = new ArrayList<>();
        monotonePartDiag = new ArrayList<>();
        triangulationLines = new ArrayList<>();

        //setting the background black
        setBackground(new Color(73, 74, 71));

        //setting the canvas size
        // setPreferredSize(new Dimension(1200, 800));
    }

    

    public void initPoints(int n) {
        clear();

        int ll = 50;
        int ul = 250;

        for (int i = 0; i < n; i++) {
            int x = ll + (int) (Math.random() * (ul - ll));
            int y = ll + (int) (Math.random() * (ul - ll));
            y = -y;
            pointList.add(new Point(3 * x, 3 * y));
        }
        System.out.println(pointList);
        repaint();
    }

    public void generatePolygon() {
        dcel = new DCEL();
        trapezoidalLines.clear();
        partitionLines.clear();
        triangulationLines.clear();
        polygonGen.pointList = pointList;
        polygonGen.dcel = dcel;
        polygonGen.generatePolygon();
        circularPointList = polygonGen.circularPointList;

        repaint();
    }

    public void trapezoidalization() {
        trapezoidalLines.clear();
        partitionLines.clear();
        trapezoidalization.dcel = dcel;
        trapezoidalization.trapezoidalLines = trapezoidalLines;
        trapezoidalization.generateTrapezoid();

        repaint();
    }

    public void monotonePartition() {
        trapezoidalLines.clear();
        partitionLines.clear();
        monotonePartDiag.clear();
        monotonePartition.dcel = dcel;
        monotonePartition.monotonepartLines = partitionLines;
        monotonePartition.monotonePartDiag = monotonePartDiag;
        monotonePartition.generate();

        dcelList = monotonePartition.getSeparateDCEL();

        repaint();
    }

    public void triangulatePartitions() {
        triangulationLines.clear();
        trapezoidalLines.clear();
        for (DCEL dcel : dcelList) {
            Triangulation trisol = new Triangulation(dcel, triangulationLines);
            trisol.generate();
        }

        repaint();
    }

    public void dualGraph() {
        dualGraph.dcelList = dcelList;
        dualGraph.partitionDiagonalsList = monotonePartDiag;
        dualGraph.generate();

        repaint();
    }

    public void threecolor() {
        triangulationLines.clear();
        partitionLines.clear();
        dualGraph.solve();
        colour = dualGraph.clr;
        finale = true;
        dualGraph.dualEdgeList.clear();
        dualGraph.dualVertexArray.clear();

        repaint();
    }

    public void zoomIn(){
        scale *= 1.1;
        repaint();
        
    }

    public void zoomOut(){
        scale /= 1.1;
        repaint();
    }

    public void lefT(){
        translateX -= 10;
        repaint();
    }
    public void righT(){
        translateX += 10;
        repaint();
    }
    public void dowN(){
        translateY+= 10;
        repaint();
    }
    public void uP(){
        translateY -= 10;
        repaint();
    }


    public void paintComponent(Graphics page) {
        super.paintComponent(page);

        //Showing the Points
        Graphics2D g2d = (Graphics2D) page;
        // AffineTransform oldTransform = g2d.getTransform();

        // Apply zoom and pan transformations
        g2d.translate(translateX, translateY);
        g2d.scale(scale, scale);

        page.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        page.setColor(Color.green);
        for (Point spot : pointList) {
            if (finale) { // If min vertex guard solution is present
                if (colour.get(spot) == 1)
                    page.setColor(Color.RED);
                else if (colour.get(spot) == 2)
                    page.setColor(Color.BLUE);
                else
                    page.setColor(Color.GREEN);
                if (colour.get(spot) == dualGraph.minc) {
                    page.fillOval(spot.x - 8, -1 * spot.y - 9, 19, 19);
                    page.setColor(Color.WHITE);
                    page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
                } else {
                    page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
                }
                page.setColor(Color.GREEN);
                page.drawString("Vertex Guards: " + dualGraph.ct[dualGraph.minc], 5, 42);
            } else
                page.fillOval(spot.x - 3, -1 * spot.y - 3, 7, 7);
        }

        //rendering the polygon constructed
        page.setColor(new Color(224, 199, 70));
        if (circularPointList != null && circularPointList.size() > 1) {
            for (int ii = 0; ii < circularPointList.size() - 1; ii++) {
                page.drawLine(circularPointList.get(ii).x, -1 * circularPointList.get(ii).y, circularPointList.get(ii + 1).x, -1 * circularPointList.get(ii + 1).y);
            }
            page.drawLine(circularPointList.get(circularPointList.size() - 1).x, -1 * circularPointList.get(circularPointList.size() - 1).y, circularPointList.get(0).x, -1 * circularPointList.get(0).y);
        }

        //rendering the Trapezoidalization segments
        page.setColor(new Color(112, 189, 19));
        if (trapezoidalLines != null && trapezoidalLines.size() > 0) {
            for (int ii = 0; ii < trapezoidalLines.size(); ii++)
                page.drawLine(trapezoidalLines.get(ii).a.x, -1 * trapezoidalLines.get(ii).a.y, trapezoidalLines.get(ii).b.x, -1 * trapezoidalLines.get(ii).b.y);
        }

        //rendering the partition segments
        page.setColor(Color.GREEN);
        if (partitionLines != null && partitionLines.size() > 0) {
            for (int ii = 0; ii < partitionLines.size(); ii++)
                page.drawLine(partitionLines.get(ii).a.x, -1 * partitionLines.get(ii).a.y, partitionLines.get(ii).b.x, -1 * partitionLines.get(ii).b.y);
        }

        //rendering the trig segments
        page.setColor(Color.GREEN);
        if (triangulationLines != null && triangulationLines.size() > 0) {
            for (int ii = 0; ii < triangulationLines.size(); ii++)
                page.drawLine(triangulationLines.get(ii).a.x, -1 * triangulationLines.get(ii).a.y, triangulationLines.get(ii).b.x, -1 * triangulationLines.get(ii).b.y);
        }

        //rendering dual tree edges and vertices
        if (dualGraph.dualEdgeList != null && dualGraph.dualEdgeList.size() > 0) {
            page.setColor(Color.YELLOW);
            if (dualGraph.dualEdgeList != null && dualGraph.dualEdgeList.size() > 0) {
                for (int ii = 0; ii < dualGraph.dualEdgeList.size(); ii++)
                    page.drawLine(dualGraph.dualEdgeList.get(ii).a.x, -1 * dualGraph.dualEdgeList.get(ii).a.y,
                            dualGraph.dualEdgeList.get(ii).b.x, -1 * dualGraph.dualEdgeList.get(ii).b.y);
            }
            page.setColor(Color.RED);
            for (Vertex spot : dualGraph.dualVertexArray) {
                page.fillOval(spot.toPoint().x - 3, -1 * spot.toPoint().y - 3, 7, 7);
            }
        }

        page.setColor(Color.GREEN);
        //display the points count
        page.drawString("Vertices: " + pointList.size(), 5, 20);
    }

    //this method clears the canvas
    public void clear() {
        //clear all list of points and waypoints
        pointList.clear();
        circularPointList.clear();
        trapezoidalLines.clear();
        partitionLines.clear();
        triangulationLines.clear();
        dualGraph = new DualGraph();
        //clear the canvas
        finale = false;
        repaint();
    }
}
